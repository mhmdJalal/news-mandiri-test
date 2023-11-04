package com.mhmdjalal.newsapp.data.models.responses


import com.mhmdjalal.newsapp.data.models.Source

@kotlinx.serialization.Serializable
data class SourceResponse(
    val sources: List<Source>? = null,
    val status: String? = null,
    val code: String? = null,
    val message: String? = null
)