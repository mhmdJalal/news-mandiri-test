package com.mhmdjalal.newsapp.network

import com.mhmdjalal.newsapp.utils.Constants
import com.mhmdjalal.newsapp.utils.ErrorUtils
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.HttpResponse
import io.ktor.client.call.body
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.SerializationException
import org.json.JSONObject
import java.net.SocketTimeoutException

enum class ResourceState {
    SUCCESS,
    ERROR,
    LOADING
}

data class Resource<out T>(
    val state: ResourceState,
    val data: T?,
    val message: String?,
    val showLoading: Boolean?
) {

    companion object {

        fun <T> success(data: T?): Resource<T> {
            return Resource(ResourceState.SUCCESS, data, null, null)
        }

        fun <T> error(msg: String?, data: T? = null): Resource<T> {
            return Resource(ResourceState.ERROR, data, msg, null)
        }

        fun <T> loading(showLoading: Boolean): Resource<T> {
            return Resource(ResourceState.LOADING, null, null, showLoading)
        }

    }

}

suspend inline fun <reified T> request(
    crossinline response: suspend () -> HttpResponse
): Flow<Resource<T>> = flow {
    try {
        emit(Resource.loading(showLoading = true))
        handlingResponse<T>(response)
            .collect { emit(it) }
    } catch (throwable: Throwable) {
        throwable.printStackTrace()
        val messageError = ErrorUtils.getThrowableErrMsg(throwable)
        emit(Resource.error(messageError))
    } finally {
        emit(Resource.loading(showLoading = false))
    }
}

suspend inline fun <reified T> handlingResponse(
    crossinline response: suspend () -> HttpResponse
): Flow<Resource<T>> = flow {
    var messageError: String? = null
    val result = response()
    if (result.status.value in Constants.SUCCESS_CODE) {
        // handle success response
        result.body<T>()?.let { body ->
            emit(Resource.success(body))
        }
    } else {

        val errBody = result.bodyAsText()
        try {
            val resError = JSONObject(errBody)
            if (resError.has("message")) {
                messageError = resError.getString("message")
            }

            if (resError.has("meta")) {
                val metaError = JSONObject(resError.getString("meta"))
                messageError = metaError.getString("message")
            }
        } catch (e: SocketTimeoutException) {
            messageError = "Waktu koneksi habis"
        } catch (e: NoConnectivityException) {
            messageError = e.message
        } catch (e: NoInternetException) {
            messageError = e.message
        } catch (e: SerializationException) {
            e.printStackTrace()
            messageError = "Terjadi kesalahan saat menguraikan data"
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            messageError = ErrorUtils.getThrowableErrMsg(throwable)
        } finally {
            if (messageError != null) emit(Resource.error(messageError))
        }
    }
}