package com.mhmdjalal.newsapp.repositories

import com.mhmdjalal.newsapp.data.models.Category
import com.mhmdjalal.newsapp.data.models.responses.ArticleResponse
import com.mhmdjalal.newsapp.network.ApiServices
import com.mhmdjalal.newsapp.network.Resource
import com.mhmdjalal.newsapp.network.request
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * @author Created by Muhamad Jalaludin on 20/10/2022
 */
class MainRepositoryImpl(private val apiServices: ApiServices): MainRepository {

    private val dummyCategories = listOf(
        Category(name = "Business", categoryKey = "business"),
        Category(name = "Entertainment", categoryKey = "entertainment"),
        Category(name = "General", categoryKey = "general"),
        Category(name = "Health", categoryKey = "health"),
        Category(name = "Science", categoryKey = "science"),
        Category(name = "Sports", categoryKey = "sports"),
        Category(name = "Technology", categoryKey = "technology")
    )

    override suspend fun getCategories(response: (List<Category>) -> Unit) {
        response(dummyCategories)
    }

    /**
     * @param query: q, sources, page, pageSize
     */
    override suspend fun getArticlesBySource(query: HashMap<String, String>): Flow<Resource<ArticleResponse>> = flow {
        request<ArticleResponse> { apiServices.getArticles(query) }
            .collect { result ->
                emit(result)
            }
    }

}