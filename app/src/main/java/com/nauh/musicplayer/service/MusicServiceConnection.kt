package com.nauh.musicplayer.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.nauh.musicplayer.data.model.Song

/**
 * Service connection class to manage communication between UI and MusicService
 * Handles MediaController setup and provides playback control methods
 */
class MusicServiceConnection(private val context: Context) {

    private var mediaControllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null
    private var playbackStateListener: PlaybackStateListener? = null
    private var isConnected = false
    private var pendingPlaylist: Pair<List<Song>, Int>? = null
    private var progressUpdateHandler: Handler? = null
    private var progressUpdateRunnable: Runnable? = null

    companion object {
        private const val TAG = "MusicServiceConnection"
        private const val PROGRESS_UPDATE_INTERVAL = 1000L // 1 second
    }
    
    interface PlaybackStateListener {
        fun onPlaybackStateChanged(isPlaying: Boolean)
        fun onProgressUpdate(position: Long, duration: Long)
        fun onCurrentSongChanged(song: Song?)
        fun onPlaybackError(error: String)
        fun onConnectionError(error: String)
    }
    
    fun setPlaybackStateListener(listener: PlaybackStateListener) {
        this.playbackStateListener = listener
    }
    
    fun connect() {
        Log.d(TAG, "Connecting to MusicService...")

        // Start the service first to ensure it's running
        val serviceIntent = Intent(context, MusicService::class.java)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
            Log.d(TAG, "Service started successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start service", e)
            playbackStateListener?.onConnectionError("Failed to start music service")
            return
        }

        val sessionToken = SessionToken(context, ComponentName(context, MusicService::class.java))
        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        mediaControllerFuture?.addListener({
            try {
                mediaController = mediaControllerFuture?.get()
                if (mediaController != null) {
                    onMediaControllerConnected()
                } else {
                    Log.e(TAG, "MediaController is null after connection")
                    playbackStateListener?.onConnectionError("Failed to connect to music service")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting MediaController", e)
                playbackStateListener?.onConnectionError("Connection error: ${e.message}")
            }
        }, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            context.mainExecutor
        } else {
            // For API < 28, use a handler-based executor
            java.util.concurrent.Executor { command ->
                Handler(Looper.getMainLooper()).post(command)
            }
        })
    }
    
    private fun onMediaControllerConnected() {
        Log.d(TAG, "MediaController connected successfully")
        isConnected = true
        
        mediaController?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                Log.d(TAG, "Playback state changed: isPlaying = $isPlaying")
                playbackStateListener?.onPlaybackStateChanged(isPlaying)
                
                if (isPlaying) {
                    startProgressUpdates()
                } else {
                    stopProgressUpdates()
                }
            }
            
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                Log.d(TAG, "Media item changed: ${mediaItem?.mediaMetadata?.title}")
                mediaItem?.let { item ->
                    // Convert MediaItem back to Song
                    val song = Song(
                        id = item.mediaId,
                        title = item.mediaMetadata.title?.toString() ?: "Unknown",
                        artist = item.mediaMetadata.artist?.toString() ?: "Unknown",
                        album = item.mediaMetadata.albumTitle?.toString() ?: "Unknown",
                        duration = mediaController?.duration ?: 0,
                        artworkUrl = item.mediaMetadata.artworkUri?.toString() ?: "",
                        streamUrl = item.localConfiguration?.uri?.toString() ?: ""
                    )
                    playbackStateListener?.onCurrentSongChanged(song)
                }
            }
            
            override fun onPlaybackStateChanged(playbackState: Int) {
                Log.d(TAG, "Player state changed: $playbackState")
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        Log.d(TAG, "Player is buffering")
                    }
                    Player.STATE_READY -> {
                        Log.d(TAG, "Player is ready")
                    }
                    Player.STATE_ENDED -> {
                        Log.d(TAG, "Playback ended")
                        stopProgressUpdates()
                    }
                    Player.STATE_IDLE -> {
                        Log.d(TAG, "Player is idle")
                    }
                }
            }
        })
        
        // Handle pending playlist if any
        pendingPlaylist?.let { (songs, index) ->
            Log.d(TAG, "Playing pending playlist")
            playPlaylist(songs, index)
            pendingPlaylist = null
        }
    }
    
    private fun startProgressUpdates() {
        if (progressUpdateHandler == null) {
            progressUpdateHandler = Handler(Looper.getMainLooper())
        }
        
        progressUpdateRunnable = object : Runnable {
            override fun run() {
                mediaController?.let { controller ->
                    val position = controller.currentPosition
                    val duration = controller.duration
                    if (duration > 0) {
                        playbackStateListener?.onProgressUpdate(position, duration)
                    }
                }
                progressUpdateHandler?.postDelayed(this, PROGRESS_UPDATE_INTERVAL)
            }
        }
        
        progressUpdateRunnable?.let { runnable ->
            progressUpdateHandler?.post(runnable)
        }
    }
    
    private fun stopProgressUpdates() {
        progressUpdateRunnable?.let { runnable ->
            progressUpdateHandler?.removeCallbacks(runnable)
        }
    }
    
    fun isConnected(): Boolean {
        return isConnected && mediaController != null
    }

    fun disconnect() {
        Log.d(TAG, "Disconnecting from MusicService")
        isConnected = false
        pendingPlaylist = null

        stopProgressUpdates()
        progressUpdateHandler = null
        progressUpdateRunnable = null

        mediaController?.release()
        mediaControllerFuture?.cancel(true)
        mediaController = null
        mediaControllerFuture = null
    }
    
    fun playPlaylist(songs: List<Song>, startIndex: Int = 0) {
        if (!isConnected || mediaController == null) {
            Log.d(TAG, "Not connected, storing playlist for later")
            pendingPlaylist = Pair(songs, startIndex)
            return
        }

        Log.d(TAG, "Playing playlist with ${songs.size} songs, starting at index $startIndex")

        try {
            val mediaItems = songs.map { song -> MusicService.createMediaItem(song) }
            mediaController?.setMediaItems(mediaItems, startIndex, 0)
            mediaController?.prepare()
            mediaController?.play()
            Log.d(TAG, "Playlist started successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error playing playlist", e)
            playbackStateListener?.onPlaybackError("Failed to play playlist: ${e.message}")
        }
    }
    
    fun playSong(song: Song) {
        playPlaylist(listOf(song), 0)
    }
    
    fun playPause() {
        mediaController?.let { controller ->
            if (controller.isPlaying) {
                controller.pause()
            } else {
                controller.play()
            }
        }
    }
    
    fun skipToNext() {
        mediaController?.seekToNext()
    }
    
    fun skipToPrevious() {
        mediaController?.seekToPrevious()
    }
    
    fun seekTo(position: Long) {
        mediaController?.seekTo(position)
    }
    
    fun isPlaying(): Boolean {
        return mediaController?.isPlaying ?: false
    }
    
    fun getCurrentPosition(): Long {
        return mediaController?.currentPosition ?: 0
    }
    
    fun getDuration(): Long {
        return mediaController?.duration ?: 0
    }
}
