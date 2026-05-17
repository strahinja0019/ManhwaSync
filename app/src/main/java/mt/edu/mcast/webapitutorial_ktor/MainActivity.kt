package mt.edu.mcast.webapitutorial_ktor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
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
                    Greeting(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Greeting(
    modifier: Modifier = Modifier,
    repository: OpenLibraryRepository = remember { OpenLibraryRepository() }
) {

    var mangaList by remember { mutableStateOf<List<MangaUI>>(emptyList()) }
    var query by remember { mutableStateOf("") }
    var debouncedQuery by remember { mutableStateOf("") }
    var debounceJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isOnCooldown by remember { mutableStateOf(false) }

    var scope = rememberCoroutineScope()

    suspend fun loadBooks(searchQuery: String) {
        val result = repository.getBooksByTitle(searchQuery)

        result.fold(
            onSuccess = { data ->
                mangaList = data
            },
            onFailure = {
                mangaList = emptyList()
            }
        )
    }

    LaunchedEffect(query) {
        debounceJob?.cancel()
        debounceJob = scope.launch {
            kotlinx.coroutines.delay(500)
            debouncedQuery = query
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 50.dp, start = 10.dp, end = 10.dp)
    ) {

        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search Book by title") },
                placeholder = { Text("Book Title") },
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        loadBooks(query)
                        isLoading = false

                        // Start cooldown after search
                        isOnCooldown = true
                        kotlinx.coroutines.delay(500)  // 500ms cooldown
                        isOnCooldown = false
                    }
                },
                enabled = !isLoading && query.isNotEmpty() && !isOnCooldown,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = if (isOnCooldown) androidx.compose.ui.graphics.Color.Black else androidx.compose.material3.MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (isLoading) "Searching..." else if (isOnCooldown) "Cooldown..." else "Search")
            }

        }

        LazyColumn {
            items(mangaList) { manga ->
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Row {
                        GlideImage(
                            modifier = Modifier.size(85.dp),
                            model = manga.coverUrl,
                            contentDescription = "Book Cover",
                            loading = placeholder { CircularProgressIndicator() },
                            contentScale = ContentScale.Crop
                        )

                        Text(
                            modifier = Modifier.padding(5.dp),
                            text = manga.title ?: "No title"
                        )
                    }
                }
            }
        }
    }
}
