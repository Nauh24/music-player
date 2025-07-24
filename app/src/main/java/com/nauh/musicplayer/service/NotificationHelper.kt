package com.nauh.musicplayer.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.app.NotificationCompat as MediaNotificationCompat
import com.nauh.musicplayer.R
import com.nauh.musicplayer.data.model.Song
import com.nauh.musicplayer.ui.PlayerActivity

/**
 * Helper class for managing music playback notifications
 * Creates and updates notifications with media controls
 */
class NotificationHelper(private val context: Context) {
    
    companion object {
        const val CHANNEL_ID = "music_playback_channel"
        const val NOTIFICATION_ID = 1001
        
        // Notification actions
        const val ACTION_PLAY_PAUSE = "action_play_pause"
        const val ACTION_PREVIOUS = "action_previous"
        const val ACTION_NEXT = "action_next"
        const val ACTION_STOP = "action_stop"
    }
    
    private val notificationManager = NotificationManagerCompat.from(context)
    
    init {
        createNotificationChannel()
    }
    
    /**
     * Create notification channel for Android O and above
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controls for music playback"
                setShowBadge(false)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Create or update the music playback notification
     */
    fun createNotification(
        song: Song,
        isPlaying: Boolean,
        canGoPrevious: Boolean = true,
        canGoNext: Boolean = true
    ): android.app.Notification {
        
        // Intent to open the player activity when notification is tapped
        val contentIntent = Intent(context, PlayerActivity::class.java).apply {
            putExtra(PlayerActivity.EXTRA_SONG, song)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Create action intents
        val playPauseIntent = createActionIntent(ACTION_PLAY_PAUSE)
        val previousIntent = createActionIntent(ACTION_PREVIOUS)
        val nextIntent = createActionIntent(ACTION_NEXT)
        val stopIntent = createActionIntent(ACTION_STOP)
        
        // Build the notification
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_music_note)
            .setContentTitle(song.title)
            .setContentText(song.getArtistAlbumText())
            .setSubText(song.album)
            .setContentIntent(contentPendingIntent)
            .setDeleteIntent(stopIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setPriority(NotificationCompat.PRIORITY_LOW)
        
        // Add media style with actions
        val mediaStyle = MediaNotificationCompat.MediaStyle()
            .setShowActionsInCompactView(0, 1, 2) // Show previous, play/pause, next in compact view
        
        builder.setStyle(mediaStyle)
        
        // Add action buttons
        if (canGoPrevious) {
            builder.addAction(
                R.drawable.ic_skip_previous,
                "Previous",
                previousIntent
            )
        }

        val playPauseIcon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow
        val playPauseText = if (isPlaying) "Pause" else "Play"
        builder.addAction(playPauseIcon, playPauseText, playPauseIntent)

        if (canGoNext) {
            builder.addAction(
                R.drawable.ic_skip_next,
                "Next",
                nextIntent
            )
        }
        
        return builder.build()
    }
    
    /**
     * Show the notification
     */
    fun showNotification(notification: android.app.Notification) {
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    /**
     * Cancel the notification
     */
    fun cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }
    
    /**
     * Create a PendingIntent for notification actions
     */
    private fun createActionIntent(action: String): PendingIntent {
        val intent = Intent(context, MusicService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            context,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
