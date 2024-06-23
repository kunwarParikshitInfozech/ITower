package com.isl.leaseManagement.utils

import android.content.Context
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object Utilities {
    fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }
    fun getDateFromISO8601(inputDate: String): String {
        val inputFormatter = DateTimeFormatter.ISO_DATE_TIME
        val outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

        val zonedDateTime = ZonedDateTime.parse(inputDate, inputFormatter)
        val localDateTime = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()

        return localDateTime.format(outputFormatter)
    }
}