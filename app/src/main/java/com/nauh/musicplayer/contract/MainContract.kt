package com.nauh.musicplayer.contract

import com.nauh.musicplayer.data.model.Song

/**
 * Contract interface for Main screen MVP pattern
 * Defines the communication between View, Presenter, and Model
 */
interface MainContract {
    
    /**
     * View interface - defines what the View can do
     */
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showSongs(songs: List<Song>)
        fun showError(message: String)
        fun showEmptyState()
        fun navigateToPlayer(song: Song, playlist: List<Song>)
        fun updateCurrentPlayingSong(song: Song?)
        fun showSearchResults(songs: List<Song>)
        fun clearSearchResults()
    }
    
    /**
     * Presenter interface - defines what the Presenter can do
     */
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadSongs()
        fun onSongClicked(song: Song, playlist: List<Song>)
        fun searchSongs(query: String)
        fun clearSearch()
        fun refreshSongs()
        fun onCurrentSongChanged(song: Song?)
    }
}
