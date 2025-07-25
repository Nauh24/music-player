package com.nauh.musicplayer.mvp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.media3.common.Player
import com.nauh.musicplayer.model.Song
import com.nauh.musicplayer.service.MusicService
import kotlinx.coroutines.*

class PlayerPresenter(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner
) : PlayerContract.Presenter {
    
    private var view: PlayerContract.View? = null
    private var musicService: MusicService? = null
    private var isBound = false
    
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
    
    override fun attachView(view: PlayerContract.View) {
        this.view = view
        bindMusicService()
    }
    
    override fun detachView() {
        this.view = null
        unbindMusicService()
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
                    view?.updateSongInfo(it)
                }
            }

            service.setOnPlayingStateChangedCallback { isPlaying ->
                view?.updatePlayPauseButton(isPlaying)
            }

            service.setOnProgressChangedCallback { current, duration ->
                view?.updateProgress(current, duration)
            }

            service.setOnShuffleChangedCallback { isShuffleEnabled ->
                view?.updateShuffleButton(isShuffleEnabled)
            }

            service.setOnRepeatModeChangedCallback { repeatMode ->
                view?.updateRepeatButton(repeatMode)
            }
        }
    }
    
    override fun initializePlayer(song: Song, songs: List<Song>, position: Int) {
        view?.showLoading()
        
        // Start music service if not already started
        val intent = Intent(context, MusicService::class.java)
        context.startService(intent)
        
        // Wait for service to be bound, then play the song
        if (isBound) {
            musicService?.playSong(song, songs, position)
        } else {
            // Service will be bound soon, the song will be played when service connects
            // For now, just update the UI with song info
            view?.updateSongInfo(song)
        }
        
        view?.hideLoading()
    }
    
    override fun onPlayPauseClicked() {
        musicService?.playPause()
    }
    
    override fun onPreviousClicked() {
        musicService?.previous()
    }
    
    override fun onNextClicked() {
        musicService?.next()
    }
    
    override fun onShuffleClicked() {
        musicService?.toggleShuffle()
    }
    
    override fun onRepeatClicked() {
        musicService?.toggleRepeat()
    }
    
    override fun onSeekTo(position: Long) {
        musicService?.seekTo(position)
    }
    
    override fun onBackPressed() {
        view?.finish()
    }
}
