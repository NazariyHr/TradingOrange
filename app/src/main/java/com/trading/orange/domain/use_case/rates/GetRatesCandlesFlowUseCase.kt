package com.trading.orange.domain.use_case.rates

import com.trading.orange.domain.model.rates.CandleStick
import com.trading.orange.domain.model.rates.toCandleSticks
import com.trading.orange.domain.repository.RatesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetRatesCandlesFlowUseCase @Inject constructor(
    private val ratesRepository: RatesRepository
) {
    operator fun invoke(instrumentName: String): Flow<List<CandleStick>> =
        ratesRepository.getRatesFlow(instrumentName).map { it.toCandleSticks() }
}