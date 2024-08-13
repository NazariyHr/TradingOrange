package com.trading.orange.domain.use_case.rates

import com.trading.orange.domain.repository.RatesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPreparedBetAmountFlowUseCase @Inject constructor(
    private val ratesRepository: RatesRepository
) {
    operator fun invoke(): Flow<Int> =
        ratesRepository.getPreparedBetAmountFlow()
}