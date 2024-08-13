package com.trading.orange.data.local_db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.trading.orange.data.local_db.dao.RateDao
import com.trading.orange.data.local_db.entity.RateEntity

@Database(
    entities = [RateEntity::class],
    version = 1
)
abstract class RatesDatabase : RoomDatabase() {
    abstract val rateDao: RateDao
}