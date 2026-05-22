package mt.edu.mcast.webapitutorial_ktor

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Button
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import mt.edu.mcast.webapitutorial_ktor.ui.theme.AppTheme
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import mt.edu.mcast.webapitutorial_ktor.ui.theme.CrimsonDarkScheme
import mt.edu.mcast.webapitutorial_ktor.ui.theme.EmeraldDarkScheme
import mt.edu.mcast.webapitutorial_ktor.ui.theme.MonoDarkScheme
import mt.edu.mcast.webapitutorial_ktor.ui.theme.OceanDarkScheme
import mt.edu.mcast.webapitutorial_ktor.ui.theme.getPrimaryColor
import kotlin.math.sqrt

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    currentTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit
) {
    val context = LocalContext.current

    ShakeTriggerScreen(
        onShakeDetected = {
            onThemeChange(AppTheme.SECRET)
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Settings", style = MaterialTheme.typography.headlineMedium)

        LazyRow(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(AppTheme.entries) { theme ->
                if (theme != AppTheme.SECRET) {

                    // 1. Dynamic variable mapping each enum to a specific theme color token

                    // 2. Fixed conditional modifier layout sizing and border
                    val buttonModifier = if (theme.displayName == currentTheme.displayName) {
                        Modifier
                            .width(114.dp)
                            .height(42.dp)
                            .border(
                                4.dp,
                                MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.extraLarge
                            )
                    } else {
                        Modifier
                            .width(110.dp)
                            .height(38.dp)
                    }

                    Button(
                        onClick = { onThemeChange(theme) },
                        modifier = buttonModifier,
                        colors = ButtonDefaults.buttonColors(containerColor = theme.getPrimaryColor { primary })
                    ) {
                        Text(theme.displayName)
                    }
                }
            }
        }
        // to test it with a command do
        /*
        & $env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe -s emulator-5554 shell am broadcast -a android.appwidget.action.APPWIDGET_UPDATE --eia appWidgetIds 6 -n mt.edu.mcast.webapitutorial_ktor/.RandomBookWidget
         */

        //This is to test it with a button by manually sending the request
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            onClick = {
                android.util.Log.d("WidgetTest", "Manual updateRequest call")

                val updateRequest = OneTimeWorkRequestBuilder<WidgetUpdateWorker>().build()
                WorkManager.getInstance(context).enqueueUniqueWork(
                    "WidgetManualTest",
                    ExistingWorkPolicy.REPLACE,
                    updateRequest
                )
                android.util.Log.d("WidgetTest", "Enqueued one-time worker for testing")
            },
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Test Widget Manual updateRequest")
        }

        Button(
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            onClick = {
            android.util.Log.d("WidgetTest", "Manual WidgetManager setup")
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = android.content.ComponentName(context, RandomBookWidget::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            val intent = Intent(context, RandomBookWidget::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            }
            context.sendBroadcast(intent)
        }) {
            Text("Test Widget Manual WidgetManager")
        }


    }

}


class ShakeDetector(private val onShake: () -> Unit) : SensorEventListener {

    private var lastShakeTime: Long = 0

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // Calculate G-force relative to Earth's gravity
            val gX = x / SensorManager.GRAVITY_EARTH
            val gY = y / SensorManager.GRAVITY_EARTH
            val gZ = z / SensorManager.GRAVITY_EARTH

            val gForce = sqrt(gX * gX + gY * gY + gZ * gZ)

            // 2.7G is a standard threshold for an intentional human shake
            if (gForce > SHAKE_THRESHOLD_G_FORCE) {
                val currentTime = System.currentTimeMillis()

                // Prevent multiple triggers from a single continuous movement
                if (lastShakeTime + SHAKE_SLOP_TIME_MS > currentTime) {
                    return
                }

                lastShakeTime = currentTime
                onShake()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for shake detection
    }

    companion object {
        private const val SHAKE_THRESHOLD_G_FORCE = 2.7f
        private const val SHAKE_SLOP_TIME_MS = 3000
    }
}

@Composable
fun ShakeTriggerScreen(
    onShakeDetected: () -> Unit
) {
    val context = LocalContext.current

// Safely capture the latest function reference without restarting the effect
    val currentOnShakeDetected = rememberUpdatedState(onShakeDetected)

    DisposableEffect(context) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Initialize detector with the callback action
        val shakeDetector = ShakeDetector {
            currentOnShakeDetected.value()
        }

        // Register the hardware listener
        if (accelerometer != null) {
            sensorManager.registerListener(
                shakeDetector,
                accelerometer,
                SensorManager.SENSOR_DELAY_UI
            )
        }

        // Unregister automatically when Composable leaves the composition
        onDispose {
            sensorManager.unregisterListener(shakeDetector)
        }
    }
}