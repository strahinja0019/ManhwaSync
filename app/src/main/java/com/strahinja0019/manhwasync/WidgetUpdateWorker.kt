package com.strahinja0019.manhwasync

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WidgetUpdateWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        android.util.Log.d("WidgetWork", "========== PERIODIC WORKER FIRED ==========")
        android.util.Log.d("WidgetWork", "This ran from WorkManager scheduled task, not onUpdate()")

        try {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, RandomManhwaWidget::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            android.util.Log.d("WidgetWork", "Found ${appWidgetIds.size} widgets to update")

            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context.applicationContext, appWidgetManager, appWidgetId)
            }
            android.util.Log.d("WidgetWork", "Worker completed successfully")
            Result.success()
        } catch (e: Exception) {
            android.util.Log.e("WidgetWork", "Worker failed with exception", e)
            e.printStackTrace()
            Result.retry()
        }
    }
}
