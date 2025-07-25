package com.nauh.musicplayer.service

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.nauh.musicplayer.data.model.Song
import com.nauh.musicplayer.service.NotificationHelper
import com.nauh.musicplayer.ui.MainActivity

/**
 * Background music service using ExoPlayer and MediaSession
 * Handles audio playback, notifications, and media controls
 */
class MusicService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer
    private lateinit var notificationHelper: NotificationHelper
    private var currentSong: Song? = null



    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "MusicService onCreate")
        notificationHelper = NotificationHelper(this)
        initializePlayer()
        initializeMediaSession()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "MusicService onStartCommand")

        // Start foreground service if we have a current song
        currentSong?.let { song ->
            startForegroundWithNotification(song, player.isPlaying)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundWithNotification(song: Song, isPlaying: Boolean) {
        try {
            val notification = notificationHelper.createNotification(song, isPlaying)
            startForeground(NotificationHelper.NOTIFICATION_ID, notification)
            Log.d(TAG, "Started foreground service with notification")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start foreground service", e)
        }
    }

    private fun updateNotification(song: Song, isPlaying: Boolean) {
        try {
            val notification = notificationHelper.createNotification(song, isPlaying)
            notificationHelper.updateNotification(notification)

            // Start foreground service if not already started
            if (isPlaying) {
                startForeground(NotificationHelper.NOTIFICATION_ID, notification)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update notification", e)
        }
    }
    
    private fun initializePlayer() {
        Log.d(TAG, "Initializing ExoPlayer")
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true
            )
            .setHandleAudioBecomingNoisy(true)
            .build()

        // Add player listener for debugging and error handling
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                val stateString = when (playbackState) {
                    Player.STATE_IDLE -> "IDLE"
                    Player.STATE_BUFFERING -> "BUFFERING"
                    Player.STATE_READY -> "READY"
                    Player.STATE_ENDED -> "ENDED"
                    else -> "UNKNOWN"
                }
                Log.d(TAG, "Player state changed to: $stateString")

                // Log current media item when ready
                if (playbackState == Player.STATE_READY) {
                    val currentItem = player.currentMediaItem
                    Log.d(TAG, "Now playing: ${currentItem?.mediaMetadata?.title} from ${currentItem?.localConfiguration?.uri}")
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                Log.d(TAG, "Player isPlaying changed to: $isPlaying")

                // Update notification when playback state changes
                currentSong?.let { song ->
                    updateNotification(song, isPlaying)
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                Log.d(TAG, "Media item transition: ${mediaItem?.mediaMetadata?.title}")
                mediaItem?.let { item ->
                    // Update current song and notification
                    val song = Song(
                        id = item.mediaId,
                        title = item.mediaMetadata.title?.toString() ?: "Unknown",
                        artist = item.mediaMetadata.artist?.toString() ?: "Unknown",
                        album = item.mediaMetadata.albumTitle?.toString() ?: "Unknown",
                        duration = player.duration.takeIf { it > 0 } ?: 0,
                        artworkUrl = item.mediaMetadata.artworkUri?.toString() ?: "",
                        streamUrl = item.localConfiguration?.uri?.toString() ?: ""
                    )
                    currentSong = song
                    updateNotification(song, player.isPlaying)
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e(TAG, "Player error occurred: ${error.message}", error)
                Log.e(TAG, "Error code: ${error.errorCode}")

                // Log the problematic URL
                val currentItem = player.currentMediaItem
                Log.e(TAG, "Error with media item: ${currentItem?.localConfiguration?.uri}")
            }
        })

        Log.d(TAG, "ExoPlayer initialized successfully")
    }
    
    private fun initializeMediaSession() {
        val sessionActivityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        
        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(sessionActivityPendingIntent)
            .setCallback(MediaSessionCallback())
            .build()
    }
    
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }
    
    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
    
    /**
     * Custom MediaSession callback to handle media commands
     */
    private inner class MediaSessionCallback : MediaSession.Callback {

        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>
        ): ListenableFuture<MutableList<MediaItem>> {
            Log.d(TAG, "onAddMediaItems called with ${mediaItems.size} items")
            val updatedMediaItems = mediaItems.map { mediaItem ->
                Log.d(TAG, "Processing MediaItem: ${mediaItem.mediaMetadata.title} with URI: ${mediaItem.localConfiguration?.uri}")
                mediaItem.buildUpon()
                    .setUri(mediaItem.localConfiguration?.uri ?: mediaItem.requestMetadata.mediaUri)
                    .build()
            }.toMutableList()
            return Futures.immediateFuture(updatedMediaItems)
        }
    }
    
    companion object {
        private const val TAG = "MusicService"

        /**
         * Convert Song object to MediaItem for ExoPlayer
         */
        fun createMediaItem(song: Song): MediaItem {
            Log.d(TAG, "Creating MediaItem for song: ${song.title} with URL: ${song.streamUrl}")

            // Validate stream URL
            if (song.streamUrl.isBlank()) {
                Log.e(TAG, "Stream URL is empty for song: ${song.title}")
                throw IllegalArgumentException("Stream URL cannot be empty")
            }

            val metadata = MediaMetadata.Builder()
                .setTitle(song.title)
                .setArtist(song.artist)
                .setAlbumTitle(song.album)
                .setArtworkUri(
                    try {
                        android.net.Uri.parse(song.artworkUrl)
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to parse artwork URL: ${song.artworkUrl}", e)
                        null
                    }
                )
                .build()

            val mediaItem = MediaItem.Builder()
                .setUri(song.streamUrl)
                .setMediaId(song.id)
                .setMediaMetadata(metadata)
                .build()

            Log.d(TAG, "MediaItem created successfully for: ${song.title}")
            return mediaItem
        }
        
        /**
         * Create a list of MediaItems from a list of Songs
         */
        fun createMediaItems(songs: List<Song>): List<MediaItem> {
            return songs.map { createMediaItem(it) }
        }
    }
}
