package com.nauh.musicplayer.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nauh.musicplayer.R
import com.nauh.musicplayer.contract.MainContract
import com.nauh.musicplayer.data.model.Song
import com.nauh.musicplayer.presenter.MainPresenter
import com.nauh.musicplayer.ui.adapter.SongAdapter

/**
 * Main Activity implementing MVP pattern
 * Displays the list of songs and handles user interactions
 */
class MainActivity : AppCompatActivity(), MainContract.View {

    private lateinit var presenter: MainPresenter
    private lateinit var songAdapter: SongAdapter

    // UI Components
    private lateinit var searchEditText: EditText
    private lateinit var songsRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var miniPlayer: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupRecyclerView()
        setupSearchView()
        initializePresenter()

        // Load songs when activity starts
        presenter.loadSongs()
    }

    private fun initializeViews() {
        // Setup toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        searchEditText = findViewById(R.id.searchEditText)
        songsRecyclerView = findViewById(R.id.songsRecyclerView)
        progressBar = findViewById(R.id.progressBar)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        miniPlayer = findViewById(R.id.miniPlayer)
    }

    private fun setupRecyclerView() {
        songAdapter = SongAdapter(
            onSongClick = { song, playlist ->
                presenter.onSongClicked(song, playlist)
            },
            onMoreOptionsClick = { song ->
                // Handle more options click (e.g., show popup menu)
                showSongOptions(song)
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
                if (query.isEmpty()) {
                    presenter.clearSearch()
                } else {
                    presenter.searchSongs(query)
                }
            }
        })
    }

    private fun initializePresenter() {
        presenter = MainPresenter()
        presenter.attachView(this)
    }

    private fun showSongOptions(song: Song) {
        // For now, just show a toast
        // In a real app, this would show a popup menu with options like "Add to playlist", "Share", etc.
        Toast.makeText(this, "Options for ${song.title}", Toast.LENGTH_SHORT).show()
    }

    // MVP View Interface Implementation
    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
        songsRecyclerView.visibility = View.GONE
        emptyStateLayout.visibility = View.GONE
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    override fun showSongs(songs: List<Song>) {
        hideLoading()
        songsRecyclerView.visibility = View.VISIBLE
        emptyStateLayout.visibility = View.GONE
        songAdapter.submitList(songs)
    }

    override fun showError(message: String) {
        hideLoading()
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showEmptyState() {
        hideLoading()
        songsRecyclerView.visibility = View.GONE
        emptyStateLayout.visibility = View.VISIBLE
    }

    override fun navigateToPlayer(song: Song, playlist: List<Song>) {
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra(PlayerActivity.EXTRA_SONG, song)
            putParcelableArrayListExtra(PlayerActivity.EXTRA_PLAYLIST, ArrayList(playlist))
        }
        startActivity(intent)
    }

    override fun updateCurrentPlayingSong(song: Song?) {
        songAdapter.updateCurrentPlayingSong(song)
        // Show/hide mini player based on whether a song is playing
        miniPlayer.visibility = if (song != null) View.VISIBLE else View.GONE
    }

    override fun showSearchResults(songs: List<Song>) {
        showSongs(songs)
    }

    override fun clearSearchResults() {
        // This will be handled by showing all songs again
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}