package com.mhmdjalal.newsapp.network

import com.mhmdjalal.newsapp.data.models.responses.ArticleResponse
import com.mhmdjalal.newsapp.data.models.responses.SourceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface ApiServices {

    @GET("/v2/sources")
    suspend fun getSources(
        @QueryMap query: Map<String, String?>
    ): Response<SourceResponse>

    @GET("/v2/everything")
    suspend fun getArticles(
        @QueryMap query: Map<String, String?>
    ): Response<ArticleResponse>

}