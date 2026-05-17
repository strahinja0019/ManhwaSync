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
                    title = book.attributes?.title?.englishMainTitle,
                    coverUrl = cover?.relationshipsAttributes?.fileName?.let {
                        "https://uploads.mangadex.org/covers/${book.id}/$it"
                    }
                )
            }

            cache[query] = result // Store in cache
            result
        }
}
