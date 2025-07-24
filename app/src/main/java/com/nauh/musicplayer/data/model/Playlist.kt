package com.nauh.musicplayer.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data model representing a playlist containing multiple songs
 */
@Parcelize
data class Playlist(
    val id: String,
    val name: String,
    val description: String? = null,
    val songs: List<Song> = emptyList(),
    val artworkUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable {
    
    /**
     * Returns total duration of all songs in the playlist
     */
    fun getTotalDuration(): Long {
        return songs.sumOf { it.duration }
    }
    
    /**
     * Returns formatted total duration string
     */
    fun getFormattedTotalDuration(): String {
        val totalSeconds = getTotalDuration() / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, totalSeconds % 60)
        } else {
            String.format("%02d:%02d", minutes, totalSeconds % 60)
        }
    }
    
    /**
     * Returns the number of songs in the playlist
     */
    fun getSongCount(): Int = songs.size
}
