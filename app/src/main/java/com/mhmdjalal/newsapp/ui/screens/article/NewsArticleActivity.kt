package com.mhmdjalal.newsapp.ui.screens.article

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mhmdjalal.newsapp.R
import com.mhmdjalal.newsapp.data.models.Source
import com.mhmdjalal.newsapp.databinding.ActivityNewsArticleBinding
import com.mhmdjalal.newsapp.network.ResourceState
import com.mhmdjalal.newsapp.ui.base.BaseActivity
import com.mhmdjalal.newsapp.ui.screens.article.adapter.ArticleAdapter
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
class NewsArticleActivity : BaseActivity() {

    private val binding by viewBinding(ActivityNewsArticleBinding::inflate)

    private val viewModel by viewModel<ArticleViewModel>()

    private lateinit var articleAdapter: ArticleAdapter

    private var source: Source? = null
    private var keyword: String? = null

    private val onClickRetry: (View) -> Unit = {
        it.disabled(0.5f)
        sync()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        source = intent.getParcelableExtra(EXTRA_SOURCE)
        supportActionBar?.title = "Articles"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.textTitlePage.text = str(R.string.text_articles_source, source?.name, source?.url)

        articleAdapter = ArticleAdapter { item ->
            start<DetailNewsArticleActivity> {
                putExtra(DetailNewsArticleActivity.EXTRA_ARTICLE, item)
            }
        }
        initRecyclerView()

        binding.swipeRefreshLayout.setOnRefreshListener {
            sync(refreshData = true)
        }

        sync()
    }

    private var isLoading = false
    private fun initRecyclerView() = with(binding) {
        recyclerArticle.apply {
            adapter = articleAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?

                    if (!isLoading) {
                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == articleAdapter.getData().size - 1) {
                            sync()
                        }
                    }
                }
            })
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
                sync(refreshData = true)
                return true
            }

        })

        val searchView = item?.actionView as SearchView
        searchView.queryHint = str(R.string.hint_search_article)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                hideKeyboard(currentFocus ?: View(this@NewsArticleActivity))
                keyword = query
                sync(refreshData = true)
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                keyword = query
                if (query.isNullOrEmpty()) {
                    sync(refreshData = true)
                }
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override suspend fun onObserveAction() {
        super.onObserveAction()

        viewModel.articleResponse.observe(this) {
            when (it.state) {
                ResourceState.SUCCESS -> {
                    articleAdapter.setData(it.data?.articles ?: emptyList())

                    if (articleAdapter.getData().isEmpty()) {
                        binding.recyclerArticle.gone()
                        binding.layoutError.handleResponseError("No data.", onClickRetry)
                    } else {
                        binding.recyclerArticle.visible()
                        binding.layoutError.root.gone()
                    }
                }
                ResourceState.ERROR -> {
                    binding.recyclerArticle.gone()
                    binding.layoutError.handleResponseError(it.message ?: "-", onClickRetry)
                }
                ResourceState.LOADING -> {
                    isLoading = it.showLoading ?: false

                    if (isLoading) {
                        if (articleAdapter.getData().isNotEmpty()) {
                            articleAdapter.addLoadingView()
                            binding.recyclerArticle.smoothScrollToPosition(articleAdapter.itemCount)
                        } else {
                            binding.progressCircular.visible()
                        }
                    } else {
                        articleAdapter.removeLoadingView()
                        binding.swipeRefreshLayout.stopRefreshing()
                        binding.progressCircular.gone()
                    }
                }
            }
        }
    }

    private fun sync(refreshData: Boolean = false) {
        if (refreshData) articleAdapter.clearData()
        val queries = hashMapOf<String, String?>()
        source?.id?.let { queries["sources"] = it }
        keyword?.let { queries["q"] = it }
        viewModel.getArticlesBySource(queries, refreshData)
    }

    companion object {
        const val EXTRA_SOURCE = "source"
    }
}