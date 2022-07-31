package com.mohammedev.allmightpedia.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.mohammedev.allmightpedia.Activities.ArtViewActivity;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.FanArtPost;
import com.mohammedev.allmightpedia.ui.profile.ProfileFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsViewHolder> {
    public ArrayList<FanArtPost> postArrayList;
    private final Context context;
    ShimmerFrameLayout shimmerFrameLayout;


    public PostsAdapter(ArrayList<FanArtPost> postArrayList , Context context , ShimmerFrameLayout shimmerFrameLayout) {
        this.postArrayList = postArrayList;
        this.context = context;
        this.shimmerFrameLayout = shimmerFrameLayout;
    }

    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_out_layout , parent , false);

        return new PostsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {
        shimmerFrameLayout.startShimmer();
        FanArtPost currentPost = postArrayList.get(position);
        Picasso.with(context).load(currentPost.getPostImageUrl()).into(holder.postOutImage, new Callback() {
            @Override
            public void onSuccess() {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError() {

            }
        });


        holder.postOutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , ArtViewActivity.class);
                intent.putExtra("Post" , currentPost);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        private ImageView postOutImage;
        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            postOutImage = itemView.findViewById(R.id.post_out_img);

        }

    }

    public void updateList(ArrayList<FanArtPost> fanArtPosts){
        postArrayList.clear();
        postArrayList.addAll(fanArtPosts);
        notifyDataSetChanged();
    }
}
