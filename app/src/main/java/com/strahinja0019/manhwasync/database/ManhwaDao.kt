//ManhwaDao.kt
package com.strahinja0019.manhwasync.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ManhwaDao {
    // Real-time Flow triggers UI recomposition instantly upon changes
    @Query("SELECT * FROM saved_manhwas")
    fun getAllSavedManhwas(): Flow<List<ManhwaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManhwa(manhwa: ManhwaEntity)

    @Query("DELETE FROM saved_manhwas WHERE id = :manhwaId")
    suspend fun deleteManhwaById(manhwaId: String)

    @Query("SELECT * FROM saved_manhwas ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomManhwa(): ManhwaEntity?
}