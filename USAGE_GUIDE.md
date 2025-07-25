# Hướng dẫn sử dụng Music Player App

## Tổng quan

Ứng dụng Music Player được xây dựng theo kiến trúc MVP (Model-View-Presenter) với các tính năng:
- Phát nhạc online
- Service chạy nền
- Notification điều khiển
- Giao diện hiện đại

## Cách sử dụng

### 1. Màn hình chính (MainActivity)

**Chức năng:**
- Hiển thị danh sách bài hát
- Tìm kiếm bài hát
- Mini player khi có nhạc đang phát

**Cách sử dụng:**
1. Mở ứng dụng để xem danh sách bài hát
2. Sử dụng thanh tìm kiếm để tìm bài hát theo tên, nghệ sĩ hoặc album
3. Click vào bài hát để bắt đầu phát nhạc
4. Mini player sẽ xuất hiện ở dưới cùng màn hình

### 2. Mini Player

**Chức năng:**
- Hiển thị thông tin bài hát đang phát
- Điều khiển play/pause, previous, next
- Thanh progress hiển thị tiến độ phát

**Cách sử dụng:**
- Click vào mini player để mở màn hình player đầy đủ
- Sử dụng các nút điều khiển để play/pause, chuyển bài

### 3. Màn hình Player (PlayerActivity)

**Chức năng:**
- Hiển thị đầy đủ thông tin bài hát
- Điều khiển phát nhạc chi tiết
- Shuffle và Repeat mode
- Seek bar để tua nhạc

**Cách sử dụng:**
1. Click vào mini player hoặc bài hát để mở
2. Sử dụng seek bar để tua đến vị trí mong muốn
3. Click nút shuffle để bật/tắt phát ngẫu nhiên
4. Click nút repeat để chuyển đổi chế độ lặp:
   - Tắt lặp
   - Lặp tất cả
   - Lặp một bài

### 4. Notification

**Chức năng:**
- Hiển thị khi có nhạc đang phát
- Điều khiển play/pause, previous, next
- Hoạt động ngay cả khi app bị đóng

**Cách sử dụng:**
- Notification tự động xuất hiện khi phát nhạc
- Click vào notification để mở app
- Sử dụng các nút trên notification để điều khiển

## Kiến trúc MVP

### Model
- `Song`: Data class chứa thông tin bài hát
- `MusicRepository`: Quản lý dữ liệu nhạc

### View  
- `MainActivity`: Giao diện danh sách bài hát
- `PlayerActivity`: Giao diện phát nhạc

### Presenter
- `MainPresenter`: Logic xử lý cho MainActivity
- `PlayerPresenter`: Logic xử lý cho PlayerActivity

## Service và Notification

### MusicService
- Chạy nền để phát nhạc liên tục
- Sử dụng ExoPlayer để phát media
- Quản lý playlist và trạng thái phát

### NotificationHelper
- Tạo và cập nhật notification
- Xử lý các action từ notification
- Hiển thị thông tin bài hát đang phát

## Công nghệ sử dụng

- **ExoPlayer**: Thư viện phát media mạnh mẽ của Google
- **Kotlin Coroutines**: Xử lý bất đồng bộ
- **RecyclerView**: Hiển thị danh sách hiệu quả
- **Material Design**: Giao diện hiện đại
- **Service**: Chạy nền
- **Notification**: Điều khiển từ notification panel

## Lưu ý

1. **Quyền**: App cần quyền INTERNET và FOREGROUND_SERVICE
2. **Kết nối**: Cần kết nối internet để phát nhạc online
3. **Pin**: Service chạy nền có thể ảnh hưởng đến pin
4. **Notification**: Có thể tắt notification trong cài đặt hệ thống

## Troubleshooting

### Không phát được nhạc
- Kiểm tra kết nối internet
- Đảm bảo URL nhạc hợp lệ
- Restart app nếu cần

### Service không hoạt động
- Kiểm tra quyền FOREGROUND_SERVICE
- Đảm bảo app không bị kill bởi hệ thống
- Kiểm tra cài đặt battery optimization

### Notification không hiển thị
- Kiểm tra quyền POST_NOTIFICATIONS (Android 13+)
- Kiểm tra cài đặt notification của app
- Restart app để tạo lại notification channel
