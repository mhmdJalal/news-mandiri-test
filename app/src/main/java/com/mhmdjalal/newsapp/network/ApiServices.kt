package com.mhmdjalal.newsapp.network

import io.ktor.client.HttpClient
import io.ktor.client.request.get

class ApiServices(private val client: HttpClient) {

    suspend fun getSources(request: Map<String, String>) =
        client.get("/v2/sources") {
            url {
                request.map {
                    parameters.append(it.key, it.value)
                }
            }
        }

    suspend fun getArticles(request: Map<String, String>) =
        client.get("/v2/everything") {
            url {
                request.map {
                    parameters.append(it.key, it.value)
                }
            }
        }

}