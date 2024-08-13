package com.trading.orange.data.rates

import android.content.Context
import android.util.Log
import com.trading.orange.data.local_db.RatesDatabase
import com.trading.orange.data.local_db.entity.RateEntity
import com.trading.orange.data.local_db.entity.SignalEntity
import com.trading.orange.data.local_db.entity.toRateData
import com.trading.orange.data.local_db.entity.toRateEntity
import com.trading.orange.data.local_db.entity.toSignal
import com.trading.orange.data.server.ServerDataManager
import com.trading.orange.domain.model.rates.Bet
import com.trading.orange.domain.model.rates.BetResult
import com.trading.orange.domain.model.rates.Instrument
import com.trading.orange.domain.model.rates.InstrumentWithCurrentRate
import com.trading.orange.domain.model.rates.RateData
import com.trading.orange.domain.model.rates.Signal
import com.trading.orange.domain.model.rates.SignalType
import com.trading.orange.domain.model.rates.currencyInstruments
import com.trading.orange.domain.model.rates.toInstrument
import com.trading.orange.domain.repository.RatesRepository
import com.trading.orange.domain.repository.UserBalanceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.Calendar
import kotlin.random.Random
import kotlin.random.nextInt

@OptIn(ExperimentalCoroutinesApi::class)
class RatesRepositoryImpl(
    private val context: Context,
    private val userRepository: UserBalanceRepository,
    private val coefficientSimulator: CoefficientSimulator,
    private val serverDataManager: ServerDataManager,
    private val ratesDatabase: RatesDatabase,
    private val betResultsManager: BetResultsManager,
    private val prepareBetAmountManager: PrepareBetAmountManager,
    private val signalsManager: SignalsManager
) : RatesRepository {

    companion object {
        private const val TAG = "RatesRepositoryImpl"
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val ratesDataHoldersFlow =
        MutableSharedFlow<Map<String, ExchangeRatesDataHolder>>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    private val betPreferences: MutableMap<String, BetsManager> = mutableMapOf()
    private val instruments = (currencyInstruments).map { it.toInstrument() }
    private val ratesDataHolders: MutableMap<String, ExchangeRatesDataHolder> = mutableMapOf()

    init {
        initSimulationsAndBets()
    }

    private fun initSimulationsAndBets() {
        scope.launch {
            instruments.forEach { instrument ->
                val ratesDataHolder = ExchangeRatesDataHolder(
                    instrument = instrument,
                    ratesDatabase = ratesDatabase
                )
                ratesDataHolders[instrument.name] = ratesDataHolder
                betPreferences[instrument.name] = BetsManager(
                    instrumentName = instrument.name,
                    coefficientSimulator = coefficientSimulator,
                    exchangeRatesDataHolder = ratesDataHolder,
                    userRepository = userRepository,
                    betResultsManager = betResultsManager,
                    context = context
                )
            }
            ratesDataHoldersFlow.emit(ratesDataHolders)
            startSimulation()
        }
    }

    private fun startSimulation(
        ratesUpdateInterval: Long = 1000L * 1
    ) {
        scope.launch {
            Log.d(TAG, "Simulation started")
            val nextRateTime = Calendar.getInstance().timeInMillis
            val ratesEntities = mutableListOf<RateEntity>()

            instruments.forEach { instrument ->
                val ratesFromLocalDataBase =
                    ratesDatabase.rateDao.getAllByName(instrument.name).map { it.toRateData() }
                if (ratesFromLocalDataBase.isEmpty()) {
                    Log.d(TAG, "No any rates saved in local database.")
                    val generatedRatesInStart =
                        generateRates(instrument, nextRateTime, ratesUpdateInterval)
                    generatedRatesInStart.forEach { rate ->
                        ratesEntities.add(rate.toRateEntity(instrument.name))
                    }
                } else {
                    Log.d(
                        TAG,
                        "Rates restored successfully from local database. Rates count: ${ratesFromLocalDataBase.count()}"
                    )
                }
            }
            if (ratesEntities.isNotEmpty()) {
                ratesDatabase.rateDao.insertAll(ratesEntities)
                Log.d(TAG, "New rates generated, and saved. Rates count: ${ratesEntities.count()}")
            }

            signalsSimulationLoop()

            simulateRatesChange(ratesUpdateInterval)
        }
    }

    private suspend fun generateRates(
        instrument: Instrument,
        nextRateT: Long,
        ratesUpdateInterval: Long
    ): List<RateData> {
        var nextRateTime = nextRateT
        var firstExchangeRate = 0f

        val loadRateFromServer = suspend {
            firstExchangeRate = serverDataManager.getExchangeRates()
                .find { it.name == instrument.name }?.price ?: 0f
        }

        var tryAgain = true
        var tryTimes = 1
        while (tryAgain) {
            try {
                loadRateFromServer()
                tryAgain = false
            } catch (e: HttpException) {
                e.printStackTrace()
                tryAgain = tryTimes < 2
            }
            tryTimes++
        }

        val generatedRates = mutableListOf<RateData>()
        generatedRates.add(
            RateData(
                time = nextRateTime,
                rateValue = firstExchangeRate
            )
        )

        var lastGeneratedRate = firstExchangeRate
        repeat(40 * 60) {
            nextRateTime -= ratesUpdateInterval
            lastGeneratedRate = generateNewRate(lastGeneratedRate)

            generatedRates.add(
                RateData(
                    time = nextRateTime,
                    rateValue = lastGeneratedRate
                )
            )
        }

        return generatedRates
    }

    private fun generateNewRate(oldRate: Float): Float {
        val randomValue = Random.nextInt(1..14)
        val randomChangePercent = randomValue.toFloat() / 10000f
        val randomChange = oldRate * randomChangePercent
        return if (Random.nextBoolean()) {
            oldRate - randomChange
        } else {
            oldRate + randomChange
        }
    }

    private fun simulateRatesChange(intervalInMillis: Long) {
        scope.launch {
            while (true) {
                delay(intervalInMillis)
                val newRateTime = Calendar.getInstance().timeInMillis

                val newRates = mutableMapOf<String, RateData>()

                instruments.forEach { instrument ->
                    newRates[instrument.name] = RateData(
                        time = newRateTime,
                        rateValue = generateNewRate(
                            ratesDatabase.rateDao.getLastByName(instrument.name)?.rateValue ?: 0f
                        )
                    )
                }

                val ratesEntities = mutableListOf<RateEntity>()
                instruments.forEach { instrument ->
                    newRates[instrument.name]?.let {
                        ratesEntities.add(it.toRateEntity(instrument.name))
                    }
                }
                ratesDatabase.rateDao.insertAll(ratesEntities)
                Log.d(TAG, "New rates generated, and saved. Rates count: ${newRates.count()}")
            }
        }
    }

    private fun signalsSimulationLoop() {
        scope.launch {
            while (true) {
                signalsManager.removeOutdatedSignals()
                val activeSignals = signalsManager.getAllSignals()
                if (activeSignals.count() < 10) {
                    val newSignals =
                        generateNewSignals(newSignalsAmount = 10 - activeSignals.count())
                    signalsManager.addAllSignals(newSignals)
                }
                delay(1000L)
            }
        }
    }

    private fun generateNewSignals(newSignalsAmount: Int): List<SignalEntity> {
        val newSignals = mutableListOf<SignalEntity>()
        repeat(newSignalsAmount) {
            val exchangeRateHolder = ratesDataHolders.values.random()
            val instrument = exchangeRateHolder.instrument
            val startTime = Calendar.getInstance().timeInMillis
            val timeSeconds = Random.nextInt(30, 60 * 5)

            newSignals.add(
                SignalEntity(
                    id = 0,
                    instrumentName = instrument.name,
                    copies = Random.nextInt(10, 70),
                    type = SignalType.entries[Random.nextInt(0, 4)].name,
                    startTime = startTime,
                    timeSeconds = timeSeconds
                )
            )
        }
        return newSignals
    }


    override fun getRatesFlow(instrumentName: String): Flow<List<RateData>> =
        ratesDataHoldersFlow
            .asSharedFlow()
            .map { it[instrumentName] }
            .filterNotNull()
            .flatMapLatest { it.getExchangeRatesFlow() }

    override fun getBetFlow(instrumentName: String): Flow<Bet?> =
        betPreferences[instrumentName]?.getBetFlow() ?: flowOf(null)

    override fun addBet(instrumentName: String, bet: Bet) {
        scope.launch {
            betPreferences[instrumentName]?.addBet(bet)
        }
    }

    override fun getCoefficientFlow(): SharedFlow<Float> = coefficientSimulator.getCoefficientFlow()

    override fun getInstrumentsFlow(): Flow<List<Instrument>> =
        ratesDataHoldersFlow
            .asSharedFlow()
            .flatMapLatest {
                flowOf(it.values.toList())
            }
            .flatMapLatest { simulators ->
                combine(simulators.map { simulator -> flowOf(simulator.instrument) }) {
                    it.toList()
                }
            }

    override fun getInstrumentsWithCurrentRateFlow(): Flow<List<InstrumentWithCurrentRate>> =
        ratesDataHoldersFlow
            .asSharedFlow()
            .flatMapLatest {
                flowOf(it.values.toList())
            }
            .flatMapLatest { simulators ->
                combine(simulators
                    .map { simulator ->
                        combine(
                            flowOf(simulator.instrument),
                            simulator.getLastRateFlow(),
                            simulator.getPreviousRateFlow()
                        ) { instrument, lastRate, previousRate ->
                            val changePercent = 100 - (100f * lastRate / previousRate)
                            InstrumentWithCurrentRate(
                                instrument = instrument,
                                value = lastRate,
                                lastChangePercent = changePercent
                            )
                        }
                    }
                ) {
                    it.toList()
                }
            }

    override fun getAllBetResultsFlow(): Flow<List<BetResult>> =
        betResultsManager.getAllBetResultsFlow()

    override fun getLastBetResultFlow(): Flow<BetResult?> =
        betResultsManager.getLastNotSeenBetResultFlow()

    override fun setBetResultsAsSeen() {
        betResultsManager.setBetResultsAsSeen()
    }

    override fun getPreparedBetAmountFlow(): Flow<Int> =
        prepareBetAmountManager.getPreparedBetAmountFlow()

    override fun setNewPreparedBetAmount(preparedBetAmount: Int) {
        prepareBetAmountManager.setNewPreparedBetAmount(preparedBetAmount)
    }

    override fun getSignalsListFlow(): Flow<List<Signal>> =
        signalsManager.getAllSignalsFlow().flatMapLatest { signalsEntities ->
            combine(
                signalsEntities.map { signalsEntity ->
                    combine(
                        ratesDataHoldersFlow
                            .asSharedFlow()
                            .map { it[signalsEntity.instrumentName] }
                            .filterNotNull()
                            .flatMapLatest { it.getLastRateFlow() },
                        prepareBetAmountManager.getPreparedBetAmountFlow(),
                        getBetFlow(signalsEntity.instrumentName).map { bet -> bet == null }
                    ) { currInstrumentRate, amountForBet, availableToBet ->
                        signalsEntity.toSignal(
                            currInstrumentRate,
                            amountForBet,
                            availableToBet
                        )
                    }
                }
            ) {
                it.toList()
            }
        }

    override fun incrementSignalCopies(signalId: Int) {
        scope.launch {
            signalsManager.incrementSignalCopies(signalId)
        }
    }
}