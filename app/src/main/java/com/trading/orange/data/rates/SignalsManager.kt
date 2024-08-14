package com.trading.orange.data.rates

import com.trading.orange.data.local_db.RatesDatabase
import com.trading.orange.data.local_db.entity.SignalEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.Calendar
import javax.inject.Inject

class SignalsManager @Inject constructor(
    private val ratesDatabase: RatesDatabase
) {
    fun getAllSignalsFlow(): Flow<List<SignalEntity>> =
        ratesDatabase.signalDao
            .getAllFlow()
            .distinctUntilChanged()

    suspend fun getAllSignals(): List<SignalEntity> =
        ratesDatabase.signalDao
            .getAll()

    suspend fun removeOutdatedSignals() {
        val outdatedSignals = getAllSignals()
            .filter { signal ->
                val currTime = Calendar.getInstance().timeInMillis
                val endTime = signal.startTime + 1000 * signal.timeSeconds
                currTime >= endTime
            }
        if (outdatedSignals.isNotEmpty()) {
            ratesDatabase.signalDao.deleteAll(outdatedSignals)
        }
    }

    suspend fun addAllSignals(signals: List<SignalEntity>) {
        ratesDatabase.signalDao.insertAll(signals)
    }

    suspend fun incrementSignalCopies(signalId: Int) {
        ratesDatabase.signalDao.incrementCopies(signalId)
    }
}