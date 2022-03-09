package com.mohammedev.allmightpedia.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mohammedev.allmightpedia.ui.gallery.GalleryFragmentFavourites;
import com.mohammedev.allmightpedia.ui.gallery.GalleryFragmentList;

public class GalleryPagerAdapter extends FragmentStateAdapter {

    public GalleryPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new GalleryFragmentList();
            case 1:
                return new GalleryFragmentFavourites();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
