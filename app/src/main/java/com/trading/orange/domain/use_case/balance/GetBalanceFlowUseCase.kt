package com.trading.orange.domain.use_case.balance

import com.trading.orange.domain.repository.UserBalanceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBalanceFlowUseCase @Inject constructor(
    private val userRepository: UserBalanceRepository
) {
    operator fun invoke(): Flow<Float> {
        return userRepository.getUserBalanceFlow()
    }
}