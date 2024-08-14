package com.trading.orange.domain.use_case.rates

import com.trading.orange.domain.model.rates.BetResult
import com.trading.orange.domain.repository.RatesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class GetLastNotSeenBetResultFlowUseCase @Inject constructor(
    private val ratesRepository: RatesRepository
) {
    operator fun invoke(): Flow<BetResult?> = ratesRepository.getLastBetResultFlow()
}