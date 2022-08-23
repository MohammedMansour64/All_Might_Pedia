package com.mohammedev.allmightpedia.ui.fansArt;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mohammedev.allmightpedia.Activities.AddNewPostActivity;
import com.mohammedev.allmightpedia.Activities.LoginActivity;
import com.mohammedev.allmightpedia.Adapters.FanArtAdapter;
import com.mohammedev.allmightpedia.Adapters.HighlightedPostsAdapter;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.FanArtPost;
import com.mohammedev.allmightpedia.data.User;
import com.mohammedev.allmightpedia.utils.CurrentUserData;
import com.mohammedev.allmightpedia.utils.ViewSpaces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import pl.droidsonroids.gif.GifImageView;


public class FansArtFragment extends Fragment{

    FanArtAdapter fanArtAdapter;
    ArrayList<FanArtPost> fanPostsList = new ArrayList<>();
    FloatingActionButton fabNewPost;
    RecyclerView feedRecyclerView;
    RecyclerView highlightRecyclerView;
    SkeletonScreen postsSkeletonScreen;
    SkeletonScreen highlightSkeletonScreen;

    TextView noPostsText;
    GifImageView noPostsGifImage;
    TextView highLightedPostsText;
    TextView refreshFeedText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fans_art, container, false);

        fabNewPost = view.findViewById(R.id.add_new_post_fab);

        noPostsGifImage = view.findViewById(R.id.no_posts_gif);
        noPostsText = view.findViewById(R.id.no_result_txt);
        highLightedPostsText = view.findViewById(R.id.textView25);
        refreshFeedText = view.findViewById(R.id.refresh_feed_text);

        refreshFeedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshFeed();
            }
        });

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

        feedRecyclerView = view.findViewById(R.id.feedRecyclerView);
        highlightRecyclerView = view.findViewById(R.id.highlighted_arts_recyclerView);



        feedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        feedRecyclerView.addItemDecoration(new ViewSpaces(20));
        feedRecyclerView.setNestedScrollingEnabled(false);

        highlightRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        highlightRecyclerView.addItemDecoration(new ViewSpaces(20));
        postsSkeletonScreen = Skeleton.bind(feedRecyclerView).load(R.layout.layout_img_skeleton).show();
        highlightSkeletonScreen = Skeleton.bind(highlightRecyclerView).load(R.layout.layout_highlighted_skeleton).show();

        fetchFeed();
        return view;
    }

    public void fetchFeed(){
        ArrayList<User> userList = new ArrayList<>();
        Query usersQuery = FirebaseDatabase.getInstance().getReference().child("users");
        usersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot users: snapshot.getChildren()){
                    userList.add(users.getValue(User.class));
                }



                if (!userList.isEmpty()){
                    for (int i = 0; i < userList.size(); i++){
                        Query userPostsQuery = FirebaseDatabase.getInstance().getReference().child("users")
                                .child(userList.get(i).getUserID()).child("posts");

                        userPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {


                                for (DataSnapshot posts: snapshot.getChildren()){
                                    FanArtPost fanArtPost = posts.getValue(FanArtPost.class);
                                    fanPostsList.add(fanArtPost);
                                }

                                if (!fanPostsList.isEmpty()){
                                    getTopLiked(fanPostsList);
                                    fanArtAdapter = new FanArtAdapter(fanPostsList, getContext() , userList , postsSkeletonScreen);
                                    feedRecyclerView.setAdapter(fanArtAdapter);

                                }else {
                                    noPostsText.setVisibility(View.VISIBLE);
                                    noPostsGifImage.setVisibility(View.VISIBLE);
                                    refreshFeedText.setVisibility(View.VISIBLE);
                                    feedRecyclerView.setVisibility(View.INVISIBLE);
                                    highlightRecyclerView.setVisibility(View.INVISIBLE);
                                    highLightedPostsText.setVisibility(View.INVISIBLE);
                                    highlightSkeletonScreen.hide();
                                    postsSkeletonScreen.hide();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }


                        });

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                        if (userList.isEmpty()){
                            noPostsText.setVisibility(View.VISIBLE);
                            noPostsGifImage.setVisibility(View.VISIBLE);
                            refreshFeedText.setVisibility(View.VISIBLE);
                            feedRecyclerView.setVisibility(View.INVISIBLE);
                            highlightRecyclerView.setVisibility(View.INVISIBLE);
                            highLightedPostsText.setVisibility(View.INVISIBLE);
                            highlightSkeletonScreen.hide();
                            postsSkeletonScreen.hide();
                        }


            }
        }, 5000);



    }

    public void refreshFeed(){
        noPostsText.setVisibility(View.INVISIBLE);
        noPostsGifImage.setVisibility(View.INVISIBLE);
        refreshFeedText.setVisibility(View.INVISIBLE);
        feedRecyclerView.setVisibility(View.VISIBLE);
        highlightRecyclerView.setVisibility(View.VISIBLE);
        highLightedPostsText.setVisibility(View.VISIBLE);
        highlightSkeletonScreen = Skeleton.bind(highlightRecyclerView).load(R.layout.layout_highlighted_skeleton).show();
        postsSkeletonScreen = Skeleton.bind(highlightRecyclerView).load(R.layout.layout_highlighted_skeleton).show();
        fetchFeed();
    }

    public void getTopLiked(ArrayList<FanArtPost> fanArtPosts){

        ArrayList<FanArtPost> highLightedPosts = new ArrayList<>();
        fanArtPosts.sort(Comparator.comparingInt(FanArtPost::getLikeCounter));
        Collections.reverse(fanArtPosts);

        if (fanArtPosts.size() > 2){
            for (int i = 0; i < 3; i++){
                highLightedPosts.add(fanArtPosts.get(i));
            }

            HighlightedPostsAdapter highlightedPostsAdapter = new HighlightedPostsAdapter(highLightedPosts, getContext());
            highlightRecyclerView.setAdapter(highlightedPostsAdapter);

        }
    }

}