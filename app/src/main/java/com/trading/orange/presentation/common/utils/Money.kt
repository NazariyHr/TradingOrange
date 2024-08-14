package com.trading.orange.presentation.common.utils

import java.util.Locale
import kotlin.math.abs

fun Float.formatBalance(): String {
    return "$" + String.format(Locale.US, "%,.2f", this)
}

fun Float.formatResult(withSign: Boolean = false): String {
    return (if (withSign) (if (this < 0f) "-" else "+") else "") +
            "$" +
            String.format(
                Locale.US,
                "%.2f",
                abs(this)
            )
}
