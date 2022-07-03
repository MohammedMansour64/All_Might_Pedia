package com.mohammedev.allmightpedia.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mohammedev.allmightpedia.Adapters.PostsAdapter;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.FanArtPost;
import com.mohammedev.allmightpedia.ui.profile.ProfileFragment;
import com.mohammedev.allmightpedia.utils.CurrentUserData;
import com.mohammedev.allmightpedia.utils.DoubleClickListener;
import com.squareup.picasso.Picasso;

public class ArtViewActivity extends AppCompatActivity {

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
    private FanArtPost fanArtPost;

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


            postUserNameTxt.setText(fanArtPost.getUserName());
            Picasso.with(this).load(fanArtPost.getUserImageUrl()).into(postUserImage);
            Picasso.with(this).load(fanArtPost.getPostImageUrl()).into(postImage);

            likeCounter = fanArtPost.getLikeCounter();

            likeCounterTxt.setText(String.valueOf(likeCounter));
            likeButton = fanArtPost.isLikeButton();

            if (likeButton) {
                likeImage.setImageResource(R.drawable.ic_heart_red);
            }


            likeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (likeButton) {
                        likeCounter--;
                        fanArtPost.setLikeCounter(likeCounter);
                        likeImage.setImageResource(R.drawable.ic_heart);
                        likeCounterTxt.setText(String.valueOf(likeCounter));
                        likeButton = false;
                        updateLikes();

                    } else if (!likeButton) {
                        likeCounter++;
                        likeImage.setImageResource(R.drawable.ic_heart_red);
                        fanArtPost.setLikeCounter(likeCounter);
                        likeCounterTxt.setText(String.valueOf(likeCounter));
                        likeButton = true;
                        updateLikes();


                    }
                }
            });

            postImage.setOnClickListener(new DoubleClickListener() {
                @Override
                public void onDoubleClick(View v) {
                    animationFunction();

                    if (!likeButton) {
                        likeCounter++;
                        likeImage.setImageResource(R.drawable.ic_heart_red);
                        fanArtPost.setLikeCounter(likeCounter);
                        likeCounterTxt.setText(String.valueOf(likeCounter));
                        updateLikes();
                        likeButton = true;
                    }
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

    public void updateLikes() {
        String userUID = CurrentUserData.USER_UID;
        String imageUID = fanArtPost.getImageID();
        if (userUID != null && imageUID != null){
            FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
            System.out.println("UID: " + userUID + " imageID: " + fanArtPost.getImageID());
            DatabaseReference dataBaseReference = dataBase.getReference("users").child(userUID).child("posts").child(imageUID);
            dataBaseReference.child("likeCounter").setValue(likeCounter);

            dataBase.getReference("users").child(userUID).child("posts").child(imageUID).child("likeButton").setValue(likeButton);
            Toast.makeText(this, String.valueOf(likeButton), Toast.LENGTH_SHORT).show();

        }

    }
}