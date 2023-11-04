package com.mhmdjalal.newsapp.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val name: String,
    val categoryKey: String
): Parcelable
