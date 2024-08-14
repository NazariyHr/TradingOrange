package com.trading.orange.domain.use_case.balance

import com.trading.orange.domain.repository.UserBalanceRepository
import javax.inject.Inject

class ResetBalanceUseCase @Inject constructor(
    private val userBalanceRepository: UserBalanceRepository
) {
    suspend operator fun invoke() =
        userBalanceRepository.resetBalance()
}