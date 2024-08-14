package com.trading.orange.data.local_db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.trading.orange.data.local_db.entity.RateEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

@Dao
interface RateDao {
    @Insert
    suspend fun insert(rate: RateEntity)

    @Insert
    suspend fun insertAll(rates: List<RateEntity>)

    @Query("select * from rateentity where name like :name ORDER BY time ASC")
    suspend fun getAllByName(name: String): List<RateEntity>

    @Query("select * from rateentity where name like :name ORDER BY time ASC")
    fun getAllByNameFlow(name: String): Flow<List<RateEntity>>

    @Query("select * from rateentity where name like :name ORDER BY time DESC limit 1")
    fun getLastByName(name: String): RateEntity?

    @Query("select * from rateentity where name like :name ORDER BY time DESC limit :lastCount")
    fun getLastsByNameFlow(name: String, lastCount: Int): Flow<List<RateEntity>>

    fun getLastByNameFlow(name: String): Flow<RateEntity> =
        getLastsByNameFlow(name, 1)
            .map { it.lastOrNull() }
            .filterNotNull()

    fun getPreviousByNameFlow(name: String): Flow<RateEntity> =
        getLastsByNameFlow(name, 2)
            .map { it.lastOrNull() }
            .filterNotNull()
}