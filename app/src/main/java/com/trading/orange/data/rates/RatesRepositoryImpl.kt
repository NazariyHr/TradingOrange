package com.trading.orange.data.rates

import android.content.Context
import android.util.Log
import com.trading.orange.data.local_db.RatesDatabase
import com.trading.orange.data.local_db.entity.RateEntity
import com.trading.orange.data.local_db.entity.toRateData
import com.trading.orange.data.local_db.entity.toRateEntity
import com.trading.orange.data.server.ServerDataManager
import com.trading.orange.domain.model.rates.Bet
import com.trading.orange.domain.model.rates.BetResult
import com.trading.orange.domain.model.rates.Instrument
import com.trading.orange.domain.model.rates.InstrumentWithCurrentRate
import com.trading.orange.domain.model.rates.RateData
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
    private val betResultsManager: BetResultsManager
) : RatesRepository {

    companion object {
        private const val TAG = "RatesRepositoryImpl"

        private const val SIGNALS_PREFERENCES_NAME = "prefs_signals"
//        private const val SIGNALS = "signals"
//        private const val SIGNALS_GENERATE_TIME = "signals_generate_time"
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

//    private val prefs by lazy {
//        context.getSharedPreferences(SIGNALS_PREFERENCES_NAME, Context.MODE_PRIVATE)
//    }
    // private val signalsFlow = MutableSharedFlow<List<Signal>>(replay = 1)

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
        ratesUpdateInterval: Long = 1000L * 1/*,
        signalsUpdateInterval: Long = 1000L * 60 * 60*/
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

//            val signalsInPrefs = getSignalsFromPrefs()
//            signalsFlow.emit(signalsInPrefs)
//            if (signalsInPrefs.isNotEmpty()) {
//                Log.d(TAG, "Signals restored from prefs. Signals count: ${signalsInPrefs.count()}")
//            }
//
//            val lastTime = getLastSignalsGenerateTimeFromPrefs()
//            val currTime = Calendar.getInstance().timeInMillis
//            val passedTime = currTime - lastTime
//
//            if (lastTime == 0L || passedTime >= signalsUpdateInterval) {
//                Log.d(
//                    TAG,
//                    "Last signals update time was long ago, start to update signals without delay"
//                )
//                simulateSignalsChange(0L, signalsUpdateInterval)
//            } else {
//                val delay = signalsUpdateInterval - passedTime
//                Log.d(
//                    TAG,
//                    "Last signals update was recently, initial delay is ${delay / 1000} seconds"
//                )
//                simulateSignalsChange(delay, signalsUpdateInterval)
//            }

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


//    private fun getSignalsFromPrefs(): List<Signal> {
//        val signalsStr = prefs.getString(SIGNALS, "")
//        if (signalsStr.isNullOrEmpty()) return emptyList()
//        val type = object : TypeToken<List<Signal>>() {}.type
//        return Gson().fromJson(signalsStr, type)
//    }
//
//    private fun saveNewSignals(signals: List<Signal>) {
//        val newSignalsStr = Gson().toJson(signals)
//        prefs.edit { putString(SIGNALS, newSignalsStr) }
//    }
//
//    private fun generateNewSignals(): List<Signal> {
//        return ratesDataHolders.values.map {
//            it.getLastRate().let { rateValue ->
//                val buyLowPercent = Random.nextInt(300, 800).toFloat() / 10000f
//                val buyHighPercent = Random.nextInt(300, 800).toFloat() / 10000f
//
//                val target1Percent = Random.nextInt(100, 300).toFloat() / 10000f
//                val target2Percent = Random.nextInt(200, 400).toFloat() / 10000f
//                val target3Percent = Random.nextInt(400, 800).toFloat() / 10000f
//                var stopLossPercent = Random.nextInt(400, 1100).toFloat() / 10000f
//
//                if (buyLowPercent > stopLossPercent) {
//                    stopLossPercent = buyLowPercent
//                }
//                Signal(
//                    instrument = it.instrument,
//                    currentAsk = rateValue,
//                    buyStart = rateValue - (rateValue * buyLowPercent),
//                    buyEnd = rateValue + (rateValue * buyHighPercent),
//                    targets = listOf(
//                        ValueWithPercent(
//                            rateValue + (rateValue * target1Percent),
//                            target1Percent
//                        ),
//                        ValueWithPercent(
//                            rateValue + (rateValue * target2Percent),
//                            target2Percent
//                        ),
//                        ValueWithPercent(
//                            rateValue + (rateValue * target3Percent),
//                            target3Percent
//                        )
//                    ),
//                    stopLoss = ValueWithPercent(
//                        rateValue - (rateValue * stopLossPercent),
//                        stopLossPercent
//                    )
//                )
//            }
//        }
//    }
//
//    private fun getLastSignalsGenerateTimeFromPrefs(): Long {
//        return prefs.getLong(SIGNALS_GENERATE_TIME, 0L)
//    }
//
//    private fun saveLastSignalsGenerateTime(time: Long) {
//        prefs.edit { putLong(SIGNALS_GENERATE_TIME, time) }
//    }
//
//    private fun simulateSignalsChange(startDelay: Long, intervalInMillis: Long) {
//        scope.launch {
//            delay(startDelay)
//            while (true) {
//                val signals = generateNewSignals()
//                saveNewSignals(signals = signals)
//                signalsFlow.emit(signals)
//
//                val newSignalsTime = Calendar.getInstance().timeInMillis
//                saveLastSignalsGenerateTime(newSignalsTime)
//
//                Log.d(TAG, "New signals generated, and saved. Signals count: ${signals.count()}")
//
//                delay(intervalInMillis)
//            }
//        }
//    }


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

    //    override fun getInstrumentsSignalsFlow(): Flow<List<Signal>> = signalsFlow.asSharedFlow()
}