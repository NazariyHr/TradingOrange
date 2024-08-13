package com.trading.orange.domain.use_case.rates

import com.trading.orange.domain.model.rates.Bet
import com.trading.orange.domain.repository.RatesRepository
import javax.inject.Inject

class AddBetUseCase @Inject constructor(
    private val ratesRepository: RatesRepository
) {
    operator fun invoke(instrumentName: String, bet: Bet) =
        ratesRepository.addBet(instrumentName, bet)
}