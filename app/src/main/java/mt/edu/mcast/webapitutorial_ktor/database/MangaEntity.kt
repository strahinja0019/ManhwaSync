//MangaEntity.kt
package mt.edu.mcast.webapitutorial_ktor.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import mt.edu.mcast.webapitutorial_ktor.MangaUI

@Entity(tableName = "saved_mangas")
data class MangaEntity(
    @PrimaryKey val id: String,
    val title: String,
    val coverUrl: String,           // Keeps the original web hyperlink
    val localCoverPath: String? = null, // ADD THIS: Holds the offline device path (e.g., /data/user/0/.../manga_123.jpg)
    val chapterCount: Int? = null
) {
    fun toMangaUI(): MangaUI {
        return MangaUI(
            id = id,
            title = title,
            // STRATEGY: If offline, use the local path if it exists; otherwise fall back to the remote web URL
            coverUrl = localCoverPath ?: coverUrl,
            chapterCount = chapterCount
        )
    }
}