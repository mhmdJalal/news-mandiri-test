package com.mhmdjalal.newsapp.network

import java.io.IOException

/**
 * @author Created by Muhamad Jalaludin on 16/02/2021
 */
class NoInternetException : IOException() {
    override val message: String
        get() =
            "No internet available, please check your connected WiFi or Data"
}