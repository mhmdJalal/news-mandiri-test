package com.mhmdjalal.newsapp.ui.screens.article

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhmdjalal.newsapp.data.models.Pagination
import com.mhmdjalal.newsapp.data.models.responses.ArticleResponse
import com.mhmdjalal.newsapp.network.Resource
import com.mhmdjalal.newsapp.repositories.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.ceil

/**
 * @author Created by Muhamad Jalaludin on 22/10/2022
 */
class ArticleViewModel(private val repository: MainRepository): ViewModel() {

    private var _articleResponse = MutableLiveData<Resource<ArticleResponse>>()
    val articleResponse: LiveData<Resource<ArticleResponse>>
        get() = _articleResponse

    private var _pagination = MutableStateFlow(Pagination())
    private val pagination =_pagination.asStateFlow()

    private fun calculatePagination(totalResults: Int, refreshData: Boolean = false) {
        val totalPage = totalResults.toDouble().div(PAGE_SIZE)
        val paginationTemp = Pagination(
            total = totalResults,
            totalPage = ceil(totalPage).toInt(),
            current = pagination.value.current.plus(1).takeIf { !refreshData } ?: 1
        )

        _pagination.update { paginationTemp }
    }

    fun getArticlesBySource(query: HashMap<String, String?>, refreshData: Boolean = false) {
        if (pagination.value.current >= pagination.value.totalPage) return

        viewModelScope.launch {
            val defaultQueries = hashMapOf<String, String?>()
            defaultQueries["pageSize"] = "$PAGE_SIZE"
            defaultQueries["page"] = "${pagination.value.current.inc()}".takeIf { !refreshData } ?: "1"
            defaultQueries.putAll(query)

            repository.getArticlesBySource(defaultQueries)
                .collect { result ->
                    result.data?.totalResults?.let { size ->
                        calculatePagination(size, refreshData)
                    }
                    _articleResponse.value = result
                }
        }
    }

    companion object {
        private const val PAGE_SIZE = 10
    }

}