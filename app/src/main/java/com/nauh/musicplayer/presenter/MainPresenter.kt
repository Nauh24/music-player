package com.nauh.musicplayer.presenter

import com.nauh.musicplayer.contract.MainContract
import com.nauh.musicplayer.data.model.Song
import com.nauh.musicplayer.data.repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Presenter for the Main screen
 * Handles business logic and communication between View and Model
 */
class MainPresenter(
    private val repository: MusicRepository = MusicRepository.getInstance()
) : MainContract.Presenter {
    
    private var view: MainContract.View? = null
    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())
    private var currentSongs: List<Song> = emptyList()
    private var searchJob: Job? = null
    
    override fun attachView(view: MainContract.View) {
        this.view = view
    }
    
    override fun detachView() {
        this.view = null
        searchJob?.cancel()
    }
    
    override fun loadSongs() {
        view?.showLoading()
        
        presenterScope.launch {
            try {
                val result = repository.getAllSongs()
                
                withContext(Dispatchers.Main) {
                    view?.hideLoading()
                    
                    result.fold(
                        onSuccess = { songs ->
                            currentSongs = songs
                            if (songs.isEmpty()) {
                                view?.showEmptyState()
                            } else {
                                view?.showSongs(songs)
                            }
                        },
                        onFailure = { exception ->
                            view?.showError(exception.message ?: "Failed to load songs")
                        }
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view?.hideLoading()
                    view?.showError(e.message ?: "An unexpected error occurred")
                }
            }
        }
    }
    
    override fun onSongClicked(song: Song, playlist: List<Song>) {
        view?.navigateToPlayer(song, playlist)
    }
    
    override fun searchSongs(query: String) {
        if (query.isBlank()) {
            clearSearch()
            return
        }
        
        // Cancel previous search
        searchJob?.cancel()
        
        searchJob = presenterScope.launch {
            try {
                val result = repository.searchSongs(query)
                
                withContext(Dispatchers.Main) {
                    result.fold(
                        onSuccess = { songs ->
                            view?.showSearchResults(songs)
                        },
                        onFailure = { exception ->
                            view?.showError(exception.message ?: "Search failed")
                        }
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view?.showError(e.message ?: "Search failed")
                }
            }
        }
    }
    
    override fun clearSearch() {
        searchJob?.cancel()
        view?.clearSearchResults()
        view?.showSongs(currentSongs)
    }
    
    override fun refreshSongs() {
        loadSongs()
    }
    
    override fun onCurrentSongChanged(song: Song?) {
        view?.updateCurrentPlayingSong(song)
    }
}
