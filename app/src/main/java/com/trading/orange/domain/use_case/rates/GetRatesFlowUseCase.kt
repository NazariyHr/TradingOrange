package com.trading.orange.domain.use_case.rates

import com.trading.orange.domain.model.rates.RateData
import com.trading.orange.domain.repository.RatesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRatesFlowUseCase @Inject constructor(
    private val ratesRepository: RatesRepository
) {
    operator fun invoke(instrumentName: String): Flow<List<RateData>> =
        ratesRepository.getRatesFlow(instrumentName)
}