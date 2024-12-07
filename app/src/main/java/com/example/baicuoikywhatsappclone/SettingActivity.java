package com.example.baicuoikywhatsappclone;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.baicuoikywhatsappclone.Model.User;
import com.example.baicuoikywhatsappclone.databinding.ActivitySettingsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SettingActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo Firebase
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // Xử lý nút quay lại
        binding.backArrow.setOnClickListener(view -> {
            Intent intent = new Intent(SettingActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Lưu thông tin
        binding.saveButton.setOnClickListener(view -> {
            String status = binding.etStatus.getText().toString().trim();
            String username = binding.txtUsername.getText().toString().trim();

            if (!status.isEmpty() && !username.isEmpty()) {
                HashMap<String, Object> obj = new HashMap<>();
                obj.put("username", username);
                obj.put("status", status);

                database.getReference().child("User").child(FirebaseAuth.getInstance().getUid())
                        .updateChildren(obj)
                        .addOnSuccessListener(aVoid -> Toast.makeText(SettingActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(SettingActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(SettingActivity.this, "Please Enter Username and Status", Toast.LENGTH_SHORT).show();
            }
        });

        database.getReference().child("User").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            // Hiển thị ảnh đại diện
                            if (user.getProfilePic() != null && !user.getProfilePic().isEmpty()) {
                                Picasso.get()
                                        .load(user.getProfilePic())
                                        .placeholder(R.drawable.avatar) // Ảnh mặc định
                                        .into(binding.profileImage);
                            } else {
                                binding.profileImage.setImageResource(R.drawable.avatar); // Ảnh mặc định nếu không có ảnh đại diện
                            }

                            // Hiển thị tên và trạng thái
                            binding.txtUsername.setText(user.getUsername());
                            binding.etStatus.setText(user.getStatus());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SettingActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                    }
                });



        // Xử lý nút thêm ảnh
        binding.plus.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 25);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 25 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            binding.profileImage.setImageURI(selectedImageUri); // Hiển thị ảnh vừa chọn trên giao diện

            // Tham chiếu tới Firebase Storage
            final StorageReference storageReference = storage.getReference()
                    .child("profile_image")
                    .child(FirebaseAuth.getInstance().getUid());

            // Upload ảnh lên Firebase Storage
            storageReference.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot ->
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Lưu đường dẫn URL của ảnh vào Firebase Realtime Database
                        database.getReference().child("User")
                                .child(FirebaseAuth.getInstance().getUid())
                                .child("profilePic")
                                .setValue(uri.toString())
                                .addOnSuccessListener(aVoid -> Toast.makeText(SettingActivity.this, "Profile picture updated successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(SettingActivity.this, "Failed to update profile picture", Toast.LENGTH_SHORT).show());
                    }).addOnFailureListener(e -> Toast.makeText(SettingActivity.this, "Failed to get image URL", Toast.LENGTH_SHORT).show())
            ).addOnFailureListener(e -> Toast.makeText(SettingActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
        }
    }

}
