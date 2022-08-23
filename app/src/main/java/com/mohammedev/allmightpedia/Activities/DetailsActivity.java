package com.mohammedev.allmightpedia.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mohammedev.allmightpedia.Adapters.DetailsActivityViewPagerAdapter;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.InfoItem;
import com.mohammedev.allmightpedia.data.Moment;

public class DetailsActivity extends AppCompatActivity {

    private static void onConfigureTab(TabLayout.Tab tab, int position) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        TextView infoTitle , infoDesc;

        infoTitle = findViewById(R.id.info_title);
        infoDesc = findViewById(R.id.info_desc);
        infoDesc.setMovementMethod(new ScrollingMovementMethod());

        String className = getIntent().getStringExtra("Class");
        if (className != null) {
            if (className.equals("InfoListActivity")) {
                System.out.println(className);
                InfoItem infoItem = getIntent().getParcelableExtra("infoItem");
                infoTitle.setText(infoItem.getInfoTitle());
                String infoDescString = infoItem.getInfoDesc();
                infoDesc.setText(infoDescString);

                ViewPager2 viewPager2 = findViewById(R.id.view_pager2);
                TabLayout viewPagerTabLayoutDotIndicator = findViewById(R.id.tabDots);
                DetailsActivityViewPagerAdapter fragmentPagerAdapter = new DetailsActivityViewPagerAdapter(DetailsActivity.this, infoItem.getInfoVideoUrl(), infoItem.getInfoImageUrl(), viewPagerTabLayoutDotIndicator);
                viewPager2.setAdapter(fragmentPagerAdapter);

                new TabLayoutMediator(viewPagerTabLayoutDotIndicator, viewPager2, DetailsActivity::onConfigureTab).attach();

            } else if (className.equals("SlideShowFragment")) {
                System.out.println(className);

                String momentTitle = getIntent().getStringExtra("MomentTitle");
                String momentDesc = getIntent().getStringExtra("MomentDesc");
                String momentVideo = getIntent().getStringExtra("MomentVideo");
                String momentImage = getIntent().getStringExtra("MomentImage");

                infoTitle.setText(momentTitle);
                infoDesc.setText(momentDesc);

                ViewPager2 viewPager2 = findViewById(R.id.view_pager2);
                TabLayout viewPagerTabLayoutDotIndicator = findViewById(R.id.tabDots);
                DetailsActivityViewPagerAdapter fragmentPagerAdapter = new DetailsActivityViewPagerAdapter(DetailsActivity.this, momentVideo, momentImage, viewPagerTabLayoutDotIndicator);
                viewPager2.setAdapter(fragmentPagerAdapter);
            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                overridePendingTransition(R.anim.slide_in_left , R.anim.slide_out_right);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left , R.anim.slide_out_right);
    }
}