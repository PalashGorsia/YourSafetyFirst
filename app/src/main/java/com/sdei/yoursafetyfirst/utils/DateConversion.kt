package com.app.yoursafetyfirst.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

object DateConversion {

    fun utcConversion(date:String):String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        try {
            val date: Date = format.parse(date)
            val sdf = SimpleDateFormat("yyyy/MM/dd").apply {
                timeZone = TimeZone.getDefault()
            }
            val sdf1 = SimpleDateFormat("HH:mm").apply {
                timeZone = TimeZone.getDefault()
            }

            val date1 = sdf.format(date)
            val time = sdf1.format(date)
           return "$date1  $time"
        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }

    }
}