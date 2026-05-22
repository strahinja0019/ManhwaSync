//ManhwaEntity.kt
package com.strahinja0019.manhwasync.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.strahinja0019.manhwasync.ManhwaUI

@Entity(tableName = "saved_manhwas")
data class ManhwaEntity(
    @PrimaryKey val id: String,
    val title: String,
    val coverUrl: String,           // Keeps the original web hyperlink
    val localCoverPath: String? = null, // ADD THIS: Holds the offline device path (e.g., /data/user/0/.../manhwa_123.jpg)
    val chapterCount: Int? = null
) {
    fun toManhwaUI(): ManhwaUI {
        return ManhwaUI(
            id = id,
            title = title,
            // STRATEGY: If offline, use the local path if it exists; otherwise fall back to the remote web URL
            coverUrl = localCoverPath ?: coverUrl,
            chapterCount = chapterCount
        )
    }
}