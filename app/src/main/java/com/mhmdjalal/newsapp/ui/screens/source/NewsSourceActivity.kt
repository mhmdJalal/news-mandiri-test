package com.mhmdjalal.newsapp.ui.screens.source

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mhmdjalal.newsapp.R
import com.mhmdjalal.newsapp.data.models.Category
import com.mhmdjalal.newsapp.databinding.ActivityNewsSourceBinding
import com.mhmdjalal.newsapp.network.ResourceState.*
import com.mhmdjalal.newsapp.ui.base.BaseActivity
import com.mhmdjalal.newsapp.ui.screens.article.NewsArticleActivity
import com.mhmdjalal.newsapp.ui.screens.source.adapter.SourceAdapter
import com.mhmdjalal.newsapp.utils.ViewExt.disabled
import com.mhmdjalal.newsapp.utils.ViewExt.gone
import com.mhmdjalal.newsapp.utils.ViewExt.stopRefreshing
import com.mhmdjalal.newsapp.utils.ViewExt.visible
import com.mhmdjalal.newsapp.utils.handleResponseError
import com.mhmdjalal.newsapp.utils.hideKeyboard
import com.mhmdjalal.newsapp.utils.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import splitties.activities.start
import splitties.resources.str

/**
 * @author Created by Muhamad Jalaludin on 21/10/2022
 */
class NewsSourceActivity : BaseActivity() {

    private val binding by viewBinding(ActivityNewsSourceBinding::inflate)

    private val viewModel by viewModel<SourceViewModel>()

    private lateinit var sourceAdapter: SourceAdapter

    private var category: Category? = null
    private var keyword: String? = null

    private val onClickRetry: (View) -> Unit = {
        it.disabled(alphaParam = 0.5f)
        sync(sourceRequestState = SourceRequestState.Synchronize)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        category = intent.getParcelableExtra(EXTRA_CATEGORY)
        supportActionBar?.title = str(R.string.text_selected_category, category?.name)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sourceAdapter = SourceAdapter { item ->
            start<NewsArticleActivity> {
                putExtra(NewsArticleActivity.EXTRA_SOURCE, item)
            }
        }
        initRecyclerView()

        binding.swipeRefreshLayout.setOnRefreshListener {
            sync(sourceRequestState = SourceRequestState.Synchronize)
        }

        sync(sourceRequestState = SourceRequestState.Synchronize)
    }

    private var isLoading = false
    private fun initRecyclerView() = with(binding) {
        recyclerSource.apply {
            adapter = sourceAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?

                    if (!isLoading) {
                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == sourceAdapter.getData().size.dec()) {
                            sync(sourceRequestState = SourceRequestState.Scrolling)
                        }
                    }
                }
            })
        }
    }

    override suspend fun onObserveAction() {
        super.onObserveAction()

        viewModel.newsSourceList.observe(this) {
            when (it.state) {
                SUCCESS -> {
                    if (it.data?.allSources.isNullOrEmpty()) {
                        binding.recyclerSource.gone()
                        binding.layoutError.handleResponseError("No data.", onClickRetry)
                    } else {
                        binding.recyclerSource.visible()
                        binding.layoutError.root.gone()
                    }

                    sourceAdapter.setData(it.data?.paginateSources ?: emptyList())
                }
                ERROR -> {
                    binding.recyclerSource.gone()
                    binding.layoutError.handleResponseError(it.message ?: "-", onClickRetry)
                }
                LOADING -> {
                    isLoading = it.showLoading ?: false

                    if (isLoading) {
                        if (sourceAdapter.getData().isNotEmpty()) {
                            sourceAdapter.addLoadingView()
                            binding.recyclerSource.smoothScrollToPosition(sourceAdapter.itemCount)
                        } else {
                            binding.progressCircular.visible()
                        }
                    } else {
                        sourceAdapter.removeLoadingView()
                        binding.swipeRefreshLayout.stopRefreshing()
                        binding.progressCircular.gone()
                    }
                }
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val item = menu.findItem(R.id.action_search)
        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                keyword = null
                sync(sourceRequestState = SourceRequestState.Synchronize)
                return true
            }

        })

        val searchView = item?.actionView as SearchView
        searchView.queryHint = str(R.string.hint_search_source)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                hideKeyboard(currentFocus ?: View(this@NewsSourceActivity))
                keyword = query
                sync(sourceRequestState = SourceRequestState.Searching)
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                keyword = query
                if (query.isNullOrEmpty()) {
                    sync(sourceRequestState = SourceRequestState.Searching)
                }
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    private fun sync(sourceRequestState: SourceRequestState) {
        if (sourceRequestState != SourceRequestState.Scrolling) sourceAdapter.clearData()

        val queries = hashMapOf<String, String?>()
        queries["category"] = category?.categoryKey
        keyword?.let { queries["q"] = it }
        viewModel.getSourcesByCategory(queries, sourceRequestState)
    }

    companion object {
        const val EXTRA_CATEGORY = "category"
    }
}