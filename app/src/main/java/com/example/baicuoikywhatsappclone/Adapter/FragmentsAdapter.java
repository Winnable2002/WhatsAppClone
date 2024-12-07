package com.example.baicuoikywhatsappclone.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.baicuoikywhatsappclone.Fragments.CallsFragment;
import com.example.baicuoikywhatsappclone.Fragments.ChatsFragment;
import com.example.baicuoikywhatsappclone.Fragments.StatusFragment;

public class FragmentsAdapter extends FragmentStatePagerAdapter {

    public FragmentsAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ChatsFragment();
            case 1:
                return new CallsFragment();
            case 2:
                return new StatusFragment();
            default:
                return new ChatsFragment(); // Mặc định trả về ChatsFragment
        }
    }

    @Override
    public int getCount() {
        return 3; // Tổng số tab
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Chats"; // Tên tab cho fragment chat
            case 1:
                return "Calls"; // Tên tab cho fragment gọi
            case 2:
                return "Status"; // Tên tab cho fragment trạng thái
            default:
                return null;
        }
    }
}
