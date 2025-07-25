package com.nauh.musicplayer.mvp

import com.nauh.musicplayer.model.Song

interface MainContract {
    
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showSongs(songs: List<Song>)
        fun showEmptyState()
        fun showError(message: String)
        fun navigateToPlayer(song: Song, songs: List<Song>, position: Int)
        fun showMiniPlayer(song: Song)
        fun hideMiniPlayer()
        fun updateMiniPlayer(song: Song, isPlaying: Boolean, progress: Int)
    }
    
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadSongs()
        fun searchSongs(query: String)
        fun onSongClicked(song: Song, songs: List<Song>, position: Int)
        fun onMiniPlayerClicked()
        fun onPlayPauseClicked()
        fun onPreviousClicked()
        fun onNextClicked()
    }
}
