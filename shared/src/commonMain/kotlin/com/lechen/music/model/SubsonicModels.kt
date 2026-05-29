package com.lechen.music.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubsonicResponse(
    val status: String = "",
    val version: String = "",
    @SerialName("error") val error: ErrorInfo? = null,
    @SerialName("albumList") val albumList: AlbumList? = null,
    @SerialName("albumList2") val albumList2: AlbumList? = null,
    @SerialName("searchResult2") val searchResult: SearchResult? = null,
    @SerialName("searchResult3") val searchResult3: SearchResult? = null,
    @SerialName("playlists") val playlists: PlaylistsWrapper? = null,
    @SerialName("playlist") val playlist: Playlist? = null,
    @SerialName("internetRadioStations") val radioStations: RadioStationsWrapper? = null,
    @SerialName("songs") val songs: SongsWrapper? = null,
    @SerialName("artist") val artist: Artist? = null,
    @SerialName("album") val album: Album? = null,
    @SerialName("starred") val starred: Starred? = null,
    @SerialName("starred2") val starred2: Starred? = null,
    @SerialName("lyrics") val lyrics: Lyrics? = null,
    @SerialName("similarSongs") val similarSongs: SimilarSongs? = null,
    @SerialName("similarSongs2") val similarSongs2: SimilarSongs? = null,
    @SerialName("musicFolders") val musicFolders: MusicFoldersWrapper? = null,
    @SerialName("artists") val artists: ArtistsWrapper? = null,
    @SerialName("genres") val genres: GenresWrapper? = null,
    @SerialName("song") val song: Child? = null,
    @SerialName("albumInfo") val albumInfo: AlbumInfo? = null,
    @SerialName("artistInfo") val artistInfo: ArtistInfo? = null,
    @SerialName("artistInfo2") val artistInfo2: ArtistInfo? = null,
    @SerialName("chatMessages") val chatMessages: ChatMessages? = null,
    @SerialName("bookmarks") val bookmarks: Bookmarks? = null,
    @SerialName("scanStatus") val scanStatus: ScanStatus? = null,
)

@Serializable
data class ErrorInfo(
    val code: Int = 0,
    val message: String = "",
)

@Serializable
data class AlbumList(
    val album: List<Child> = emptyList(),
)

@Serializable
data class SearchResult(
    val song: List<Child> = emptyList(),
    val album: List<Child> = emptyList(),
    val artist: List<Artist> = emptyList(),
)

@Serializable
data class PlaylistsWrapper(
    val playlist: List<Playlist> = emptyList(),
)

@Serializable
data class Playlist(
    val id: String = "",
    val name: String = "",
    val comment: String? = null,
    val songCount: Int = 0,
    val duration: Int = 0,
    val created: String = "",
    val changed: String = "",
    val coverArt: String? = null,
    val songs: SongsWrapper? = null,
)

@Serializable
data class SongsWrapper(
    val song: List<Child> = emptyList(),
)

@Serializable
data class RadioStationsWrapper(
    val internetRadioStation: List<InternetRadioStation> = emptyList(),
)

@Serializable
data class InternetRadioStation(
    val id: String = "",
    val name: String = "",
    val streamUrl: String = "",
    val homepageUrl: String? = null,
    val description: String? = null,
)

@Serializable
data class Child(
    val id: String = "",
    val parent: String? = null,
    val isDir: Boolean = false,
    val title: String = "",
    val album: String? = null,
    val artist: String? = null,
    val track: Int? = null,
    val year: Int? = null,
    val genre: String? = null,
    val coverArt: String? = null,
    val size: Long? = null,
    val contentType: String? = null,
    val suffix: String? = null,
    val duration: Int? = null,
    val bitRate: Int? = null,
    val path: String? = null,
    val playCount: Int? = null,
    val created: String = "",
    val starred: String? = null,
    val albumId: String? = null,
    val artistId: String? = null,
    val type: String? = null,
)

@Serializable
data class Artist(
    val id: String = "",
    val name: String = "",
    val coverArt: String? = null,
    val albumCount: Int = 0,
    val artistImageUrl: String? = null,
    val starred: String? = null,
    val albums: List<Child> = emptyList(),
)

@Serializable
data class Album(
    val id: String = "",
    val name: String = "",
    val artist: String? = null,
    val artistId: String? = null,
    val coverArt: String? = null,
    val songCount: Int = 0,
    val duration: Int = 0,
    val playCount: Int? = null,
    val created: String = "",
    val starred: String? = null,
    val year: Int? = null,
    val genre: String? = null,
    val song: List<Child> = emptyList(),
)

@Serializable
data class Starred(
    val song: List<Child> = emptyList(),
    val album: List<Child> = emptyList(),
    val artist: List<Artist> = emptyList(),
)

@Serializable
data class Lyrics(
    val artist: String? = null,
    val title: String? = null,
    val value: String? = null,
)

@Serializable
data class SimilarSongs(
    val song: List<Child> = emptyList(),
)

@Serializable
data class MusicFoldersWrapper(
    val musicFolder: List<MusicFolder> = emptyList(),
)

@Serializable
data class MusicFolder(
    val id: String = "",
    val name: String = "",
)

@Serializable
data class ArtistsWrapper(
    val index: List<ArtistIndex> = emptyList(),
)

@Serializable
data class ArtistIndex(
    val name: String = "",
    val artist: List<Artist> = emptyList(),
)

@Serializable
data class GenresWrapper(
    val genre: List<Genre> = emptyList(),
)

@Serializable
data class Genre(
    val songCount: Int = 0,
    val albumCount: Int = 0,
    val value: String = "",
)

@Serializable
data class AlbumInfo(
    val notes: String? = null,
    val musicBrainzId: String? = null,
    val lastFmUrl: String? = null,
    val smallImageUrl: String? = null,
    val mediumImageUrl: String? = null,
    val largeImageUrl: String? = null,
)

@Serializable
data class ArtistInfo(
    val biography: String? = null,
    val musicBrainzId: String? = null,
    val lastFmUrl: String? = null,
    val smallImageUrl: String? = null,
    val mediumImageUrl: String? = null,
    val largeImageUrl: String? = null,
    val similarArtist: List<Artist> = emptyList(),
)

@Serializable
data class ChatMessages(
    val chatMessage: List<ChatMessage> = emptyList(),
)

@Serializable
data class ChatMessage(
    val username: String = "",
    val time: Long = 0,
    val message: String = "",
)

@Serializable
data class Bookmarks(
    val bookmark: List<Bookmark> = emptyList(),
)

@Serializable
data class Bookmark(
    val position: Long = 0,
    val username: String = "",
    val comment: String? = null,
    val created: String = "",
    val changed: String = "",
    val entry: Child? = null,
)

@Serializable
data class ScanStatus(
    val scanning: Boolean = false,
    val count: Long? = null,
)
