package com.mhmdjalal.newsapp.utils

import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorUtils {
    fun getThrowableErrMsg(throwable: Throwable): String = when (throwable) {
        is ClientRequestException ->
            when (throwable.response.status) {
                HttpStatusCode.Unauthorized -> "Unable to access data"
                HttpStatusCode.NotFound -> "Data not found"
                HttpStatusCode.InternalServerError -> "There was a problem with the server"
                HttpStatusCode.BadRequest -> "Invalid data"
                HttpStatusCode.Forbidden -> "Session has ended"
                else -> "Oops, An error occurred, please try again in a moment"
            }
        is UnknownHostException -> "Unknown Error"
        is ConnectException -> "No internet connected"
        is SocketTimeoutException -> "No internet connected"
        else -> "Whoops! Something went wrong!"
    }
}