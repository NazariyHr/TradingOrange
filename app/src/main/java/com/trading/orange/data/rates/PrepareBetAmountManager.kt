package com.trading.orange.data.rates

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class PrepareBetAmountManager(context: Context) {
    companion object {
        private const val PREFERENCES_NAME_PREFIX = "prefs_prepare_bet_amount"

        private const val BET_AMOUNT = "bet_amount"
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val prefs by lazy {
        context.getSharedPreferences(
            PREFERENCES_NAME_PREFIX,
            Context.MODE_PRIVATE
        )
    }

    private val preparedBetAmountFlow = MutableSharedFlow<Int>(replay = 1)

    init {
        updatePreparedBetAmountFlow()
    }

    private fun getPreparedBetAmountFromPrefs(): Int {
        return prefs.getInt(BET_AMOUNT, 0)
    }

    private fun updatePreparedBetAmountFlow() {
        scope.launch {
            val newPreparedBetAmount = getPreparedBetAmountFromPrefs()
            preparedBetAmountFlow.emit(newPreparedBetAmount)
        }
    }

    fun getPreparedBetAmountFlow(): Flow<Int> = preparedBetAmountFlow.asSharedFlow()

    fun setNewPreparedBetAmount(preparedBetAmount: Int) {
        prefs.edit { putInt(BET_AMOUNT, preparedBetAmount) }
        updatePreparedBetAmountFlow()
    }
}