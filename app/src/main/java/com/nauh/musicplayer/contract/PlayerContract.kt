package com.nauh.musicplayer.contract

import com.nauh.musicplayer.data.model.Song

/**
 * Contract interface for Player screen MVP pattern
 * Defines the communication between View, Presenter, and Model for music playback
 */
interface PlayerContract {
    
    /**
     * View interface - defines what the Player View can do
     */
    interface View {
        fun showSongInfo(song: Song)
        fun updatePlayPauseButton(isPlaying: Boolean)
        fun updateProgress(currentPosition: Long, duration: Long)
        fun updateSeekBar(position: Int, max: Int)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun enablePreviousButton(enabled: Boolean)
        fun enableNextButton(enabled: Boolean)
        fun showShuffleState(isShuffled: Boolean)
        fun showRepeatState(repeatMode: Int)
        fun updatePlaylist(songs: List<Song>, currentIndex: Int)
    }
    
    /**
     * Presenter interface - defines what the Player Presenter can do
     */
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun initializePlayer(song: Song, playlist: List<Song>)
        fun playPause()
        fun seekTo(position: Long)
        fun skipToNext()
        fun skipToPrevious()
        fun toggleShuffle()
        fun toggleRepeat()
        fun onProgressUpdate(position: Long, duration: Long)
        fun onPlaybackStateChanged(isPlaying: Boolean)
        fun onSongChanged(song: Song)
        fun onPlaylistChanged(songs: List<Song>, currentIndex: Int)
    }
    
    /**
     * Repeat modes for the player
     */
    object RepeatMode {
        const val OFF = 0
        const val ONE = 1
        const val ALL = 2
    }
}
