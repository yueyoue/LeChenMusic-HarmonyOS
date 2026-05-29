package com.lechen.music.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    navController: NavController,
    viewModel: MainViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val song = uiState.currentSong

    var showPlaylist by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showSleepTimer by remember { mutableStateOf(false) }
    var currentView by remember { mutableIntStateOf(0) } // -1=lyrics, 0=cover, 1=similar

    // Load lyrics and similar songs when song changes
    LaunchedEffect(song?.id) {
        song?.let {
            viewModel.loadLyrics(it.artist ?: "", it.title)
            viewModel.loadSimilarSongs(it.id)
        }
    }

    if (song == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text("未在播放", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("正在播放") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showMoreMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "更多")
                    }
                    DropdownMenu(
                        expanded = showMoreMenu,
                        onDismissRequest = { showMoreMenu = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("添加到歌单") },
                            onClick = {
                                showMoreMenu = false
                                // Show playlist selection dialog
                            },
                            leadingIcon = { Icon(Icons.Default.PlaylistAdd, null) },
                        )
                        DropdownMenuItem(
                            text = { Text("定时停止") },
                            onClick = {
                                showMoreMenu = false
                                showSleepTimer = true
                            },
                            leadingIcon = { Icon(Icons.Default.Timer, null) },
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Cover art area with swipe
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { _, dragAmount ->
                            if (dragAmount < -50) {
                                // Swipe left -> similar songs
                                currentView = 1
                            } else if (dragAmount > 50) {
                                // Swipe right -> lyrics
                                currentView = -1
                            }
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                when (currentView) {
                    -1 -> {
                        // Lyrics view
                        Card(
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(24.dp),
                                contentAlignment = Alignment.TopCenter,
                            ) {
                                Text(
                                    text = uiState.lyrics ?: "暂无歌词",
                                    style = MaterialTheme.typography.bodyLarge,
                                    lineHeight = 28.sp,
                                )
                            }
                        }
                    }
                    0 -> {
                        // Cover art
                        Card(
                            modifier = Modifier
                                .size(300.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(8.dp),
                        ) {
                            AsyncImage(
                                model = viewModel.getCoverArtUrl(song.coverArt ?: song.id),
                                contentDescription = song.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }
                    1 -> {
                        // Similar songs
                        Card(
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(16.dp),
                            ) {
                                Text(
                                    "相似歌曲",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                if (uiState.similarSongs.isEmpty()) {
                                    Text("暂无推荐", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                } else {
                                    uiState.similarSongs.take(10).forEach { s ->
                                        ListItem(
                                            headlineContent = { Text(s.title, maxLines = 1) },
                                            supportingContent = { Text(s.artist ?: "", maxLines = 1) },
                                            modifier = Modifier.clickable {
                                                viewModel.playSong(s, uiState.similarSongs)
                                                currentView = 0
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Song info + favorite
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${song.artist ?: "未知歌手"} · ${song.album ?: "未知专辑"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                IconButton(onClick = { viewModel.toggleStar(song.id) }) {
                    Icon(
                        if (uiState.isCurrentSongStarred) Icons.Default.Favorite
                        else Icons.Default.FavoriteBorder,
                        contentDescription = "收藏",
                        tint = if (uiState.isCurrentSongStarred) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar
            val duration = uiState.duration.coerceAtLeast(1)
            Slider(
                value = uiState.currentPosition.toFloat() / duration.toFloat(),
                onValueChange = { viewModel.seekTo((it * duration).toLong()) },
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    formatTime(uiState.currentPosition),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    formatTime(uiState.duration),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Play mode
                IconButton(onClick = {
                    val nextMode = when (uiState.playMode) {
                        PlayMode.SEQUENTIAL -> PlayMode.SHUFFLE
                        PlayMode.SHUFFLE -> PlayMode.REPEAT_ONE
                        PlayMode.REPEAT_ONE -> PlayMode.REPEAT_ALL
                        PlayMode.REPEAT_ALL -> PlayMode.SEQUENTIAL
                    }
                    viewModel.setPlayMode(nextMode)
                }) {
                    Icon(
                        when (uiState.playMode) {
                            PlayMode.SEQUENTIAL -> Icons.Default.Repeat
                            PlayMode.SHUFFLE -> Icons.Default.Shuffle
                            PlayMode.REPEAT_ONE -> Icons.Default.RepeatOne
                            PlayMode.REPEAT_ALL -> Icons.Default.Repeat
                        },
                        contentDescription = "播放模式",
                        tint = if (uiState.playMode != PlayMode.SEQUENTIAL)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // Previous
                IconButton(onClick = { viewModel.previousSong() }) {
                    Icon(
                        Icons.Default.SkipPrevious,
                        contentDescription = "上一曲",
                        modifier = Modifier.size(36.dp),
                    )
                }

                // Play/Pause
                FilledIconButton(
                    onClick = { viewModel.togglePlayPause() },
                    modifier = Modifier.size(64.dp),
                    shape = CircleShape,
                ) {
                    Icon(
                        if (uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (uiState.isPlaying) "暂停" else "播放",
                        modifier = Modifier.size(36.dp),
                    )
                }

                // Next
                IconButton(onClick = { viewModel.nextSong() }) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = "下一曲",
                        modifier = Modifier.size(36.dp),
                    )
                }

                // Playlist
                IconButton(onClick = { showPlaylist = true }) {
                    Icon(
                        Icons.Default.QueueMusic,
                        contentDescription = "播放列表",
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Sleep timer dialog
    if (showSleepTimer) {
        AlertDialog(
            onDismissRequest = { showSleepTimer = false },
            title = { Text("定时停止") },
            text = {
                Column {
                    listOf("15分钟", "30分钟", "1小时", "2小时").forEach { label ->
                        TextButton(
                            onClick = { showSleepTimer = false },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(label)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSleepTimer = false }) {
                    Text("取消")
                }
            },
        )
    }

    // Playlist bottom sheet
    if (showPlaylist) {
        ModalBottomSheet(onDismissRequest = { showPlaylist = false }) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "播放列表 (${uiState.playlist.size}首)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                uiState.playlist.forEachIndexed { index, s ->
                    ListItem(
                        headlineContent = {
                            Text(
                                s.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = if (s.id == song.id) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface,
                            )
                        },
                        supportingContent = { Text(s.artist ?: "", maxLines = 1) },
                        trailingContent = {
                            if (s.id == song.id) {
                                Icon(
                                    Icons.Default.Equalizer,
                                    null,
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        },
                        modifier = Modifier.clickable {
                            viewModel.playSong(s, uiState.playlist)
                            showPlaylist = false
                        },
                    )
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

private fun Modifier.clickable(onClick: () -> Unit): Modifier {
    return this.then(
        Modifier.pointerInput(Unit) {
            detectHorizontalDragGestures { _, _ -> }
        }
    )
}
