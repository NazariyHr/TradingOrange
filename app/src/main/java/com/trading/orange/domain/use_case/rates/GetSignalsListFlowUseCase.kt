package com.trading.orange.domain.use_case.rates

import com.trading.orange.domain.model.rates.Signal
import com.trading.orange.domain.repository.RatesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class GetSignalsListFlowUseCase @Inject constructor(
    private val ratesRepository: RatesRepository
) {
    operator fun invoke(): Flow<List<Signal>> = ratesRepository.getSignalsListFlow()
}