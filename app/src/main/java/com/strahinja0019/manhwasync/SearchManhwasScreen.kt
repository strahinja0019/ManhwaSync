//SearchManhwasScreen.kt
package com.strahinja0019.manhwasync

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.strahinja0019.manhwasync.mangadex.MangaDexRepository

@Composable
fun SearchManhwasScreen(
    modifier: Modifier = Modifier,
    repository: MangaDexRepository,
    savedManhwaList: List<ManhwaUI>,
    onOpenMihon: (String) -> Unit,
    onToggleFavorite: (ManhwaUI) -> Unit,
    savingManhwaId: SnapshotStateList<String?>
) {
    var manhwaList by remember { mutableStateOf<List<ManhwaUI>>(emptyList()) }
    var query by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isOnCooldown by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    suspend fun loadManhwas(searchQuery: String) {
        val result = repository.getManhwasByTitle(searchQuery)
        result.fold(
            onSuccess = { data -> manhwaList = data },
            onFailure = { manhwaList = emptyList() }
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
                label = { Text("Search Manhwa by title") },
                placeholder = { Text("Manhwa Title") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        loadManhwas(query)
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
            // Inside LazyColumn items block of SearchManhwasScreen.kt
            items(
                items = manhwaList,
                key = { it.id ?: it.title ?: it.hashCode() }
            ) { manhwa ->
                val isFavorite = savedManhwaList.any { it.id == manhwa.id }

                val isThisSaving = savingManhwaId.contains(manhwa.id)
                ManhwaSwipeItem(
                    manhwaItem = manhwa,
                    isFavorite = isFavorite,
                    onToggleFavorite = { onToggleFavorite(manhwa) },
                    onItemClick = {manhwa.id?.let { id -> onOpenMihon(id) } },
                    showChapterCount = false // Do not display chapter numbers on Search layout rows
                ){
                    if (isThisSaving) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                    //the ManhwaSwipeItem has content parameter so just put the load bar here
                }
            }
        }
    }
}