//MangaDao.kt
package mt.edu.mcast.webapitutorial_ktor.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MangaDao {
    // Real-time Flow triggers UI recomposition instantly upon changes
    @Query("SELECT * FROM saved_mangas")
    fun getAllSavedMangas(): Flow<List<MangaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManga(manga: MangaEntity)

    @Query("DELETE FROM saved_mangas WHERE id = :mangaId")
    suspend fun deleteMangaById(mangaId: String)
}