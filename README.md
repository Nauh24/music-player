# Android Music Streaming Player

A comprehensive Android music streaming application built with Kotlin using the MVP (Model-View-Presenter) architecture pattern. The app features online music streaming, background playback, and a modern UI designed entirely with XML layouts.

## 🏗️ Architecture

This application follows the **MVP (Model-View-Presenter)** architecture pattern with clear separation of concerns:

### Model Layer
- **Data Models**: `Song`, `Playlist` - Core data structures
- **Repository**: `MusicRepository` - Data access abstraction
- **API Service**: `MusicApiService` - Network data source interface

### View Layer
- **Activities**: `MainActivity`, `PlayerActivity` - UI components
- **Adapters**: `SongAdapter` - RecyclerView data binding
- **XML Layouts**: All UI designed with XML (no Jetpack Compose)

### Presenter Layer
- **Presenters**: `MainPresenter`, `PlayerPresenter` - Business logic
- **Contracts**: MVP interfaces defining communication protocols

### Service Layer
- **MusicService**: Background audio playback using ExoPlayer
- **NotificationHelper**: Media notification management

## 🎵 Features

### Core Functionality
- ✅ **Music Library**: Display songs in list format with album artwork
- ✅ **Online Streaming**: Stream music from remote URLs
- ✅ **Background Playback**: Continue playing when app is minimized
- ✅ **Media Controls**: Play, pause, skip, seek functionality
- ✅ **Search**: Find songs by title, artist, or album
- ✅ **Notifications**: Media controls in notification panel

### Player Features
- ✅ **Full-screen Player**: Dedicated player activity with large album art
- ✅ **Progress Control**: Seek bar for track scrubbing
- ✅ **Shuffle & Repeat**: Playback mode controls
- ✅ **Mini Player**: Compact player in main activity
- ✅ **Navigation**: Previous/next track controls

### Technical Features
- ✅ **MVP Architecture**: Clean separation of concerns
- ✅ **ExoPlayer Integration**: Professional audio streaming
- ✅ **Lifecycle Management**: Proper handling of Android lifecycles
- ✅ **Error Handling**: Graceful network and playback error management
- ✅ **Loading States**: User feedback during data loading

## 🛠️ Technical Stack

### Core Technologies
- **Language**: Kotlin
- **Architecture**: MVP (Model-View-Presenter)
- **UI**: XML Layouts (Material Design)
- **Audio**: ExoPlayer (Media3)
- **Networking**: Retrofit + OkHttp
- **Image Loading**: Glide
- **Async**: Kotlin Coroutines

### Dependencies
```kotlin
// ExoPlayer for audio streaming
implementation("androidx.media3:media3-exoplayer:1.2.1")
implementation("androidx.media3:media3-ui:1.2.1")
implementation("androidx.media3:media3-session:1.2.1")

// Networking
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")

// Image loading
implementation("com.github.bumptech.glide:glide:4.16.0")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

## 📱 UI Design

The app features a modern, dark-themed interface inspired by popular music streaming apps:

### Main Screen
- **Toolbar**: App title and navigation
- **Search Bar**: Real-time song search
- **Song List**: RecyclerView with album art, song info, and duration
- **Mini Player**: Compact controls when music is playing

### Player Screen
- **Large Album Artwork**: Prominent visual focus
- **Song Information**: Title, artist, album details
- **Progress Controls**: Seek bar with time indicators
- **Media Controls**: Play/pause, skip, shuffle, repeat buttons
- **Navigation**: Back button to return to main screen

### Color Scheme
- **Primary**: Spotify-inspired green (#1DB954)
- **Background**: Dark theme (#121212, #1E1E1E)
- **Text**: White primary, gray secondary
- **Accents**: Colorful gradients for album placeholders

## 🔧 Setup Instructions

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK API 24+ (Android 7.0)
- Kotlin 1.8+

### Installation
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle dependencies
4. Run on device or emulator

### Configuration
The app currently uses mock data for demonstration. To integrate with a real API:

1. Update `MusicApiService` with your API endpoints
2. Modify `MusicRepository` to use actual network calls
3. Replace mock data in `MockMusicData` with real data sources

## 📂 Project Structure

```
app/src/main/java/com/nauh/musicplayer/
├── contract/                 # MVP contracts
│   ├── MainContract.kt
│   └── PlayerContract.kt
├── data/                     # Data layer
│   ├── api/
│   │   └── MusicApiService.kt
│   ├── model/
│   │   ├── Song.kt
│   │   └── Playlist.kt
│   └── repository/
│       └── MusicRepository.kt
├── presenter/                # Business logic
│   ├── MainPresenter.kt
│   └── PlayerPresenter.kt
├── service/                  # Background services
│   ├── MusicService.kt
│   └── NotificationHelper.kt
├── ui/                       # View layer
│   ├── adapter/
│   │   └── SongAdapter.kt
│   ├── MainActivity.kt
│   └── PlayerActivity.kt
└── MusicPlayerApplication.kt
```

## 🎯 Key Implementation Details

### MVP Pattern Implementation
- **Contracts**: Define clear interfaces between layers
- **Presenters**: Handle business logic and coordinate between Model and View
- **Views**: Pure UI logic, no business logic
- **Models**: Data management and API communication

### Background Playback
- **MediaSessionService**: Handles background audio playback
- **ExoPlayer**: Professional-grade audio streaming
- **Notifications**: Media controls accessible from notification panel
- **Audio Focus**: Proper handling of audio interruptions

### Error Handling
- **Network Errors**: Graceful handling of connectivity issues
- **Playback Errors**: User-friendly error messages
- **Loading States**: Visual feedback during operations
- **Lifecycle Management**: Proper cleanup and state management

## 🚀 Future Enhancements

### Planned Features
- [ ] **Playlists**: Create and manage custom playlists
- [ ] **Favorites**: Mark songs as favorites
- [ ] **Offline Mode**: Download songs for offline playback
- [ ] **Equalizer**: Audio enhancement controls
- [ ] **Social Features**: Share songs and playlists
- [ ] **Lyrics**: Display synchronized lyrics
- [ ] **Recommendations**: AI-powered music suggestions

### Technical Improvements
- [ ] **Database**: Local caching with Room
- [ ] **Testing**: Unit and integration tests
- [ ] **CI/CD**: Automated build and deployment
- [ ] **Performance**: Memory and battery optimization
- [ ] **Accessibility**: Enhanced accessibility features

## 📄 License

This project is created for educational purposes and demonstrates Android development best practices using MVP architecture.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bugs and feature requests.

---

**Note**: This application uses mock data for demonstration. In a production environment, you would integrate with actual music streaming APIs and handle proper licensing and content delivery.
