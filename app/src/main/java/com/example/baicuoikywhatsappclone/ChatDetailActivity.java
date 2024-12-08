package com.example.baicuoikywhatsappclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.baicuoikywhatsappclone.Adapter.ChatAdapter;
import com.example.baicuoikywhatsappclone.Model.MessageModel;
import com.example.baicuoikywhatsappclone.databinding.ActivityChatDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatDetailActivity extends AppCompatActivity {

    ActivityChatDetailBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ArrayList<MessageModel> messageModels;
    ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo binding
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Thiết lập Firebase và các thành phần khác
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        final String senderID = auth.getUid();
        String recieveId = getIntent().getStringExtra("userID");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");

        binding.userName.setText(userName);
        if (profilePic != null) {
            Picasso.get().load(profilePic).placeholder(R.drawable.avatar).into(binding.profileImage);
        }

        binding.backArrow.setOnClickListener(view -> {
            Intent intent = new Intent(ChatDetailActivity.this, MainActivity.class);
            startActivity(intent);
        });

        messageModels = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageModels, this);

        binding.chatRecycleView.setAdapter(chatAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Hiển thị tin nhắn mới nhất ở cuối
        binding.chatRecycleView.setLayoutManager(layoutManager);

        final String senderRoom = senderID + recieveId;
        final String recieverRoom = recieveId + senderID;

        // Lấy dữ liệu tin nhắn từ Firebase
        database.getReference().child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModels.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            MessageModel model = snapshot1.getValue(MessageModel.class);
                            if (model != null) {
                                model.setMessageId(snapshot1.getKey());
                                messageModels.add(model);
                            }
                        }
                        chatAdapter.notifyDataSetChanged();

                        // Cuộn đến tin nhắn cuối cùng
                        binding.chatRecycleView.scrollToPosition(messageModels.size() - 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Xử lý lỗi
                    }
                });

        // Hàm gửi tin nhắn
        View.OnClickListener sendMessageListener = view -> sendMessage(senderID, recieveId, senderRoom, recieverRoom);

        // Thiết lập nút gửi
        binding.send.setOnClickListener(sendMessageListener);

        // Thiết lập hành động gửi khi nhấn Enter trên bàn phím
        binding.enterMessage.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND || (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                sendMessage(senderID, recieveId, senderRoom, recieverRoom);
                return true;
            }
            return false;
        });
    }

    private void sendMessage(String senderID, String recieveId, String senderRoom, String recieverRoom) {
        String message = binding.enterMessage.getText().toString().trim();
        if (!message.isEmpty()) {
            // Tạo đối tượng MessageModel
            final MessageModel model = new MessageModel(senderID, message, System.currentTimeMillis());
            binding.enterMessage.setText("");

            // Gửi tin nhắn vào Firebase
            database.getReference().child("chats")
                    .child(senderRoom)
                    .push()
                    .setValue(model)
                    .addOnSuccessListener(unused -> {
                        database.getReference().child("chats")
                                .child(recieverRoom)
                                .push()
                                .setValue(model);
                    });

            // Cập nhật danh sách tin nhắn cục bộ và giao diện
            messageModels.add(model);
            chatAdapter.notifyItemInserted(messageModels.size() - 1);
            binding.chatRecycleView.scrollToPosition(messageModels.size() - 1);
        }
    }
}
