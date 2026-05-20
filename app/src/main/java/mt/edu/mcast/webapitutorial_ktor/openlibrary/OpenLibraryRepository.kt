//OpenLibraryRepository.kt
package mt.edu.mcast.webapitutorial_ktor.openlibrary

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import mt.edu.mcast.webapitutorial_ktor.MangaUI


class OpenLibraryRepository {
    private val cache = mutableMapOf<String, List<MangaUI>>()
    private val BASE = "https://api.mangadex.dev/manga"

    suspend fun getBooksByTitle(query: String): Result<List<MangaUI>> =
        runCatching {
            // Check cache first
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

            cache[query] = result // Store in cache
            result
        }

    // Inside your OpenLibraryRepository class

    suspend fun getMaxChapters(mangaId: String): Result<Int> =
        runCatching {
            // Fetch from production MangaDex API
            val response = ktorClientOL.get("$BASE/$mangaId/aggregate") {
                // Filter by English to keep the UI localized and clean
                parameter("translatedLanguage[]", "en")
            }.body<MangaDexAggregateResponse>()

            // 1. Flatten all ChapterDto objects out of the nested volumes map
            val allChapters: List<ChapterDto> = response.volumes.values.flatMap { volumeDto ->
                volumeDto.chapters.values
            }

            // 2. Extract every single distinct chapter chapter-number or ID.
            // Since chapter IDs are unique to individual uploads, tracking by distinct 'chapter'
            // numbers within a strict flat volume set works if chapters don't reset.
            // But since you want to support volume resets (e.g. Vol 1 Ch 1-10, Vol 2 Ch 1-10),
            // we count the absolute number of distinct logical story segments.
            val uniqueChaptersCount = allChapters
                .mapNotNull { it.chapter } // Extract strings like "1", "2", "10.5"
                .distinct()               // Deduplicate overlapping scanlation team entries
                .size                     // Get the total size of unique story chapters

            uniqueChaptersCount
        }
}
