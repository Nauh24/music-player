package com.nauh.musicplayer.service

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.nauh.musicplayer.data.model.Song
import com.nauh.musicplayer.ui.MainActivity

/**
 * Background music service using ExoPlayer and MediaSession
 * Handles audio playback, notifications, and media controls
 */
class MusicService : MediaSessionService() {
    
    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer
    
    override fun onCreate() {
        super.onCreate()
        initializePlayer()
        initializeMediaSession()
    }
    
    private fun initializePlayer() {
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
            val updatedMediaItems = mediaItems.map { mediaItem ->
                mediaItem.buildUpon()
                    .setUri(mediaItem.requestMetadata.mediaUri)
                    .build()
            }.toMutableList()
            return Futures.immediateFuture(updatedMediaItems)
        }
    }
    
    companion object {
        /**
         * Convert Song object to MediaItem for ExoPlayer
         */
        fun createMediaItem(song: Song): MediaItem {
            val metadata = MediaMetadata.Builder()
                .setTitle(song.title)
                .setArtist(song.artist)
                .setAlbumTitle(song.album)
                .setArtworkUri(android.net.Uri.parse(song.artworkUrl))
                .build()
            
            return MediaItem.Builder()
                .setUri(song.streamUrl)
                .setMediaId(song.id)
                .setMediaMetadata(metadata)
                .build()
        }
        
        /**
         * Create a list of MediaItems from a list of Songs
         */
        fun createMediaItems(songs: List<Song>): List<MediaItem> {
            return songs.map { createMediaItem(it) }
        }
    }
}
