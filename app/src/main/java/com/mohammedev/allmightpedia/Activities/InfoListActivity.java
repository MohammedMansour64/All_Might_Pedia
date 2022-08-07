package com.mohammedev.allmightpedia.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mohammedev.allmightpedia.Adapters.InfoListAdapter;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.InfoItem;

import java.util.ArrayList;

public class InfoListActivity extends AppCompatActivity {
    ArrayList<InfoItem> infoItemList = new ArrayList<>();
    RecyclerView recyclerView;
    InfoListAdapter infoListAdapter;
    ProgressBar infoProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_list);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

         infoProgress= findViewById(R.id.info_progress);


        recyclerView = findViewById(R.id.recyclerview_info);

        Bundle bundle = getIntent().getExtras();

        String infoTypeString = bundle.getString("infoType");
        String infoTypeText = bundle.getString("infoTypeText");

        fetchInfoList(infoTypeString , infoTypeText);


    }

    public void fetchInfoList(String infoType , String infoTypeText){
        infoProgress.setVisibility(View.VISIBLE);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("additionalInfo").child(infoType);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot infoItem: snapshot.getChildren()){
                    InfoItem infoItem1 = infoItem.getValue(InfoItem.class);
                    infoItemList.add(infoItem1);

                }
                System.out.println(infoItemList.size());
                infoListAdapter = new InfoListAdapter(infoItemList , infoTypeText , InfoListActivity.this);

                recyclerView.setAdapter(infoListAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(InfoListActivity.this , RecyclerView.VERTICAL , false));
                infoProgress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}