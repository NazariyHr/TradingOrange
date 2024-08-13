package com.trading.orange.data.rates

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import kotlin.random.Random
import kotlin.random.nextInt

class CoefficientSimulator @Inject constructor() {
    companion object {
        private const val TAG = "CoefficientSimulator"
    }

    private val coefficientFlow = MutableSharedFlow<Float>(replay = 1)
    private var coefficient = 0f
    private val mutexForCoefficient = Mutex()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        startSimulation()
    }

    private fun startSimulation() {
        scope.launch {
            Log.d(TAG, "Coefficient simulation started")
            startCoefficientSimulation()
        }
    }

    private suspend fun startCoefficientSimulation() {
        scope.launch {
            while (true) {
                mutexForCoefficient.withLock {
                    coefficient = Random.nextInt(40..99).toFloat() / 100f
                    coefficientFlow.emit(coefficient)
                    Log.d(TAG, "New coefficient generated: $coefficient")
                }
                delay(1000 * 60)
            }
        }
    }

    fun getCoefficientFlow(): SharedFlow<Float> = coefficientFlow.asSharedFlow()

    fun getCoefficient(): Float = coefficient
}