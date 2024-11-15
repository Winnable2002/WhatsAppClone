package com.example.baicuoikywhatsappclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.baicuoikywhatsappclone.Adapter.ChatAdapter;
import com.example.baicuoikywhatsappclone.Model.MessageModel;
import com.example.baicuoikywhatsappclone.databinding.ActivityChatDetailBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class ChatDetailActivity extends AppCompatActivity {

    ActivityChatDetailBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;

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
        Picasso.get().load(profilePic).placeholder(R.drawable.avatar).into(binding.profileImage);

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatDetailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final ArrayList<MessageModel> messageModels= new ArrayList<>();
        final ChatAdapter chatAdapter= new ChatAdapter(messageModels, this, recieveId);

        binding.chatRecycleView.setAdapter(chatAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecycleView.setLayoutManager(layoutManager);

        final String senderRoom = senderID + recieveId;
        final String recieverRoom = recieveId + senderID;

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

                    }
                });

        // Hàm gửi tin nhắn
        View.OnClickListener sendMessageListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(senderID, recieveId, senderRoom, recieverRoom, messageModels);
            }
        };

        // Thiết lập nút gửi
        binding.send.setOnClickListener(sendMessageListener);

        // Thiết lập hành động gửi khi nhấn Enter trên bàn phím
        binding.enterMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    sendMessage(senderID, recieveId, senderRoom, recieverRoom, messageModels);
                    return true;
                }
                return false;
            }
        });
    }

    private void sendMessage(String senderID, String recieveId, String senderRoom, String recieverRoom, ArrayList<MessageModel> messageModels) {
        String message = binding.enterMessage.getText().toString().trim();
        if (!message.isEmpty()) {
            final MessageModel model = new MessageModel(senderID, message);
            model.setTimestam(new Date().getTime());
            binding.enterMessage.setText("");

            database.getReference().child("chats")
                    .child(senderRoom)
                    .push()
                    .setValue(model)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            database.getReference().child("chats")
                                    .child(recieverRoom)
                                    .push()
                                    .setValue(model)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            // Cuộn đến tin nhắn cuối cùng sau khi gửi
                                            binding.chatRecycleView.smoothScrollToPosition(messageModels.size() - 1);
                                        }
                                    });
                        }
                    });
        }
    }
}
