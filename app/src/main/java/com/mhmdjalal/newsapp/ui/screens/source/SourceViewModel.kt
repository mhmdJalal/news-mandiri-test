package com.mhmdjalal.newsapp.ui.screens.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhmdjalal.newsapp.data.models.Pagination
import com.mhmdjalal.newsapp.data.models.Source
import com.mhmdjalal.newsapp.data.models.SourcesPaginate
import com.mhmdjalal.newsapp.network.Resource
import com.mhmdjalal.newsapp.network.ResourceState.SUCCESS
import com.mhmdjalal.newsapp.repositories.NewsSourceRepository
import com.mhmdjalal.newsapp.repositories.NewsSourceRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.ceil

enum class SourceRequestState {
    Searching, Scrolling, Synchronize
}

/**
 * @author Created by Muhamad Jalaludin on 21/10/2022
 */
class SourceViewModel(private val repository: NewsSourceRepository) : ViewModel() {

    private var _newsSourceList = MutableLiveData<Resource<SourcesPaginate>>()
    val newsSourceList: LiveData<Resource<SourcesPaginate>>
        get() = _newsSourceList

    private var _originalSources = MutableStateFlow(emptyList<Source>())
    private val originalSources = _originalSources.asStateFlow()

    private var _searchSources = MutableStateFlow(emptyList<Source>())
    private val searchSources = _searchSources.asStateFlow()

    private var _pagination = MutableStateFlow(Pagination())
    private val pagination = _pagination.asStateFlow()

    private fun updatePagination() {
        val paginationTemp = Pagination(
            total = pagination.value.total,
            totalPage = pagination.value.totalPage,
            current = pagination.value.current.plus(1)
        )

        _pagination.update { paginationTemp }
    }

    private fun initializePagination(totalResults: Int) {
        val totalPage = totalResults.toDouble().div(NewsSourceRepositoryImpl.PAGE_SIZE)
        val paginationTemp = Pagination(
            total = totalResults,
            totalPage = ceil(totalPage).toInt()
        )

        _pagination.update { paginationTemp }
    }

    fun getSourcesByCategory(
        query: HashMap<String, String>,
        sourceRequestState: SourceRequestState
    ) {
        // returning request if state is scrolling and current page greater than total page
        if (sourceRequestState == SourceRequestState.Scrolling &&
            pagination.value.current > pagination.value.totalPage) return

        viewModelScope.launch {
            val defaultQueries = hashMapOf<String, String>()
            defaultQueries["pageSize"] = "${NewsSourceRepositoryImpl.PAGE_SIZE}"
            defaultQueries["page"] = pagination.value.current.toString()
                .takeIf { sourceRequestState == SourceRequestState.Scrolling } ?: "1"
            defaultQueries.putAll(query)

            val currentSources = (searchSources.takeIf {
                sourceRequestState == SourceRequestState.Searching &&
                        searchSources.value.isNotEmpty()
            } ?: originalSources).value
            repository.fetchSources(currentSources, defaultQueries)
                .collect { result ->
                    if (result.state == SUCCESS) {
                        when (sourceRequestState) {
                            SourceRequestState.Synchronize -> {
                                // set pagination for original sources
                                _originalSources.update { result.data?.allSources ?: emptyList() }
                                initializePagination(result.data?.allSources?.size ?: 0)
                            }
                            SourceRequestState.Searching -> {
                                // set sources and pagination for searching
                                _searchSources.update { result.data?.allSources ?: emptyList() }
                                initializePagination(result.data?.allSources?.size ?: 0)
                            }
                            SourceRequestState.Scrolling -> {
                                // only update pagination if state is scrolling
                                updatePagination()
                            }
                        }
                    }

                    _newsSourceList.value = result
                }
        }
    }

}