package com.mohammedev.allmightpedia.ui.fansArt;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.FanArtPost;
import com.mohammedev.allmightpedia.utils.CurrentUserData;
import com.mohammedev.allmightpedia.utils.ViewSpaces;

import java.util.ArrayList;

public class FansArtFragment extends Fragment {

    FanArtAdapter fanArtAdapter;
    ArrayList<FanArtPost> fanPostsList = new ArrayList<>();
    FloatingActionButton fabNewPost;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fans_art, container, false);

        fabNewPost = view.findViewById(R.id.add_new_post_fab);

        fabNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CurrentUserData.USER_UID != "" || CurrentUserData.USER_DATA != null){
                    Intent newPostIntent = new Intent(getActivity(), AddNewPostActivity.class);
                    startActivity(newPostIntent);
                }else {
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                            .setTitle(R.string.sign_in_required)
                            .setMessage(R.string.sign_in_required_to_upload_posts)
                            .setPositiveButton("Sign in", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent signInIntent = new Intent(getActivity() , LoginActivity.class);
                                    startActivity(signInIntent);
                                    getActivity().finish();
                                }
                            }).show();

                    alertDialog.create();
                }

            }
        });

        if (CurrentUserData.USER_DATA != null){
            fanPostsList.add(new FanArtPost("Mohammed Mansour" , CurrentUserData.USER_DATA.getImageUrl() , CurrentUserData.USER_DATA.getImageUrl()
                    , true , 15));
            fanPostsList.add(new FanArtPost("Mohammed Mansour" , CurrentUserData.USER_DATA.getImageUrl() , CurrentUserData.USER_DATA.getImageUrl()
                    , true , 15));
            fanPostsList.add(new FanArtPost("Mohammed Mansour" , CurrentUserData.USER_DATA.getImageUrl() , CurrentUserData.USER_DATA.getImageUrl()
                    , true , 15));
            fanPostsList.add(new FanArtPost("Mohammed Mansour" , CurrentUserData.USER_DATA.getImageUrl() , CurrentUserData.USER_DATA.getImageUrl()
                    , true , 15));
            fanPostsList.add(new FanArtPost("Mohammed Mansour" , CurrentUserData.USER_DATA.getImageUrl() , CurrentUserData.USER_DATA.getImageUrl()
                    , true , 15));
        }



        ObservableRecyclerView observableRecyclerView = view.findViewById(R.id.feedRecyclerView);

        observableRecyclerView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
            @Override
            public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

            }

            @Override
            public void onDownMotionEvent() {

            }

            @Override
            public void onUpOrCancelMotionEvent(ScrollState scrollState) {

            }
        });

        fanArtAdapter = new FanArtAdapter(fanPostsList, getContext());

        observableRecyclerView.setAdapter(fanArtAdapter);
        observableRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        observableRecyclerView.addItemDecoration(new ViewSpaces(20));


        return view;
    }


}