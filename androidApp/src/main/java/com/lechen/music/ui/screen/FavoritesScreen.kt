package com.lechen.music.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.lechen.music.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    navController: NavController,
    viewModel: MainViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("歌曲", "专辑")

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("收藏") })
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) },
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    if (uiState.starredSongs.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = androidx.compose.ui.Alignment.Center,
                        ) {
                            Text("暂无收藏歌曲", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn {
                            items(uiState.starredSongs) { song ->
                                ListItem(
                                    headlineContent = {
                                        Text(song.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    },
                                    supportingContent = {
                                        Text(
                                            "${song.artist ?: "未知歌手"} - ${song.album ?: ""}",
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
                                        IconButton(onClick = { viewModel.toggleStar(song.id) }) {
                                            Icon(Icons.Default.Favorite, null, tint = MaterialTheme.colorScheme.primary)
                                        }
                                    },
                                    modifier = Modifier.clickable {
                                        viewModel.playSong(song, uiState.starredSongs)
                                        navController.navigate(Screen.Player.route)
                                    },
                                )
                            }
                        }
                    }
                }
                1 -> {
                    if (uiState.starredAlbums.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = androidx.compose.ui.Alignment.Center,
                        ) {
                            Text("暂无收藏专辑", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn {
                            items(uiState.starredAlbums) { album ->
                                ListItem(
                                    headlineContent = { Text(album.title) },
                                    supportingContent = { Text(album.artist ?: "未知歌手") },
                                    leadingContent = {
                                        AsyncImage(
                                            model = viewModel.getCoverArtUrl(album.coverArt ?: album.id),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop,
                                        )
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
