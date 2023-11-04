package com.mhmdjalal.newsapp.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.mhmdjalal.newsapp.R
import splitties.resources.color


/**
 * circular progress for load image
 */
fun circularProgress(context: Context, size: Int = CircularProgressDrawable.DEFAULT): CircularProgressDrawable {
    val circularProgressDrawable = CircularProgressDrawable(context)
    circularProgressDrawable.setColorSchemeColors(context.color(R.color.purple_500))
    circularProgressDrawable.strokeWidth = Constants.CIRCULAR_STROKE_WIDTH
    circularProgressDrawable.centerRadius = Constants.CIRCULAR_RADIUS
    circularProgressDrawable.setStyle(size)
    circularProgressDrawable.start()
    return circularProgressDrawable
}

fun requestOptionsCircular(context: Context): RequestOptions {
    val circularProgress = circularProgress(context)
    return RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(circularProgress)
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}