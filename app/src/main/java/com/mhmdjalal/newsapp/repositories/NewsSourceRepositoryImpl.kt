package com.mhmdjalal.newsapp.repositories

import com.mhmdjalal.newsapp.data.models.Source
import com.mhmdjalal.newsapp.data.models.SourcesPaginate
import com.mhmdjalal.newsapp.data.models.responses.SourceResponse
import com.mhmdjalal.newsapp.network.ApiServices
import com.mhmdjalal.newsapp.network.Resource
import com.mhmdjalal.newsapp.network.ResourceState
import com.mhmdjalal.newsapp.network.request
import com.mhmdjalal.newsapp.utils.Constants
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * @author Created by Muhamad Jalaludin on 20/10/2022
 */
class NewsSourceRepositoryImpl(private val apiServices: ApiServices) : NewsSourceRepository {

    /**
     * @param query: category
     */
    override suspend fun fetchSources(
        currentSources: List<Source>,
        query: HashMap<String, String>
    ): Flow<Resource<SourcesPaginate>> = flow {
        val page = query.getValue("page").toIntOrNull() ?: 1
        val keyword = query.getOrDefault("q", "")

        if (currentSources.isEmpty()) {
            // fetch from network
            request<SourceResponse> { apiServices.getSources(query) }
                .collect { result ->

                    when (result.state) {
                        ResourceState.SUCCESS -> {
                            println("Fetched from network, ${result.data?.sources}")
                            emit(
                                Resource.success(
                                    SourcesPaginate(
                                        allSources = result.data?.sources ?: emptyList(),
                                        paginateSources = result.data?.sources.filterSources(
                                            page,
                                            keyword
                                        ) ?: emptyList()
                                    )
                                )
                            )
                        }
                        ResourceState.ERROR -> {
                            emit(Resource.error(result.message))
                        }
                        ResourceState.LOADING -> {
                            emit(Resource.loading(result.showLoading ?: false))
                        }
                    }
                }
        } else {
            // load from previous fetched sources
            coroutineScope {
                emit(Resource.loading(showLoading = true))
                delay(Constants.FAKE_LOADING_DURATION)
                val filteredSources = currentSources.filterSources(keyword)
                emit(
                    Resource.success(
                        SourcesPaginate(
                            allSources = filteredSources ?: emptyList(),
                            paginateSources = filteredSources?.take(page * PAGE_SIZE)
                                ?: emptyList()
                        )
                    )
                )
                println("Fetched sources, $currentSources")
                delay(Constants.FAKE_FETCHED_DURATION)
                emit(Resource.loading(showLoading = false))
            }
        }
    }

    private fun List<Source>?.filterSources(keyword: String? = null): List<Source>? {
        return this?.filter {
            it.name?.contains(keyword ?: "", ignoreCase = true) == true
        }
    }

    private fun List<Source>?.filterSources(page: Int, keyword: String? = null): List<Source>? {
        return this?.filter {
            it.name?.contains(keyword ?: "", ignoreCase = true) == true
        }?.take(page * PAGE_SIZE)
    }

    companion object {
        const val PAGE_SIZE = 8
    }

}