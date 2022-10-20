package com.mhmdjalal.newsapp.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface ApiServices {

    @GET("/v2/sources")
    suspend fun getSources(
        @QueryMap query: Map<String, String>
    )

    @GET("/v2/top-headlines")
    suspend fun getTopHeadlines(
        @QueryMap query: Map<String, String>
    )


    @GET("/v2/everything")
    suspend fun getEverything(
        @QueryMap query: Map<String, String>
    )

}