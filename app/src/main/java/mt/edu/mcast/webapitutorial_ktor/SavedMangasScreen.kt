package mt.edu.mcast.webapitutorial_ktor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SavedMangasScreen(
    modifier: Modifier = Modifier,
    savedMangaList: List<MangaUI>,
    onOpenMihon: (String) -> Unit,
    onRemove: (MangaUI) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 10.dp, end = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Saved Mangas (${savedMangaList.size})",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        if (savedMangaList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No saved mangas yet", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = savedMangaList,
                    key = { it.id ?: it.hashCode() }
                ) { manga ->
                    MangaSwipeItem(
                        mangaItem = manga,
                        isFavorite = true,
                        onToggleFavorite = { onRemove(manga) },
                        onItemClick = { manga.id?.let { id -> onOpenMihon(id) } }
                    )
                }
            }
        }
    }
}