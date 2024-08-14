package com.trading.orange.data.rates

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.trading.orange.domain.model.rates.Bet
import com.trading.orange.domain.model.rates.BetType
import com.trading.orange.domain.model.rates.toInstrument
import com.trading.orange.domain.repository.UserBalanceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.abs

class BetsManager(
    private val instrumentName: String,
    private val exchangeRatesDataHolder: ExchangeRatesDataHolder,
    private val coefficientSimulator: CoefficientSimulator,
    private val userRepository: UserBalanceRepository,
    private val betResultsManager: BetResultsManager,
    context: Context
) {
    companion object {
        private const val PREFERENCES_NAME_PREFIX = "prefs_bets"

        private const val BET_INFO = "bet_info"
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val prefs by lazy {
        context.getSharedPreferences(
            PREFERENCES_NAME_PREFIX + "_" + instrumentName,
            Context.MODE_PRIVATE
        )
    }

    private val betFlow = MutableSharedFlow<Bet?>(replay = 1)

    init {
        updateBetFlow()
        val bet = getBetFromPrefs()
        bet?.let {
            startBetTimer(it)
        }
    }


    private fun getBetFromPrefs(): Bet? {
        val betStr = prefs.getString(BET_INFO, "")
        if (betStr.isNullOrEmpty()) return null
        return Gson().fromJson(betStr, Bet::class.java)
    }

    private fun updateBetFlow() {
        scope.launch {
            val newBet = getBetFromPrefs()
            betFlow.emit(newBet)
        }
    }

    fun getBetFlow(): SharedFlow<Bet?> = betFlow.asSharedFlow()

    fun addBet(bet: Bet) {
        scope.launch {
            val betStr = Gson().toJson(bet)
            prefs.edit { putString(BET_INFO, betStr) }
            updateBetFlow()
            userRepository.addToBalance(-bet.amount.toFloat())
        }
        startBetTimer(bet)
    }

    private fun clearBet() {
        prefs.edit { remove(BET_INFO) }
        updateBetFlow()
    }

    private fun startBetTimer(bet: Bet) {
        scope.launch {
            val betDuration = bet.timeSeconds * 1000L
            val currTime = Calendar.getInstance().timeInMillis
            val endTime = bet.startTime + betDuration

            if (currTime < endTime) {
                delay(endTime - currTime)
            }

            val coefficient = coefficientSimulator.getCoefficient()
            val startRate = bet.rateOnStart
            val endRate = exchangeRatesDataHolder.getExchangeRates().minBy {
                abs(it.time - (bet.startTime + betDuration))
            }.rateValue
            val betAmount = bet.amount.toFloat()

            val wins = when (bet.type) {
                BetType.UP -> if (endRate >= startRate) {
                    betAmount + betAmount * coefficient
                } else {
                    0f
                }

                BetType.DOWN -> if (endRate <= startRate) {
                    betAmount + betAmount * coefficient
                } else {
                    0f
                }
            }

            val betResult = if (wins == 0f) -bet.amount.toFloat() else wins

            betResultsManager.addNewBetResult(
                instrument = instrumentName.toInstrument(),
                betAmount = bet.amount.toFloat(),
                result = betResult,
                time = endTime
            )
            clearBet()
            userRepository.addToBalance(wins)
        }
    }
}