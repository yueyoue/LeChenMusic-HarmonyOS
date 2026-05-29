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
import androidx.compose.ui.Alignment
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
fun SearchScreen(
    navController: NavController,
    viewModel: MainViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("搜索") })
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("搜索歌曲、歌手、专辑") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Clear, null)
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )

            // Search button
            if (query.isNotBlank()) {
                Button(
                    onClick = { viewModel.search(query) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    Text("搜索")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val results = uiState.searchResults
                if (results != null) {
                    LazyColumn {
                        // Artists
                        if (results.artist.isNotEmpty()) {
                            item {
                                Text(
                                    "歌手",
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                )
                            }
                            items(results.artist) { artist ->
                                ListItem(
                                    headlineContent = { Text(artist.name) },
                                    supportingContent = { Text("${artist.albumCount} 张专辑") },
                                    leadingContent = {
                                        Icon(Icons.Default.Person, null)
                                    },
                                )
                            }
                        }

                        // Albums
                        if (results.album.isNotEmpty()) {
                            item {
                                Text(
                                    "专辑",
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                )
                            }
                            items(results.album) { album ->
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

                        // Songs
                        if (results.song.isNotEmpty()) {
                            item {
                                Text(
                                    "歌曲",
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                )
                            }
                            items(results.song) { song ->
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
                                    modifier = Modifier.clickable {
                                        viewModel.playSong(song, results.song)
                                        navController.navigate(Screen.Player.route)
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
