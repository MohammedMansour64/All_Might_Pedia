package com.mohammedev.allmightpedia.ui.fansArt;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
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


public class FansArtFragment extends Fragment{

    FanArtAdapter fanArtAdapter;
    ArrayList<FanArtPost> fanPostsList = new ArrayList<>();
    FloatingActionButton fabNewPost;
    RecyclerView feedRecyclerView;
    RecyclerView highlightRecyclerView;
    SkeletonScreen skeletonScreen;
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

        feedRecyclerView = view.findViewById(R.id.feedRecyclerView);
        highlightRecyclerView = view.findViewById(R.id.highlighted_arts_recyclerView);



        feedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        feedRecyclerView.addItemDecoration(new ViewSpaces(20));
        feedRecyclerView.setNestedScrollingEnabled(false);

        highlightRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        highlightRecyclerView.addItemDecoration(new ViewSpaces(20));
        skeletonScreen = Skeleton.bind(feedRecyclerView).load(R.layout.layout_img_skeleton).show();
        skeletonScreen = Skeleton.bind(highlightRecyclerView).load(R.layout.layout_highlighted_skeleton).show();

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

                if (userList.size() > 0){
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
                                skeletonScreen.hide();
                                getTopLiked(fanPostsList);
                                fanArtAdapter = new FanArtAdapter(fanPostsList, getContext() , userList);
                                feedRecyclerView.setAdapter(fanArtAdapter);


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