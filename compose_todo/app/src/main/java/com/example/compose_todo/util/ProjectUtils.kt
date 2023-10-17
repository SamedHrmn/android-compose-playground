package com.example.compose_todo.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ProjectUtils{
    fun convertMillisToDate(milliseconds: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = Date(milliseconds)
        return sdf.format(date)
    }

    fun convertDateToMillis(dateString: String): Long? {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        try {
            if (dateString.isEmpty()) return null

            val date = sdf.parse(dateString)
            return date?.time ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0L
    }
}