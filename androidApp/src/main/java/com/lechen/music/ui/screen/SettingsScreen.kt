package com.lechen.music.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lechen.music.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: MainViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedCacheSize by remember { mutableIntStateOf(2) } // GB

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            // 音乐缓存
            Text(
                "音乐缓存",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf(2, 4, 8, 16).forEach { size ->
                    FilterChip(
                        selected = selectedCacheSize == size,
                        onClick = { selectedCacheSize = size },
                        label = { Text("${size}GB") },
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // 存储空间管理
            Text(
                "存储空间管理",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            )

            ListItem(
                headlineContent = { Text("音乐文件缓存") },
                supportingContent = { Text("计算中...") },
                leadingContent = { Icon(Icons.Default.Folder, null) },
            )

            ListItem(
                headlineContent = { Text("其他数据") },
                supportingContent = { Text("计算中...") },
                leadingContent = { Icon(Icons.Default.Storage, null) },
            )

            OutlinedButton(
                onClick = { viewModel.clearCache() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("清除缓存")
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // 服务器信息
            Text(
                "服务器信息",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            )

            ListItem(
                headlineContent = { Text("服务器地址") },
                supportingContent = { Text(uiState.serverUrl) },
                leadingContent = { Icon(Icons.Default.Language, null) },
            )

            ListItem(
                headlineContent = { Text("用户名") },
                supportingContent = { Text(uiState.username) },
                leadingContent = { Icon(Icons.Default.Person, null) },
            )

            ListItem(
                headlineContent = { Text("歌曲数量") },
                supportingContent = { Text("${uiState.songCount}") },
                leadingContent = { Icon(Icons.Default.MusicNote, null) },
            )

            ListItem(
                headlineContent = { Text("专辑数量") },
                supportingContent = { Text("${uiState.albumCount}") },
                leadingContent = { Icon(Icons.Default.Album, null) },
            )

            ListItem(
                headlineContent = { Text("歌单数量") },
                supportingContent = { Text("${uiState.playlistCount}") },
                leadingContent = { Icon(Icons.Default.QueueMusic, null) },
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // 退出登录
            OutlinedButton(
                onClick = {
                    viewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                Icon(Icons.Default.Logout, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("退出登录")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
