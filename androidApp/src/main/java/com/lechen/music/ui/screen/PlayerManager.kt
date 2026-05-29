package com.lechen.music.ui.screen

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.lechen.music.model.Child
import com.lechen.music.model.PlayMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PlayerManager(private val context: Context) {
    private var exoPlayer: ExoPlayer? = null

    private val _currentSong = MutableStateFlow<Child?>(null)
    val currentSong: StateFlow<Child?> = _currentSong

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration

    private var currentPlaylist: List<Child> = emptyList()

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_READY) {
                _duration.value = exoPlayer?.duration ?: 0
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            // Update current song based on media item index
            val index = exoPlayer?.currentMediaItemIndex ?: 0
            if (index in currentPlaylist.indices) {
                _currentSong.value = currentPlaylist[index]
            }
        }
    }

    private fun ensurePlayer(): ExoPlayer {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                addListener(playerListener)
            }
        }
        return exoPlayer!!
    }

    fun playSong(song: Child, playlist: List<Child>) {
        currentPlaylist = playlist
        val player = ensurePlayer()

        val mediaItems = playlist.map { s ->
            MediaItem.Builder()
                .setUri(getStreamUrl(s.id))
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(s.title)
                        .setArtist(s.artist ?: "")
                        .setAlbumTitle(s.album ?: "")
                        .build()
                )
                .build()
        }

        player.setMediaItems(mediaItems)
        val index = playlist.indexOf(song)
        if (index >= 0) {
            player.seekTo(index, 0)
        }
        player.prepare()
        player.play()

        _currentSong.value = song
        startPositionUpdate()
    }

    fun togglePlayPause() {
        val player = exoPlayer ?: return
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    fun nextSong(playlist: List<Child>, playMode: PlayMode) {
        val player = exoPlayer ?: return
        when (playMode) {
            PlayMode.SHUFFLE -> {
                val randomIndex = (playlist.indices).random()
                player.seekTo(randomIndex, 0)
                player.play()
            }
            PlayMode.REPEAT_ONE -> {
                player.seekTo(player.currentMediaItemIndex, 0)
                player.play()
            }
            PlayMode.SEQUENTIAL, PlayMode.REPEAT_ALL -> {
                if (player.hasNextMediaItem()) {
                    player.seekToNext()
                } else if (playMode == PlayMode.REPEAT_ALL && playlist.isNotEmpty()) {
                    player.seekTo(0, 0)
                }
                player.play()
            }
        }
    }

    fun previousSong(playlist: List<Child>, playMode: PlayMode) {
        val player = exoPlayer ?: return
        if (player.currentPosition > 3000) {
            player.seekTo(0)
        } else if (player.hasPreviousMediaItem()) {
            player.seekToPrevious()
        }
        player.play()
    }

    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
    }

    private fun startPositionUpdate() {
        // In a real app, use a coroutine or handler to update position periodically
        Thread {
            while (exoPlayer != null && exoPlayer!!.isPlaying) {
                _currentPosition.value = exoPlayer?.currentPosition ?: 0
                try { Thread.sleep(500) } catch (_: Exception) { break }
            }
        }.start()
    }

    private fun getStreamUrl(songId: String): String {
        // This should use the SubsonicClient's getStreamUrl
        // For now return a placeholder - the actual URL will be constructed by the repository
        return "stream_url_for_$songId"
    }

    fun release() {
        exoPlayer?.removeListener(playerListener)
        exoPlayer?.release()
        exoPlayer = null
    }
}
