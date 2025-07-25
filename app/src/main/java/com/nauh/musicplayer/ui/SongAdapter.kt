package com.nauh.musicplayer.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nauh.musicplayer.R
import com.nauh.musicplayer.model.Song

class SongAdapter(
    private val onSongClick: (Song, List<Song>, Int) -> Unit,
    private val onMoreOptionsClick: (Song) -> Unit
) : ListAdapter<Song, SongAdapter.SongViewHolder>(SongDiffCallback()) {
    
    private var currentPlayingSongId: String? = null
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = getItem(position)
        holder.bind(song, position)
    }
    
    fun setCurrentPlayingSong(songId: String?) {
        val oldPlayingSongId = currentPlayingSongId
        currentPlayingSongId = songId
        
        // Update the old playing song item
        oldPlayingSongId?.let { oldId ->
            val oldPosition = currentList.indexOfFirst { it.id == oldId }
            if (oldPosition != -1) {
                notifyItemChanged(oldPosition)
            }
        }
        
        // Update the new playing song item
        songId?.let { newId ->
            val newPosition = currentList.indexOfFirst { it.id == newId }
            if (newPosition != -1) {
                notifyItemChanged(newPosition)
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
        
        fun bind(song: Song, position: Int) {
            songTitle.text = song.title
            artistAlbum.text = song.getArtistAlbum()
            songDuration.text = song.getFormattedDuration()
            
            // Show/hide playing indicator
            val isCurrentlyPlaying = song.id == currentPlayingSongId
            playingIndicator.visibility = if (isCurrentlyPlaying) View.VISIBLE else View.GONE
            
            // Set click listeners
            itemView.setOnClickListener {
                onSongClick(song, currentList, position)
            }
            
            moreOptions.setOnClickListener {
                onMoreOptionsClick(song)
            }
            
            // Load album artwork (placeholder for now)
            albumArtwork.setImageResource(R.drawable.placeholder_album_art)
        }
    }
    
    private class SongDiffCallback : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem
        }
    }
}
