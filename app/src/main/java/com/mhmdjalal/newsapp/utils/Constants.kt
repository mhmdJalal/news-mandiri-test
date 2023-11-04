package com.mhmdjalal.newsapp.utils

/**
 * @author Created by Muhamad Jalaludin on 04/11/23
 */
object Constants {
    const val READ_TIMEOUT: Long = 60
    const val CONNECT_TIMEOUT: Long = 60
    const val NETWORK_TIMEOUT = 60000
    const val CIRCULAR_RADIUS: Float = 30f
    const val CIRCULAR_STROKE_WIDTH = 5f
    const val PROGRESS_COMPLETE = 100
    const val HALF_OPACITY = 0.5f
    const val SOCKET_TIMEOUT_MS = 1500
    const val SOCKET_PORT = 53
    const val FAKE_LOADING_DURATION: Long = 1500
    const val FAKE_FETCHED_DURATION: Long = 100

    private const val SUCCESS_CODE_START = 200
    private const val SUCCESS_CODE_END = 299
    val SUCCESS_CODE = SUCCESS_CODE_START .. SUCCESS_CODE_END
}