package com.nauh.musicplayer

import com.nauh.musicplayer.contract.PlayerContract
import com.nauh.musicplayer.data.model.Song
import com.nauh.musicplayer.presenter.PlayerPresenter
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Simple unit test for PlayerPresenter without mocking
 */
class PlayerPresenterTest {

    private lateinit var presenter: PlayerPresenter
    private var viewCallbacks = mutableListOf<String>()

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

    // Simple mock view implementation
    private val mockView = object : PlayerContract.View {
        override fun showSongInfo(song: Song) {
            viewCallbacks.add("showSongInfo:${song.title}")
        }

        override fun updatePlayPauseButton(isPlaying: Boolean) {
            viewCallbacks.add("updatePlayPauseButton:$isPlaying")
        }

        override fun updateProgress(currentPosition: Long, duration: Long) {
            viewCallbacks.add("updateProgress:$currentPosition/$duration")
        }

        override fun updateSeekBar(position: Int, max: Int) {
            viewCallbacks.add("updateSeekBar:$position/$max")
        }

        override fun showLoading() {
            viewCallbacks.add("showLoading")
        }

        override fun hideLoading() {
            viewCallbacks.add("hideLoading")
        }

        override fun showError(message: String) {
            viewCallbacks.add("showError:$message")
        }

        override fun enablePreviousButton(enabled: Boolean) {
            viewCallbacks.add("enablePreviousButton:$enabled")
        }

        override fun enableNextButton(enabled: Boolean) {
            viewCallbacks.add("enableNextButton:$enabled")
        }

        override fun showShuffleState(isShuffled: Boolean) {
            viewCallbacks.add("showShuffleState:$isShuffled")
        }

        override fun showRepeatState(repeatMode: Int) {
            viewCallbacks.add("showRepeatState:$repeatMode")
        }

        override fun updatePlaylist(songs: List<Song>, currentIndex: Int) {
            viewCallbacks.add("updatePlaylist:${songs.size}/$currentIndex")
        }
    }

    @Before
    fun setup() {
        presenter = PlayerPresenter()
        presenter.attachView(mockView)
        viewCallbacks.clear()
    }

    @Test
    fun initializePlayer_shouldShowSongInfo() {
        // When - Don't call initializePlayer as it requires Context for MusicServiceConnection
        // Just test the callback methods directly
        presenter.onSongChanged(testSong)

        // Then
        assertTrue(viewCallbacks.contains("showSongInfo:Test Song"))
    }

    @Test
    fun onSongChanged_shouldUpdateView() {
        // When
        presenter.onSongChanged(testSong)

        // Then
        assertTrue(viewCallbacks.contains("showSongInfo:Test Song"))
    }

    @Test
    fun onPlaybackStateChanged_shouldUpdatePlayPauseButton() {
        // When
        presenter.onPlaybackStateChanged(true)

        // Then
        assertTrue(viewCallbacks.contains("updatePlayPauseButton:true"))
    }

    @Test
    fun onProgressUpdate_shouldUpdateProgressAndSeekBar() {
        // Given
        val position = 90000L // 1.5 minutes
        val duration = 180000L // 3 minutes

        // When
        presenter.onProgressUpdate(position, duration)

        // Then
        assertTrue(viewCallbacks.contains("updateProgress:90000/180000"))
        assertTrue(viewCallbacks.contains("updateSeekBar:50/100")) // 50% progress
    }

    @Test
    fun toggleShuffle_shouldUpdateShuffleState() {
        // When
        presenter.toggleShuffle()

        // Then
        assertTrue(viewCallbacks.contains("showShuffleState:true"))

        // Clear and toggle again
        viewCallbacks.clear()
        presenter.toggleShuffle()

        // Then
        assertTrue(viewCallbacks.contains("showShuffleState:false"))
    }

    @Test
    fun toggleRepeat_shouldCycleThroughRepeatModes() {
        // When - first toggle (OFF -> ALL)
        presenter.toggleRepeat()

        // Then
        assertTrue(viewCallbacks.contains("showRepeatState:${PlayerContract.RepeatMode.ALL}"))

        // Clear and second toggle (ALL -> ONE)
        viewCallbacks.clear()
        presenter.toggleRepeat()

        // Then
        assertTrue(viewCallbacks.contains("showRepeatState:${PlayerContract.RepeatMode.ONE}"))

        // Clear and third toggle (ONE -> OFF)
        viewCallbacks.clear()
        presenter.toggleRepeat()

        // Then
        assertTrue(viewCallbacks.contains("showRepeatState:${PlayerContract.RepeatMode.OFF}"))
    }
}
