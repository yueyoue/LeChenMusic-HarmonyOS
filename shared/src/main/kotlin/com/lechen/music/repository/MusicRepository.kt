package com.lechen.music.repository

import com.lechen.music.api.SubsonicClient
import com.lechen.music.model.*

class MusicRepository(private val client: SubsonicClient) {

    suspend fun login(server: String, username: String, password: String): Boolean {
        client.configure(server, username, password)
        return client.ping()
    }

    suspend fun getNewestAlbums(size: Int = 20): List<Child> {
        return client.getRecentAlbums(size)
    }

    suspend fun getRandomAlbums(size: Int = 20): List<Child> {
        return client.getRandomAlbums(size)
    }

    suspend fun getDailyRecommendations(): List<Child> {
        return try {
            val randomSongs = client.getAlbumList(type = "random", size = 30)
            if (randomSongs.isNotEmpty()) {
                randomSongs.shuffled().take(20)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRecentPlayed(): List<Child> {
        return try {
            client.getStarred()?.song ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun search(query: String): SearchResult {
        return client.search(query)
    }

    suspend fun getPlaylists(): List<Playlist> {
        return client.getPlaylists()
    }

    suspend fun getPlaylistDetail(id: String): Playlist? {
        return client.getPlaylist(id)
    }

    suspend fun getRadioStations(): List<InternetRadioStation> {
        return client.getInternetRadioStations()
    }

    suspend fun getAlbumDetail(id: String): Album? {
        return client.getAlbum(id)
    }

    suspend fun getArtists(): List<ArtistIndex> {
        return client.getArtists()
    }

    suspend fun getArtistDetail(id: String): Artist? {
        return client.getArtist(id)
    }

    suspend fun getStarredSongs(): List<Child> {
        return client.getStarred()?.song ?: emptyList()
    }

    suspend fun getStarredAlbums(): List<Child> {
        return client.getStarred()?.album ?: emptyList()
    }

    suspend fun starSong(id: String) {
        client.starSong(id)
    }

    suspend fun unstarSong(id: String) {
        client.unstarSong(id)
    }

    suspend fun getLyrics(artist: String, title: String): String? {
        return client.getLyrics(artist, title)?.value
    }

    suspend fun getSimilarSongs(id: String): List<Child> {
        return client.getSimilarSongs(id)
    }

    suspend fun scrobble(id: String) {
        client.scrobble(id)
    }

    suspend fun addToPlaylist(playlistId: String, songId: String) {
        client.addToPlaylist(playlistId, songId)
    }

    fun getStreamUrl(id: String, maxBitRate: Int? = null): String {
        return client.getStreamUrl(id, maxBitRate)
    }

    fun getCoverArtUrl(id: String, size: Int? = null): String {
        return client.getCoverArtUrl(id, size)
    }

    fun getServerUrl(): String = client.getServerUrl()
    fun getUsername(): String = client.getUsername()
}
