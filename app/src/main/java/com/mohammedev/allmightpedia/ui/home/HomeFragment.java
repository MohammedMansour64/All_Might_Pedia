package com.mohammedev.allmightpedia.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mohammedev.allmightpedia.Activities.InfoListActivity;
import com.mohammedev.allmightpedia.Adapters.AdditionalInfoAdapter;
import com.mohammedev.allmightpedia.data.InfoItem;
import com.mohammedev.allmightpedia.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private ArrayList<InfoItem> infoItemList = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        String[] additionalInfoArray = {"All Might Smashes info" , "All Might Hero Ages" , "All Might battles" ,
                                        "All Might Outfits" , "More All Might Info"};

        ListView additionalInfoListView = binding.listView;
        AdditionalInfoAdapter additionalInfoAdapter = new AdditionalInfoAdapter(getContext(), 0, additionalInfoArray);

        additionalInfoListView.setAdapter(additionalInfoAdapter);

        additionalInfoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BackgroundTask backgroundTask;
                switch (position){
                    case 0:
                        backgroundTask = new BackgroundTask("all_might_smashes");
                        backgroundTask.start();
                      break;
                  case 1:
                        backgroundTask = new BackgroundTask("all_might_hero_ages");
                        backgroundTask.start();
                      break;
                  case 2:
                      backgroundTask = new BackgroundTask("all_might_fights");
                      backgroundTask.start();
                      break;
                  case 3:
                      backgroundTask = new BackgroundTask("all_might_outfits");
                      backgroundTask.start();
                      break;
                  case 4:
                      backgroundTask = new BackgroundTask("more_all_might");
                      backgroundTask.start();
                        break;
                }

            }
        });

        final TextView quote = binding.quoteTxt;
        final TextView description = binding.allMightDescription;
        homeViewModel.getQuote().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                quote.setText(s);

            }
        });

        homeViewModel.getDescription().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                description.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void fetchInfoList(String listName){
        Query query = FirebaseDatabase.getInstance().getReference().child("additionalInfo").child(listName);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                infoItemList.clear();
                for (DataSnapshot infoItem: snapshot.getChildren()){
                    InfoItem infoItem1 = infoItem.getValue(InfoItem.class);
                    infoItemList.add(infoItem1);
                }
                Intent listIntent = new Intent(getActivity(), InfoListActivity.class);
                listIntent.putParcelableArrayListExtra("infoItemList", infoItemList);
                startActivity(listIntent);
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
