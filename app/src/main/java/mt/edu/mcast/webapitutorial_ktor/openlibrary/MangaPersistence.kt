//MangaPersistence.kt
package mt.edu.mcast.webapitutorial_ktor.openlibrary

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mt.edu.mcast.webapitutorial_ktor.ui.theme.AppTheme

val Context.dataStore by preferencesDataStore(name = "manga_storage")

object MangaPersistence {
    private val THEME_KEY = stringPreferencesKey("app_theme")

    suspend fun saveTheme(context: Context, theme: AppTheme) {
        context.dataStore.edit { it[THEME_KEY] = theme.name }
    }

    fun getTheme(context: Context): Flow<AppTheme> {
        return context.dataStore.data.map { preferences ->
            AppTheme.fromString(preferences[THEME_KEY] ?: AppTheme.OCEAN.name)
        }
    }
}