package com.example.baicuoikywhatsappclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.baicuoikywhatsappclone.Adapter.ChatAdapter;
import com.example.baicuoikywhatsappclone.Model.MessageModel;
import com.example.baicuoikywhatsappclone.databinding.ActivityChatDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatDetailActivity extends AppCompatActivity {

    private ActivityChatDetailBinding binding;
    private FirebaseDatabase database;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Thiết lập ViewBinding
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Thiết lập Firebase
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        // Lấy thông tin từ Intent
        final String senderID = auth.getUid();
        String receiveId = getIntent().getStringExtra("userID");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");

        if (senderID == null || receiveId == null) {
            Toast.makeText(this, "Lỗi dữ liệu người dùng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị thông tin người nhận
        binding.userName.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.avatar).into(binding.profileImage);

        // Nút quay lại
        binding.backArrow.setOnClickListener(v -> {
            startActivity(new Intent(ChatDetailActivity.this, MainActivity.class));
            finish();
        });

        // Tạo danh sách và adapter cho RecyclerView
        ArrayList<MessageModel> messageModels = new ArrayList<>();
        ChatAdapter chatAdapter = new ChatAdapter(messageModels, this, receiveId);
        binding.chatRecycleView.setAdapter(chatAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Cuộn tới tin nhắn cuối khi mở chat
        binding.chatRecycleView.setLayoutManager(layoutManager);

        // Phòng chat (senderRoom và receiverRoom)
        final String senderRoom = senderID + receiveId;
        final String receiverRoom = receiveId + senderID;

        // Lắng nghe tin nhắn trong phòng senderRoom
        database.getReference().child("chats").child(senderRoom)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                        MessageModel model = snapshot.getValue(MessageModel.class);
                        if (model != null) {
                            model.setMessageId(snapshot.getKey());
                            messageModels.add(model);
                            chatAdapter.notifyItemInserted(messageModels.size() - 1);
                            binding.chatRecycleView.scrollToPosition(messageModels.size() - 1); // Cuộn đến cuối
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                        // Xử lý thay đổi (nếu cần)
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        // Xử lý tin nhắn bị xóa (nếu cần)
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {
                        // Xử lý di chuyển thứ tự (nếu cần)
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ChatDetailActivity.this, "Lỗi tải tin nhắn!", Toast.LENGTH_SHORT).show();
                    }
                });

        // Gửi tin nhắn khi nhấn nút gửi
        binding.send.setOnClickListener(v -> sendMessage(senderID, receiveId, senderRoom, receiverRoom));

        // Gửi tin nhắn khi nhấn Enter
        binding.enterMessage.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                    (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN)) {
                sendMessage(senderID, receiveId, senderRoom, receiverRoom);
                return true;
            }
            return false;
        });
    }

    private void sendMessage(String senderID, String receiveId, String senderRoom, String receiverRoom) {
        String message = binding.enterMessage.getText().toString().trim();
        if (message.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tin nhắn!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo model tin nhắn
        MessageModel model = new MessageModel(senderID, message);
        model.setTimestam(new Date().getTime());
        binding.enterMessage.setText(""); // Xóa nội dung nhập sau khi gửi

        // Gửi tin nhắn vào Firebase (phòng của người gửi)
        database.getReference().child("chats").child(senderRoom)
                .push()
                .setValue(model)
                .addOnSuccessListener(unused -> {
                    // Sau khi gửi, lưu tin nhắn vào phòng của người nhận
                    database.getReference().child("chats").child(receiverRoom)
                            .push()
                            .setValue(model)
                            .addOnFailureListener(e -> Toast.makeText(ChatDetailActivity.this, "Lỗi lưu tin nhắn ở phòng nhận!", Toast.LENGTH_SHORT).show());

                    // Gửi thông báo tới người nhận
                    sendFCMNotification(receiveId, message);
                })
                .addOnFailureListener(e -> Toast.makeText(ChatDetailActivity.this, "Lỗi gửi tin nhắn!", Toast.LENGTH_SHORT).show());
    }

    private void sendFCMNotification(String receiverID, String message) {
        // Lấy FCM token của người nhận từ Firebase Database
        database.getReference().child("users").child(receiverID).child("fcmToken")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().getValue() != null) {
                        String token = task.getResult().getValue(String.class);
                        sendPushNotification(token, message);
                    } else {
                        Toast.makeText(this, "Không thể lấy token của người nhận!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendPushNotification(String token, String message) {
        String url = "https://fcm.googleapis.com/fcm/send";
        String serverKey = "<YOUR_FCM_SERVER_KEY>"; // Thay bằng Server Key từ Firebase Console

        Map<String, Object> payload = new HashMap<>();
        payload.put("to", token);
        payload.put("data", Map.of("message", message, "sender", auth.getUid()));

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(payload),
                response -> Toast.makeText(ChatDetailActivity.this, "Thông báo đã gửi!", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(ChatDetailActivity.this, "Lỗi gửi thông báo!", Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key=" + serverKey);
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
