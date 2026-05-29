package com.lechen.music.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistsScreen(
    navController: NavController,
    viewModel: MainViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        if (uiState.artists.isEmpty()) {
            viewModel.loadArtists()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("歌手") })
        },
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
            ) {
                uiState.artists.forEach { index ->
                    item {
                        Text(
                            text = index.name,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                    }
                    items(index.artist) { artist ->
                        ListItem(
                            headlineContent = { Text(artist.name) },
                            supportingContent = { Text("${artist.albumCount} 张专辑") },
                            leadingContent = {
                                Icon(Icons.Default.Person, null)
                            },
                            modifier = Modifier.clickable {
                                // Navigate to artist detail
                            },
                        )
                    }
                }
            }
        }
    }
}
