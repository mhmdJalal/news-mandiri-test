package com.mhmdjalal.newsapp.network

import java.io.IOException

/**
 * @author Created by Muhamad Jalaludin on 16/02/2021
 */
class NoConnectivityException : IOException() {
    override val message: String
        get() =
            "No network available, please check your WiFi or Data connection"
}