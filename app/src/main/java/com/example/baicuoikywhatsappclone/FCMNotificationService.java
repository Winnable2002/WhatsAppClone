package com.example.baicuoikywhatsappclone;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FCMNotificationService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "default_channel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Lấy thông tin từ thông báo FCM
        String title = "Thông báo";
        String body = "Bạn có thông báo mới.";
        String chatId = ""; // ID phòng chat được gửi từ server

        // Lấy nội dung notification (nếu có)
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }

        // Lấy dữ liệu từ server (nếu có)
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            chatId = data.get("chatId"); // Ví dụ: lấy dữ liệu chatId từ server
        }

        // Gọi hàm hiển thị thông báo
        showNotificationWithIntent(title, body, chatId);
    }

    private void showNotificationWithIntent(String title, String body, String chatId) {
        // Tạo Intent để mở ChatDetailActivity khi nhấn vào thông báo
        Intent intent = new Intent(this, ChatDetailActivity.class);
        intent.putExtra("chatId", chatId); // Truyền chatId qua intent
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Tạo NotificationChannel (cho API >= 26)
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Thông báo chính", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Thông báo từ ứng dụng");
            notificationManager.createNotificationChannel(channel);
        }

        // Tạo thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification) // Thêm icon vào drawable
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent); // Liên kết Intent với thông báo

        // Hiển thị thông báo
        notificationManager.notify(1, builder.build());
    }
}
