package com.mohammedev.allmightpedia.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.mohammedev.allmightpedia.utils.DetailsActivityPhotoFragment;
import com.mohammedev.allmightpedia.utils.DetailsActivityVideoFragment;

public class DetailsActivityViewPagerAdapter extends FragmentStateAdapter {

    String videoUrl;
    String photoUrl;
    TabLayout tabLayout;
    public DetailsActivityViewPagerAdapter(@NonNull FragmentActivity fragmentActivity , String videoUrl , String photoUrl , TabLayout tabLayout) {
        super(fragmentActivity);
        this.videoUrl = videoUrl;
        this.photoUrl = photoUrl;
        this.tabLayout = tabLayout;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (videoUrl != null){
            System.out.println("videoUrl is not null" + videoUrl);
            switch (position){
                case 0:
                    return new DetailsActivityPhotoFragment(photoUrl);

                case 1:
                    return new DetailsActivityVideoFragment(videoUrl);
            }
        }else {
            System.out.println("videoUrl is null");
            return new DetailsActivityPhotoFragment(photoUrl);

        }

        return null;
    }

    @Override
    public int getItemCount() {
        if (videoUrl != null ){
            return 2;
        }else {
            return 1;
        }
    }
}
