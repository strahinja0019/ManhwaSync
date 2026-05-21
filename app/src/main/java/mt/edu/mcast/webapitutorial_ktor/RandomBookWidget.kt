//RandomBookWidget.kt
package mt.edu.mcast.webapitutorial_ktor

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mt.edu.mcast.webapitutorial_ktor.database.AppDatabase

class RandomBookWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {}
    override fun onDisabled(context: Context) {}
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = RemoteViews(context.packageName, R.layout.random_book_widget)

    CoroutineScope(Dispatchers.IO).launch {
        // 1. Get Room database instance and read the saved list out of the Flow stream
        val database = AppDatabase.getDatabase(context.applicationContext)
        val savedEntities = database.mangaDao().getAllSavedMangas().first()

        // 2. Pick a manga entity from the collection (or select a random one)
        val entity = savedEntities.shuffled().firstOrNull()

        withContext(Dispatchers.Main) {
            if (entity != null) {
                val mangaId = entity.id
                val title = entity.title

                views.setTextViewText(R.id.txtvBookTitle, title)

                val mihonIntent = Intent(Intent.ACTION_SEARCH).apply {
                    `package` = "app.mihon"
                    putExtra("query", "id:$mangaId")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }

                val pendingIntent = PendingIntent.getActivity(
                    context,
                    appWidgetId,
                    mihonIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                views.setOnClickPendingIntent(R.id.btnOpenMihon, pendingIntent)
            } else {
                views.setTextViewText(R.id.txtvBookTitle, "No saved manga")

                // Clear click action if no manga exists
                views.setOnClickPendingIntent(R.id.btnOpenMihon, null)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}