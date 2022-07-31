package com.mohammedev.allmightpedia.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.TextView;

import com.mohammedev.allmightpedia.Adapters.DetailsActivityVIewPagerAdapter;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.InfoItem;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ViewPager2 viewPager2 = findViewById(R.id.view_pager2);
        DetailsActivityVIewPagerAdapter fragmentPagerAdapter = new DetailsActivityVIewPagerAdapter(DetailsActivity.this);

        viewPager2.setAdapter(fragmentPagerAdapter);

        Bundle bundle = getIntent().getExtras();

        InfoItem infoItem = bundle.getParcelable("infoItem");

        TextView infoTitle , infoDesc;

        infoTitle = findViewById(R.id.info_title);
        infoDesc = findViewById(R.id.info_desc);

        infoTitle.setText(infoItem.getInfoTitle());
        infoDesc.setText(infoItem.getInfoDesc());

    }
}