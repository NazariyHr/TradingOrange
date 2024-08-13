package com.trading.orange.domain.model.rates

import android.os.Parcelable
import com.trading.orange.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class Instrument(
    val name: String
) : Parcelable {
    fun getIcon(): Int {
        return when (name) {
            "GBP" -> R.drawable.ic_gbp
            "EUR" -> R.drawable.ic_eur
            "JPY" -> R.drawable.ic_jpy
            "CHF" -> R.drawable.ic_chf
            "AUD" -> R.drawable.ic_aud
            "NZD" -> R.drawable.ic_nzd
            "RUB" -> R.drawable.ic_rub
            else -> 0
        }
    }
}

val currencyInstruments = listOf("GBP", "EUR", "JPY", "CHF", "AUD", "NZD", "RUB")

fun String.toInstrument(): Instrument {
    return when (this) {
        in currencyInstruments -> Instrument(this)
        else -> throw UnknownError("Unknown instrument")
    }
}