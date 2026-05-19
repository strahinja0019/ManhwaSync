package mt.edu.mcast.webapitutorial_ktor

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import mt.edu.mcast.webapitutorial_ktor.openlibrary.MangaPersistence
import mt.edu.mcast.webapitutorial_ktor.openlibrary.OpenLibraryRepository
import mt.edu.mcast.webapitutorial_ktor.ui.theme.WebAPITutorial_KtorTheme

@Serializable
data class MangaUI(
    val id: String?,
    val title: String?,
    val coverUrl: String?
)

enum class Destination(
    val iconRes: Int,
    val label: String,
    val contentDescription: String
) {
    SAVED(
        iconRes = R.drawable.ic_saved,
        label = "Saved",
        contentDescription = "Saved Mangas"
    ),
    SEARCH(
        iconRes = R.drawable.ic_search,
        label = "Search",
        contentDescription = "Search Mangas"
    ),
    SETTINGS(
        iconRes = R.drawable.ic_settings,
        label = "Settings",
        contentDescription = "Settings"
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WebAPITutorial_KtorTheme {
                MangaApp()
            }
        }
    }
}

@Composable
fun MangaApp(
    modifier: Modifier = Modifier,
    repository: OpenLibraryRepository = remember { OpenLibraryRepository() }
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val startDestination = Destination.SEARCH
    var selectedDestination by rememberSaveable { mutableStateOf(startDestination) }

    // Load persisted favorites from DataStore
    val savedMangaList by MangaPersistence.getMangaList(context).collectAsState(initial = emptyList())

    val toggleFavorite = { manga: MangaUI ->
        scope.launch {
            val updated = if (savedMangaList.any { it.id == manga.id }) {
                savedMangaList.filter { it.id != manga.id }
            } else {
                savedMangaList + manga
            }
            MangaPersistence.saveMangaList(context, updated)
        }
        Unit
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                Destination.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = selectedDestination == destination,
                        onClick = { selectedDestination = destination },
                        icon = {
                            Icon(
                                painter = painterResource(id = destination.iconRes),
                                contentDescription = destination.contentDescription,
                                tint = Color.White
                            )
                        },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { contentPadding ->
        when (selectedDestination) {
            Destination.SAVED -> {
                SavedMangasScreen(
                    savedMangaList = savedMangaList,
                    modifier = Modifier.padding(contentPadding),
                    onOpenMihon = { mangaId -> openMihon(context, mangaId) },
                    onRemove = { manga -> toggleFavorite(manga) }
                )
            }
            Destination.SEARCH -> {
                SearchMangasScreen(
                    repository = repository,
                    savedMangaList = savedMangaList,
                    modifier = Modifier.padding(contentPadding),
                    onOpenMihon = { mangaId -> openMihon(context, mangaId) },
                    onToggleFavorite = { manga -> toggleFavorite(manga) }
                )
            }
            Destination.SETTINGS -> {
                SettingsScreen(modifier = Modifier.padding(contentPadding))
            }
        }
    }
}

private fun openMihon(context: android.content.Context, mangaId: String) {
    try {
        val intent = Intent(Intent.ACTION_SEARCH).apply {
            `package` = "app.mihon"
            putExtra("query", "id:$mangaId")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Mihon not installed", Toast.LENGTH_SHORT).show()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun MangaSwipeItem(
    mangaItem: MangaUI,
    isFavorite: Boolean,
    onToggleFavorite: (isFavorite: Boolean) -> Unit,
    onItemClick: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { false },
        positionalThreshold = { totalDistance -> totalDistance * 0.7f }
    )

    var hasTriggeredAction by remember { mutableStateOf(false) }
    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(dismissState.progress) {
        if (!isInitialized) {
            isInitialized = true
            return@LaunchedEffect
        }
        if (dismissState.progress == 1f && !hasTriggeredAction) {
            onToggleFavorite(isFavorite)
            hasTriggeredAction = true
        } else if (dismissState.progress < 1f && hasTriggeredAction) {
            hasTriggeredAction = false
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            val backgroundColor = if (isFavorite) {
                Color.hsv(hue = 0f, saturation = 0.4f, value = 0.4f)
            } else {
                Color.hsv(hue = 302f, saturation = 0.681f, value = 0.812f)
            }
            val iconResource = if (isFavorite) R.drawable.ic_unfavourite else R.drawable.ic_favourite

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor, shape = RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    painter = painterResource(id = iconResource),
                    contentDescription = if (isFavorite) "Remove from saved" else "Save",
                    tint = Color.White
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onItemClick() },
            border = if (isFavorite) BorderStroke(4.dp, Color.Magenta) else BorderStroke(0.dp, Color.Transparent),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                remember(mangaItem.coverUrl) {
                    movableContentOf {
                        GlideImage(
                            modifier = Modifier.size(85.dp),
                            model = mangaItem.coverUrl,
                            contentDescription = "Book Cover",
                            loading = placeholder { CircularProgressIndicator(modifier = Modifier.size(24.dp)) },
                            contentScale = ContentScale.Crop
                        ) {
                            it.diskCacheStrategy(DiskCacheStrategy.ALL)
                                .dontAnimate()
                                .dontTransform()
                        }
                    }
                }.invoke()

                Text(
                    text = mangaItem.title ?: "No title",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}