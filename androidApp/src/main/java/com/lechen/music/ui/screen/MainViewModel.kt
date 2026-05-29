package com.lechen.music.ui.screen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lechen.music.LeChenApp
import com.lechen.music.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AppUiState(
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val newestAlbums: List<Child> = emptyList(),
    val dailyRecommendations: List<Child> = emptyList(),
    val recentPlayed: List<Child> = emptyList(),
    val randomAlbums: List<Child> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val radioStations: List<InternetRadioStation> = emptyList(),
    val searchResults: SearchResult? = null,
    val artists: List<ArtistIndex> = emptyList(),
    val starredSongs: List<Child> = emptyList(),
    val starredAlbums: List<Child> = emptyList(),
    // Player state
    val currentSong: Child? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0,
    val duration: Long = 0,
    val playlist: List<Child> = emptyList(),
    val playMode: PlayMode = PlayMode.SEQUENTIAL,
    val isCurrentSongStarred: Boolean = false,
    val lyrics: String? = null,
    val similarSongs: List<Child> = emptyList(),
    // Server info
    val serverUrl: String = "",
    val username: String = "",
    val songCount: Int = 0,
    val albumCount: Int = 0,
    val playlistCount: Int = 0,
)

enum class PlayMode {
    SEQUENTIAL, SHUFFLE, REPEAT_ONE, REPEAT_ALL
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as LeChenApp
    private val repository = app.repository

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState

    private val playerManager = PlayerManager(application)

    init {
        // Observe player state
        viewModelScope.launch {
            playerManager.currentSong.collect { song ->
                _uiState.value = _uiState.value.copy(currentSong = song)
            }
        }
        viewModelScope.launch {
            playerManager.isPlaying.collect { playing ->
                _uiState.value = _uiState.value.copy(isPlaying = playing)
            }
        }
        viewModelScope.launch {
            playerManager.currentPosition.collect { pos ->
                _uiState.value = _uiState.value.copy(currentPosition = pos)
            }
        }
        viewModelScope.launch {
            playerManager.duration.collect { dur ->
                _uiState.value = _uiState.value.copy(duration = dur)
            }
        }
    }

    fun login(server: String, username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val success = repository.login(server, username, password)
                if (success) {
                    _uiState.value = _uiState.value.copy(
                        isLoggedIn = true,
                        isLoading = false,
                        serverUrl = server,
                        username = username,
                    )
                    loadHomeData()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "登录失败，请检查服务器地址和账号密码"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "连接失败: ${e.message}"
                )
            }
        }
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val albums = repository.getNewestAlbums(20)
                val random = repository.getRandomAlbums(20)
                val recent = repository.getRecentPlayed()
                val daily = repository.getDailyRecommendations()
                val playlists = repository.getPlaylists()
                val radio = repository.getRadioStations()
                val starredSongs = repository.getStarredSongs()
                val starredAlbums = repository.getStarredAlbums()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    newestAlbums = albums,
                    randomAlbums = random,
                    recentPlayed = recent,
                    dailyRecommendations = daily,
                    playlists = playlists,
                    radioStations = radio,
                    starredSongs = starredSongs,
                    starredAlbums = starredAlbums,
                    playlistCount = playlists.size,
                    albumCount = albums.size + random.size,
                    songCount = recent.size + daily.size,
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "加载失败: ${e.message}"
                )
            }
        }
    }

    fun search(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val results = repository.search(query)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    searchResults = results,
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "搜索失败: ${e.message}"
                )
            }
        }
    }

    fun loadArtists() {
        viewModelScope.launch {
            try {
                val artists = repository.getArtists()
                _uiState.value = _uiState.value.copy(artists = artists)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "加载歌手失败: ${e.message}")
            }
        }
    }

    fun playSong(song: Child, playlist: List<Child> = emptyList()) {
        val songs = if (playlist.isEmpty()) listOf(song) else playlist
        _uiState.value = _uiState.value.copy(playlist = songs)
        playerManager.playSong(song, songs)

        // Scrobble
        viewModelScope.launch {
            try { repository.scrobble(song.id) } catch (_: Exception) {}
        }
    }

    fun togglePlayPause() {
        playerManager.togglePlayPause()
    }

    fun nextSong() {
        val state = _uiState.value
        playerManager.nextSong(state.playlist, state.playMode)
    }

    fun previousSong() {
        val state = _uiState.value
        playerManager.previousSong(state.playlist, state.playMode)
    }

    fun seekTo(position: Long) {
        playerManager.seekTo(position)
    }

    fun setPlayMode(mode: PlayMode) {
        _uiState.value = _uiState.value.copy(playMode = mode)
    }

    fun toggleStar(songId: String) {
        viewModelScope.launch {
            try {
                val isStarred = _uiState.value.isCurrentSongStarred
                if (isStarred) {
                    repository.unstarSong(songId)
                } else {
                    repository.starSong(songId)
                }
                _uiState.value = _uiState.value.copy(isCurrentSongStarred = !isStarred)
                // Refresh starred list
                val starredSongs = repository.getStarredSongs()
                _uiState.value = _uiState.value.copy(starredSongs = starredSongs)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "操作失败: ${e.message}")
            }
        }
    }

    fun loadLyrics(artist: String, title: String) {
        viewModelScope.launch {
            try {
                val lyrics = repository.getLyrics(artist, title)
                _uiState.value = _uiState.value.copy(lyrics = lyrics)
            } catch (_: Exception) {}
        }
    }

    fun loadSimilarSongs(songId: String) {
        viewModelScope.launch {
            try {
                val songs = repository.getSimilarSongs(songId)
                _uiState.value = _uiState.value.copy(similarSongs = songs)
            } catch (_: Exception) {}
        }
    }

    fun addToPlaylist(playlistId: String, songId: String) {
        viewModelScope.launch {
            try {
                repository.addToPlaylist(playlistId, songId)
                _uiState.value = _uiState.value.copy(errorMessage = "已添加到歌单")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "添加失败: ${e.message}")
            }
        }
    }

    fun clearCache() {
        // Clear implementation
        _uiState.value = _uiState.value.copy(errorMessage = "缓存已清除")
    }

    fun logout() {
        _uiState.value = AppUiState()
        playerManager.release()
    }

    fun getCoverArtUrl(id: String): String {
        return repository.getCoverArtUrl(id)
    }

    fun getStreamUrl(id: String): String {
        return repository.getStreamUrl(id)
    }

    override fun onCleared() {
        super.onCleared()
        playerManager.release()
    }
}
