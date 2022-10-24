package com.mhmdjalal.newsapp.repositories

import com.mhmdjalal.newsapp.data.models.Category
import com.mhmdjalal.newsapp.data.models.responses.ArticleResponse
import com.mhmdjalal.newsapp.network.Resource
import kotlinx.coroutines.flow.Flow

/**
 * @author Created by Muhamad Jalaludin on 20/10/2022
 */
interface MainRepository {
    suspend fun getCategories(response: (List<Category>) -> Unit)
    suspend fun getArticlesBySource(query: HashMap<String, String?>): Flow<Resource<ArticleResponse>>
}