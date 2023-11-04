package com.mhmdjalal.newsapp.data.models.responses


import com.mhmdjalal.newsapp.data.models.Article

@kotlinx.serialization.Serializable
data class ArticleResponse(
    val articles: List<Article>? = null,
    val totalResults: Int? = null,
    val status: String? = null,
    val code: String? = null,
    val message: String? = null
)