//MangaDexRepository.kt
package com.strahinja0019.manhwasync.mangadex

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import com.strahinja0019.manhwasync.ManhwaUI
import com.strahinja0019.manhwasync.database.ManhwaDao
import com.strahinja0019.manhwasync.database.ManhwaEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.scale
import io.ktor.client.statement.readRawBytes

class MangaDexRepository(private val manhwaDao: ManhwaDao) {
    private val cache = mutableMapOf<String, List<ManhwaUI>>()
    private val BASE = "https://api.mangadex.dev/manga"

    // Stream saved data directly from the room database as ManhwaUI instances
    val savedManhwasStream: Flow<List<ManhwaUI>> = manhwaDao.getAllSavedManhwas().map { entities ->
        entities.map { it.toManhwaUI() }
    }

    // Pass context here to unlock local system directory lookups
    suspend fun saveToFavorites(context: Context, manhwa: ManhwaUI) {
        if (manhwa.id == null) return
        // 1. Fetch total chapter counts asynchronously over network stream
        val localPath = downloadAndCompressCover(context, manhwa.id, manhwa.coverUrl)

        // 2. Stream the cover image file and download/compress it into local cache files
        val chapterCount = getMaxChapters(manhwa.id).getOrNull() ?: 0

        // 3. Initialize mapping logic including our new internal device link
        val entity = ManhwaEntity(
            id = manhwa.id,
            title = manhwa.title ?: "Unknown Title",
            coverUrl = manhwa.coverUrl ?: "",
            localCoverPath = localPath, // Saved locally to disk!
            chapterCount = chapterCount
        )

        manhwaDao.insertManhwa(entity)
    }

    suspend fun removeFromFavorites(context: Context, manhwaId: String) {
        // 1. Find the file reference location inside your internal system structure
        val directory = File(context.filesDir, "manhwa_covers")
        val fileTarget = File(directory, "cover_${manhwaId}.jpg")

        // 2. Delete the compressed file asset off the device storage allocation space safely
        if (fileTarget.exists()) {
            fileTarget.delete()
        }

        // 3. Clear the entity row data completely out of Room table memory rows
        manhwaDao.deleteManhwaById(manhwaId)
    }

    suspend fun getManhwasByTitle(query: String): Result<List<ManhwaUI>> =
        runCatching {
            cache[query]?.let { return Result.success(it) }

            val response = ktorClientOL.get(BASE) {
                parameter("title", query)
                parameter("order[relevance]", "desc")
                parameter("includes[]", "cover_art")
                parameter("contentRating[]", "safe")
                parameter("limit", "100")
            }.body<MangaDexTitleResponse>()

            val result = response.data.map { manhwa ->
                val cover = manhwa.relationships.firstOrNull {
                    it.relationshipsType == "cover_art"
                }
                ManhwaUI(
                    id = manhwa.id,
                    title = (manhwa.attributes?.title?.englishMainTitle)?: ((manhwa.attributes?.altTitles?.get(0)?.englishAltTitle)?:"Failed to load Title"),
                    coverUrl = cover?.relationshipsAttributes?.fileName?.let {
                        "https://uploads.mangadex.org/covers/${manhwa.id}/$it"
                    }
                )
            }

            cache[query] = result
            result
        }

    suspend fun getMaxChapters(manhwaId: String): Result<Int> =
        runCatching {
            val response = ktorClientOL.get("$BASE/$manhwaId/aggregate") {
                parameter("translatedLanguage[]", "en")
            }.body<MangaDexAggregateResponse>()

            val allChapters: List<ChapterDto> = response.volumes.values.flatMap { volumeDto ->
                volumeDto.chapters.values
            }

            val uniqueChaptersCount = allChapters
                .mapNotNull { it.chapter }
                .distinct()
                .size

            uniqueChaptersCount
        }

    private suspend fun downloadAndCompressCover(
        context: Context,
        manhwaId: String,
        webUrl: String?
    ): String? {
        if (webUrl.isNullOrEmpty()) return null
        return runCatching {
            val responseBytes = ktorClientOL.get(webUrl).readRawBytes()
            var bitmap = BitmapFactory.decodeByteArray(responseBytes, 0, responseBytes.size)
                ?: return null

            // Match your ImageView size: 96dp → ~200px at 2x density
            bitmap = bitmap.scale(200, 200)

            val directory = File(context.filesDir, "manhwa_covers").apply { mkdirs() }
            val targetFile = File(directory, "cover_${manhwaId}.jpg")

            FileOutputStream(targetFile).use { outStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outStream)
            }

            targetFile.absolutePath
        }.getOrNull()
    }

}