package com.mohammedev.allmightpedia.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.FanArtPost;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HighlightedPostsAdapter extends RecyclerView.Adapter<HighlightedPostsAdapter.HighlightedPostsViewHolder>{
    private ArrayList<FanArtPost> postArrayList;
    private Context context;

    public HighlightedPostsAdapter(ArrayList<FanArtPost> postArrayList , Context context) {
        this.postArrayList = postArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public HighlightedPostsAdapter.HighlightedPostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.highlighted_moments_list_item , parent , false);

        return new HighlightedPostsAdapter.HighlightedPostsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HighlightedPostsAdapter.HighlightedPostsViewHolder holder, int position) {
        FanArtPost currentPost = postArrayList.get(position);
        holder.highlightedMomentTextView.setText(currentPost.getUserName());
        Picasso.with(context).load(currentPost.getPostImageUrl()).into(holder.highlightedMomentImageView);
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    public static class HighlightedPostsViewHolder extends RecyclerView.ViewHolder {
        private ImageView highlightedMomentImageView;
        private TextView highlightedMomentTextView;
        public HighlightedPostsViewHolder(@NonNull View itemView) {
            super(itemView);
            highlightedMomentImageView = itemView.findViewById(R.id.highlighted_moment_img);
            highlightedMomentTextView = itemView.findViewById(R.id.highlighted_moment_name);

        }

    }
}
