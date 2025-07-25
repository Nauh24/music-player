package com.nauh.musicplayer

import com.nauh.musicplayer.contract.PlayerContract
import com.nauh.musicplayer.data.model.Song
import com.nauh.musicplayer.presenter.PlayerPresenter
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

/**
 * Unit test for PlayerPresenter
 */
class PlayerPresenterTest {
    
    @Mock
    private lateinit var mockView: PlayerContract.View
    
    private lateinit var presenter: PlayerPresenter
    
    private val testSong = Song(
        id = "1",
        title = "Test Song",
        artist = "Test Artist",
        album = "Test Album",
        duration = 180000, // 3 minutes
        artworkUrl = "test_artwork_url",
        streamUrl = "test_stream_url"
    )
    
    private val testPlaylist = listOf(
        testSong,
        Song(
            id = "2",
            title = "Test Song 2",
            artist = "Test Artist 2",
            album = "Test Album 2",
            duration = 210000,
            artworkUrl = "test_artwork_url_2",
            streamUrl = "test_stream_url_2"
        )
    )
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        presenter = PlayerPresenter()
        presenter.attachView(mockView)
    }
    
    @Test
    fun initializePlayer_shouldShowSongInfo() {
        // When
        presenter.initializePlayer(testSong, testPlaylist)
        
        // Then
        verify(mockView).showSongInfo(testSong)
        verify(mockView).updatePlaylist(testPlaylist, 0)
    }
    
    @Test
    fun onSongChanged_shouldUpdateView() {
        // When
        presenter.onSongChanged(testSong)
        
        // Then
        verify(mockView).showSongInfo(testSong)
    }
    
    @Test
    fun onPlaybackStateChanged_shouldUpdatePlayPauseButton() {
        // When
        presenter.onPlaybackStateChanged(true)
        
        // Then
        verify(mockView).updatePlayPauseButton(true)
    }
    
    @Test
    fun onProgressUpdate_shouldUpdateProgressAndSeekBar() {
        // Given
        val position = 90000L // 1.5 minutes
        val duration = 180000L // 3 minutes
        
        // When
        presenter.onProgressUpdate(position, duration)
        
        // Then
        verify(mockView).updateProgress(position, duration)
        verify(mockView).updateSeekBar(50, 100) // 50% progress
    }
    
    @Test
    fun toggleShuffle_shouldUpdateShuffleState() {
        // When
        presenter.toggleShuffle()
        
        // Then
        verify(mockView).showShuffleState(true)
        
        // When toggle again
        presenter.toggleShuffle()
        
        // Then
        verify(mockView).showShuffleState(false)
    }
    
    @Test
    fun toggleRepeat_shouldCycleThroughRepeatModes() {
        // When - first toggle (OFF -> ALL)
        presenter.toggleRepeat()
        
        // Then
        verify(mockView).showRepeatState(PlayerContract.RepeatMode.ALL)
        
        // When - second toggle (ALL -> ONE)
        presenter.toggleRepeat()
        
        // Then
        verify(mockView).showRepeatState(PlayerContract.RepeatMode.ONE)
        
        // When - third toggle (ONE -> OFF)
        presenter.toggleRepeat()
        
        // Then
        verify(mockView).showRepeatState(PlayerContract.RepeatMode.OFF)
    }
}
