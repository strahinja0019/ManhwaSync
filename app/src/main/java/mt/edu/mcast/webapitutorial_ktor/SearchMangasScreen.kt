package mt.edu.mcast.webapitutorial_ktor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mt.edu.mcast.webapitutorial_ktor.openlibrary.OpenLibraryRepository

@Composable
fun SearchMangasScreen(
    modifier: Modifier = Modifier,
    repository: OpenLibraryRepository,
    savedMangaList: List<MangaUI>,
    onOpenMihon: (String) -> Unit,
    onToggleFavorite: (MangaUI) -> Unit
) {
    var mangaList by remember { mutableStateOf<List<MangaUI>>(emptyList()) }
    var query by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isOnCooldown by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    suspend fun loadBooks(searchQuery: String) {
        val result = repository.getBooksByTitle(searchQuery)
        result.fold(
            onSuccess = { data -> mangaList = data },
            onFailure = { mangaList = emptyList() }
        )
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
            // Inside LazyColumn items block of SearchMangasScreen.kt
            items(
                items = mangaList,
                key = { it.id ?: it.title ?: it.hashCode() }
            ) { manga ->
                val isFavorite = savedMangaList.any { it.id == manga.id }

                MangaSwipeItem(
                    mangaItem = manga,
                    isFavorite = isFavorite,
                    onToggleFavorite = { onToggleFavorite(manga) },
                    onItemClick = {manga.id?.let { id -> onOpenMihon(id) } },
                    showChapterCount = false // Do not display chapter numbers on Search layout rows
                )
            }
        }
    }
}