package com.trading.orange.data.rates

import com.trading.orange.data.local_db.RatesDatabase
import com.trading.orange.data.local_db.entity.toRateData
import com.trading.orange.data.local_db.entity.toRateEntity
import com.trading.orange.domain.model.rates.Instrument
import com.trading.orange.domain.model.rates.RateData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class ExchangeRatesDataHolder(
    val instrument: Instrument,
    private val ratesDatabase: RatesDatabase
) {
    private val exchangeRatesFlow: Flow<List<RateData>> =
        ratesDatabase.rateDao
            .getAllByNameFlow(instrument.name)
            .map { rateEntities ->
                rateEntities.map { rateEntity -> rateEntity.toRateData() }
            }
    private val lastRateFlow =
        ratesDatabase.rateDao.getLastByNameFlow(instrument.name).distinctUntilChanged()
    private val previousRateFlow =
        ratesDatabase.rateDao.getPreviousByNameFlow(instrument.name).distinctUntilChanged()

    suspend fun saveNewRateData(rateData: List<RateData>) {
        ratesDatabase.rateDao.insertAll(rateData.map { it.toRateEntity(instrument.name) })
    }

    fun getExchangeRatesFlow(): Flow<List<RateData>> = exchangeRatesFlow.map { it }

    suspend fun getExchangeRates(): List<RateData> =
        ratesDatabase.rateDao.getAllByName(instrument.name).map { it.toRateData() }

    fun getLastRateFlow(): Flow<Float> =
        lastRateFlow.map { it.rateValue }

    fun getLastRate(): Float = ratesDatabase.rateDao.getLastByName(instrument.name)?.rateValue ?: 0f

    fun getPreviousRateFlow(): Flow<Float> = previousRateFlow.map { it.rateValue }
}