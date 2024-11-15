package com.example.baicuoikywhatsappclone.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.baicuoikywhatsappclone.Adapter.UsersAdapter;
import com.example.baicuoikywhatsappclone.Model.User;
import com.example.baicuoikywhatsappclone.databinding.FragmentChatsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class ChatsFragment extends Fragment {

    FragmentChatsBinding binding;
    ArrayList<User> list = new ArrayList<>();
    FirebaseDatabase database;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();

        // Kiểm tra context để tránh lỗi NullPointerException
        if (getContext() != null) {
            UsersAdapter adapter = new UsersAdapter(requireContext(), list);
            binding.chatRecycleView.setAdapter(adapter);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            binding.chatRecycleView.setLayoutManager(layoutManager);

            database.getReference().child("User").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            user.setUserID(dataSnapshot.getKey());
                            if (!user.getUserID().equals(FirebaseAuth.getInstance().getUid())) {
                                list.add(user);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();

                    // Cuộn đến tin nhắn cuối cùng
                    if (list.size() > 0) {
                        binding.chatRecycleView.scrollToPosition(list.size() - 1);
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseError", "Error: " + error.getMessage());
                }
            });
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Giải phóng binding để tránh rò rỉ bộ nhớ
    }
}
