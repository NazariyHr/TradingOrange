package com.trading.orange.domain.repository

import com.trading.orange.domain.model.rates.Bet
import com.trading.orange.domain.model.rates.BetResult
import com.trading.orange.domain.model.rates.Instrument
import com.trading.orange.domain.model.rates.InstrumentWithCurrentRate
import com.trading.orange.domain.model.rates.RateData
import kotlinx.coroutines.flow.Flow

interface RatesRepository {
    fun getRatesFlow(instrumentName: String): Flow<List<RateData>>
    fun getCoefficientFlow(): Flow<Float>
    fun getBetFlow(instrumentName: String): Flow<Bet?>
    fun addBet(instrumentName: String, bet: Bet)
    fun getInstrumentsFlow(): Flow<List<Instrument>>
    fun getInstrumentsWithCurrentRateFlow(): Flow<List<InstrumentWithCurrentRate>>
    fun getAllBetResultsFlow(): Flow<List<BetResult>>
    fun getLastBetResultFlow(): Flow<BetResult?>
    fun setBetResultsAsSeen()
}