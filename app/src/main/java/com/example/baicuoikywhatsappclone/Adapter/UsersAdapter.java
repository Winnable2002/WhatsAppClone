package com.example.baicuoikywhatsappclone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baicuoikywhatsappclone.ChatDetailActivity;
import com.example.baicuoikywhatsappclone.Model.User;
import com.example.baicuoikywhatsappclone.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private final ArrayList<User> list; // Sử dụng final để tránh lỗi gán lại
    private final Context context;

    public UsersAdapter(Context context, ArrayList<User> list) {
        this.context = context;
        this.list = list != null ? list : new ArrayList<>(); // Kiểm tra null và khởi tạo list trống nếu cần
    }

    // ViewHolder class bên trong UsersAdapter
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView userName, lastMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.userNameList);
            lastMessage = itemView.findViewById(R.id.lastMessage);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_show_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = list.get(position);

        // Kiểm tra và tải ảnh
        if (user.getProfilePic() != null && !user.getProfilePic().isEmpty()) {
            Picasso.get().load(user.getProfilePic()).placeholder(R.drawable.avatar3).into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.avatar3); // Đặt ảnh mặc định nếu link ảnh bị lỗi
        }

        // Kiểm tra và thiết lập tên người dùng
        if (user.getUsername() != null) {
            holder.userName.setText(user.getUsername());
        } else {
            holder.userName.setText("Unknown"); // Đặt tên mặc định nếu userName bị lỗi
        }

        // Sự kiện click để chuyển sang ChatDetailActivity
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context.getApplicationContext(), ChatDetailActivity.class);

            // Kiểm tra dữ liệu và thêm vào Intent
            if (user.getUserID() != null) {
                intent.putExtra("userID", user.getUserID());
            }
            if (user.getUsername() != null) {
                intent.putExtra("userName", user.getUsername());
            }
            if (user.getProfilePic() != null) {
                intent.putExtra("profilePic", user.getProfilePic());
            }

            // Bắt đầu Activity
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
