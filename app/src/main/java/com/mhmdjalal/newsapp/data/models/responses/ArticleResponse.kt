package com.mhmdjalal.newsapp.data.models.responses


import com.mhmdjalal.newsapp.data.models.Article
import com.squareup.moshi.Json

data class ArticleResponse(
    @Json(name = "articles")
    val articles: List<Article>?,
    @Json(name = "totalResults")
    val totalResults: Int?,
    @Json(name = "status")
    val status: String?,
    @Json(name = "code")
    val code: String?,
    @Json(name = "message")
    val message: String?
)