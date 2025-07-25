package com.nauh.musicplayer.utils

object Constants {
    const val NOTIFICATION_CHANNEL_ID = "music_playback_channel"
    const val NOTIFICATION_ID = 1001
    
    const val ACTION_PLAY = "action_play"
    const val ACTION_PAUSE = "action_pause"
    const val ACTION_PREVIOUS = "action_previous"
    const val ACTION_NEXT = "action_next"
    const val ACTION_STOP = "action_stop"
    
    const val EXTRA_SONG = "extra_song"
    const val EXTRA_SONG_LIST = "extra_song_list"
    const val EXTRA_CURRENT_POSITION = "extra_current_position"
    
    const val UPDATE_INTERVAL = 1000L // 1 second
}
