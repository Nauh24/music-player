package com.nauh.musicplayer.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.nauh.musicplayer.R
import com.nauh.musicplayer.contract.PlayerContract
import com.nauh.musicplayer.data.model.Song
import com.nauh.musicplayer.presenter.PlayerPresenter

/**
 * Player Activity implementing MVP pattern
 * Provides full-screen music player interface with controls
 */
class PlayerActivity : AppCompatActivity(), PlayerContract.View {

    companion object {
        const val EXTRA_SONG = "extra_song"
        const val EXTRA_PLAYLIST = "extra_playlist"
    }

    private lateinit var presenter: PlayerPresenter
    
    // UI Components
    private lateinit var toolbar: Toolbar
    private lateinit var albumArtwork: ImageView
    private lateinit var songTitle: TextView
    private lateinit var artistName: TextView
    private lateinit var albumName: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var currentTime: TextView
    private lateinit var totalTime: TextView
    private lateinit var playPauseButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var shuffleButton: ImageButton
    private lateinit var repeatButton: ImageButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        
        initializeViews()
        setupToolbar()
        setupSeekBar()
        initializePresenter()
        
        // Get song and playlist from intent
        val song = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_SONG, Song::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Song>(EXTRA_SONG)
        }

        val playlist = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra(EXTRA_PLAYLIST, Song::class.java) ?: emptyList()
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra<Song>(EXTRA_PLAYLIST) ?: emptyList()
        }
        
        if (song != null) {
            presenter.initializePlayer(song, playlist)
        } else {
            finish() // Close activity if no song provided
        }
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.playerToolbar)
        albumArtwork = findViewById(R.id.playerAlbumArtwork)
        songTitle = findViewById(R.id.playerSongTitle)
        artistName = findViewById(R.id.playerArtistName)
        albumName = findViewById(R.id.playerAlbumName)
        seekBar = findViewById(R.id.seekBar)
        currentTime = findViewById(R.id.currentTime)
        totalTime = findViewById(R.id.totalTime)
        playPauseButton = findViewById(R.id.playPauseButton)
        previousButton = findViewById(R.id.previousButton)
        nextButton = findViewById(R.id.nextButton)
        shuffleButton = findViewById(R.id.shuffleButton)
        repeatButton = findViewById(R.id.repeatButton)
        progressBar = findViewById(R.id.playerProgressBar)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    // Calculate position based on progress and duration
                    // This will be implemented when we integrate with actual player
                }
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let { bar ->
                    // Seek to the selected position
                    val progress = bar.progress
                    val duration = bar.max
                    if (duration > 0) {
                        val position = (progress.toFloat() / 100f * duration).toLong()
                        presenter.seekTo(position)
                    }
                }
            }
        })
        
        // Setup control button listeners
        playPauseButton.setOnClickListener {
            presenter.playPause()
        }
        
        previousButton.setOnClickListener {
            presenter.skipToPrevious()
        }
        
        nextButton.setOnClickListener {
            presenter.skipToNext()
        }
        
        shuffleButton.setOnClickListener {
            presenter.toggleShuffle()
        }
        
        repeatButton.setOnClickListener {
            presenter.toggleRepeat()
        }
    }

    private fun initializePresenter() {
        presenter = PlayerPresenter()
        presenter.attachView(this)
        presenter.initializeMusicService(this)
    }

    // MVP View Interface Implementation
    override fun showSongInfo(song: Song) {
        songTitle.text = song.title
        artistName.text = song.artist
        albumName.text = song.album
        totalTime.text = song.getFormattedDuration()
        
        // Load album artwork
        Glide.with(this)
            .load(song.artworkUrl)
            .placeholder(R.drawable.placeholder_album_art)
            .error(R.drawable.placeholder_album_art)
            .transform(RoundedCorners(32))
            .into(albumArtwork)
    }

    override fun updatePlayPauseButton(isPlaying: Boolean) {
        val iconRes = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow
        playPauseButton.setImageResource(iconRes)
    }

    override fun updateProgress(currentPosition: Long, duration: Long) {
        val minutes = (currentPosition / 1000) / 60
        val seconds = (currentPosition / 1000) % 60
        currentTime.text = String.format("%02d:%02d", minutes, seconds)
    }

    override fun updateSeekBar(position: Int, max: Int) {
        seekBar.max = max
        seekBar.progress = position
    }

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun enablePreviousButton(enabled: Boolean) {
        previousButton.isEnabled = enabled
        previousButton.alpha = if (enabled) 1.0f else 0.5f
    }

    override fun enableNextButton(enabled: Boolean) {
        nextButton.isEnabled = enabled
        nextButton.alpha = if (enabled) 1.0f else 0.5f
    }

    override fun showShuffleState(isShuffled: Boolean) {
        val color = if (isShuffled) {
            ContextCompat.getColor(this, R.color.primary)
        } else {
            ContextCompat.getColor(this, R.color.text_secondary)
        }
        shuffleButton.setColorFilter(color)
    }

    override fun showRepeatState(repeatMode: Int) {
        val (iconRes, color) = when (repeatMode) {
            PlayerContract.RepeatMode.OFF -> {
                R.drawable.ic_repeat to ContextCompat.getColor(this, R.color.text_secondary)
            }
            PlayerContract.RepeatMode.ALL -> {
                R.drawable.ic_repeat to ContextCompat.getColor(this, R.color.primary)
            }
            PlayerContract.RepeatMode.ONE -> {
                R.drawable.ic_repeat to ContextCompat.getColor(this, R.color.primary)
            }
            else -> {
                R.drawable.ic_repeat to ContextCompat.getColor(this, R.color.text_secondary)
            }
        }
        
        repeatButton.setImageResource(iconRes)
        repeatButton.setColorFilter(color)
    }

    override fun updatePlaylist(songs: List<Song>, currentIndex: Int) {
        // Update playlist information if needed
        // This could be used to show playlist info or update navigation buttons
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}
