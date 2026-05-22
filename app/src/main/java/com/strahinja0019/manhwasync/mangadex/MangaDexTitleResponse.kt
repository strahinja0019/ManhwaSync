//MangaDexTitleResponse.kt
package com.strahinja0019.manhwasync.mangadex

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class MangaDexTitleResponse (

    @SerialName("result"   ) var result   : String?         = null, //ok or error
    @SerialName("data"     ) var data     : ArrayList<Data> = arrayListOf(),
    @SerialName("total"    ) var total    : Int?            = null // total of how many results

)

@Serializable
data class Data (

    @SerialName("id"            ) var id            : String?                  = null, //id of manga/manhwa
    @SerialName("type"          ) var type          : String?                  = null, //manga (idk if it can be author)
    @SerialName("attributes"    ) var attributes    : Attributes?              = Attributes(), //
    @SerialName("relationships" ) var relationships : ArrayList<Relationships> = arrayListOf() //arrays with "type":author,artist,cover_art,manga,creator

)

@Serializable
data class Attributes (

    @SerialName("title"                          ) var title                          : Title?               = Title(),
    @SerialName("altTitles"                      ) var altTitles                      : ArrayList<AltTitles> = arrayListOf(),
)

@Serializable
data class Relationships (

    @SerialName("type"       ) var relationshipsType       : String?     = null, //author,artist,cover_art,manga,creator
    @SerialName("attributes" ) var relationshipsAttributes : RelationshipsAttribute? = RelationshipsAttribute() //fileName

)

@Serializable
data class Title (

    @SerialName("en" ) var englishMainTitle : String? = null

)

@Serializable
data class AltTitles (

    @SerialName("en" ) var englishAltTitle : String? = null

)

@Serializable
data class RelationshipsAttribute (

    @SerialName("fileName"       ) var fileName       : String?      = null

)