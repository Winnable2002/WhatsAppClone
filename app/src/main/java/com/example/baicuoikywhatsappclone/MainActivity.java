package com.example.baicuoikywhatsappclone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.baicuoikywhatsappclone.Adapter.FragmentsAdapter;
import com.example.baicuoikywhatsappclone.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        mAuth = FirebaseAuth.getInstance();

        FragmentsAdapter adapter = new FragmentsAdapter(getSupportFragmentManager());
        binding.viewPager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        // Gọi hàm lấy FCM Token
        getFCMToken();
    }


    // Phương thức để tạo menu trên ActionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu); // menu.xml sẽ chứa các mục như Setting, Group Chat, Logout
        return super.onCreateOptionsMenu(menu);
    }

    // Xử lý các lựa chọn của menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.setting) {
            // Hiển thị Toast khi chọn Setting
           // Toast.makeText(this, "Setting is Clicked", Toast.LENGTH_SHORT).show();
            Intent intent2= new Intent(MainActivity.this,SettingActivity.class);
            startActivity(intent2);
            return true;

        } else if (id == R.id.groupChat) {
            // Hiển thị Toast khi chọn Group Chat
            //Toast.makeText(this, "Group Chat is Started", Toast.LENGTH_SHORT).show();
           Intent intent1 = new Intent(MainActivity.this,GroupChatActivity.class);
           startActivity(intent1);
            // Thêm code điều hướng đến màn hình nhóm trò chuyện nếu cần
        } else if (id == R.id.logout) {
            // Đăng xuất người dùng và chuyển đến màn hình đăng nhập
            mAuth.signOut();
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
            finish(); // Đảm bảo Activity này không còn trong stack
        }

        return super.onOptionsItemSelected(item);
    }

    void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                Log.d("FCM", "FCM Token: " + token);
            } else {
                // Xử lý lỗi nếu không lấy được token
                Log.e("FCM Token", "Failed to retrieve token", task.getException());
                Toast.makeText(MainActivity.this, "Failed to retrieve FCM Token", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
