package com.mohammedev.allmightpedia.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mohammedev.allmightpedia.Adapters.PostsAdapter;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.FanArtPost;
import com.mohammedev.allmightpedia.data.User;
import com.mohammedev.allmightpedia.ui.profile.ProfileFragment;
import com.mohammedev.allmightpedia.utils.CurrentUserData;
import com.mohammedev.allmightpedia.utils.DoubleClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ArtViewActivity extends AppCompatActivity {

    private final DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference();

    AnimatedVectorDrawableCompat avd;
    AnimatedVectorDrawable avd2;

    private TextView postUserNameTxt;
    private ImageView postUserImage;
    private ImageView postImage;
    private ImageView likeImage;
    private ImageView heartAnimationImage;
    private TextView likeCounterTxt;
    private boolean likeButton;
    private int likeCounter;
    private String imageID;
    private String userID;
    private FanArtPost fanArtPost;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_art_view);

        postUserNameTxt = findViewById(R.id.user_name_post);
        postUserImage = findViewById(R.id.user_image_post);
        postImage = findViewById(R.id.post_image);
        likeImage = findViewById(R.id.post_like_btn);
        likeCounterTxt = findViewById(R.id.post_like_counter);
        heartAnimationImage = findViewById(R.id.heart_animation_image);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            fanArtPost = (FanArtPost) bundle.getSerializable("Post");
            imageID = fanArtPost.getImageID();

            postUserNameTxt.setText(fanArtPost.getUserName());
            Picasso.with(this).load(fanArtPost.getUserImageUrl()).into(postUserImage);
            Picasso.with(this).load(fanArtPost.getPostImageUrl()).into(postImage);

            likeCounter = fanArtPost.getLikeCounter();

            likeCounterTxt.setText(String.valueOf(likeCounter));
            likeButton = checkIfLiked(fanArtPost.getLikedUsers());
            userID = fanArtPost.getUserID();

            if (likeButton) {
                likeImage.setImageResource(R.drawable.ic_heart_red);
            }


            likeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {

                        if (likeButton) {
                            likeCounter--;
                            fanArtPost.setLikeCounter(likeCounter);
                            likeImage.setImageResource(R.drawable.ic_heart);
                            likeCounterTxt.setText(String.valueOf(likeCounter));
                            dislikeFunction(likeCounter, imageID, userID);

                        } else if (!likeButton) {
                            likeCounter++;
                            likeImage.setImageResource(R.drawable.ic_heart_red);
                            fanArtPost.setLikeCounter(likeCounter);
                            likeCounterTxt.setText(String.valueOf(likeCounter));
                            likeFunction(likeCounter, imageID, userID);


                        }
                    }else{
                        AlertDialog alertDialog = new AlertDialog.Builder(ArtViewActivity.this)
                                .setTitle(R.string.sign_in_required)
                                .setMessage(R.string.sign_in_required_to_interact_with_art)
                                .setPositiveButton("Sign in", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent signInIntent = new Intent(ArtViewActivity.this , LoginActivity.class);
                                        startActivity(signInIntent);
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();

                        alertDialog.create();
                    }
                }
            });

            postImage.setOnClickListener(new DoubleClickListener() {
                @Override
                public void onDoubleClick(View v) {
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        animationFunction();
                        if (!likeButton) {
                            likeCounter++;
                            likeImage.setImageResource(R.drawable.ic_heart_red);
                            fanArtPost.setLikeCounter(likeCounter);
                            likeCounterTxt.setText(String.valueOf(likeCounter));
                            likeFunction(likeCounter, imageID, userID);
                        }
                    }else {
                        AlertDialog alertDialog = new AlertDialog.Builder(ArtViewActivity.this)
                                .setTitle(R.string.sign_in_required)
                                .setMessage(R.string.sign_in_required_to_interact_with_art)
                                .setPositiveButton("Sign in", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent signInIntent = new Intent(ArtViewActivity.this , LoginActivity.class);
                                        startActivity(signInIntent);
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();

                        alertDialog.create();
                    }
                }
            });

            postUserNameTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ArtViewActivity.this , ProfileActivity.class);
                    intent.putExtra("userID" , fanArtPost.getUserID());
                    startActivity(intent);
                }
            });

            postUserImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ArtViewActivity.this , ProfileActivity.class);
                    intent.putExtra("userID" , fanArtPost.getUserID());
                    startActivity(intent);
                }
            });
        }

    }

    public void animationFunction(){
        Drawable drawable = heartAnimationImage.getDrawable();

        heartAnimationImage.setAlpha(0.70f);

        if (drawable instanceof AnimatedVectorDrawableCompat){
            avd = (AnimatedVectorDrawableCompat) drawable;
            avd.start();
        }else if (drawable instanceof AnimatedVectorDrawable){
            avd2 = (AnimatedVectorDrawable) drawable;
            avd2.start();
        }
    }

    public void likeFunction(int likeCounter , String imageID , String userUID){

        if (userUID != null && imageID != null && fanArtPost.getLikedUsers() != null){
            FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
            DatabaseReference dataBaseReference = dataBase.getReference("users").child(userUID).child("posts").child(imageID);
            dataBaseReference.child("likedUsers").child(userUID).setValue(CurrentUserData.USER_DATA.getUserName());
            dataBaseReference.child("likeCounter").setValue(likeCounter);

            fetchPost();

        }

    }

    public void dislikeFunction(int likeCounter , String imageID , String userUID){

        if (userUID != null && imageID != null && fanArtPost.getLikedUsers() != null){
            FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
            DatabaseReference dataBaseReference = dataBase.getReference("users").child(userUID).child("posts").child(imageID);
            dataBaseReference.child("likedUsers").child(userUID).removeValue();
            dataBaseReference.child("likeCounter").setValue(likeCounter);

            fetchPost();

        }

    }

    public boolean checkIfLiked(HashMap<String, String> likedUsers) {
        HashMap<String, String> likedUsers1 = new HashMap<>();
        likedUsers1.put("test" , "user1");
        likedUsers1.putAll(likedUsers);
        if (likedUsers1 != null) {
            String userID = CurrentUserData.USER_UID;

            if (likedUsers1.containsKey(userID)) {
                return true;
            } else if (!likedUsers1.containsKey(userID)) {
                return false;
            }
            return false;

        }
        return false;
    }

    public void fetchPost() {
        databaseReference.child("users").child(userID)
                .child("posts").child(imageID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FanArtPost fanArtPost = snapshot.getValue(FanArtPost.class);

                postUserNameTxt.setText(fanArtPost.getUserName());

                Picasso.with(ArtViewActivity.this).load(fanArtPost.getUserImageUrl()).into(postUserImage);
                Picasso.with(ArtViewActivity.this).load(fanArtPost.getPostImageUrl()).into(postImage);

                likeCounter = fanArtPost.getLikeCounter();

                likeCounterTxt.setText(String.valueOf(likeCounter));

                likeButton = checkIfLiked(fanArtPost.getLikedUsers());
                likeCounter = fanArtPost.getLikeCounter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (userID != null){
            if (Objects.equals(CurrentUserData.USER_UID, userID)){
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.post_menu , menu);
                return true;
            }else {
                return false;
            }
        }else{
            System.out.println("NUll!!");
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_post){
            deletePost(userID, imageID);
            return true;
        }
        return false;
    }

    public void deletePost(String userID, String imageID){
        databaseReference.child("users").child(userID).child("posts").child(imageID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    System.out.println(ds.getRef());
                    ds.getRef().removeValue();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        storage.getReference().child("users").child(userID).child("posts").child(imageID).delete();


    }


}