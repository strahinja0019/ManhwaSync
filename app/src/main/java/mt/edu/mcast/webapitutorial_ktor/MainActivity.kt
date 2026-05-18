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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import mt.edu.mcast.webapitutorial_ktor.openlibrary.OpenLibraryRepository
import mt.edu.mcast.webapitutorial_ktor.ui.theme.WebAPITutorial_KtorTheme

data class MangaUI(
    val id: String?,
    val title: String?,
    val coverUrl: String?
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WebAPITutorial_KtorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MangaSearchAndFavoriteScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MangaSearchAndFavoriteScreen(
    modifier: Modifier = Modifier,
    repository: OpenLibraryRepository = remember { OpenLibraryRepository() }
) {
    var mangaList by remember { mutableStateOf<List<MangaUI>>(emptyList()) }
    var query by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isOnCooldown by remember { mutableStateOf(false) }

    // Track favorites using manga IDs
    val favorites = remember { mutableStateListOf<String>() }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    suspend fun loadBooks(searchQuery: String) {
        val result = repository.getBooksByTitle(searchQuery)
        result.fold(
            onSuccess = { data -> mangaList = data },
            onFailure = { mangaList = emptyList() }
        )
    }

    fun openMihon(mangaId: String) {
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 10.dp, end = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search Book by title") },
                placeholder = { Text("Book Title") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        loadBooks(query)
                        isLoading = false

                        isOnCooldown = true
                        delay(500)
                        isOnCooldown = false
                    }
                },
                enabled = !isLoading && query.isNotEmpty() && !isOnCooldown,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = if (isOnCooldown) Color.Black else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (isLoading) "Searching..." else if (isOnCooldown) "Cooldown..." else "Search")
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = mangaList,
                key = { it.id ?: it.title ?: it.hashCode() }
            ) { manga ->
                val isFavorite = favorites.contains(manga.id)

                MangaSwipeItem(
                    mangaItem = manga,
                    isFavorite = isFavorite,
                    onToggleFavorite = { currentIsFavorite ->
                        manga.id?.let { id ->
                            if (currentIsFavorite) {
                                favorites.remove(id)
                            } else {
                                favorites.add(id)
                            }
                        }
                    },
                    onItemClick = {
                        manga.id?.let { id -> openMihon(id) }
                    }
                )
            }
        }
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
            // 1. Dynamically select background color based on favorite state
            val backgroundColor = if (isFavorite) {
                // If already favorited, action will remove it -> Show a subtle dark/red tinted slate gray
                Color.hsv(hue = 0f, saturation = 0.4f, value = 0.4f)
            } else {
                // If not favorited, action adds it -> Show your custom magenta HSV color
                Color.hsv(hue = 302f, saturation = 0.681f, value = 0.812f)
            }

            // 2. Dynamically select the drawable asset icon
            val iconResource = if (isFavorite) {
                R.drawable.ic_unfavourite
            } else {
                R.drawable.ic_favourite
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor, shape = RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    painter = painterResource(id = iconResource),
                    contentDescription = if (isFavorite) "Remove Highlight" else "Toggle Highlight",
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
            border = if (isFavorite) {
                BorderStroke(4.dp, Color.Magenta)
            } else {
                BorderStroke(0.dp, Color.Transparent)
            },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
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