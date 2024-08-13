package com.trading.orange.domain.use_case.rates

import com.trading.orange.domain.model.rates.Instrument
import com.trading.orange.domain.repository.RatesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetInstrumentsFlowUseCase @Inject constructor(
    private val ratesRepository: RatesRepository
) {
    operator fun invoke(): Flow<List<Instrument>> =
        ratesRepository.getInstrumentsFlow()
}