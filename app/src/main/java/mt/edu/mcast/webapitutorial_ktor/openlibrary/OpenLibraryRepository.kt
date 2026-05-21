//OpenLibraryRepository.kt
package mt.edu.mcast.webapitutorial_ktor.openlibrary

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import mt.edu.mcast.webapitutorial_ktor.MangaUI
import mt.edu.mcast.webapitutorial_ktor.database.MangaDao
import mt.edu.mcast.webapitutorial_ktor.database.MangaEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import java.io.File
import java.io.FileOutputStream

class OpenLibraryRepository(private val mangaDao: MangaDao) {
    private val cache = mutableMapOf<String, List<MangaUI>>()
    private val BASE = "https://api.mangadex.dev/manga"

    // Stream saved data directly from the room database as MangaUI instances
    val savedMangasStream: Flow<List<MangaUI>> = mangaDao.getAllSavedMangas().map { entities ->
        entities.map { it.toMangaUI() }
    }

    // Pass context here to unlock local system directory lookups
    suspend fun saveToFavorites(context: Context, manga: MangaUI) {
        if (manga.id == null) return

        // 1. Fetch total chapter counts asynchronously over network stream
        val chapterCount = getMaxChapters(manga.id).getOrNull() ?: 0

        // 2. Stream the cover image file and download/compress it into local cache files
        val localPath = downloadAndCompressCover(context, manga.id, manga.coverUrl)

        // 3. Initialize mapping logic including our new internal device link
        val entity = MangaEntity(
            id = manga.id,
            title = manga.title ?: "Unknown Title",
            coverUrl = manga.coverUrl ?: "",
            localCoverPath = localPath, // Saved locally to disk!
            chapterCount = chapterCount
        )

        mangaDao.insertManga(entity)
    }

    suspend fun removeFromFavorites(context: Context, mangaId: String) {
        // 1. Find the file reference location inside your internal system structure
        val directory = File(context.filesDir, "manga_covers")
        val fileTarget = File(directory, "cover_${mangaId}.jpg")

        // 2. Delete the compressed file asset off the device storage allocation space safely
        if (fileTarget.exists()) {
            fileTarget.delete()
        }

        // 3. Clear the entity row data completely out of Room table memory rows
        mangaDao.deleteMangaById(mangaId)
    }

    suspend fun getBooksByTitle(query: String): Result<List<MangaUI>> =
        runCatching {
            cache[query]?.let { return Result.success(it) }

            val response = ktorClientOL.get(BASE) {
                parameter("title", query)
                parameter("order[relevance]", "desc")
                parameter("includes[]", "cover_art")
                parameter("contentRating[]", "safe")
                parameter("limit", "100")
            }.body<MangaDexTitleResponse>()

            val result = response.data.map { book ->
                val cover = book.relationships.firstOrNull {
                    it.relationshipsType == "cover_art"
                }
                MangaUI(
                    id = book.id,
                    title = (book.attributes?.title?.englishMainTitle)?: ((book.attributes?.altTitles?.get(0)?.englishAltTitle)?:"Failed to load Title"),
                    coverUrl = cover?.relationshipsAttributes?.fileName?.let {
                        "https://uploads.mangadex.org/covers/${book.id}/$it"
                    }
                )
            }

            cache[query] = result
            result
        }

    suspend fun getMaxChapters(mangaId: String): Result<Int> =
        runCatching {
            val response = ktorClientOL.get("$BASE/$mangaId/aggregate") {
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

    private suspend fun downloadAndCompressCover(context: Context, mangaId: String, webUrl: String?): String? {
        if (webUrl.isNullOrEmpty()) return null
        return runCatching {
            // 1. Download raw web bytes via Ktor client engine
            val responseBytes = ktorClientOL.get(webUrl).readBytes()

            // 2. Decode bytes into an uncompressed memory Bitmap asset
            val bitmap = BitmapFactory.decodeByteArray(responseBytes, 0, responseBytes.size) ?: return null

            // 3. Set up the target file destination pointer inside private app folder
            val directory = File(context.filesDir, "manga_covers").apply { mkdirs() }
            val targetFile = File(directory, "cover_${mangaId}.jpg")

            // 4. Compress the bitmap tightly into the file stream as a lightweight JPEG (quality 75%)
            FileOutputStream(targetFile).use { outStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outStream)
            }

            // Return absolute path string where the device can always reference it offline
            targetFile.absolutePath
        }.getOrNull()
    }
}