package com.trading.orange.data.local_db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.trading.orange.domain.model.rates.RateData

@Entity
data class RateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val time: Long,
    val rateValue: Float
)

fun RateEntity.toRateData(): RateData {
    return RateData(
        time,
        rateValue
    )
}

fun RateData.toRateEntity(name: String): RateEntity {
    return RateEntity(
        id = 0,
        name = name,
        time = time,
        rateValue = rateValue
    )
}