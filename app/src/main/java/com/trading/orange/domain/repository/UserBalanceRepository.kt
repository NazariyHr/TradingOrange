package com.trading.orange.domain.repository

import kotlinx.coroutines.flow.SharedFlow

interface UserBalanceRepository {
    fun getUserBalanceFlow(): SharedFlow<Float>
    suspend fun addToBalance(sum: Float): Float
    suspend fun resetBalance()
}