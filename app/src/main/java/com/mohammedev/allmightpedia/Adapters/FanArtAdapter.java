package com.mohammedev.allmightpedia.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.FanArtPost;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FanArtAdapter extends RecyclerView.Adapter<FanArtAdapter.FanArtViewHolder> {
    ArrayList<FanArtPost> fansList;
    Context context;

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
        FanArtPost currentFanArtPost = fansList.get(position);
        holder.userNameTxt.setText(currentFanArtPost.getUserName());
        holder.likeCounterTxt.setText(String.valueOf(currentFanArtPost.getLikeCounter()));

        Picasso.with(context).load(currentFanArtPost.getUserImageUrl()).into(holder.userImage);
        Picasso.with(context).load(currentFanArtPost.getPostImageUrl()).into(holder.postImage);

    }

    @Override
    public int getItemCount() {
        return fansList.size();
    }

    public class FanArtViewHolder extends RecyclerView.ViewHolder {
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
        }
    }
}
