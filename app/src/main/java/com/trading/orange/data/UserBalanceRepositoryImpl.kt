package com.trading.orange.data

import android.content.Context
import androidx.core.content.edit
import com.trading.orange.domain.repository.UserBalanceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class UserBalanceRepositoryImpl(
    context: Context
) : UserBalanceRepository {
    companion object {
        private const val PREFERENCES_NAME = "prefs_user_balance"

        private const val BALANCE_INITIATED = "balance_initiated"
        private const val BALANCE = "balance"
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val prefs by lazy {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).apply {
            if (!getBoolean(BALANCE_INITIATED, false)) {
                edit()
                    .apply {
                        putFloat(BALANCE, 1000f)
                        putBoolean(BALANCE_INITIATED, true)
                    }
                    .apply()
            }
        }
    }

    private val balanceFlow = MutableSharedFlow<Float>(replay = 1)

    init {
        updateBalanceFlow()
    }

    private fun getBalanceFromPrefs() = prefs.getFloat(BALANCE, 0f)

    private fun updateBalanceFlow() {
        scope.launch {
            val newBalance = getBalanceFromPrefs()
            balanceFlow.emit(newBalance)
        }
    }

    override fun getUserBalanceFlow(): SharedFlow<Float> = balanceFlow.asSharedFlow()

    override suspend fun addToBalance(sum: Float): Float {
        return scope.async {
            var newBalance = getBalanceFromPrefs() + sum
            if (newBalance < 0) {
                newBalance = 0f
            }
            updateBalance(newBalance)
            newBalance
        }.await()
    }

    override suspend fun resetBalance() {
        return scope.async {
            updateBalance(1000f)
        }.await()
    }

    private fun updateBalance(newBalance: Float) {
        prefs.edit { putFloat(BALANCE, newBalance) }
        updateBalanceFlow()
    }
}