package com.trading.orange.domain.model.rates

import android.os.Parcelable
import com.trading.orange.R
import kotlinx.parcelize.Parcelize
import java.util.Calendar

@Parcelize
data class Signal(
    val id: Int,
    val instrument: Instrument,
    val currInstrumentRate: Float,
    val amountForBet: Int,
    val copies: Int,
    val type: SignalType,
    val startTime: Long,
    val timeSeconds: Int,
    val availableToBet: Boolean
) : Parcelable {
    fun getSignalTypeIcon(): Int {
        return when (type) {
            SignalType.UP -> R.drawable.ic_single_up
            SignalType.DOUBLE_UP -> R.drawable.ic_doubled_up
            SignalType.DOWN -> R.drawable.ic_single_down
            SignalType.DOUBLE_DOWN -> R.drawable.ic_doubled_down
        }
    }

    fun isUp() = type == SignalType.UP || type == SignalType.DOUBLE_UP
}

enum class SignalType {
    UP, DOUBLE_UP, DOWN, DOUBLE_DOWN
}

fun Signal.toBet(): Bet {
    val currTime = Calendar.getInstance().timeInMillis
    val endTime =
        Calendar.getInstance().apply { timeInMillis = startTime + 1000 * timeSeconds }.timeInMillis

    return Bet(
        startTime = currTime,
        rateOnStart = currInstrumentRate,
        timeSeconds = (endTime - currTime).toInt() / 1000,
        amount = amountForBet,
        type = if (type == SignalType.UP || type == SignalType.DOUBLE_UP) BetType.UP else BetType.DOWN
    )
}