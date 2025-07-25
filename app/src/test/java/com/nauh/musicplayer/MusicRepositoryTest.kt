package com.nauh.musicplayer

import com.nauh.musicplayer.data.MusicRepository
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*

class MusicRepositoryTest {
    
    private val repository = MusicRepository()
    
    @Test
    fun `getSongs returns non-empty list`() = runBlocking {
        val songs = repository.getSongs()
        assertTrue("Songs list should not be empty", songs.isNotEmpty())
        assertTrue("Should have at least 5 songs", songs.size >= 5)
    }
    
    @Test
    fun `searchSongs with empty query returns all songs`() = runBlocking {
        val allSongs = repository.getSongs()
        val searchResult = repository.searchSongs("")
        assertEquals("Empty search should return all songs", allSongs.size, searchResult.size)
    }
    
    @Test
    fun `searchSongs filters correctly`() = runBlocking {
        val searchResult = repository.searchSongs("Harry")
        assertTrue("Should find Harry Styles songs", searchResult.isNotEmpty())
        assertTrue("All results should contain 'Harry'", 
            searchResult.all { it.artist.contains("Harry", ignoreCase = true) })
    }
    
    @Test
    fun `song duration formatting works correctly`() = runBlocking {
        val songs = repository.getSongs()
        val firstSong = songs.first()
        val formattedDuration = firstSong.getFormattedDuration()
        assertTrue("Duration should be in MM:SS format", 
            formattedDuration.matches(Regex("\\d+:\\d{2}")))
    }
}
