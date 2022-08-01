package com.mohammedev.allmightpedia.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mohammedev.allmightpedia.Adapters.DetailsActivityVIewPagerAdapter;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.InfoItem;

public class DetailsActivity extends AppCompatActivity {

    private static void onConfigureTab(TabLayout.Tab tab, int position) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Bundle bundle = getIntent().getExtras();

        InfoItem infoItem = bundle.getParcelable("infoItem");

        TextView infoTitle , infoDesc;

        infoTitle = findViewById(R.id.info_title);
        infoDesc = findViewById(R.id.info_desc);

        infoTitle.setText(infoItem.getInfoTitle());
        infoDesc.setText(infoItem.getInfoDesc());

        ViewPager2 viewPager2 = findViewById(R.id.view_pager2);
        TabLayout viewPagerTabLayoutDotIndicator = findViewById(R.id.tabDots);
        DetailsActivityVIewPagerAdapter fragmentPagerAdapter = new DetailsActivityVIewPagerAdapter(DetailsActivity.this , infoItem.getInfoVideoUrl() , infoItem.getInfoImageUrl());
        viewPager2.setAdapter(fragmentPagerAdapter);

        new TabLayoutMediator(viewPagerTabLayoutDotIndicator, viewPager2, DetailsActivity::onConfigureTab).attach();

    }
}