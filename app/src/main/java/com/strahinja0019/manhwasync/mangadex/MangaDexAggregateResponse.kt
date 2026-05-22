//MangaDexAggregateResponse.kt
package com.strahinja0019.manhwasync.mangadex

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class MangaDexAggregateResponse(
    @SerialName("result") val result: String? = null,
    @SerialName("volumes") val volumes: Map<String, VolumeDto> = emptyMap()
)

@Serializable
data class VolumeDto(
    @SerialName("volume") val volume: String? = null,
    @SerialName("count") val count: Int? = null,
    @SerialName("chapters") val chapters: Map<String, ChapterDto> = emptyMap()
)

@Serializable
data class ChapterDto(
    @SerialName("chapter") val chapter: String? = null,
    @SerialName("id") val id: String? = null,
    // Sometimes MangaDex returns an array of alternate group IDs under "others".
    // We can include it just in case, but it's optional for our math!
    @SerialName("others") val others: List<String> = emptyList(),
    @SerialName("count") val count: Int? = null
)