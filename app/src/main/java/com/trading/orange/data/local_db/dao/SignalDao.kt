package com.trading.orange.data.local_db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.trading.orange.data.local_db.entity.SignalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SignalDao {
    @Insert
    suspend fun insert(signal: SignalEntity)

    @Insert
    suspend fun insertAll(signals: List<SignalEntity>)

    @Query("select * from signalentity")
    suspend fun getAll(): List<SignalEntity>

    @Query("select * from signalentity")
    fun getAllFlow(): Flow<List<SignalEntity>>

    @Query("update signalentity set copies = copies + 1 where id = :signalId")
    suspend fun incrementCopies(signalId: Int)

    @Delete
    suspend fun deleteAll(signals: List<SignalEntity>)
}