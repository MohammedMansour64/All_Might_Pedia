package com.mohammedev.allmightpedia.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.InfoItem;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Bundle bundle = getIntent().getExtras();

        InfoItem infoItem = bundle.getParcelable("infoItem");

        TextView infoTitle , infoDesc;

        infoTitle = findViewById(R.id.info_title);
        infoDesc = findViewById(R.id.info_name);

        infoTitle.setText(infoItem.getInfoTitle());
//        infoDesc.setText(infoItem.getInfoDesc());

    }
}