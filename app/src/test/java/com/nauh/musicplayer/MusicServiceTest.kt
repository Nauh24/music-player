package com.nauh.musicplayer

import com.nauh.musicplayer.data.model.Song
import com.nauh.musicplayer.service.MusicService
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit test for MusicService functionality
 */
class MusicServiceTest {
    
    @Test
    fun createMediaItem_withValidSong_isCorrect() {
        val song = Song(
            id = "1",
            title = "Test Song",
            artist = "Test Artist",
            album = "Test Album",
            duration = 180000,
            artworkUrl = "https://example.com/artwork.jpg",
            streamUrl = "https://example.com/song.mp3"
        )
        
        val mediaItem = MusicService.createMediaItem(song)
        
        assertEquals(song.id, mediaItem.mediaId)
        assertEquals(song.title, mediaItem.mediaMetadata.title.toString())
        assertEquals(song.artist, mediaItem.mediaMetadata.artist.toString())
        assertEquals(song.album, mediaItem.mediaMetadata.albumTitle.toString())
        assertEquals(song.streamUrl, mediaItem.localConfiguration?.uri.toString())
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun createMediaItem_withEmptyStreamUrl_throwsException() {
        val song = Song(
            id = "1",
            title = "Test Song",
            artist = "Test Artist",
            album = "Test Album",
            duration = 180000,
            artworkUrl = "https://example.com/artwork.jpg",
            streamUrl = ""
        )
        
        MusicService.createMediaItem(song)
    }
}
