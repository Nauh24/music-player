package com.nauh.musicplayer.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nauh.musicplayer.R
import com.nauh.musicplayer.model.Song
import com.nauh.musicplayer.mvp.MainContract
import com.nauh.musicplayer.mvp.MainPresenter
import com.nauh.musicplayer.utils.Constants

class MainActivity : AppCompatActivity(), MainContract.View {
    
    private lateinit var presenter: MainPresenter
    private lateinit var songAdapter: SongAdapter
    
    // Views
    private lateinit var searchEditText: EditText
    private lateinit var songsRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var miniPlayer: View
    private lateinit var miniAlbumArtwork: ImageView
    private lateinit var miniSongTitle: TextView
    private lateinit var miniArtistName: TextView
    private lateinit var miniPlayPauseButton: ImageButton
    private lateinit var miniPreviousButton: ImageButton
    private lateinit var miniNextButton: ImageButton
    private lateinit var miniProgressBar: ProgressBar
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupRecyclerView()
        setupSearchView()
        setupMiniPlayer()
        
        presenter = MainPresenter(this, this)
        presenter.attachView(this)
        presenter.loadSongs()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
    
    private fun initViews() {
        searchEditText = findViewById(R.id.searchEditText)
        songsRecyclerView = findViewById(R.id.songsRecyclerView)
        progressBar = findViewById(R.id.progressBar)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        miniPlayer = findViewById(R.id.miniPlayer)
        miniAlbumArtwork = findViewById(R.id.miniAlbumArtwork)
        miniSongTitle = findViewById(R.id.miniSongTitle)
        miniArtistName = findViewById(R.id.miniArtistName)
        miniPlayPauseButton = findViewById(R.id.miniPlayPauseButton)
        miniPreviousButton = findViewById(R.id.miniPreviousButton)
        miniNextButton = findViewById(R.id.miniNextButton)
        miniProgressBar = findViewById(R.id.miniProgressBar)
    }
    
    private fun setupRecyclerView() {
        songAdapter = SongAdapter(
            onSongClick = { song, songs, position ->
                presenter.onSongClicked(song, songs, position)
            },
            onMoreOptionsClick = { song ->
                // Handle more options click (show popup menu, etc.)
                showMoreOptionsMenu(song)
            }
        )
        
        songsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = songAdapter
        }
    }
    
    private fun setupSearchView() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim() ?: ""
                presenter.searchSongs(query)
            }
        })
    }
    
    private fun setupMiniPlayer() {
        miniPlayer.setOnClickListener {
            presenter.onMiniPlayerClicked()
        }
        
        miniPlayPauseButton.setOnClickListener {
            presenter.onPlayPauseClicked()
        }
        
        miniPreviousButton.setOnClickListener {
            presenter.onPreviousClicked()
        }
        
        miniNextButton.setOnClickListener {
            presenter.onNextClicked()
        }
    }
    
    private fun showMoreOptionsMenu(song: Song) {
        // For now, just show a simple toast
        // In a real app, you might show a popup menu with options like "Add to playlist", "Share", etc.
        Toast.makeText(this, "More options for: ${song.title}", Toast.LENGTH_SHORT).show()
    }
    
    // MainContract.View implementation
    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
        songsRecyclerView.visibility = View.GONE
        emptyStateLayout.visibility = View.GONE
    }
    
    override fun hideLoading() {
        progressBar.visibility = View.GONE
    }
    
    override fun showSongs(songs: List<Song>) {
        songsRecyclerView.visibility = View.VISIBLE
        emptyStateLayout.visibility = View.GONE
        songAdapter.submitList(songs)
    }
    
    override fun showEmptyState() {
        songsRecyclerView.visibility = View.GONE
        emptyStateLayout.visibility = View.VISIBLE
    }
    
    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    
    override fun navigateToPlayer(song: Song, songs: List<Song>, position: Int) {
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra(Constants.EXTRA_SONG, song)
            putParcelableArrayListExtra(Constants.EXTRA_SONG_LIST, ArrayList(songs))
            putExtra(Constants.EXTRA_CURRENT_POSITION, position)
        }
        startActivity(intent)
    }
    
    override fun showMiniPlayer(song: Song) {
        miniPlayer.visibility = View.VISIBLE
        miniSongTitle.text = song.title
        miniArtistName.text = song.artist
        miniAlbumArtwork.setImageResource(R.drawable.placeholder_album_art)
        
        // Update the adapter to show playing indicator
        songAdapter.setCurrentPlayingSong(song.id)
    }
    
    override fun hideMiniPlayer() {
        miniPlayer.visibility = View.GONE
        songAdapter.setCurrentPlayingSong(null)
    }
    
    override fun updateMiniPlayer(song: Song, isPlaying: Boolean, progress: Int) {
        // Ensure we're on the main thread
        runOnUiThread {
            miniSongTitle.text = song.title
            miniArtistName.text = song.artist

            val playPauseIcon = if (isPlaying) {
                R.drawable.ic_pause
            } else {
                R.drawable.ic_play_arrow
            }
            miniPlayPauseButton.setImageResource(playPauseIcon)

            miniProgressBar.progress = progress

            // Update the adapter to show playing indicator
            songAdapter.setCurrentPlayingSong(song.id)

            // Ensure mini player is visible
            if (miniPlayer.visibility != View.VISIBLE) {
                miniPlayer.visibility = View.VISIBLE
            }
        }
    }
}
