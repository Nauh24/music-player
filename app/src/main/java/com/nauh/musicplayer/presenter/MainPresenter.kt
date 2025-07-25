package com.nauh.musicplayer.mvp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.nauh.musicplayer.data.MusicRepository
import com.nauh.musicplayer.model.Song
import com.nauh.musicplayer.service.MusicService
import kotlinx.coroutines.*

class MainPresenter(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner
) : MainContract.Presenter {
    
    private var view: MainContract.View? = null
    private val repository = MusicRepository()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    private var musicService: MusicService? = null
    private var isBound = false
    private var currentSongs: List<Song> = emptyList()
    
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            isBound = true
            observeMusicService()
        }
        
        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
            isBound = false
        }
    }
    
    override fun attachView(view: MainContract.View) {
        this.view = view
        bindMusicService()
    }
    
    override fun detachView() {
        this.view = null
        unbindMusicService()
        scope.cancel()
    }
    
    private fun bindMusicService() {
        val intent = Intent(context, MusicService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }
    
    private fun unbindMusicService() {
        if (isBound) {
            context.unbindService(serviceConnection)
            isBound = false
        }
    }
    
    private fun observeMusicService() {
        musicService?.let { service ->
            service.setOnSongChangedCallback { song ->
                song?.let {
                    view?.showMiniPlayer(it)
                } ?: view?.hideMiniPlayer()
            }

            service.setOnPlayingStateChangedCallback { isPlaying ->
                service.getCurrentSong()?.let { song ->
                    val progress = if (service.getDuration() > 0) {
                        ((service.getCurrentPosition().toFloat() / service.getDuration()) * 100).toInt()
                    } else 0
                    view?.updateMiniPlayer(song, isPlaying, progress)
                }
            }

            service.setOnProgressChangedCallback { current, duration ->
                service.getCurrentSong()?.let { song ->
                    val progress = if (duration > 0) {
                        ((current.toFloat() / duration) * 100).toInt()
                    } else 0
                    view?.updateMiniPlayer(song, service.isPlaying(), progress)
                }
            }

            // Initialize mini player with current state if music is already playing
            service.getCurrentSong()?.let { song ->
                val isPlaying = service.isPlaying()
                val progress = if (service.getDuration() > 0) {
                    ((service.getCurrentPosition().toFloat() / service.getDuration()) * 100).toInt()
                } else 0
                view?.showMiniPlayer(song)
                view?.updateMiniPlayer(song, isPlaying, progress)
            }
        }
    }
    
    override fun loadSongs() {
        view?.showLoading()
        
        scope.launch {
            try {
                val songs = repository.getSongs()
                currentSongs = songs
                
                if (songs.isEmpty()) {
                    view?.showEmptyState()
                } else {
                    view?.showSongs(songs)
                }
            } catch (e: Exception) {
                view?.showError("Failed to load songs: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }
    
    override fun searchSongs(query: String) {
        scope.launch {
            try {
                val songs = repository.searchSongs(query)
                currentSongs = songs
                
                if (songs.isEmpty()) {
                    view?.showEmptyState()
                } else {
                    view?.showSongs(songs)
                }
            } catch (e: Exception) {
                view?.showError("Search failed: ${e.message}")
            }
        }
    }
    
    override fun onSongClicked(song: Song, songs: List<Song>, position: Int) {
        // Start music service if not already started
        val intent = Intent(context, MusicService::class.java)
        context.startService(intent)
        
        // Navigate to player
        view?.navigateToPlayer(song, songs, position)
        
        // Play the song
        musicService?.playSong(song, songs, position)
    }
    
    override fun onMiniPlayerClicked() {
        musicService?.getCurrentSong()?.let { currentSong ->
            val currentPosition = currentSongs.indexOfFirst { it.id == currentSong.id }
            if (currentPosition != -1) {
                view?.navigateToPlayer(currentSong, currentSongs, currentPosition)
            }
        }
    }
    
    override fun onPlayPauseClicked() {
        musicService?.let { service ->
            service.playPause()

            // Immediately update UI to provide instant feedback
            service.getCurrentSong()?.let { song ->
                val isPlaying = service.isPlaying()
                val progress = if (service.getDuration() > 0) {
                    ((service.getCurrentPosition().toFloat() / service.getDuration()) * 100).toInt()
                } else 0
                view?.updateMiniPlayer(song, isPlaying, progress)
            }
        }
    }
    
    override fun onPreviousClicked() {
        musicService?.let { service ->
            service.previous()

            // Update UI after song change
            service.getCurrentSong()?.let { song ->
                val isPlaying = service.isPlaying()
                val progress = if (service.getDuration() > 0) {
                    ((service.getCurrentPosition().toFloat() / service.getDuration()) * 100).toInt()
                } else 0
                view?.updateMiniPlayer(song, isPlaying, progress)
            }
        }
    }

    override fun onNextClicked() {
        musicService?.let { service ->
            service.next()

            // Update UI after song change
            service.getCurrentSong()?.let { song ->
                val isPlaying = service.isPlaying()
                val progress = if (service.getDuration() > 0) {
                    ((service.getCurrentPosition().toFloat() / service.getDuration()) * 100).toInt()
                } else 0
                view?.updateMiniPlayer(song, isPlaying, progress)
            }
        }
    }
}
