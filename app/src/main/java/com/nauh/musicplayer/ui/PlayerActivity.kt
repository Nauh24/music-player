package com.nauh.musicplayer.ui

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.media3.common.Player
import com.nauh.musicplayer.R
import com.nauh.musicplayer.model.Song
import com.nauh.musicplayer.mvp.PlayerContract
import com.nauh.musicplayer.mvp.PlayerPresenter
import com.nauh.musicplayer.utils.Constants
import com.nauh.musicplayer.utils.TimeUtils

class PlayerActivity : AppCompatActivity(), PlayerContract.View {
    
    private lateinit var presenter: PlayerPresenter
    
    // Views
    private lateinit var toolbar: Toolbar
    private lateinit var playerAlbumArtwork: ImageView
    private lateinit var playerSongTitle: TextView
    private lateinit var playerArtistName: TextView
    private lateinit var playerAlbumName: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var currentTime: TextView
    private lateinit var totalTime: TextView
    private lateinit var shuffleButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var playPauseButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var repeatButton: ImageButton
    private lateinit var playerProgressBar: ProgressBar
    
    private var isUserSeeking = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        
        initViews()
        setupToolbar()
        setupSeekBar()
        setupClickListeners()
        
        presenter = PlayerPresenter(this, this)
        presenter.attachView(this)
        
        // Get data from intent
        val song = intent.getParcelableExtra<Song>(Constants.EXTRA_SONG)
        val songs = intent.getParcelableArrayListExtra<Song>(Constants.EXTRA_SONG_LIST) ?: emptyList()
        val position = intent.getIntExtra(Constants.EXTRA_CURRENT_POSITION, 0)
        
        if (song != null) {
            presenter.initializePlayer(song, songs, position)
        } else {
            finish()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
    
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        presenter.onBackPressed()
    }
    
    private fun initViews() {
        toolbar = findViewById(R.id.playerToolbar)
        playerAlbumArtwork = findViewById(R.id.playerAlbumArtwork)
        playerSongTitle = findViewById(R.id.playerSongTitle)
        playerArtistName = findViewById(R.id.playerArtistName)
        playerAlbumName = findViewById(R.id.playerAlbumName)
        seekBar = findViewById(R.id.seekBar)
        currentTime = findViewById(R.id.currentTime)
        totalTime = findViewById(R.id.totalTime)
        shuffleButton = findViewById(R.id.shuffleButton)
        previousButton = findViewById(R.id.previousButton)
        playPauseButton = findViewById(R.id.playPauseButton)
        nextButton = findViewById(R.id.nextButton)
        repeatButton = findViewById(R.id.repeatButton)
        playerProgressBar = findViewById(R.id.playerProgressBar)
    }
    
    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    
    private fun setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    currentTime.text = TimeUtils.formatTime((progress * seekBar!!.max / 100).toLong())
                }
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isUserSeeking = true
            }
            
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isUserSeeking = false
                val position = (seekBar!!.progress.toFloat() / seekBar.max * seekBar.tag as Long).toLong()
                presenter.onSeekTo(position)
            }
        })
    }
    
    private fun setupClickListeners() {
        shuffleButton.setOnClickListener {
            presenter.onShuffleClicked()
        }
        
        previousButton.setOnClickListener {
            presenter.onPreviousClicked()
        }
        
        playPauseButton.setOnClickListener {
            presenter.onPlayPauseClicked()
        }
        
        nextButton.setOnClickListener {
            presenter.onNextClicked()
        }
        
        repeatButton.setOnClickListener {
            presenter.onRepeatClicked()
        }
    }
    
    // PlayerContract.View implementation
    override fun showLoading() {
        playerProgressBar.visibility = View.VISIBLE
    }
    
    override fun hideLoading() {
        playerProgressBar.visibility = View.GONE
    }
    
    override fun updateSongInfo(song: Song) {
        playerSongTitle.text = song.title
        playerArtistName.text = song.artist
        playerAlbumName.text = song.album
        playerAlbumArtwork.setImageResource(R.drawable.placeholder_album_art)
        
        // Set total time
        totalTime.text = song.getFormattedDuration()
        seekBar.tag = song.duration // Store duration for seek calculation
    }
    
    override fun updatePlayPauseButton(isPlaying: Boolean) {
        val iconRes = if (isPlaying) {
            R.drawable.ic_pause
        } else {
            R.drawable.ic_play_arrow
        }
        playPauseButton.setImageResource(iconRes)
    }
    
    override fun updateProgress(currentPosition: Long, duration: Long) {
        if (!isUserSeeking && duration > 0) {
            val progress = ((currentPosition.toFloat() / duration) * 100).toInt()
            seekBar.progress = progress
            currentTime.text = TimeUtils.formatTime(currentPosition)
            
            if (seekBar.tag == null) {
                seekBar.tag = duration
                totalTime.text = TimeUtils.formatTime(duration)
            }
        }
    }
    
    override fun updateShuffleButton(isShuffleEnabled: Boolean) {
        val tintColor = if (isShuffleEnabled) {
            getColor(R.color.primary)
        } else {
            getColor(R.color.text_secondary)
        }
        shuffleButton.setColorFilter(tintColor)
    }
    
    override fun updateRepeatButton(repeatMode: Int) {
        val (iconRes, tintColor) = when (repeatMode) {
            Player.REPEAT_MODE_OFF -> {
                R.drawable.ic_repeat to getColor(R.color.text_secondary)
            }
            Player.REPEAT_MODE_ALL -> {
                R.drawable.ic_repeat to getColor(R.color.primary)
            }
            Player.REPEAT_MODE_ONE -> {
                R.drawable.ic_repeat_one to getColor(R.color.primary)
            }
            else -> {
                R.drawable.ic_repeat to getColor(R.color.text_secondary)
            }
        }
        
        repeatButton.setImageResource(iconRes)
        repeatButton.setColorFilter(tintColor)
    }
    
    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
