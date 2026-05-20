//OceanTheme.kt
package mt.edu.mcast.webapitutorial_ktor.ui.theme
import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext

val OceanLightScheme = lightColorScheme(
    primary = oceanprimaryLight,
    onPrimary = oceanonPrimaryLight,
    primaryContainer = oceanprimaryContainerLight,
    onPrimaryContainer = oceanonPrimaryContainerLight,
    secondary = oceansecondaryLight,
    onSecondary = oceanonSecondaryLight,
    secondaryContainer = oceansecondaryContainerLight,
    onSecondaryContainer = oceanonSecondaryContainerLight,
    tertiary = oceantertiaryLight,
    onTertiary = oceanonTertiaryLight,
    tertiaryContainer = oceantertiaryContainerLight,
    onTertiaryContainer = oceanonTertiaryContainerLight,
    error = oceanerrorLight,
    onError = oceanonErrorLight,
    errorContainer = oceanerrorContainerLight,
    onErrorContainer = oceanonErrorContainerLight,
    background = oceanbackgroundLight,
    onBackground = oceanonBackgroundLight,
    surface = oceansurfaceLight,
    onSurface = oceanonSurfaceLight,
    surfaceVariant = oceansurfaceVariantLight,
    onSurfaceVariant = oceanonSurfaceVariantLight,
    outline = oceanoutlineLight,
    outlineVariant = oceanoutlineVariantLight,
    scrim = oceanscrimLight,
    inverseSurface = oceaninverseSurfaceLight,
    inverseOnSurface = oceaninverseOnSurfaceLight,
    inversePrimary = oceaninversePrimaryLight,
    surfaceDim = oceansurfaceDimLight,
    surfaceBright = oceansurfaceBrightLight,
    surfaceContainerLowest = oceansurfaceContainerLowestLight,
    surfaceContainerLow = oceansurfaceContainerLowLight,
    surfaceContainer = oceansurfaceContainerLight,
    surfaceContainerHigh = oceansurfaceContainerHighLight,
    surfaceContainerHighest = oceansurfaceContainerHighestLight,
)

val OceanDarkScheme = darkColorScheme(
    primary = oceanprimaryDark,
    onPrimary = oceanonPrimaryDark,
    primaryContainer = oceanprimaryContainerDark,
    onPrimaryContainer = oceanonPrimaryContainerDark,
    secondary = oceansecondaryDark,
    onSecondary = oceanonSecondaryDark,
    secondaryContainer = oceansecondaryContainerDark,
    onSecondaryContainer = oceanonSecondaryContainerDark,
    tertiary = oceantertiaryDark,
    onTertiary = oceanonTertiaryDark,
    tertiaryContainer = oceantertiaryContainerDark,
    onTertiaryContainer = oceanonTertiaryContainerDark,
    error = oceanerrorDark,
    onError = oceanonErrorDark,
    errorContainer = oceanerrorContainerDark,
    onErrorContainer = oceanonErrorContainerDark,
    background = oceanbackgroundDark,
    onBackground = oceanonBackgroundDark,
    surface = oceansurfaceDark,
    onSurface = oceanonSurfaceDark,
    surfaceVariant = oceansurfaceVariantDark,
    onSurfaceVariant = oceanonSurfaceVariantDark,
    outline = oceanoutlineDark,
    outlineVariant = oceanoutlineVariantDark,
    scrim = oceanscrimDark,
    inverseSurface = oceaninverseSurfaceDark,
    inverseOnSurface = oceaninverseOnSurfaceDark,
    inversePrimary = oceaninversePrimaryDark,
    surfaceDim = oceansurfaceDimDark,
    surfaceBright = oceansurfaceBrightDark,
    surfaceContainerLowest = oceansurfaceContainerLowestDark,
    surfaceContainerLow = oceansurfaceContainerLowDark,
    surfaceContainer = oceansurfaceContainerDark,
    surfaceContainerHigh = oceansurfaceContainerHighDark,
    surfaceContainerHighest = oceansurfaceContainerHighestDark,
)