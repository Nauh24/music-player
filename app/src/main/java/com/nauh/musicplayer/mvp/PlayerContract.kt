package com.nauh.musicplayer.mvp

import com.nauh.musicplayer.model.Song

interface PlayerContract {
    
    interface View {
        fun showLoading()
        fun hideLoading()
        fun updateSongInfo(song: Song)
        fun updatePlayPauseButton(isPlaying: Boolean)
        fun updateProgress(currentPosition: Long, duration: Long)
        fun updateShuffleButton(isShuffleEnabled: Boolean)
        fun updateRepeatButton(repeatMode: Int)
        fun showError(message: String)
        fun finish()
    }
    
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun initializePlayer(song: Song, songs: List<Song>, position: Int)
        fun onPlayPauseClicked()
        fun onPreviousClicked()
        fun onNextClicked()
        fun onShuffleClicked()
        fun onRepeatClicked()
        fun onSeekTo(position: Long)
        fun onBackPressed()
    }
}
