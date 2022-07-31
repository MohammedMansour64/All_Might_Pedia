package com.mohammedev.allmightpedia.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mohammedev.allmightpedia.utils.DetailsActivityPhotoFragment;
import com.mohammedev.allmightpedia.utils.DetailsActivityVideoFragment;

public class DetailsActivityVIewPagerAdapter extends FragmentStateAdapter {
    public DetailsActivityVIewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new DetailsActivityPhotoFragment();

            case 1:
                return new DetailsActivityVideoFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
