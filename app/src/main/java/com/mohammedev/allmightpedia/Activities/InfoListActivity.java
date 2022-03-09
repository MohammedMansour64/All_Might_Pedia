package com.mohammedev.allmightpedia.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.mohammedev.allmightpedia.Adapters.InfoListAdapter;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.InfoItem;

import java.util.ArrayList;

public class InfoListActivity extends AppCompatActivity {
    ArrayList<InfoItem> infoItemList;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_list);

        listView = findViewById(R.id.listView);

        Bundle bundle = getIntent().getExtras();

        infoItemList = bundle.getParcelableArrayList("infoItemList");

        InfoListAdapter infoListAdapter = new InfoListAdapter(InfoListActivity.this, 0 , infoItemList);

        listView.setAdapter(infoListAdapter);

    }
}