package com.mhmdjalal.newsapp.utils

import org.ocpsoft.prettytime.PrettyTime
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun convertTime(value: String?): String {
        return try {
            val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(value ?: "")
            val prettyTime = PrettyTime(Locale.US)
            prettyTime.format(date)
        } catch (e: ParseException) {
            "-"
        }
    }
}