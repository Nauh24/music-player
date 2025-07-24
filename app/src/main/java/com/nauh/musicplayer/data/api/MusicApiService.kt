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
                artist = "Pomme & Yor Jaz",
                album = "Sample Album",
                duration = 180000, // 3 minutes
                artworkUrl = "https://via.placeholder.com/300x300/FF6B6B/FFFFFF?text=Song+1",
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                genre = "Pop"
            ),
            Song(
                id = "2",
                title = "Again & Again",
                artist = "Artist",
                album = "Sample Album",
                duration = 210000, // 3.5 minutes
                artworkUrl = "https://via.placeholder.com/300x300/4ECDC4/FFFFFF?text=Song+2",
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
                genre = "Rock"
            ),
            Song(
                id = "3",
                title = "Ain't No Mountain High Enough",
                artist = "Marvin Gaye & Tammi Terrell",
                album = "Classic Hits",
                duration = 195000, // 3.25 minutes
                artworkUrl = "https://via.placeholder.com/300x300/45B7D1/FFFFFF?text=Song+3",
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
                genre = "Soul"
            ),
            Song(
                id = "4",
                title = "All I Have to Do Is Dream",
                artist = "The Everly Brothers",
                album = "Classic Collection",
                duration = 165000, // 2.75 minutes
                artworkUrl = "https://via.placeholder.com/300x300/96CEB4/FFFFFF?text=Song+4",
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
                genre = "Country"
            ),
            Song(
                id = "5",
                title = "All Night",
                artist = "Icey Banks",
                album = "Night Sessions",
                duration = 225000, // 3.75 minutes
                artworkUrl = "https://via.placeholder.com/300x300/FFEAA7/FFFFFF?text=Song+5",
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
                genre = "Electronic"
            ),
            Song(
                id = "6",
                title = "Escape (The Piña Colada Song)",
                artist = "Rupert Holmes",
                album = "Greatest Hits",
                duration = 270000, // 4.5 minutes
                artworkUrl = "https://via.placeholder.com/300x300/DDA0DD/FFFFFF?text=Song+6",
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
                genre = "Pop"
            )
        )
    }
}
