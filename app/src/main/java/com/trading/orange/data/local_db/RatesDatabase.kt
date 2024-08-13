package com.trading.orange.data.local_db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.trading.orange.data.local_db.dao.BetResultDao
import com.trading.orange.data.local_db.dao.RateDao
import com.trading.orange.data.local_db.dao.SignalDao
import com.trading.orange.data.local_db.entity.BetResultEntity
import com.trading.orange.data.local_db.entity.RateEntity
import com.trading.orange.data.local_db.entity.SignalEntity

@Database(
    entities = [RateEntity::class, BetResultEntity::class, SignalEntity::class],
    version = 1
)
abstract class RatesDatabase : RoomDatabase() {
    abstract val rateDao: RateDao
    abstract val betResultDao: BetResultDao
    abstract val signalDao: SignalDao
}