package com.lechen.music.api

import com.lechen.music.model.*
import com.lechen.music.util.SubsonicAuth
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class SubsonicClient(
    private var serverUrl: String = "",
    private var username: String = "",
    private var password: String = "",
) {
    private var salt: String = ""
    private var token: String = ""

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                coerceInputValues = true
            })
        }
        install(Logging) {
            level = LogLevel.NONE
        }
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    fun configure(server: String, user: String, pass: String) {
        serverUrl = server.trimEnd('/')
        username = user
        password = pass
        salt = SubsonicAuth.generateSalt()
        token = SubsonicAuth.generateToken(pass, salt)
    }

    private fun baseUrl(): String = "$serverUrl/rest"

    private fun commonParams(): Map<String, String> = mapOf(
        "u" to username,
        "t" to token,
        "s" to salt,
        "v" to "1.16.1",
        "c" to "LeChenMusic",
        "f" to "json",
    )

    private fun buildUrl(endpoint: String, params: Map<String, String> = emptyMap()): String {
        val urlBuilder = URLBuilder("${baseUrl()}/$endpoint")
        commonParams().forEach { (k, v) -> urlBuilder.parameters.append(k, v) }
        params.forEach { (k, v) -> urlBuilder.parameters.append(k, v) }
        return urlBuilder.buildString()
    }

    private suspend inline fun <reified T> get(endpoint: String, params: Map<String, String> = emptyMap()): T {
        val url = buildUrl(endpoint, params)
        val responseText = client.get(url).body<String>()
        val response = json.decodeFromString<SubsonicResponse>(responseText)
        if (response.status != "ok") {
            throw SubsonicException(
                code = response.error?.code ?: -1,
                message = response.error?.message ?: "Unknown error"
            )
        }
        return json.decodeFromString<SubsonicResponse>(responseText) as T
    }

    suspend fun ping(): Boolean {
        return try {
            val url = buildUrl("ping")
            val responseText = client.get(url).body<String>()
            val response = json.decodeFromString<SubsonicResponse>(responseText)
            response.status == "ok"
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getAlbumList(
        type: String = "newest",
        size: Int = 20,
        offset: Int = 0,
    ): List<Child> {
        val params = mapOf(
            "type" to type,
            "size" to size.toString(),
            "offset" to offset.toString(),
        )
        val url = buildUrl("getAlbumList2", params)
        val responseText = client.get(url).body<String>()
        val response = json.decodeFromString<SubsonicResponse>(responseText)
        return response.albumList2?.album ?: emptyList()
    }

    suspend fun getRandomAlbums(size: Int = 20): List<Child> {
        return getAlbumList(type = "random", size = size)
    }

    suspend fun getRecentAlbums(size: Int = 20): List<Child> {
        return getAlbumList(type = "newest", size = size)
    }

    suspend fun getRecentPlay(size: Int = 50): List<Child> {
        val params = mapOf("size" to size.toString())
        val url = buildUrl("getStarred2", params)
        val responseText = client.get(url).body<String>()
        val response = json.decodeFromString<SubsonicResponse>(responseText)
        return response.starred2?.song ?: emptyList()
    }

    suspend fun getNowPlaying(size: Int = 50): List<Child> {
        val params = mapOf("size" to size.toString())
        val url = buildUrl("getNowPlaying", params)
        val responseText = client.get(url).body<String>()
        val response = json.decodeFromString<SubsonicResponse>(responseText)
        return response.songs?.song ?: emptyList()
    }

    suspend fun search(
        query: String,
        artistCount: Int = 10,
        albumCount: Int = 10,
        songCount: Int = 20,
    ): SearchResult {
        val params = mapOf(
            "query" to query,
            "artistCount" to artistCount.toString(),
            "albumCount" to albumCount.toString(),
            "songCount" to songCount.toString(),
        )
        val url = buildUrl("search3", params)
        val responseText = client.get(url).body<String>()
        val response = json.decodeFromString<SubsonicResponse>(responseText)
        return response.searchResult ?: SearchResult()
    }

    suspend fun getPlaylists(): List<Playlist> {
        val url = buildUrl("getPlaylists")
        val responseText = client.get(url).body<String>()
        val response = json.decodeFromString<SubsonicResponse>(responseText)
        return response.playlists?.playlist ?: emptyList()
    }

    suspend fun getPlaylist(id: String): Playlist? {
        val params = mapOf("id" to id)
        val url = buildUrl("getPlaylist", params)
        val responseText = client.get(url).body<String>()
        val response = json.decodeFromString<SubsonicResponse>(responseText)
        return response.playlist
    }

    suspend fun getInternetRadioStations(): List<InternetRadioStation> {
        val url = buildUrl("getInternetRadioStations")
        val responseText = client.get(url).body<String>()
        val response = json.decodeFromString<SubsonicResponse>(responseText)
        return response.radioStations?.internetRadioStation ?: emptyList()
    }

    suspend fun getAlbum(id: String): Album? {
        val params = mapOf("id" to id)
        val url = buildUrl("getAlbum", params)
        val responseText = client.get(url).body<String>()
        val response = json.decodeFromString<SubsonicResponse>(responseText)
        return response.album
    }

    suspend fun getArtists(): List<ArtistIndex> {
        val url = buildUrl("getArtists")
        val responseText = client.get(url).body<String>()
        val response = json.decodeFromString<SubsonicResponse>(responseText)
        return response.artists?.index ?: emptyList()
    }

    suspend fun getArtist(id: String): Artist? {
        val params = mapOf("id" to id)
        val url = buildUrl("getArtist", params)
        val responseText = client.get(url).body<String>()
        val response = json.decodeFromString<SubsonicResponse>(responseText)
        return response.artist
    }

    suspend fun getStarred(): Starred? {
        val url = buildUrl("getStarred2")
        val responseText = client.get(url).body<String>()
        val response = json.decodeFromString<SubsonicResponse>(responseText)
        return response.starred2
    }

    suspend fun starSong(id: String) {
        val params = mapOf("id" to id)
        client.get(buildUrl("star", params)).body<String>()
    }

    suspend fun unstarSong(id: String) {
        val params = mapOf("id" to id)
        client.get(buildUrl("unstar", params)).body<String>()
    }

    suspend fun getLyrics(artist: String, title: String): Lyrics? {
        val params = mapOf("artist" to artist, "title" to title)
        val url = buildUrl("getLyrics", params)
        val responseText = client.get(url).body<String>()
        val response = json.decodeFromString<SubsonicResponse>(responseText)
        return response.lyrics
    }

    suspend fun getSimilarSongs(id: String, count: Int = 20): List<Child> {
        val params = mapOf("id" to id, "count" to count.toString())
        val url = buildUrl("getSimilarSongs2", params)
        val responseText = client.get(url).body<String>()
        val response = json.decodeFromString<SubsonicResponse>(responseText)
        return response.similarSongs2?.song ?: emptyList()
    }

    suspend fun scrobble(id: String, submission: Boolean = true) {
        val params = mapOf(
            "id" to id,
            "submission" to submission.toString(),
        )
        client.get(buildUrl("scrobble", params)).body<String>()
    }

    suspend fun addToPlaylist(playlistId: String, songId: String) {
        val params = mapOf("playlistId" to playlistId, "songIdToAdd" to songId)
        client.get(buildUrl("updatePlaylist", params)).body<String>()
    }

    fun getStreamUrl(id: String, maxBitRate: Int? = null): String {
        val params = mutableMapOf("id" to id)
        maxBitRate?.let { params["maxBitRate"] = it.toString() }
        return buildUrl("stream", params)
    }

    fun getCoverArtUrl(id: String, size: Int? = null): String {
        val params = mutableMapOf("id" to id)
        size?.let { params["size"] = it.toString() }
        return buildUrl("getCoverArt", params)
    }

    fun getServerUrl(): String = serverUrl
    fun getUsername(): String = username
    fun getPassword(): String = password
}

class SubsonicException(val code: Int, override val message: String) : Exception(message)
