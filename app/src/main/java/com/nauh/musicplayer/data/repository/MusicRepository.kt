package com.nauh.musicplayer.data.repository

import com.nauh.musicplayer.data.api.MockMusicData
import com.nauh.musicplayer.data.api.MusicApiService
import com.nauh.musicplayer.data.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Repository class that handles data operations for music
 * Implements the Repository pattern to abstract data sources
 */
class MusicRepository(
    private val apiService: MusicApiService? = null
) {
    
    /**
     * Fetch all songs from the data source
     * For demo purposes, returns mock data with simulated network delay
     */
    suspend fun getAllSongs(): Result<List<Song>> {
        return withContext(Dispatchers.IO) {
            try {
                // Simulate network delay
                delay(1000)
                
                // For demo purposes, return mock data
                // In a real app, this would call apiService.getAllSongs()
                val songs = MockMusicData.getSampleSongs()
                Result.success(songs)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Search songs by query
     */
    suspend fun searchSongs(query: String): Result<List<Song>> {
        return withContext(Dispatchers.IO) {
            try {
                delay(500) // Simulate network delay
                
                val allSongs = MockMusicData.getSampleSongs()
                val filteredSongs = allSongs.filter { song ->
                    song.title.contains(query, ignoreCase = true) ||
                    song.artist.contains(query, ignoreCase = true) ||
                    song.album.contains(query, ignoreCase = true)
                }
                
                Result.success(filteredSongs)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Get song by ID
     */
    suspend fun getSongById(id: String): Result<Song?> {
        return withContext(Dispatchers.IO) {
            try {
                delay(300) // Simulate network delay
                
                val song = MockMusicData.getSampleSongs().find { it.id == id }
                Result.success(song)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Get songs by genre
     */
    suspend fun getSongsByGenre(genre: String): Result<List<Song>> {
        return withContext(Dispatchers.IO) {
            try {
                delay(500) // Simulate network delay
                
                val allSongs = MockMusicData.getSampleSongs()
                val filteredSongs = allSongs.filter { song ->
                    song.genre?.equals(genre, ignoreCase = true) == true
                }
                
                Result.success(filteredSongs)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    companion object {
        @Volatile
        private var INSTANCE: MusicRepository? = null
        
        fun getInstance(apiService: MusicApiService? = null): MusicRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MusicRepository(apiService).also { INSTANCE = it }
            }
        }
    }
}
