package com.example.simpletodolist.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long?.toFormattedDateTime(): String {
    if (this == null) return ""

    val date = Date(this)

    val formatter = SimpleDateFormat("dd.MM.yyyy 'в' HH:mm", Locale("ru", "RU"))

    return formatter.format(date)
}