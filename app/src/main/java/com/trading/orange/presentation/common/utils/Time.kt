package com.trading.orange.presentation.common.utils

import android.icu.util.Calendar

fun Long.formatTime(): String {
    val c = Calendar.getInstance()
    c.timeInMillis = this
    val hour = c.get(Calendar.HOUR_OF_DAY)
    val minute = c.get(Calendar.MINUTE)

    val hourStr = (if (hour < 10) "0" else "") + hour
    val minuteStr = (if (minute < 10) "0" else "") + minute
    return "$hourStr:$minuteStr"
}