package com.mohammedev.allmightpedia.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mohammedev.allmightpedia.Adapters.InfoListAdapter;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.InfoItem;

import java.util.ArrayList;

public class InfoListActivity extends AppCompatActivity {
    ArrayList<InfoItem> infoItemList = new ArrayList<>();
    ListView listView;
    InfoListAdapter infoListAdapter;
    ProgressBar infoProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_list);

         infoProgress= findViewById(R.id.info_progress);


        listView = findViewById(R.id.listView_info);

        Bundle bundle = getIntent().getExtras();

        String infoTypeString = bundle.getString("infoType");

        BackgroundTask backgroundTask = new BackgroundTask(infoTypeString);
        backgroundTask.start();

        infoListAdapter = new InfoListAdapter(InfoListActivity.this, 0 , infoItemList);

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

    public void fetchInfoList(String listName){
        infoProgress.setVisibility(View.VISIBLE);
        Query query = FirebaseDatabase.getInstance().getReference().child("additionalInfo").child(listName);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                infoItemList.clear();
                for (DataSnapshot infoItem: snapshot.getChildren()){
                    InfoItem infoItem1 = infoItem.getValue(InfoItem.class);
                    infoItemList.add(infoItem1);
                    infoListAdapter.notifyDataSetChanged();
                    infoProgress.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("There was an error:" + error.getDetails());
            }
        });
    }

    class BackgroundTask extends Thread{
        String infoType;
        public BackgroundTask(String infoType) {
            this.infoType = infoType;
        }

        @Override
        public void run() {

            fetchInfoList(infoType);

        }
    }
}