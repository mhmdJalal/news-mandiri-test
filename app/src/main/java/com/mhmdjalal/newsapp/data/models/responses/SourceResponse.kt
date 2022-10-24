package com.mhmdjalal.newsapp.data.models.responses


import com.mhmdjalal.newsapp.data.models.Source
import com.squareup.moshi.Json

data class SourceResponse(
    @Json(name = "sources")
    val sources: List<Source>?,
    @Json(name = "status")
    val status: String?,
    @Json(name = "code")
    val code: String?,
    @Json(name = "message")
    val message: String?
)