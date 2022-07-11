package com.mohammedev.allmightpedia.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private ShapeableImageView userImage;
    private TextView userName , userBio , userEmail;
    private Button editButton;
    private RecyclerView recyclerView;
    public PostsAdapter postsAdapter;
    ArrayList<FanArtPost> fanArtPostArrayList = new ArrayList<>();

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

        setUserData();
    }

    public void setUserData(){
        Bundle bundle = getIntent().getExtras();

        User user = bundle.getParcelable("user");

        Picasso.with(this).load(user.getImageUrl()).into(userImage);

        userName.setText(user.getUserName());
        userBio.setText(user.getUserBio());
        userEmail.setText(user.getEmail());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(user.getUserID()).child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    FanArtPost fanArtPost = ds.getValue(FanArtPost.class);
                    fanArtPostArrayList.add(fanArtPost);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        postsAdapter = new PostsAdapter(fanArtPostArrayList , this);
        recyclerView.setAdapter(postsAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this , 3));

    }
}