package com.mhmdjalal.newsapp.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import com.mhmdjalal.newsapp.utils.Constants
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException

/**
 * @author Created by Muhamad Jalaludin on 20/10/2022
 */
class NoConnectionInterceptor(context: Context) : Interceptor {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun intercept(chain: Interceptor.Chain): Response {
        return if (!isConnectionOn()) {
            throw NoConnectivityException()
        } else if (!isInternetAvailable()) {
            throw NoInternetException()
        } else {
            chain.proceed(chain.request())
        }
    }

    /**
     * check the network is connected to WIFI/DATA
     */
    @Suppress("DEPRECATION")
    private fun isConnectionOn(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
                return when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
                    else -> false
                }
            }
            return false
        } else {
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            return if (activeNetwork != null) {
                (activeNetwork.type == ConnectivityManager.TYPE_WIFI ||
                        activeNetwork.type == ConnectivityManager.TYPE_MOBILE)
            } else false
        }
    }

    /**
     * Use socket to check the connection
     */
    private fun isInternetAvailable(): Boolean {
        return try {
            val sock = Socket()
            val sockAddress = InetSocketAddress("8.8.8.8", Constants.SOCKET_PORT)

            sock.connect(sockAddress, Constants.SOCKET_TIMEOUT_MS)
            sock.close()

            true
        } catch (e: SocketTimeoutException) {
            true // handle socket timeout pada class RequestExtension
        } catch (e: IOException) {
            false
        }
    }
}