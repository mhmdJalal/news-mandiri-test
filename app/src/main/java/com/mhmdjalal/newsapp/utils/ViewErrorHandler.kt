package com.mhmdjalal.newsapp.utils

import android.view.View
import androidx.core.view.isVisible
import com.mhmdjalal.newsapp.databinding.LayoutErrorBinding
import com.mhmdjalal.newsapp.utils.ViewExt.enabled

/**
 * @author Created by Muhamad Jalaludin on 24/10/2022
 */

fun LayoutErrorBinding.handleResponseError(errMessage: String?, listener: (View) -> Unit) {
    root.isVisible = !errMessage.isNullOrEmpty()
    if (!errMessage.isNullOrEmpty()) {
        btnRetry.enabled()
        textErrorMessage.text = errMessage
        btnRetry.setOnClickListener(listener)
    }
}