# 🎵 Android Music Player

<div align="center">

![Android](https://img.shields.io/badge/Platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)
![MVP](https://img.shields.io/badge/Architecture-MVP-orange.svg)
![ExoPlayer](https://img.shields.io/badge/Audio-ExoPlayer-red.svg)


*A modern Android music player built with MVP architecture and ExoPlayer*

</div>

## ✨ Features

- 🎵 **High-Quality Audio Streaming** - Powered by ExoPlayer for seamless playback
- 🎨 **Material Design UI** - Clean, intuitive interface following Material Design guidelines
- 🔍 **Search** - Search songs by title, artist, or album
- 🎛️ **Full Playback Controls** - Play, pause, skip, seek, shuffle, and repeat
- 📱 **Background Playback** - Continue listening while using other apps
- 🔔 **Media Notifications** - Control playback from notification panel


## 🏗️ Architecture

This app follows the **MVP (Model-View-Presenter)** pattern with clear separation of concerns:

```
app/src/main/java/com/nauh/musicplayer/
├── contract/                    # MVP Contracts
│   ├── MainContract.kt         # MainActivity interface
│   └── PlayerContract.kt       # PlayerActivity interface
├── data/                       # Data Layer
│   ├── api/
│   │   └── MusicApiService.kt  # API service interface
│   ├── model/
│   │   ├── Song.kt            # Song data model
│   │   └── Playlist.kt        # Playlist data model
│   └── repository/
│       └── MusicRepository.kt  # Repository pattern implementation
├── presenter/                  # Business Logic Layer
│   ├── MainPresenter.kt       # MainActivity business logic
│   └── PlayerPresenter.kt     # PlayerActivity business logic
├── service/                   # Background Services
│   ├── MusicService.kt        # Background music service
│   ├── MusicServiceConnection.kt # Service connection management
│   └── NotificationHelper.kt  # Notification management
├── ui/                        # View Layer
│   ├── adapter/
│   │   └── SongAdapter.kt     # RecyclerView adapter
│   ├── MainActivity.kt        # Main screen
│   └── PlayerActivity.kt      # Music player screen
└── MusicPlayerApplication.kt  # Application class
```

## 🛠️ Tech Stack

### Core Technologies
- **Language**: Kotlin 100%
- **Architecture**: MVP (Model-View-Presenter)
- **Audio Engine**: ExoPlayer (Media3) 1.2.1
- **UI Framework**: Android Views with XML layouts
- **Design System**: Material Design Components


## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Flamingo or newer
- **Android SDK**: API 24+ (Android 7.0)
- **Kotlin**: 1.9.0+
- **Gradle**: 8.0+
- **Java**: JDK 11 or higher

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/your-username/android-music-player.git
cd android-music-player
```

2. **Open in Android Studio**
```bash
# Or open directly in Android Studio
File -> Open -> Select project folder
```

3. **Sync Gradle dependencies**
```bash
./gradlew build
```

4. **Run the application**
- Connect an Android device or start an emulator
- Click "Run" in Android Studio or use:
```bash
./gradlew installDebug
```

## 📱 User Guide

### 🏠 Main Screen (MainActivity)

**Key Features:**
- 📋 **Song List**: Browse all available songs
- 🔍 **Search**: Find songs by title, artist, or album
- 🎵 **Mini Player**: Quick controls when music is playing

**How to Use:**
1. Launch the app to see the demo song collection
2. Use the search bar at the top to find specific songs
3. Tap any song to start playback
4. Mini player appears at the bottom with playback controls



### 🎮 Player Screen (PlayerActivity)

**Key Features:**
- 🎵 **Now Playing**: Current song information and artwork
- ⏯️ **Playback Controls**: Play, pause, skip, previous, seek
- 📝 **Playlist View**: See and navigate through the current playlist
- 🔀 **Shuffle & Repeat**: Toggle shuffle and repeat modes
- 📊 **Progress Bar**: Visual playback progress with seek functionality

**Controls:**
- **Play/Pause**: Tap the center button
- **Skip**: Use next/previous buttons
- **Seek**: Drag the progress bar
- **Playlist**: Tap any song in the playlist to jump to it

## 🔧 Technical Details

### Core Components

#### 1. **MusicService** - Background Music Service
```kotlin
class MusicService : MediaSessionService() {
    private var exoPlayer: ExoPlayer? = null
    private var mediaSession: MediaSession? = null

    // Key methods
    fun playPlaylist(songs: List<Song>, startIndex: Int)
    fun playPause()
    fun skipToNext() / skipToPrevious()
    fun seekTo(position: Long)
    fun setShuffleMode(enabled: Boolean)
    fun setRepeatMode(mode: Int)
}
```

#### 2. **MVP Contracts** - Interface Definitions
```kotlin
interface MainContract {
    interface View {
        fun showSongs(songs: List<Song>)
        fun showMiniPlayer(song: Song)
        fun navigateToPlayer(song: Song, songs: List<Song>, position: Int)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
    }

    interface Presenter {
        fun loadSongs()
        fun onSongClicked(song: Song, songs: List<Song>, position: Int)
        fun searchSongs(query: String)
        fun onDestroy()
    }
}
```

#### 3. **Data Models** - Data Structures
```kotlin
@Parcelize
data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val streamUrl: String,
    val artworkUrl: String,
    val duration: Long,
    val genre: String
) : Parcelable
```

#### 4. **Service Connection Management**
```kotlin
class MusicServiceConnection(private val context: Context) {
    private var mediaController: MediaController? = null
    private var isConnected = false

    fun connect()
    fun disconnect()
    fun playPlaylist(songs: List<Song>, startIndex: Int)
    fun isConnected(): Boolean
}
```


## 🐛 Troubleshooting

### Common Issues

#### ❌ Music Won't Play
**Possible Causes:**
- No internet connection (for streaming)
- Invalid or unavailable song URLs
- Missing audio permissions
- Service connection issues

**Solutions:**
1. Check internet connectivity
2. Verify song URLs in Logcat
3. Ensure app has necessary permissions
4. Check service binding in logs

#### ❌ Service Won't Start
**Possible Causes:**
- Service not registered in AndroidManifest
- Service binding errors
- Insufficient permissions

**Solutions:**
1. Verify MusicService is declared in AndroidManifest.xml
2. Check service binding logs
3. Ensure foreground service permissions are granted

#### ❌ Notifications Not Showing
**Possible Causes:**
- Missing notification permissions (Android 13+)
- Notification channel not created
- Background restrictions

**Solutions:**
1. Grant notification permissions
2. Verify NotificationChannel creation
3. Check battery optimization settings


## 
---

<div align="center">


*Made with Nguyen Van Huan*

</div>




