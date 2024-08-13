package com.trading.orange.domain.use_case.rates

import com.trading.orange.domain.repository.RatesRepository
import javax.inject.Inject

data class IncrementSignalCopiesUseCase @Inject constructor(
    private val ratesRepository: RatesRepository
) {
    operator fun invoke(signalId: Int) = ratesRepository.incrementSignalCopies(signalId)
}