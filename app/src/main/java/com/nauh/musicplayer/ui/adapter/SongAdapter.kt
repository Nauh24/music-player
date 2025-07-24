package com.nauh.musicplayer.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.nauh.musicplayer.R
import com.nauh.musicplayer.data.model.Song

/**
 * RecyclerView adapter for displaying songs in a list
 * Uses ListAdapter with DiffUtil for efficient updates
 */
class SongAdapter(
    private val onSongClick: (Song, List<Song>) -> Unit,
    private val onMoreOptionsClick: (Song) -> Unit = {}
) : ListAdapter<Song, SongAdapter.SongViewHolder>(SongDiffCallback()) {

    private var currentPlayingSong: Song? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = getItem(position)
        holder.bind(song, currentPlayingSong?.id == song.id)
    }

    /**
     * Update the currently playing song to show visual indicator
     */
    fun updateCurrentPlayingSong(song: Song?) {
        val oldPlayingSong = currentPlayingSong
        currentPlayingSong = song
        
        // Update the old playing song item
        oldPlayingSong?.let { oldSong ->
            val oldIndex = currentList.indexOfFirst { it.id == oldSong.id }
            if (oldIndex != -1) {
                notifyItemChanged(oldIndex)
            }
        }
        
        // Update the new playing song item
        song?.let { newSong ->
            val newIndex = currentList.indexOfFirst { it.id == newSong.id }
            if (newIndex != -1) {
                notifyItemChanged(newIndex)
            }
        }
    }

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val albumArtwork: ImageView = itemView.findViewById(R.id.albumArtwork)
        private val songTitle: TextView = itemView.findViewById(R.id.songTitle)
        private val artistAlbum: TextView = itemView.findViewById(R.id.artistAlbum)
        private val songDuration: TextView = itemView.findViewById(R.id.songDuration)
        private val moreOptions: ImageButton = itemView.findViewById(R.id.moreOptions)
        private val playingIndicator: ImageView = itemView.findViewById(R.id.playingIndicator)

        fun bind(song: Song, isCurrentlyPlaying: Boolean) {
            songTitle.text = song.title
            artistAlbum.text = song.getArtistAlbumText()
            songDuration.text = song.getFormattedDuration()
            
            // Show/hide playing indicator
            playingIndicator.visibility = if (isCurrentlyPlaying) View.VISIBLE else View.GONE
            
            // Load album artwork
            Glide.with(itemView.context)
                .load(song.artworkUrl)
                .placeholder(R.drawable.placeholder_album_art)
                .error(R.drawable.placeholder_album_art)
                .transform(RoundedCorners(16))
                .into(albumArtwork)
            
            // Set click listeners
            itemView.setOnClickListener {
                onSongClick(song, currentList)
            }
            
            moreOptions.setOnClickListener {
                onMoreOptionsClick(song)
            }
        }
    }

    /**
     * DiffUtil callback for efficient list updates
     */
    private class SongDiffCallback : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem
        }
    }
}
