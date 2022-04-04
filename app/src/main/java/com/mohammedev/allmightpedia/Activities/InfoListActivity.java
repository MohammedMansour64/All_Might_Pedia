package com.mohammedev.allmightpedia.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mohammedev.allmightpedia.Adapters.InfoListAdapter;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.InfoItem;

import java.util.ArrayList;

public class InfoListActivity extends AppCompatActivity {

    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_list);

        ArrayList<InfoItem> infoItemList;
        listView = findViewById(R.id.listView);

        Bundle bundle = getIntent().getExtras();

        infoItemList = bundle.getParcelableArrayList("infoItemList");

        InfoListAdapter infoListAdapter = new InfoListAdapter(InfoListActivity.this, 0 , infoItemList);

        listView.setAdapter(infoListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InfoItem clickedItem = infoItemList.get(position);

                Intent detailsIntent = new Intent(InfoListActivity.this , DetailsActivity.class);
                detailsIntent.putExtra("infoItem" , clickedItem);
                startActivity(detailsIntent);
            }
        });

    }
}