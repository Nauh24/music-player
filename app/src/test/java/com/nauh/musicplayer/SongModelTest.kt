package com.nauh.musicplayer

import com.nauh.musicplayer.data.model.Song
import org.junit.Test
import org.junit.Assert.*

/**
 * Simple unit test for Song model
 */
class SongModelTest {
    
    @Test
    fun song_getFormattedDuration_isCorrect() {
        val song = Song(
            id = "1",
            title = "Test Song",
            artist = "Test Artist",
            album = "Test Album",
            duration = 180000, // 3 minutes
            artworkUrl = "test_url",
            streamUrl = "test_stream_url"
        )
        
        assertEquals("03:00", song.getFormattedDuration())
    }
    
    @Test
    fun song_getArtistAlbumText_isCorrect() {
        val song = Song(
            id = "1",
            title = "Test Song",
            artist = "Test Artist",
            album = "Test Album",
            duration = 180000,
            artworkUrl = "test_url",
            streamUrl = "test_stream_url"
        )
        
        assertEquals("Test Artist • Test Album", song.getArtistAlbumText())
    }
}
