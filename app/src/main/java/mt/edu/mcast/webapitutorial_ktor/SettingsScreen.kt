//SettingsScreen.kt
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import mt.edu.mcast.webapitutorial_ktor.ui.theme.AppTheme
import androidx.compose.foundation.lazy.items

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
        //testing exapndable card ExpandableCard("A","b")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(AppTheme.entries) { theme ->
                Button(
                    onClick = { onThemeChange(theme) },
                    border = if (theme == currentTheme) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                ) {
                    Text(theme.displayName)
                }
            }
        }

        }
}

@Composable
fun ExpandableCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    // 1. Local expansion state tracker
    var expanded by remember { mutableStateOf(false) }

    // 2. Smooth rotation animation for the chevron indicator
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "RotationAnimation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize( // 3. Animates card boundary transitions automatically
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            ),
        onClick = { expanded = !expanded } // Toggles state when clicking anywhere on the card
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Section: Displays Title and Toggle Arrow
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(
                    modifier = Modifier
                        .weight(1f)
                        .rotate(rotationState), // Rotates the button based on current state
                    onClick = { expanded = !expanded }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrowdropdown),
                        tint = Color.White,
                        contentDescription = "Drop-down Arrow"
                    )
                }
            }

            // Expandable Content Section
            if (expanded) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
