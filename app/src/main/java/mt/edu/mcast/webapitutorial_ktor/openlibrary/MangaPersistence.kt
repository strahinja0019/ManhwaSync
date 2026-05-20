//MangaPersistence.kt
package mt.edu.mcast.webapitutorial_ktor.openlibrary

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import mt.edu.mcast.webapitutorial_ktor.MangaUI
import mt.edu.mcast.webapitutorial_ktor.ui.theme.AppTheme


val Context.dataStore by preferencesDataStore(name = "manga_storage")

object MangaPersistence {
    private val THEME_KEY = stringPreferencesKey("app_theme")
    private val MANGA_LIST_KEY = stringPreferencesKey("manga_list")

    suspend fun saveMangaList(context: Context, mangaList: List<MangaUI>) {
        val jsonString = Json.encodeToString(mangaList)
        context.dataStore.edit { preferences ->
            preferences[MANGA_LIST_KEY] = jsonString
        }
    }

    fun getMangaList(context: Context): Flow<List<MangaUI>> {
        return context.dataStore.data.map { preferences ->
            val jsonString = preferences[MANGA_LIST_KEY]
            if (!jsonString.isNullOrEmpty()) {
                Json.decodeFromString<List<MangaUI>>(jsonString)
            } else {
                emptyList()
            }
        }
    }

    suspend fun saveTheme(context: Context, theme: AppTheme) {
        context.dataStore.edit { it[THEME_KEY] = theme.name }
    }

    fun getTheme(context: Context): Flow<AppTheme> {
        return context.dataStore.data.map { preferences ->
            AppTheme.fromString(preferences[THEME_KEY] ?: AppTheme.OCEAN.name)
        }
    }
}
