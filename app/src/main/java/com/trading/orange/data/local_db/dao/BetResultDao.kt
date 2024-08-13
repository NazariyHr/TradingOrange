package com.trading.orange.data.local_db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.trading.orange.data.local_db.entity.BetResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BetResultDao {
    @Insert
    suspend fun insert(betResult: BetResultEntity)

    @Insert
    suspend fun insertAll(betResults: List<BetResultEntity>)

    @Query("select * from betresultentity ORDER BY time desc")
    fun getAllFlow(): Flow<List<BetResultEntity>>

    @Query("select * from betresultentity where seen = 0 ORDER BY time desc limit 1")
    fun getLastNotSeenFlow(): Flow<BetResultEntity?>

    @Query("update betresultentity set seen = 1")
    suspend fun setSeen()
}