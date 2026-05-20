package mt.edu.mcast.webapitutorial_ktor

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) return

        val action = intent.action

        // Define our custom action name to verify explicitly scheduled alarms
        val customAlarmAction = "${context.packageName}.ACTION_DAILY_ALARM"

        // Enforce action checking to prevent spoofing from empty or incorrect intents
        if (action == Intent.ACTION_BOOT_COMPLETED || action == customAlarmAction) {

            // 1. Only show the notification if it's the actual scheduled time (not just a device reboot)
            if (action == customAlarmAction) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                val notification = NotificationCompat.Builder(context, "info_channel")
                    .setContentTitle("Go read some manhwa!")
                    .setContentText("It is Noon! Don't forget to binge ;)")
                    .setSmallIcon(R.drawable.manhwasync)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .build()

                notificationManager.notify(2, notification)
            }

            // 2. Always reschedule tomorrow's alarm (on reboot or on alarm fire)
            MainActivity.setDailyAlarm(context)
        }
    }
}
