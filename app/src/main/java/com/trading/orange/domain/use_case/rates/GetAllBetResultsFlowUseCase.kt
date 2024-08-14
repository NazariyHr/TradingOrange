package com.trading.orange.domain.use_case.rates

import com.trading.orange.domain.model.rates.BetResult
import com.trading.orange.domain.repository.RatesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class GetAllBetResultsFlowUseCase @Inject constructor(
    private val ratesRepository: RatesRepository
) {
    operator fun invoke(): Flow<List<BetResult>> = ratesRepository.getAllBetResultsFlow()
}