package com.mhmdjalal.newsapp.di

import com.mhmdjalal.newsapp.BuildConfig
import com.mhmdjalal.newsapp.network.NoConnectionInterceptor
import com.mhmdjalal.newsapp.network.ApiServices
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author Created by Muhamad Jalaludin on 20/10/2022
 */
val networkModules = module {
    val baseUrl = BuildConfig.BASE_URL

    fun provideHttpClient(loggingInterceptor: HttpLoggingInterceptor, connectionInterceptor: NoConnectionInterceptor): OkHttpClient {
        val client = OkHttpClient.Builder()

        client.connectTimeout(60, TimeUnit.SECONDS)
        client.writeTimeout(60, TimeUnit.SECONDS)
        client.readTimeout(60, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) client.addInterceptor(loggingInterceptor)
        client.addInterceptor(connectionInterceptor)

        return client.addInterceptor {
            val original = it.request()
            val requestBuilder = original.newBuilder()
            requestBuilder.header("Content-Type", "application/json")
            requestBuilder.header("Accept", "application/json")
            requestBuilder.header("X-Api-Key", BuildConfig.NEWS_API_KEY)
            val request = requestBuilder.method(original.method, original.body).build()
            return@addInterceptor it.proceed(request)
        }.build()
    }

    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return logging
    }


    single { provideHttpLoggingInterceptor() }
    single { NoConnectionInterceptor(androidContext()) }
    single { provideHttpClient(get(), get()) }

    single {
        createService<ApiServices>(
            get(),
            baseUrl
        )
    }
}

/**
 * create service use retrofit
 */
inline fun <reified T> createService(okHttpClient: OkHttpClient, baseUrl: String): T {
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(okHttpClient)
        .build()
    return retrofit.create(T::class.java)
}