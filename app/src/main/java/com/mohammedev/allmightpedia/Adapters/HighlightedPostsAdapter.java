package com.mohammedev.allmightpedia.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mohammedev.allmightpedia.Activities.ArtViewActivity;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.FanArtPost;
import com.mohammedev.allmightpedia.data.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

        holder.highlightedMomentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , ArtViewActivity.class);
                intent.putExtra("Post" , currentPost);
                context.startActivity(intent);
            }
        });

        holder.highlightedMomentImageView.setOnClickListener(new View.OnClickListener() {
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

    public static class HighlightedPostsViewHolder extends RecyclerView.ViewHolder {
        private ImageView highlightedMomentImageView;
        private TextView highlightedMomentTextView;
        public HighlightedPostsViewHolder(@NonNull View itemView) {
            super(itemView);
            highlightedMomentImageView = itemView.findViewById(R.id.highlighted_moment_img);
            highlightedMomentTextView = itemView.findViewById(R.id.highlighted_moment_name);

        }

    }

    public void fetchFeed(){
        ArrayList<FanArtPost> fansPosts = new ArrayList<>();
        ArrayList<User> userList = new ArrayList<>();
        Query usersQuery = FirebaseDatabase.getInstance().getReference().child("users");
        usersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot users: snapshot.getChildren()){
                    userList.add(users.getValue(User.class));
                }

                if (userList.size() > 1){
                    for (int i = 0; i < userList.size(); i++){
                        Query userPostsQuery = FirebaseDatabase.getInstance().getReference().child("users")
                                .child(userList.get(i).getUserID()).child("posts");

                        userPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot posts: snapshot.getChildren()){
                                    FanArtPost fanArtPost = posts.getValue(FanArtPost.class);
                                    fansPosts.add(fanArtPost);
                                }

                                getTopLiked(fansPosts);
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
        fanArtPosts.sort(Comparator.comparingInt(FanArtPost::getLikeCounter));
        Collections.reverse(fanArtPosts);

        for (int i = fanArtPosts.size(); i >= 0; i--){
            System.out.println(i);
        }
    }
}
