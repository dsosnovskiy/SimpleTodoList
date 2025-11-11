package com.example.simpletodolist.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val DATE_TIME_FORMAT = "MMMM dd, yyyy, 'at' HH:mm"

fun Long?.toFormattedDateTimeFromMillis(): String {
    if (this == null) return ""

    val date = Date(this)

    val formatter = SimpleDateFormat(DATE_TIME_FORMAT, Locale.ENGLISH)

    return formatter.format(date)
}

fun String?.toMillisFromFormattedDateTime(): Long? {
    if (this.isNullOrBlank()) return null

    val formatter = SimpleDateFormat(DATE_TIME_FORMAT, Locale.ENGLISH)

    return try {
        val date: Date? = formatter.parse(this)
        date?.time
    } catch (e: ParseException) {
        e.printStackTrace()
        null
    }
}