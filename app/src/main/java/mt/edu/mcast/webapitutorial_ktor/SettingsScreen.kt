package mt.edu.mcast.webapitutorial_ktor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import mt.edu.mcast.webapitutorial_ktor.ui.theme.CrimsonDarkScheme
import mt.edu.mcast.webapitutorial_ktor.ui.theme.EmeraldDarkScheme
import mt.edu.mcast.webapitutorial_ktor.ui.theme.MonoDarkScheme
import mt.edu.mcast.webapitutorial_ktor.ui.theme.OceanDarkScheme
import kotlin.math.sqrt

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    currentTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit
) {

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
                    val dynamicColor = when (theme) {
                        AppTheme.CRIMSON -> CrimsonDarkScheme.primary
                        AppTheme.EMERALD -> EmeraldDarkScheme.primary
                        AppTheme.OCEAN -> OceanDarkScheme.primary
                        AppTheme.MONO -> MonoDarkScheme.primary
                        else -> {}
                    }

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
                        colors = ButtonDefaults.buttonColors(containerColor = dynamicColor as Color)
                    ) {
                        Text(theme.displayName)
                    }
                }
            }
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
        private const val SHAKE_THRESHOLD_G_FORCE = 3.5f
        private const val SHAKE_SLOP_TIME_MS = 5000
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