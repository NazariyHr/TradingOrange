package com.trading.orange.data.rates

import com.trading.orange.data.local_db.RatesDatabase
import com.trading.orange.data.local_db.entity.BetResultEntity
import com.trading.orange.data.local_db.entity.toBetResult
import com.trading.orange.domain.model.rates.BetResult
import com.trading.orange.domain.model.rates.Instrument
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class BetResultsManager @Inject constructor(
    private val ratesDatabase: RatesDatabase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun addNewBetResult(
        instrument: Instrument,
        betAmount: Float,
        result: Float,
        time: Long
    ) {
        scope.launch {
            ratesDatabase.betResultDao.insert(
                BetResultEntity(
                    id = 0,
                    instrumentName = instrument.name,
                    betAmount = betAmount,
                    result = result,
                    time = time,
                    seen = false
                )
            )
        }
    }

    fun getAllBetResultsFlow(): Flow<List<BetResult>> =
        ratesDatabase.betResultDao
            .getAllFlow()
            .distinctUntilChanged()
            .map { results -> results.map { result -> result.toBetResult() } }

    fun getLastNotSeenBetResultFlow(): Flow<BetResult?> =
        ratesDatabase.betResultDao
            .getLastNotSeenFlow()
            .distinctUntilChanged()
            .map { it?.toBetResult() }

    fun setBetResultsAsSeen() {
        scope.launch {
            ratesDatabase.betResultDao.setSeen()
        }
    }
}