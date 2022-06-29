package com.mohammedev.allmightpedia.ui.fansArt;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mohammedev.allmightpedia.Activities.AddNewPostActivity;
import com.mohammedev.allmightpedia.Activities.LoginActivity;
import com.mohammedev.allmightpedia.Adapters.FanArtAdapter;
import com.mohammedev.allmightpedia.Adapters.HighlightedPostsAdapter;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.FanArtPost;
import com.mohammedev.allmightpedia.utils.CurrentUserData;
import com.mohammedev.allmightpedia.utils.ViewSpaces;

import java.util.ArrayList;


public class FansArtFragment extends Fragment{

    FanArtAdapter fanArtAdapter;
    ArrayList<FanArtPost> fanPostsList = new ArrayList<>();
    FloatingActionButton fabNewPost;
    ConstraintLayout constraintLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fans_art, container, false);

        fabNewPost = view.findViewById(R.id.add_new_post_fab);

        fabNewPost.setOnClickListener(v -> {
            if (!CurrentUserData.USER_UID.equals("") || CurrentUserData.USER_DATA != null){
                Intent newPostIntent = new Intent(getActivity(), AddNewPostActivity.class);
                startActivity(newPostIntent);
            }else {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.sign_in_required)
                        .setMessage(R.string.sign_in_required_to_upload_posts)
                        .setPositiveButton("Sign in", (dialog, which) -> {
                            Intent signInIntent = new Intent(getActivity() , LoginActivity.class);
                            startActivity(signInIntent);
                            requireActivity().finish();
                        }).show();

                alertDialog.create();
            }

        });

        if (CurrentUserData.USER_DATA != null){
            fanPostsList.add(new FanArtPost("Mohammed Mansour" , CurrentUserData.USER_DATA.getImageUrl() , CurrentUserData.USER_DATA.getImageUrl()
                    , true , "15" , "0"));
            fanPostsList.add(new FanArtPost("Mohammed Mansour" , CurrentUserData.USER_DATA.getImageUrl() , CurrentUserData.USER_DATA.getImageUrl()
                    , true , "15" , "0"));
            fanPostsList.add(new FanArtPost("Mohammed Mansour" , CurrentUserData.USER_DATA.getImageUrl() , CurrentUserData.USER_DATA.getImageUrl()
                    , true , "15" , "0"));
            fanPostsList.add(new FanArtPost("Mohammed Mansour" , CurrentUserData.USER_DATA.getImageUrl() , CurrentUserData.USER_DATA.getImageUrl()
                    , true , "15" , "0"));
            fanPostsList.add(new FanArtPost("Mohammed Mansour" , CurrentUserData.USER_DATA.getImageUrl() , CurrentUserData.USER_DATA.getImageUrl()
                    , true , "15" , "0"));
        }



        RecyclerView feedRecyclerView = view.findViewById(R.id.feedRecyclerView);
        RecyclerView highlightRecyclerView = view.findViewById(R.id.highlighted_arts_recyclerView);

        fanArtAdapter = new FanArtAdapter(fanPostsList, getContext());
        HighlightedPostsAdapter highlightedPostsAdapter = new HighlightedPostsAdapter(fanPostsList, getContext());

        feedRecyclerView.setAdapter(fanArtAdapter);
        feedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        feedRecyclerView.addItemDecoration(new ViewSpaces(20));
        feedRecyclerView.setNestedScrollingEnabled(false);

        highlightRecyclerView.setAdapter(highlightedPostsAdapter);
        highlightRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        highlightRecyclerView.addItemDecoration(new ViewSpaces(20));

        constraintLayout = view.findViewById(R.id.constraintLayout);

        return view;
    }

}