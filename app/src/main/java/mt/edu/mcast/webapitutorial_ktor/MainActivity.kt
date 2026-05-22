package mt.edu.mcast.webapitutorial_ktor

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.core.content.ContextCompat
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import mt.edu.mcast.webapitutorial_ktor.openlibrary.MangaPersistence
import mt.edu.mcast.webapitutorial_ktor.openlibrary.OpenLibraryRepository
import mt.edu.mcast.webapitutorial_ktor.ui.theme.AppTheme
import mt.edu.mcast.webapitutorial_ktor.ui.theme.WebAPITutorial_KtorTheme
import mt.edu.mcast.webapitutorial_ktor.ui.theme.getPrimaryColor
import java.util.Calendar

@Serializable
data class MangaUI(
    val id: String?,
    val title: String?,
    val coverUrl: String?,
    val chapterCount: Int? = null // Room will inject this value into your Saved Screen layout card
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
    // Inside MainActivity.kt -> Update onCreate to initialize Room
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)

        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { _ -> }
        checkFirstTimePermissionLaunch(requestPermissionLauncher)

        setDailyAlarm(applicationContext)

        // INITIALIZE ROOM DB & REPOSITORY ENGINE HERE:
        val database =
            mt.edu.mcast.webapitutorial_ktor.database.AppDatabase.getDatabase(applicationContext)
        val repository = OpenLibraryRepository(database.mangaDao())

        setContent {
            val currentTheme by MangaPersistence.getTheme(this)
                .collectAsState(initial = AppTheme.OCEAN)

            WebAPITutorial_KtorTheme(appTheme = currentTheme) {
                MangaApp(currentTheme = currentTheme, repository = repository)
            }
        }
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val infoChannel = NotificationChannel(
                "info_channel",
                "Information Channel",
                NotificationManager.IMPORTANCE_HIGH,
            )
            notificationManager.createNotificationChannel(infoChannel)
        }
    }

    private fun checkFirstTimePermissionLaunch(launcher: androidx.activity.result.ActivityResultLauncher<String>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val sharedPreferences: SharedPreferences =
                getSharedPreferences("AppPrefs", MODE_PRIVATE)
            val isFirstRun = sharedPreferences.getBoolean("isFirstPermissionRun", true)
            if (isFirstRun) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                sharedPreferences.edit().putBoolean("isFirstPermissionRun", false).apply()
            }
        }
    }

    companion object {
        fun setDailyAlarm(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val customAlarmAction = "${context.packageName}.ACTION_DAILY_ALARM"
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                action = customAlarmAction
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                1001,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 11)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }
}

@Composable
fun MangaApp(
    currentTheme: AppTheme,
    modifier: Modifier = Modifier,
    repository: OpenLibraryRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val startDestination = Destination.SEARCH
    var selectedDestination by rememberSaveable { mutableStateOf(startDestination) }

    // REAL-TIME ROOM COUPLING STREAM:
    val savedMangaList by repository.savedMangasStream.collectAsState(initial = emptyList())

    val toggleFavorite = { manga: MangaUI ->
        scope.launch {
            val isAlreadyFavorite = savedMangaList.any { it.id == manga.id }
            if (isAlreadyFavorite) {
                manga.id?.let { repository.removeFromFavorites(context, it) } // Pass context here
            } else {
                repository.saveToFavorites(context, manga) // Pass context here
            }
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
                SettingsScreen(
                    modifier = Modifier.padding(contentPadding),
                    currentTheme = currentTheme,
                    onThemeChange = { theme ->
                        scope.launch {
                            MangaPersistence.saveTheme(context, theme)
                        }
                    }
                )
            }
        }
    }
}

private fun openMihon(context: Context, mangaId: String) {
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
    onItemClick: () -> Unit,
    showChapterCount: Boolean = false,
    trailingContent: (@Composable () -> Unit)? = null // optional slot
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
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.primary
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor, shape = RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    painter = painterResource(id = if (isFavorite) R.drawable.ic_unfavourite else R.drawable.ic_favourite),
                    contentDescription = if (isFavorite) "Remove from saved" else "Save",
                    tint = MaterialTheme.colorScheme.secondaryContainer
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onItemClick() },
            border = if (isFavorite) BorderStroke(4.dp, MaterialTheme.colorScheme.primary) else BorderStroke(
                0.dp,
                Color.Transparent
            ),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            )
            {
                remember(mangaItem.coverUrl) {
                    movableContentOf {
                        GlideImage(
                            modifier = Modifier.size(85.dp),
                            model = mangaItem.coverUrl,
                            contentDescription = "Book Cover",
                            loading = placeholder {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(
                                        24.dp
                                    )
                                )
                            },
                            contentScale = ContentScale.Crop
                        ) {
                            it.diskCacheStrategy(DiskCacheStrategy.ALL)
                                .dontAnimate()
                                .dontTransform()
                        }
                    }
                }.invoke()
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mangaItem.title ?: "No title",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )

                    // Conditionally display chapter metric rows on the Saved Screen
                    if (showChapterCount) {
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = "Chapters: ${mangaItem.chapterCount ?: "Unknown"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (trailingContent != null) {
                Box(modifier = Modifier.padding(start = 8.dp)) {
                    trailingContent()
                }

            }
        }
    }
}