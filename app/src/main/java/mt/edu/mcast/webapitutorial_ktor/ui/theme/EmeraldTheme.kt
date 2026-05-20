//EmeraldTheme.kt
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

val EmeraldLightScheme = lightColorScheme(
    primary = emeraldprimaryLight,
    onPrimary = emeraldonPrimaryLight,
    primaryContainer = emeraldprimaryContainerLight,
    onPrimaryContainer = emeraldonPrimaryContainerLight,
    secondary = emeraldsecondaryLight,
    onSecondary = emeraldonSecondaryLight,
    secondaryContainer = emeraldsecondaryContainerLight,
    onSecondaryContainer = emeraldonSecondaryContainerLight,
    tertiary = emeraldtertiaryLight,
    onTertiary = emeraldonTertiaryLight,
    tertiaryContainer = emeraldtertiaryContainerLight,
    onTertiaryContainer = emeraldonTertiaryContainerLight,
    error = emeralderrorLight,
    onError = emeraldonErrorLight,
    errorContainer = emeralderrorContainerLight,
    onErrorContainer = emeraldonErrorContainerLight,
    background = emeraldbackgroundLight,
    onBackground = emeraldonBackgroundLight,
    surface = emeraldsurfaceLight,
    onSurface = emeraldonSurfaceLight,
    surfaceVariant = emeraldsurfaceVariantLight,
    onSurfaceVariant = emeraldonSurfaceVariantLight,
    outline = emeraldoutlineLight,
    outlineVariant = emeraldoutlineVariantLight,
    scrim = emeraldscrimLight,
    inverseSurface = emeraldinverseSurfaceLight,
    inverseOnSurface = emeraldinverseOnSurfaceLight,
    inversePrimary = emeraldinversePrimaryLight,
    surfaceDim = emeraldsurfaceDimLight,
    surfaceBright = emeraldsurfaceBrightLight,
    surfaceContainerLowest = emeraldsurfaceContainerLowestLight,
    surfaceContainerLow = emeraldsurfaceContainerLowLight,
    surfaceContainer = emeraldsurfaceContainerLight,
    surfaceContainerHigh = emeraldsurfaceContainerHighLight,
    surfaceContainerHighest = emeraldsurfaceContainerHighestLight,
)

val EmeraldDarkScheme = darkColorScheme(
    primary = emeraldprimaryDark,
    onPrimary = emeraldonPrimaryDark,
    primaryContainer = emeraldprimaryContainerDark,
    onPrimaryContainer = emeraldonPrimaryContainerDark,
    secondary = emeraldsecondaryDark,
    onSecondary = emeraldonSecondaryDark,
    secondaryContainer = emeraldsecondaryContainerDark,
    onSecondaryContainer = emeraldonSecondaryContainerDark,
    tertiary = emeraldtertiaryDark,
    onTertiary = emeraldonTertiaryDark,
    tertiaryContainer = emeraldtertiaryContainerDark,
    onTertiaryContainer = emeraldonTertiaryContainerDark,
    error = emeralderrorDark,
    onError = emeraldonErrorDark,
    errorContainer = emeralderrorContainerDark,
    onErrorContainer = emeraldonErrorContainerDark,
    background = emeraldbackgroundDark,
    onBackground = emeraldonBackgroundDark,
    surface = emeraldsurfaceDark,
    onSurface = emeraldonSurfaceDark,
    surfaceVariant = emeraldsurfaceVariantDark,
    onSurfaceVariant = emeraldonSurfaceVariantDark,
    outline = emeraldoutlineDark,
    outlineVariant = emeraldoutlineVariantDark,
    scrim = emeraldscrimDark,
    inverseSurface = emeraldinverseSurfaceDark,
    inverseOnSurface = emeraldinverseOnSurfaceDark,
    inversePrimary = emeraldinversePrimaryDark,
    surfaceDim = emeraldsurfaceDimDark,
    surfaceBright = emeraldsurfaceBrightDark,
    surfaceContainerLowest = emeraldsurfaceContainerLowestDark,
    surfaceContainerLow = emeraldsurfaceContainerLowDark,
    surfaceContainer = emeraldsurfaceContainerDark,
    surfaceContainerHigh = emeraldsurfaceContainerHighDark,
    surfaceContainerHighest = emeraldsurfaceContainerHighestDark,
)