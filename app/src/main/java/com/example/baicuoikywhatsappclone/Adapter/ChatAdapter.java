package com.example.baicuoikywhatsappclone.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baicuoikywhatsappclone.Model.MessageModel;
import com.example.baicuoikywhatsappclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<MessageModel> messageModels; // Danh sách tin nhắn
    private final Context context;
    private final String recId; // ID của người nhận

    private final int SENDER_VIEW_TYPE = 1;
    private final int RECEIVER_VIEW_TYPE = 2;

    // Constructor
    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;
        this.recId = null;
    }

    // Xác định loại View (Sender/Receiver)
    @Override
    public int getItemViewType(int position) {
        if (messageModels.get(position).getuID().equals(FirebaseAuth.getInstance().getUid())) {
            return SENDER_VIEW_TYPE; // Người gửi
        } else {
            return RECEIVER_VIEW_TYPE; // Người nhận
        }
    }

    // Tạo ViewHolder dựa trên loại View
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_reciever, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    // Gán dữ liệu vào ViewHolder
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel = messageModels.get(position);

        // Xử lý sự kiện xóa tin nhắn
        holder.itemView.setOnClickListener(view -> new AlertDialog.Builder(context)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete this message?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    String senderRoom = FirebaseAuth.getInstance().getUid() + recId;
                    database.getReference().child("chats").child(senderRoom)
                            .child(messageModel.getMessageId())
                            .setValue(null);
                })
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                .show());

        // Gán nội dung tin nhắn và thời gian
        if (holder.getClass() == SenderViewHolder.class) {
            ((SenderViewHolder) holder).senderMsg.setText(messageModel.getMessage());
            ((SenderViewHolder) holder).senderTime.setText(formatTime(messageModel.getTimestamp()));
        } else if (holder.getClass() == ReceiverViewHolder.class) {
            ((ReceiverViewHolder) holder).receiverMsg.setText(messageModel.getMessage());
            ((ReceiverViewHolder) holder).receiverTime.setText(formatTime(messageModel.getTimestamp()));
        }
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    // Phương thức định dạng thời gian
    private String formatTime(Object timestamp) {
        try {
            long time = (long) timestamp; // Đảm bảo timestamp là kiểu long
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
            return simpleDateFormat.format(new Date(time));
        } catch (Exception e) {
            e.printStackTrace();
            return ""; // Trả về chuỗi trống nếu xảy ra lỗi
        }
    }

    // ViewHolder cho người nhận
    public static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView receiverMsg, receiverTime;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg = itemView.findViewById(R.id.receiverText);
            receiverTime = itemView.findViewById(R.id.receiverTime);
        }
    }

    // ViewHolder cho người gửi
    public static class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMsg, senderTime;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);
        }
    }
}