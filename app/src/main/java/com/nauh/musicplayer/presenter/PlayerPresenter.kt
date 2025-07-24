package com.nauh.musicplayer.presenter

import com.nauh.musicplayer.contract.PlayerContract
import com.nauh.musicplayer.data.model.Song

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
    
    override fun attachView(view: PlayerContract.View) {
        this.view = view
    }
    
    override fun detachView() {
        this.view = null
    }
    
    override fun initializePlayer(song: Song, playlist: List<Song>) {
        this.currentSong = song
        this.playlist = playlist
        this.currentIndex = playlist.indexOf(song).takeIf { it >= 0 } ?: 0
        
        view?.showSongInfo(song)
        view?.updatePlaylist(playlist, currentIndex)
        updateNavigationButtons()
    }
    
    override fun playPause() {
        // This will be implemented when we integrate with MusicService
        // For now, just toggle the UI state
        val isCurrentlyPlaying = true // This should come from actual player state
        view?.updatePlayPauseButton(isCurrentlyPlaying)
    }
    
    override fun seekTo(position: Long) {
        // This will be implemented when we integrate with MusicService
        // For now, just update the UI
        currentSong?.let { song ->
            view?.updateProgress(position, song.duration)
        }
    }
    
    override fun skipToNext() {
        if (playlist.isEmpty()) return
        
        currentIndex = when {
            isShuffled -> (0 until playlist.size).random()
            currentIndex < playlist.size - 1 -> currentIndex + 1
            repeatMode == PlayerContract.RepeatMode.ALL -> 0
            else -> currentIndex // Stay at current if no repeat
        }
        
        if (currentIndex < playlist.size) {
            currentSong = playlist[currentIndex]
            currentSong?.let { song ->
                view?.showSongInfo(song)
                onSongChanged(song)
            }
        }
        
        updateNavigationButtons()
    }
    
    override fun skipToPrevious() {
        if (playlist.isEmpty()) return
        
        currentIndex = when {
            isShuffled -> (0 until playlist.size).random()
            currentIndex > 0 -> currentIndex - 1
            repeatMode == PlayerContract.RepeatMode.ALL -> playlist.size - 1
            else -> currentIndex // Stay at current if no repeat
        }
        
        if (currentIndex >= 0 && currentIndex < playlist.size) {
            currentSong = playlist[currentIndex]
            currentSong?.let { song ->
                view?.showSongInfo(song)
                onSongChanged(song)
            }
        }
        
        updateNavigationButtons()
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
}
