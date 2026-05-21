//Theme.kt
package mt.edu.mcast.webapitutorial_ktor.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun WebAPITutorial_KtorTheme(
    appTheme: AppTheme = AppTheme.OCEAN,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme = when (appTheme) {
        AppTheme.CRIMSON -> if (darkTheme) CrimsonDarkScheme else CrimsonLightScheme
        AppTheme.EMERALD -> if (darkTheme) EmeraldDarkScheme else EmeraldLightScheme
        AppTheme.OCEAN   -> if (darkTheme) OceanDarkScheme   else OceanLightScheme
        AppTheme.MONO    -> if (darkTheme) MonoDarkScheme     else MonoLightScheme
        AppTheme.SECRET  -> if (darkTheme) SecretDarkScheme   else  SecretLightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}