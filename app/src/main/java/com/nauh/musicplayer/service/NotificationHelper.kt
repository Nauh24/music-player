package com.nauh.musicplayer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.nauh.musicplayer.R
import com.nauh.musicplayer.model.Song
import com.nauh.musicplayer.ui.MainActivity
import com.nauh.musicplayer.utils.Constants

class NotificationHelper(private val context: Context) {
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = context.getString(R.string.notification_channel_description)
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun createNotification(
        song: Song,
        isPlaying: Boolean,
        playPauseIntent: PendingIntent,
        previousIntent: PendingIntent,
        nextIntent: PendingIntent
    ): Notification {
        
        val contentIntent = Intent(context, MainActivity::class.java).let { intent ->
            PendingIntent.getActivity(
                context, 
                0, 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        
        val playPauseIcon = if (isPlaying) {
            R.drawable.ic_pause
        } else {
            R.drawable.ic_play_arrow
        }
        
        val playPauseText = if (isPlaying) {
            context.getString(R.string.pause)
        } else {
            context.getString(R.string.play)
        }
        
        return NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(song.title)
            .setContentText(song.artist)
            .setSubText(song.album)
            .setSmallIcon(R.drawable.ic_music_note)
            .setContentIntent(contentIntent)
            .setDeleteIntent(createStopIntent())
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(
                NotificationCompat.Action.Builder(
                    R.drawable.ic_skip_previous,
                    context.getString(R.string.previous),
                    previousIntent
                ).build()
            )
            .addAction(
                NotificationCompat.Action.Builder(
                    playPauseIcon,
                    playPauseText,
                    playPauseIntent
                ).build()
            )
            .addAction(
                NotificationCompat.Action.Builder(
                    R.drawable.ic_skip_next,
                    context.getString(R.string.next),
                    nextIntent
                ).build()
            )
            .setStyle(
                MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(createStopIntent())
            )
            .build()
    }
    
    private fun createStopIntent(): PendingIntent {
        val intent = Intent(context, MusicService::class.java).apply {
            action = Constants.ACTION_STOP
        }
        return PendingIntent.getService(
            context,
            Constants.ACTION_STOP.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
