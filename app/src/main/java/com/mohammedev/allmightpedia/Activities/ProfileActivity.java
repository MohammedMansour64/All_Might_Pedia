package com.mohammedev.allmightpedia.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohammedev.allmightpedia.Adapters.PostsAdapter;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.FanArtPost;
import com.mohammedev.allmightpedia.data.User;
import com.mohammedev.allmightpedia.utils.CurrentUserData;
import com.mohammedev.allmightpedia.utils.OnGetDataListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private ShapeableImageView userImage;
    private TextView userName , userBio , userEmail;
    private Button editButton;
    private RecyclerView recyclerView;
    public PostsAdapter postsAdapter;
    User user;
    ArrayList<FanArtPost> fanArtPostArrayList = new ArrayList<>();
    SkeletonScreen skeletonScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userImage = findViewById(R.id.user_image_profile);
        userName = findViewById(R.id.user_name_profile);
        userBio = findViewById(R.id.user_bio_profile);
        userEmail = findViewById(R.id.user_email_profile);
        editButton = findViewById(R.id.edit_profile_btn);
        recyclerView = findViewById(R.id.posts_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this , 3));
        skeletonScreen = Skeleton.bind(recyclerView).load(R.layout.layout_posts_skeleton).show();

        setUserData();
    }

    public void setUserData(){
        Bundle bundle = getIntent().getExtras();

        user = bundle.getParcelable("user");



        if (user != null){
            Picasso.with(this).load(user.getImageUrl()).into(userImage);

            userName.setText(user.getUserName());
            userBio.setText(user.getUserBio());
            userEmail.setText(user.getEmail());


            databaseReference.child("users").child(user.getUserID()).child("posts").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()){
                        FanArtPost fanArtPost = ds.getValue(FanArtPost.class);
                        fanArtPostArrayList.add(fanArtPost);
                    }

                    postsAdapter.updateList(fanArtPostArrayList);
                    postsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            postsAdapter = new PostsAdapter(fanArtPostArrayList , this);
            recyclerView.setAdapter(postsAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(this , 3));
        }else{
            String userID = bundle.getString("userID");
            databaseReference.child("users").child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user = snapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            mReadDataOnce(new OnGetDataListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess(DataSnapshot data) {
                    User user = data.getValue(User.class);

            Picasso.with(ProfileActivity.this).load(user.getImageUrl()).into(userImage);

            userName.setText(user.getUserName());
            userBio.setText(user.getUserBio());
            userEmail.setText(user.getEmail());


            databaseReference.child("users").child(user.getUserID()).child("posts").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()){
                        FanArtPost fanArtPost = ds.getValue(FanArtPost.class);
                        fanArtPostArrayList.add(fanArtPost);
                    }

                    postsAdapter.updateList(fanArtPostArrayList);
                    postsAdapter.notifyDataSetChanged();

                    if (fanArtPostArrayList != null && user.getUserID() != null){
                        getUserData(user.getUserID());

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            postsAdapter = new PostsAdapter(fanArtPostArrayList , ProfileActivity.this);
            recyclerView.setAdapter(postsAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(ProfileActivity.this , 3));
                }

                @Override
                public void onFailed(DatabaseError databaseError) {

                }
            } , userID);



        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!fanArtPostArrayList.isEmpty() && user.getUserID() != null){
            getUserData(user.getUserID());

        }
    }

    private void getUserData(String userUID) {
        databaseReference.child("users").child(userUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CurrentUserData.USER_DATA = snapshot.getValue(User.class);

                for (int i = 0; i < fanArtPostArrayList.size(); i++){
                    fanArtPostArrayList.get(i).setUserName(CurrentUserData.USER_DATA.getUserName());
                    fanArtPostArrayList.get(i).setUserImageUrl(CurrentUserData.USER_DATA.getImageUrl());

                }

                postsAdapter.updateList(fanArtPostArrayList);
                postsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("users").child(userUID).child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    if (data != null){
                        FanArtPost fanArtPost = data.getValue(FanArtPost.class);
                        fanArtPostArrayList.add(fanArtPost);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void mReadDataOnce(final OnGetDataListener listener , String userID) {
        listener.onStart();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }
}