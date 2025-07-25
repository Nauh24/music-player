package com.nauh.musicplayer.presenter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.nauh.musicplayer.contract.PlayerContract
import com.nauh.musicplayer.data.model.Song
import com.nauh.musicplayer.service.MusicServiceConnection

/**
 * Presenter for the Player screen
 * Handles business logic for music playback and communication between View and Service
 */
class PlayerPresenter : PlayerContract.Presenter {

    private var view: PlayerContract.View? = null
    private var currentSong: Song? = null
    private var playlist: List<Song> = emptyList()
    private var currentIndex: Int = 0
    private var isShuffled: Boolean = false
    private var repeatMode: Int = PlayerContract.RepeatMode.OFF
    private var musicServiceConnection: MusicServiceConnection? = null

    companion object {
        private const val TAG = "PlayerPresenter"
    }
    
    override fun attachView(view: PlayerContract.View) {
        this.view = view
    }

    override fun detachView() {
        musicServiceConnection?.disconnect()
        musicServiceConnection = null
        this.view = null
    }

    fun initializeMusicService(context: Context) {
        Log.d(TAG, "Initializing music service connection")
        musicServiceConnection = MusicServiceConnection(context).apply {
            setPlaybackStateListener(object : MusicServiceConnection.PlaybackStateListener {
                override fun onPlaybackStateChanged(isPlaying: Boolean) {
                    Log.d(TAG, "Playback state changed: $isPlaying")
                    view?.updatePlayPauseButton(isPlaying)
                }

                override fun onProgressUpdate(position: Long, duration: Long) {
                    onProgressUpdate(position, duration)
                }

                override fun onCurrentSongChanged(song: Song?) {
                    song?.let {
                        Log.d(TAG, "Current song changed: ${it.title}")
                        onSongChanged(it)
                    }
                }

                override fun onPlaybackError(error: String) {
                    Log.e(TAG, "Playback error: $error")
                    view?.showError(error)
                }

                override fun onConnectionError(error: String) {
                    Log.e(TAG, "Connection error: $error")
                    view?.showError(error)
                }
            })
            connect()
        }
    }
    
    override fun initializePlayer(song: Song, playlist: List<Song>) {
        Log.d(TAG, "Initializing player with song: ${song.title}")
        this.currentSong = song
        this.playlist = playlist
        this.currentIndex = playlist.indexOf(song).takeIf { it >= 0 } ?: 0

        view?.showSongInfo(song)
        view?.updatePlaylist(playlist, currentIndex)
        updateNavigationButtons()

        // Start playing the song automatically with a delay to ensure connection
        Log.d(TAG, "Auto-playing song: ${song.title}")
        playWithRetry(playlist, currentIndex, 0)
    }
    
    override fun playPause() {
        Log.d(TAG, "Play/Pause button pressed")
        musicServiceConnection?.playPause()
    }

    override fun seekTo(position: Long) {
        Log.d(TAG, "Seeking to position: $position")
        musicServiceConnection?.seekTo(position)
    }
    
    override fun skipToNext() {
        Log.d(TAG, "Skip to next")
        musicServiceConnection?.skipToNext()
    }

    override fun skipToPrevious() {
        Log.d(TAG, "Skip to previous")
        musicServiceConnection?.skipToPrevious()
    }
    
    override fun toggleShuffle() {
        isShuffled = !isShuffled
        view?.showShuffleState(isShuffled)
        updateNavigationButtons()
    }
    
    override fun toggleRepeat() {
        repeatMode = when (repeatMode) {
            PlayerContract.RepeatMode.OFF -> PlayerContract.RepeatMode.ALL
            PlayerContract.RepeatMode.ALL -> PlayerContract.RepeatMode.ONE
            PlayerContract.RepeatMode.ONE -> PlayerContract.RepeatMode.OFF
            else -> PlayerContract.RepeatMode.OFF
        }
        view?.showRepeatState(repeatMode)
        updateNavigationButtons()
    }
    
    override fun onProgressUpdate(position: Long, duration: Long) {
        view?.updateProgress(position, duration)
        
        // Update seek bar
        val progress = if (duration > 0) {
            ((position.toFloat() / duration.toFloat()) * 100).toInt()
        } else 0
        view?.updateSeekBar(progress, 100)
    }
    
    override fun onPlaybackStateChanged(isPlaying: Boolean) {
        view?.updatePlayPauseButton(isPlaying)
    }
    
    override fun onSongChanged(song: Song) {
        currentSong = song
        view?.showSongInfo(song)
    }
    
    override fun onPlaylistChanged(songs: List<Song>, currentIndex: Int) {
        this.playlist = songs
        this.currentIndex = currentIndex
        view?.updatePlaylist(songs, currentIndex)
        updateNavigationButtons()
    }
    
    private fun updateNavigationButtons() {
        val canGoPrevious = when {
            isShuffled -> playlist.size > 1
            repeatMode == PlayerContract.RepeatMode.ALL -> playlist.size > 1
            else -> currentIndex > 0
        }
        
        val canGoNext = when {
            isShuffled -> playlist.size > 1
            repeatMode == PlayerContract.RepeatMode.ALL -> playlist.size > 1
            else -> currentIndex < playlist.size - 1
        }
        
        view?.enablePreviousButton(canGoPrevious)
        view?.enableNextButton(canGoNext)
    }

    private fun playWithRetry(playlist: List<Song>, startIndex: Int, retryCount: Int) {
        val maxRetries = 3
        val delayMs = 500L + (retryCount * 500L) // Increase delay with each retry

        Handler(Looper.getMainLooper()).postDelayed({
            Log.d(TAG, "Attempting to play playlist (retry $retryCount)")
            val connection = musicServiceConnection

            if (connection != null && connection.isConnected()) {
                Log.d(TAG, "Service connected, playing playlist")
                connection.playPlaylist(playlist, startIndex)
            } else if (retryCount < maxRetries) {
                Log.d(TAG, "Service not connected, retrying in ${delayMs}ms")
                playWithRetry(playlist, startIndex, retryCount + 1)
            } else {
                Log.e(TAG, "Failed to connect to music service after $maxRetries retries")
                view?.showError("Failed to connect to music service")
            }
        }, delayMs)
    }
}
