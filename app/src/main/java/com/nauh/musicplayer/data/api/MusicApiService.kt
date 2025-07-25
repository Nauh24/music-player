package com.nauh.musicplayer.data.api

import com.nauh.musicplayer.data.model.Song
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * API interface for fetching music data from remote sources
 */
interface MusicApiService {
    
    /**
     * Fetch all available songs
     */
    @GET("songs")
    suspend fun getAllSongs(): Response<List<Song>>
    
    /**
     * Fetch songs by genre
     */
    @GET("songs/genre/{genre}")
    suspend fun getSongsByGenre(@Path("genre") genre: String): Response<List<Song>>
    
    /**
     * Fetch song by ID
     */
    @GET("songs/{id}")
    suspend fun getSongById(@Path("id") id: String): Response<Song>
    
    /**
     * Search songs by query
     */
    @GET("songs/search/{query}")
    suspend fun searchSongs(@Path("query") query: String): Response<List<Song>>
}

/**
 * Mock data provider for demonstration purposes
 * In a real app, this would be replaced with actual API calls
 */
object MockMusicData {
    
    fun getSampleSongs(): List<Song> {
        return listOf(
            Song(
                id = "1",
                title = "12600 lettres (Débat)",
                artist = "Franco & TP OK Jazz",
                album = "Classic Congo",
                duration = 180000, // 3 minutes
                artworkUrl = "https://via.placeholder.com/300x300/FF6B6B/FFFFFF?text=Franco",
                streamUrl = "https://commondatastorage.googleapis.com/codeskulptor-demos/DDR_assets/Kangaroo_MusiQue_-_The_Neverwritten_Role_Playing_Game.mp3",
                genre = "World Music"
            ),
            Song(
                id = "2",
                title = "Again & Again",
                artist = "Usatof",
                album = "Electronic Vibes",
                duration = 210000, // 3.5 minutes
                artworkUrl = "https://via.placeholder.com/300x300/4ECDC4/FFFFFF?text=Usatof",
                streamUrl = "https://www.learningcontainer.com/wp-content/uploads/2020/02/Kalimba.mp3",
                genre = "Electronic"
            ),
            Song(
                id = "3",
                title = "Ain't No Mountain High Enough",
                artist = "Marvin Gaye & Tammi Terrell",
                album = "Motown Classics",
                duration = 195000, // 3.25 minutes
                artworkUrl = "https://via.placeholder.com/300x300/45B7D1/FFFFFF?text=Motown",
                streamUrl = "https://file-examples.com/storage/fe68c8777b8ee92c7178b6d/2017/11/file_example_MP3_700KB.mp3",
                genre = "Soul"
            ),
            Song(
                id = "4",
                title = "All I Have to Do Is Dream",
                artist = "The Everly Brothers",
                album = "Rock & Roll Legends",
                duration = 165000, // 2.75 minutes
                artworkUrl = "https://via.placeholder.com/300x300/96CEB4/FFFFFF?text=Everly",
                streamUrl = "https://commondatastorage.googleapis.com/codeskulptor-assets/Epoq-Lepidoptera.ogg",
                genre = "Rock & Roll"
            ),
            Song(
                id = "5",
                title = "All Night",
                artist = "Siddy Ranks",
                album = "Reggae Vibes",
                duration = 225000, // 3.75 minutes
                artworkUrl = "https://via.placeholder.com/300x300/FFEAA7/FFFFFF?text=Reggae",
                streamUrl = "https://commondatastorage.googleapis.com/codeskulptor-demos/DDR_assets/Sevish_-__nbsp_.mp3",
                genre = "Reggae"
            ),
            Song(
                id = "6",
                title = "Escape (The Piña Colada Song)",
                artist = "Rupert Holmes",
                album = "70s Hits",
                duration = 280000, // 4.67 minutes
                artworkUrl = "https://via.placeholder.com/300x300/DDA0DD/FFFFFF?text=70s",
                streamUrl = "https://commondatastorage.googleapis.com/codeskulptor-assets/week7-brrring.m4a",
                genre = "Pop"
            ),
            Song(
                id = "7",
                title = "September",
                artist = "Earth, Wind & Fire",
                album = "Disco Classics",
                duration = 215000, // 3.58 minutes
                artworkUrl = "https://via.placeholder.com/300x300/74B9FF/FFFFFF?text=EWF",
                streamUrl = "https://commondatastorage.googleapis.com/codeskulptor-assets/Epoq-Lepidoptera.ogg",
                genre = "Disco"
            )
        )
    }
}
