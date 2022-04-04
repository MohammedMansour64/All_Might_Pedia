package com.mohammedev.allmightpedia.ui.gallery;

import android.app.FragmentTransaction;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mohammedev.allmightpedia.Activities.LoginActivity;
import com.mohammedev.allmightpedia.Adapters.GalleryPagerAdapter;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.databinding.FragmentGalleryBinding;
import com.mohammedev.allmightpedia.utils.CurrentUserData;

public class GalleryFragment extends Fragment {
    private FragmentGalleryBinding binding;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TabLayout tabLayout = binding.tabLayout;
        ViewPager2 viewPager2 = binding.viewPager;
        GalleryPagerAdapter pagerAdapter = new GalleryPagerAdapter(GalleryFragment.this.getActivity());
        viewPager2.setAdapter(pagerAdapter);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0){
                    getFragmentManager().beginTransaction().detach(GalleryFragment.this).attach(GalleryFragment.this).commit();
                }else if (tab.getPosition() == 1 && CurrentUserData.USER_DATA == null){
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                            .setTitle(R.string.sign_in_required_to_show_favourites)
                            .setPositiveButton("Sign in", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent signInIntent = new Intent(getActivity() , LoginActivity.class);
                                    startActivity(signInIntent);
                                }
                            }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    tabLayout.getTabAt(0).select();
                                }
                            }).show();

                    alertDialog.create();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        new TabLayoutMediator(tabLayout, viewPager2 , false , true
                , new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

            }
        }).attach();

        tabLayout.getTabAt(0).setText("Gallery");
        tabLayout.getTabAt(1).setText("Favourites");

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}