package com.nauh.musicplayer.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.nauh.musicplayer.model.Song
import com.nauh.musicplayer.ui.MainActivity
import com.nauh.musicplayer.utils.Constants
import kotlinx.coroutines.*

class MusicService : Service() {
    
    private var exoPlayer: ExoPlayer? = null
    private val binder = MusicBinder()
    private var currentSong: Song? = null
    private var playlist: List<Song> = emptyList()
    private var currentPosition = 0
    private var isShuffleEnabled = false
    private var repeatMode = Player.REPEAT_MODE_OFF
    
    // Callbacks for UI updates
    private var onSongChangedCallback: ((Song?) -> Unit)? = null
    private var onPlayingStateChangedCallback: ((Boolean) -> Unit)? = null
    private var onProgressChangedCallback: ((Long, Long) -> Unit)? = null
    private var onShuffleChangedCallback: ((Boolean) -> Unit)? = null
    private var onRepeatModeChangedCallback: ((Int) -> Unit)? = null
    
    private var progressUpdateJob: Job? = null
    private lateinit var notificationHelper: NotificationHelper
    
    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }
    
    override fun onCreate() {
        super.onCreate()
        initializePlayer()
        notificationHelper = NotificationHelper(this)
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Constants.ACTION_PLAY -> playPause()
            Constants.ACTION_PAUSE -> pause()
            Constants.ACTION_PREVIOUS -> previous()
            Constants.ACTION_NEXT -> next()
            Constants.ACTION_STOP -> stopSelf()
        }

        return START_STICKY
    }
    
    private fun initializePlayer() {
        exoPlayer = ExoPlayer.Builder(this).build().apply {
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    onPlayingStateChangedCallback?.invoke(isPlaying)
                    updateNotification()

                    if (isPlaying) {
                        startProgressUpdate()
                    } else {
                        stopProgressUpdate()
                    }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_ENDED -> {
                            if (repeatMode == Player.REPEAT_MODE_ONE) {
                                seekTo(0)
                                play()
                            } else {
                                next()
                            }
                        }
                    }
                }
            })
        }
    }
    
    fun playSong(song: Song, songs: List<Song>, position: Int) {
        currentSong = song
        playlist = songs
        currentPosition = position
        
        val mediaItem = MediaItem.fromUri(song.url)
        exoPlayer?.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
        
        onSongChangedCallback?.invoke(song)
        updateNotification()
    }
    
    fun playPause() {
        exoPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }

            // Immediately notify UI about the state change
            // This provides instant feedback while ExoPlayer's callback might have a slight delay
            onPlayingStateChangedCallback?.invoke(player.isPlaying)
        }
    }
    
    fun pause() {
        exoPlayer?.let { player ->
            player.pause()
            // Immediately notify UI about the state change
            onPlayingStateChangedCallback?.invoke(player.isPlaying)
        }
    }
    
    fun previous() {
        if (playlist.isNotEmpty()) {
            currentPosition = if (currentPosition > 0) {
                currentPosition - 1
            } else {
                playlist.size - 1
            }
            playSong(playlist[currentPosition], playlist, currentPosition)
        }
    }
    
    fun next() {
        if (playlist.isNotEmpty()) {
            currentPosition = if (isShuffleEnabled) {
                (0 until playlist.size).random()
            } else {
                if (currentPosition < playlist.size - 1) {
                    currentPosition + 1
                } else {
                    0
                }
            }
            playSong(playlist[currentPosition], playlist, currentPosition)
        }
    }
    
    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
    }
    
    fun toggleShuffle() {
        isShuffleEnabled = !isShuffleEnabled
        onShuffleChangedCallback?.invoke(isShuffleEnabled)
    }

    fun toggleRepeat() {
        repeatMode = when (repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
        exoPlayer?.repeatMode = repeatMode
        onRepeatModeChangedCallback?.invoke(repeatMode)
    }
    
    fun getCurrentPosition(): Long = exoPlayer?.currentPosition ?: 0L
    fun getDuration(): Long = exoPlayer?.duration ?: 0L
    fun isPlaying(): Boolean = exoPlayer?.isPlaying ?: false
    fun getCurrentSong(): Song? = currentSong
    fun getShuffleEnabled(): Boolean = isShuffleEnabled
    fun getRepeatMode(): Int = repeatMode

    // Callback setters
    fun setOnSongChangedCallback(callback: (Song?) -> Unit) {
        onSongChangedCallback = callback
    }

    fun setOnPlayingStateChangedCallback(callback: (Boolean) -> Unit) {
        onPlayingStateChangedCallback = callback
    }

    fun setOnProgressChangedCallback(callback: (Long, Long) -> Unit) {
        onProgressChangedCallback = callback
    }

    fun setOnShuffleChangedCallback(callback: (Boolean) -> Unit) {
        onShuffleChangedCallback = callback
    }

    fun setOnRepeatModeChangedCallback(callback: (Int) -> Unit) {
        onRepeatModeChangedCallback = callback
    }
    
    private fun startProgressUpdate() {
        progressUpdateJob?.cancel()
        progressUpdateJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive && exoPlayer?.isPlaying == true) {
                val current = getCurrentPosition()
                val duration = getDuration()
                onProgressChangedCallback?.invoke(current, duration)
                delay(Constants.UPDATE_INTERVAL)
            }
        }
    }
    
    private fun stopProgressUpdate() {
        progressUpdateJob?.cancel()
    }
    
    private fun updateNotification() {
        currentSong?.let { song ->
            val notification = notificationHelper.createNotification(
                song = song,
                isPlaying = isPlaying(),
                playPauseIntent = createPendingIntent(Constants.ACTION_PLAY),
                previousIntent = createPendingIntent(Constants.ACTION_PREVIOUS),
                nextIntent = createPendingIntent(Constants.ACTION_NEXT)
            )
            startForeground(Constants.NOTIFICATION_ID, notification)
        }
    }
    
    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, MusicService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        stopProgressUpdate()
        exoPlayer?.release()
        exoPlayer = null
    }
}
