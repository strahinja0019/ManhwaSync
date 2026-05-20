//MonoTheme.kt
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

val MonoLightScheme = lightColorScheme(
    primary = monoprimaryLight,
    onPrimary = monoonPrimaryLight,
    primaryContainer = monoprimaryContainerLight,
    onPrimaryContainer = monoonPrimaryContainerLight,
    secondary = monosecondaryLight,
    onSecondary = monoonSecondaryLight,
    secondaryContainer = monosecondaryContainerLight,
    onSecondaryContainer = monoonSecondaryContainerLight,
    tertiary = monotertiaryLight,
    onTertiary = monoonTertiaryLight,
    tertiaryContainer = monotertiaryContainerLight,
    onTertiaryContainer = monoonTertiaryContainerLight,
    error = monoerrorLight,
    onError = monoonErrorLight,
    errorContainer = monoerrorContainerLight,
    onErrorContainer = monoonErrorContainerLight,
    background = monobackgroundLight,
    onBackground = monoonBackgroundLight,
    surface = monosurfaceLight,
    onSurface = monoonSurfaceLight,
    surfaceVariant = monosurfaceVariantLight,
    onSurfaceVariant = monoonSurfaceVariantLight,
    outline = monooutlineLight,
    outlineVariant = monooutlineVariantLight,
    scrim = monoscrimLight,
    inverseSurface = monoinverseSurfaceLight,
    inverseOnSurface = monoinverseOnSurfaceLight,
    inversePrimary = monoinversePrimaryLight,
    surfaceDim = monosurfaceDimLight,
    surfaceBright = monosurfaceBrightLight,
    surfaceContainerLowest = monosurfaceContainerLowestLight,
    surfaceContainerLow = monosurfaceContainerLowLight,
    surfaceContainer = monosurfaceContainerLight,
    surfaceContainerHigh = monosurfaceContainerHighLight,
    surfaceContainerHighest = monosurfaceContainerHighestLight,
)

val MonoDarkScheme = darkColorScheme(
    primary = monoprimaryDark,
    onPrimary = monoonPrimaryDark,
    primaryContainer = monoprimaryContainerDark,
    onPrimaryContainer = monoonPrimaryContainerDark,
    secondary = monosecondaryDark,
    onSecondary = monoonSecondaryDark,
    secondaryContainer = monosecondaryContainerDark,
    onSecondaryContainer = monoonSecondaryContainerDark,
    tertiary = monotertiaryDark,
    onTertiary = monoonTertiaryDark,
    tertiaryContainer = monotertiaryContainerDark,
    onTertiaryContainer = monoonTertiaryContainerDark,
    error = monoerrorDark,
    onError = monoonErrorDark,
    errorContainer = monoerrorContainerDark,
    onErrorContainer = monoonErrorContainerDark,
    background = monobackgroundDark,
    onBackground = monoonBackgroundDark,
    surface = monosurfaceDark,
    onSurface = monoonSurfaceDark,
    surfaceVariant = monosurfaceVariantDark,
    onSurfaceVariant = monoonSurfaceVariantDark,
    outline = monooutlineDark,
    outlineVariant = monooutlineVariantDark,
    scrim = monoscrimDark,
    inverseSurface = monoinverseSurfaceDark,
    inverseOnSurface = monoinverseOnSurfaceDark,
    inversePrimary = monoinversePrimaryDark,
    surfaceDim = monosurfaceDimDark,
    surfaceBright = monosurfaceBrightDark,
    surfaceContainerLowest = monosurfaceContainerLowestDark,
    surfaceContainerLow = monosurfaceContainerLowDark,
    surfaceContainer = monosurfaceContainerDark,
    surfaceContainerHigh = monosurfaceContainerHighDark,
    surfaceContainerHighest = monosurfaceContainerHighestDark,
)
