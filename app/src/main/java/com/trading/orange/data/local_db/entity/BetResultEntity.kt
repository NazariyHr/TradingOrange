package com.trading.orange.data.local_db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.trading.orange.domain.model.rates.BetResult
import com.trading.orange.domain.model.rates.BetType
import com.trading.orange.domain.model.rates.toInstrument

@Entity
data class BetResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val instrumentName: String,
    val betType: String,
    val betAmount: Float,
    val result: Float,
    val time: Long,
    val seen: Boolean
)

fun BetResultEntity.toBetResult(): BetResult {
    return BetResult(
        instrument = instrumentName.toInstrument(),
        betType = BetType.valueOf(betType),
        betAmount = betAmount,
        result = result,
        time = time
    )
}