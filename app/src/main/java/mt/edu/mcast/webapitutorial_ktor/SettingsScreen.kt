package mt.edu.mcast.webapitutorial_ktor

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
import mt.edu.mcast.webapitutorial_ktor.ui.theme.CrimsonDarkScheme
import mt.edu.mcast.webapitutorial_ktor.ui.theme.EmeraldDarkScheme
import mt.edu.mcast.webapitutorial_ktor.ui.theme.MonoDarkScheme
import mt.edu.mcast.webapitutorial_ktor.ui.theme.OceanDarkScheme

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    currentTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit
) {
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

                // 1. Dynamic variable mapping each enum to a specific theme color token
                val dynamicColor = when (theme) {
                    AppTheme.CRIMSON -> CrimsonDarkScheme.primary
                    AppTheme.EMERALD -> EmeraldDarkScheme.primary
                    AppTheme.OCEAN   -> OceanDarkScheme.primary
                    AppTheme.MONO    -> MonoDarkScheme.primary
                }

                // 2. Fixed conditional modifier layout sizing and border
                val buttonModifier = if (theme.displayName == currentTheme.displayName) {
                    Modifier
                        .width(114.dp)
                        .height(42.dp)
                        .border(4.dp, MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.extraLarge)
                } else {
                    Modifier
                        .width(110.dp)
                        .height(38.dp)
                }

                Button(
                    onClick = { onThemeChange(theme) },
                    modifier = buttonModifier,
                    colors = ButtonDefaults.buttonColors(containerColor = dynamicColor)
                ) {
                    Text(theme.displayName)
                }
            }
        }
    }
}
