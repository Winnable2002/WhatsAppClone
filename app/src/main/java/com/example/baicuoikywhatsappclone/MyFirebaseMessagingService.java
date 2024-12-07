package com.example.baicuoikywhatsappclone;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        // Kiểm tra xem tin nhắn có chứa dữ liệu không
        if (remoteMessage.getData().size() > 0) {
            String message = remoteMessage.getData().get("message");
            String sender = remoteMessage.getData().get("sender");

            // Hiển thị thông báo trên điện thoại
            sendNotification(message, sender);
        }
    }

    private void sendNotification(String message, String sender) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "chat_notifications";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Chat Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, channelId)
                    .setContentTitle("Tin nhắn mới từ: " + sender)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.notification)
                    .build();
        }

        notificationManager.notify(0, notification);
    }
}
