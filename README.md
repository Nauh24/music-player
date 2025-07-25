# Music Player App

Ứng dụng nghe nhạc Android được xây dựng với MVP pattern, Service và Notification.

## Tính năng chính

- ✅ Hiển thị danh sách bài hát
- ✅ Phát nhạc khi click vào bài hát
- ✅ Mini player ở màn hình chính
- ✅ Màn hình player đầy đủ với các điều khiển
- ✅ Service chạy nền để phát nhạc
- ✅ Notification với điều khiển phát nhạc
- ✅ Tìm kiếm bài hát
- ✅ Shuffle và Repeat mode
- ✅ Điều khiển Previous/Next

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




