package com.strahinja0019.manhwasync

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.RemoteViews
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.strahinja0019.manhwasync.database.AppDatabase
import java.io.File
import java.util.concurrent.TimeUnit
import androidx.core.graphics.scale

class RandomManhwaWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        super.onUpdate(context, appWidgetManager, appWidgetIds)
        CoroutineScope(Dispatchers.IO).launch {
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        android.util.Log.d("WidgetWork", "onEnabled() called - scheduling periodic work")


        //val updateRequest = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(15, TimeUnit.MINUTES).build()
        //works with 15 minutes, I won't actaully test the 1-hour one because I don't want to waste time, but it should work as well
        val updateRequest = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(1, TimeUnit.HOURS).build()

        WorkManager.getInstance(context.applicationContext).enqueueUniquePeriodicWork(
            "WidgetHourlyUpdate",
            ExistingPeriodicWorkPolicy.KEEP,
            updateRequest
        )
        android.util.Log.d("WidgetWork", "Periodic work enqueued successfully")
    }

    override fun onDisabled(context: Context) {
        android.util.Log.d("WidgetWork", "onDisabled() called - canceling work")
        WorkManager.getInstance(context.applicationContext).cancelUniqueWork("WidgetHourlyUpdate")
        super.onDisabled(context)
    }
}


internal suspend fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    // Log widget update start with timestamp
    val timestamp = System.currentTimeMillis()
    android.util.Log.d("WidgetUpdate", "========== WIDGET UPDATE START ==========")
    android.util.Log.d("WidgetUpdate", "Timestamp: $timestamp | AppWidgetId: $appWidgetId")

    val views = RemoteViews(context.packageName, R.layout.random_manhwa_widget)

    try {
        val database = AppDatabase.getDatabase(context)

        // Log before RNG call
        android.util.Log.d("WidgetUpdate", "Calling getRandomManhwa()...")
        val entity = database.manhwaDao().getRandomManhwa()

        // Log the result immediately - THIS IS KEY FOR DETECTING BIAS
        android.util.Log.d("WidgetUpdate", "RNG Result: id=${entity?.id}, title=${entity?.title}")

        android.util.Log.d("Widget", "entity: ${entity?.title}")
        android.util.Log.d("Widget", "localCoverPath: ${entity?.localCoverPath}")
        android.util.Log.d("Widget", "coverUrl: ${entity?.coverUrl}")

        if (entity != null && !entity.localCoverPath.isNullOrEmpty()) {
            val imgFile = File(entity.localCoverPath)
            android.util.Log.d("Widget", "file exists: ${imgFile.exists()}")
            android.util.Log.d("Widget", "file size: ${imgFile.length()}")
        }

        // Load bitmap on IO thread before switching to Main
        var coverBitmap: Bitmap? = null
        if (entity != null && !entity.localCoverPath.isNullOrEmpty()) {
            try {
                val imgFile = File(entity.localCoverPath)
                if (imgFile.exists()) {
                    val options = BitmapFactory.Options().apply {
                        inSampleSize = 4
                    }
                    val decoded = BitmapFactory.decodeFile(imgFile.absolutePath, options)
                    // Scale to fixed small size safe for RemoteViews
                    coverBitmap = decoded?.scale(128, 128)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // If local file failed, try downloading from URL
        if (coverBitmap == null && entity != null && !entity.coverUrl.isNullOrEmpty()) {
            try {
                val url = java.net.URL(entity.coverUrl)
                val connection = url.openConnection() as java.net.HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                coverBitmap = BitmapFactory.decodeStream(input)
                input.close()
                connection.disconnect()

                // Scale to match 96dp ImageView
                coverBitmap?.let {
                    coverBitmap =  it.scale(128, 128)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        withContext(Dispatchers.Main) {
            if (entity != null) {
                val chapters = entity.chapterCount ?: 0
                val calculatedHours = chapters / 10.0

                android.util.Log.d("WidgetUpdate", "Updating UI with: ${entity.title} | Chapters: $chapters | Hours: %.1f hrs".format(calculatedHours))

                views.setTextViewText(R.id.txtvManhwaTitle, entity.title)
                views.setTextViewText(R.id.hourNeeded, String.format("%.1f", calculatedHours))

                if (coverBitmap != null) {
                    views.setImageViewBitmap(R.id.imgManhwaLogo, coverBitmap)
                } else {
                    views.setImageViewResource(R.id.imgManhwaLogo, android.R.drawable.ic_menu_gallery)
                }

                val mihonIntent = Intent(Intent.ACTION_SEARCH).apply {
                    `package` = "app.mihon"
                    putExtra("query", "id:${entity.id}")
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
                android.util.Log.w("WidgetUpdate", "No manhwa returned from getRandomManhwa()!")
                views.setTextViewText(R.id.txtvManhwaTitle, "No saved manhwa")
                views.setTextViewText(R.id.hourNeeded, "0")
                views.setImageViewResource(R.id.imgManhwaLogo, android.R.drawable.ic_menu_gallery)
                views.setOnClickPendingIntent(R.id.btnOpenMihon, null)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
            android.util.Log.d("WidgetUpdate", "========== WIDGET UPDATE COMPLETE ==========")
        }
    } catch (e: Exception) {
        android.util.Log.e("WidgetUpdate", "ERROR in updateAppWidget", e)
        e.printStackTrace()
    }
}
