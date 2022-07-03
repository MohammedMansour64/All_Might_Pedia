package com.mohammedev.allmightpedia.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mohammedev.allmightpedia.Activities.ArtViewActivity;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.FanArtPost;
import com.mohammedev.allmightpedia.utils.CurrentUserData;
import com.mohammedev.allmightpedia.utils.DoubleClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FanArtAdapter extends RecyclerView.Adapter<FanArtAdapter.FanArtViewHolder> {
    ArrayList<FanArtPost> fansList;
    Context context;
    boolean likeButton;
    int likeCounter;
    FanArtPost currentFanArtPost;

    public FanArtAdapter(ArrayList<FanArtPost> fansList, Context context) {
        this.fansList = fansList;
        this.context = context;
    }

    @NonNull
    @Override
    public FanArtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.art_post_layout , parent , false);
        return new FanArtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FanArtViewHolder holder, int position) {
        currentFanArtPost = fansList.get(position);
        likeButton = currentFanArtPost.isLikeButton();
        likeCounter = currentFanArtPost.getLikeCounter();

        holder.userNameTxt.setText(currentFanArtPost.getUserName());
        holder.likeCounterTxt.setText(String.valueOf(currentFanArtPost.getLikeCounter()));

        Picasso.with(context).load(currentFanArtPost.getUserImageUrl()).into(holder.userImage);
        Picasso.with(context).load(currentFanArtPost.getPostImageUrl()).into(holder.postImage);

        if (likeButton){
            holder.likeButtonImg.setImageResource(R.drawable.ic_heart_red);
        }

        holder.likeButtonImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkIfLiked()) {
                    likeCounter--;
                    currentFanArtPost.setLikeCounter(likeCounter);
                    holder.likeButtonImg.setImageResource(R.drawable.ic_heart);
                    holder.likeCounterTxt.setText(String.valueOf(likeCounter));
                    updateLikes();

                } else if (!checkIfLiked()) {
                    likeCounter++;
                    holder.likeButtonImg.setImageResource(R.drawable.ic_heart_red);
                    currentFanArtPost.setLikeCounter(likeCounter);
                    holder.likeCounterTxt.setText(String.valueOf(likeCounter));
                    likeButton = true;
                    updateLikes();

                }
            }
        });

        holder.postImage.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick(View v) {
                if (!likeButton) {
                    likeCounter++;
                    holder.likeButtonImg.setImageResource(R.drawable.ic_heart_red);
                    currentFanArtPost.setLikeCounter(likeCounter);
                    holder.likeCounterTxt.setText(String.valueOf(likeCounter));
                    //updateLikes();
                    likeButton = true;
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return fansList.size();
    }

    public class FanArtViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout layout;
        private TextView userNameTxt , likeCounterTxt;
        private ShapeableImageView userImage;
        private ImageView postImage , likeButtonImg;
        public FanArtViewHolder(@NonNull View itemView) {
            super(itemView);

            userNameTxt = itemView.findViewById(R.id.user_name_post);
            userImage = itemView.findViewById(R.id.user_image_post);
            postImage = itemView.findViewById(R.id.post_image);
            likeButtonImg = itemView.findViewById(R.id.post_like_btn);
            likeCounterTxt = itemView.findViewById(R.id.post_like_counter);
            layout = itemView.findViewById(R.id.constraintLayout);
        }
    }

    public void updateLikes(){
        String userUID = CurrentUserData.USER_UID;
        String imageUID = currentFanArtPost.getImageID();
        if (userUID != null && imageUID != null){
            FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
            System.out.println("UID: " + userUID + " imageID: " + currentFanArtPost.getImageID());
            DatabaseReference dataBaseReference = dataBase.getReference("users").child(userUID).child("posts").child(imageUID);
            dataBaseReference.child("likes").child(userUID).child("userName").setValue(CurrentUserData.USER_DATA.getUserName());

        }

    }

    public boolean checkIfLiked(){
        List<String> likedUsers = currentFanArtPost.getLikes();
        String userName = CurrentUserData.USER_DATA.getUserName();

        if (likedUsers.contains(userName)){
            return true;
        }else if (!likedUsers.contains(userName)){
            return false;
        }
        return false;
    }
}
