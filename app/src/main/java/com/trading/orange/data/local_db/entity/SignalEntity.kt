package com.trading.orange.data.local_db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.trading.orange.domain.model.rates.Signal
import com.trading.orange.domain.model.rates.SignalType
import com.trading.orange.domain.model.rates.toInstrument

@Entity
data class SignalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val instrumentName: String,
    val copies: Int,
    val type: String,
    val startTime: Long,
    val timeSeconds: Int
)

fun SignalEntity.toSignal(
    currInstrumentRate: Float,
    amountForBet: Int,
    availableToBet: Boolean
): Signal {
    return Signal(
        id = id,
        instrument = instrumentName.toInstrument(),
        currInstrumentRate = currInstrumentRate,
        amountForBet = amountForBet,
        copies = copies,
        type = SignalType.valueOf(type),
        startTime = startTime,
        timeSeconds = timeSeconds,
        availableToBet = availableToBet
    )
}