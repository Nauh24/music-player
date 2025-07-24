package com.nauh.musicplayer.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data model representing a song with all necessary information for streaming
 */
@Parcelize
data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long, // Duration in milliseconds
    val artworkUrl: String,
    val streamUrl: String,
    val genre: String? = null,
    val releaseYear: Int? = null,
    val trackNumber: Int? = null
) : Parcelable {
    
    /**
     * Returns formatted duration string (mm:ss)
     */
    fun getFormattedDuration(): String {
        val minutes = (duration / 1000) / 60
        val seconds = (duration / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
    
    /**
     * Returns display text for artist and album
     */
    fun getArtistAlbumText(): String {
        return "$artist • $album"
    }
}
