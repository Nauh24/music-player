package com.nauh.musicplayer.data

import com.nauh.musicplayer.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MusicRepository {
    
    // For demo purposes, we'll use sample data
    // In a real app, this would fetch from API or local database
    suspend fun getSongs(): List<Song> = withContext(Dispatchers.IO) {
        // Sample songs for demonstration
        listOf(
            Song(
                id = "1",
                title = "Blinding Lights",
                artist = "The Weeknd",
                album = "After Hours",
                duration = 200000, // 3:20
                url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                albumArtUrl = null
            ),
            Song(
                id = "2", 
                title = "Watermelon Sugar",
                artist = "Harry Styles",
                album = "Fine Line",
                duration = 174000, // 2:54
                url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
                albumArtUrl = null
            ),
            Song(
                id = "3",
                title = "Levitating",
                artist = "Dua Lipa",
                album = "Future Nostalgia",
                duration = 203000, // 3:23
                url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
                albumArtUrl = null
            ),
            Song(
                id = "4",
                title = "Good 4 U",
                artist = "Olivia Rodrigo", 
                album = "SOUR",
                duration = 178000, // 2:58
                url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
                albumArtUrl = null
            ),
            Song(
                id = "5",
                title = "Stay",
                artist = "The Kid LAROI & Justin Bieber",
                album = "F*CK LOVE 3: OVER YOU",
                duration = 141000, // 2:21
                url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
                albumArtUrl = null
            ),
            Song(
                id = "6",
                title = "Heat Waves",
                artist = "Glass Animals",
                album = "Dreamland",
                duration = 238000, // 3:58
                url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
                albumArtUrl = null
            ),
            Song(
                id = "7",
                title = "As It Was",
                artist = "Harry Styles",
                album = "Harry's House",
                duration = 167000, // 2:47
                url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3",
                albumArtUrl = null
            ),
            Song(
                id = "8",
                title = "Bad Habit",
                artist = "Steve Lacy",
                album = "Gemini Rights",
                duration = 221000, // 3:41
                url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3",
                albumArtUrl = null
            )
        )
    }
    
    suspend fun searchSongs(query: String): List<Song> = withContext(Dispatchers.IO) {
        val allSongs = getSongs()
        if (query.isBlank()) return@withContext allSongs
        
        allSongs.filter { song ->
            song.title.contains(query, ignoreCase = true) ||
            song.artist.contains(query, ignoreCase = true) ||
            song.album.contains(query, ignoreCase = true)
        }
    }
}
