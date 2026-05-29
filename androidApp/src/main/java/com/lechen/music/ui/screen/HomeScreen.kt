package com.lechen.music.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.lechen.music.model.Child
import com.lechen.music.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MainViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        if (uiState.newestAlbums.isEmpty() && !uiState.isLoading) {
            viewModel.loadHomeData()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("乐尘音乐") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Default.Settings, contentDescription = "设置")
                    }
                },
            )
        },
    ) { padding ->
        if (uiState.isLoading && uiState.newestAlbums.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 16.dp),
            ) {
                // Search bar
                item {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text("搜索歌曲、歌手、专辑") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clickable { navController.navigate(Screen.Search.route) },
                        enabled = false,
                    )
                }

                // 每日推荐
                if (uiState.dailyRecommendations.isNotEmpty()) {
                    item {
                        SectionTitle("🎯 每日推荐")
                    }
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(uiState.dailyRecommendations.take(10)) { song ->
                                SongCard(song, viewModel) { viewModel.playSong(song, uiState.dailyRecommendations) }
                            }
                        }
                    }
                }

                // 最新专辑
                if (uiState.newestAlbums.isNotEmpty()) {
                    item {
                        SectionTitle("🆕 最新专辑")
                    }
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(uiState.newestAlbums) { album ->
                                AlbumCard(album, viewModel)
                            }
                        }
                    }
                }

                // 最近播放
                if (uiState.recentPlayed.isNotEmpty()) {
                    item {
                        SectionTitle("⏱ 最近播放")
                    }
                    items(uiState.recentPlayed.take(10)) { song ->
                        SongListItem(song, viewModel) { viewModel.playSong(song, uiState.recentPlayed) }
                    }
                }

                // 随机专辑
                if (uiState.randomAlbums.isNotEmpty()) {
                    item {
                        SectionTitle("🎲 随机专辑")
                    }
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(uiState.randomAlbums) { album ->
                                AlbumCard(album, viewModel)
                            }
                        }
                    }
                }

                // 歌单
                if (uiState.playlists.isNotEmpty()) {
                    item {
                        SectionTitle("📋 歌单")
                    }
                    items(uiState.playlists) { playlist ->
                        ListItem(
                            headlineContent = { Text(playlist.name) },
                            supportingContent = { Text("${playlist.songCount} 首歌曲") },
                            leadingContent = {
                                Icon(Icons.Default.QueueMusic, null)
                            },
                            modifier = Modifier.clickable {
                                // Navigate to playlist detail
                            },
                        )
                    }
                }

                // 电台
                if (uiState.radioStations.isNotEmpty()) {
                    item {
                        SectionTitle("📻 电台")
                    }
                    items(uiState.radioStations) { station ->
                        ListItem(
                            headlineContent = { Text(station.name) },
                            supportingContent = { station.description?.let { Text(it) } },
                            leadingContent = {
                                Icon(Icons.Default.Radio, null)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
    )
}

@Composable
fun SongCard(
    song: Child,
    viewModel: MainViewModel,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column {
            AsyncImage(
                model = viewModel.getCoverArtUrl(song.coverArt ?: song.id),
                contentDescription = song.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop,
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = song.artist ?: "未知歌手",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
fun AlbumCard(
    album: Child,
    viewModel: MainViewModel,
) {
    Card(
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column {
            AsyncImage(
                model = viewModel.getCoverArtUrl(album.coverArt ?: album.id),
                contentDescription = album.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop,
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = album.title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = album.artist ?: "未知歌手",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
fun SongListItem(
    song: Child,
    viewModel: MainViewModel,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = {
            Text(song.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        supportingContent = {
            Text(
                "${song.artist ?: "未知歌手"} - ${song.album ?: "未知专辑"}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        leadingContent = {
            AsyncImage(
                model = viewModel.getCoverArtUrl(song.coverArt ?: song.id),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
            )
        },
        trailingContent = {
            IconButton(onClick = { /* More options */ }) {
                Icon(Icons.Default.MoreVert, contentDescription = null)
            }
        },
        modifier = Modifier.clickable(onClick = onClick),
    )
}
