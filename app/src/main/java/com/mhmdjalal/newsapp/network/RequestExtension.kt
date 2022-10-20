package com.mhmdjalal.newsapp.network

import android.util.Log
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import java.net.SocketTimeoutException

enum class State {
    SUCCESS,
    ERROR,
    LOADING
}

data class Resource<out T>(val state: State, val data: T?, val message: String?, val isLoading: Boolean) {

    companion object {

        fun <T> success(data: T?): Resource<T> {
            return Resource(State.SUCCESS, data, null, false)
        }

        fun <T> error(msg: String?, data: T? = null): Resource<T> {
            return Resource(State.ERROR, data, msg, false)
        }

        fun <T> loading(isLoading: Boolean = false): Resource<T> {
            return Resource(State.LOADING, null, null, isLoading)
        }

    }

}


fun <T> request(coroutineScope: CoroutineScope, response: suspend() -> Response<T>, results: (Resource<T>) -> Unit) {
    try {
        coroutineScope.launch {
            results(Resource.loading(isLoading = true))
            handlingResponse(response) {
                results(it)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("REQUEST_FAILED", "not sending request")
    }
}

suspend fun <T> handlingResponse(response: suspend() -> Response<T>, results: (Resource<T>) -> Unit) {
    try {
        val result = response()
        if (result.isSuccessful) {
            result.body()?.let { body ->
                results(Resource.loading(isLoading = false))
                results(Resource.success(body))
            }
        } else {
            val errBody = result.errorBody()?.charStream()?.readText() ?: ""
            var errMessage = errBody
            try {
                val resError = JSONObject(errBody)
                if (resError.has("message")) {
                    errMessage = resError.getString("message")
                }

                if (resError.has("meta")) {
                    val metaError = JSONObject(resError.getString("meta"))
                    errMessage = metaError.getString("message")
                }
            } catch (e: SocketTimeoutException) {
                errMessage = "Waktu koneksi habis"
            } catch (e: NoConnectivityException) {
                errMessage = e.message
            } catch (e: NoInternetException) {
                errMessage = e.message
            } catch (e: JsonDataException) {
                e.printStackTrace()
                errMessage = "Terjadi kesalahan saat menguraikan data"
            } catch (throwable: Throwable) {
                errMessage = "Ops, something went wrong!"
            }
            results(Resource.loading(isLoading = false))
            results(Resource.error(errMessage))
        }
    } catch (e: SocketTimeoutException) {
        results(Resource.error("Waktu koneksi habis"))
    } catch (e: NoConnectivityException) {
        results(Resource.error(e.message))
    } catch (e: NoInternetException) {
        results(Resource.error(e.message))
    } catch (e: JsonDataException) {
        e.printStackTrace()
        results(Resource.loading(isLoading = false))
        results(Resource.error("Terjadi kesalahan saat menguraikan data"))
    } catch (throwable: Throwable) {
        results(Resource.loading(isLoading = false))
        results(Resource.error("Ops, something went wrong!"))
    }
}
