package com.mhmdjalal.newsapp.di

import android.util.Log
import com.mhmdjalal.newsapp.BuildConfig
import com.mhmdjalal.newsapp.network.ApiServices
import com.mhmdjalal.newsapp.utils.Constants
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.headers
import io.ktor.http.append
import io.ktor.http.HttpHeaders
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
/**
 * @author Created by Muhamad Jalaludin on 20/10/2022
 */
val networkModules = module {
    singleOf(::ktorHttpClient)
    singleOf(::ApiServices)
}

private val ktorHttpClient = HttpClient(Android) {
    // only accept success response and throw exceptions if failed
    expectSuccess = true

    // setup default request
    defaultRequest {
        host = BuildConfig.BASE_URL
        headers {
            append(HttpHeaders.ContentType, ContentType.Application.Json)
            append(HttpHeaders.Accept, ContentType.Application.Json)
            append("X-Api-Key", BuildConfig.NEWS_API_KEY)
        }
        url {
            protocol = URLProtocol.HTTPS
        }
    }
    engine {
        // setup connect time out and socket time out
        connectTimeout = Constants.NETWORK_TIMEOUT
        socketTimeout = Constants.NETWORK_TIMEOUT
    }

    // json serialization
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    // logging
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                Log.v("Logger Ktor =>", message)
            }

        }
        level = LogLevel.ALL
    }

    install(ResponseObserver) {
        onResponse { response ->
            Log.d("HTTP status:", "${response.status.value}")
        }
    }
}