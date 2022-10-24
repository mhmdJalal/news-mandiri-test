package com.mhmdjalal.newsapp.ui.screens.category

import android.os.Bundle
import com.mhmdjalal.newsapp.databinding.ActivityNewsCategoryBinding
import com.mhmdjalal.newsapp.ui.base.BaseActivity
import com.mhmdjalal.newsapp.ui.screens.category.adapter.CategoryAdapter
import com.mhmdjalal.newsapp.ui.screens.source.NewsSourceActivity
import com.mhmdjalal.newsapp.utils.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import splitties.activities.start

class NewsCategoryActivity : BaseActivity() {

    private val binding by viewBinding(ActivityNewsCategoryBinding::inflate)

    private val viewModel by viewModel<CategoryViewModel>()

    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.title = "Choose News Category"

        categoryAdapter = CategoryAdapter { item ->
            start<NewsSourceActivity> {
                putExtra(NewsSourceActivity.EXTRA_CATEGORY, item)
            }
        }
        binding.recyclerCategory.adapter = categoryAdapter

        viewModel.getCategories()
    }

    override suspend fun onObserveAction() {
        super.onObserveAction()
        viewModel.categories.collect {
            categoryAdapter.setData(it)
        }
    }

}