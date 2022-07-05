package com.mohammedev.allmightpedia.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.FanArtPost;
import com.mohammedev.allmightpedia.data.User;
import com.mohammedev.allmightpedia.utils.CurrentUserData;
import com.mohammedev.allmightpedia.utils.DoubleClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class FanArtAdapter extends RecyclerView.Adapter<FanArtAdapter.FanArtViewHolder> {
    ArrayList<FanArtPost> fansList;
    Context context;
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
        likeCounter = currentFanArtPost.getLikeCounter();
        String imageID = currentFanArtPost.getImageID();
        boolean isItLiked = checkIfLiked();

        holder.userNameTxt.setText(currentFanArtPost.getUserName());
        holder.likeCounterTxt.setText(String.valueOf(currentFanArtPost.getLikeCounter()));

        Picasso.with(context).load(currentFanArtPost.getUserImageUrl()).into(holder.userImage);
        Picasso.with(context).load(currentFanArtPost.getPostImageUrl()).into(holder.postImage);

        if (isItLiked){
            holder.likeButtonImg.setImageResource(R.drawable.ic_heart_red);
        }

        holder.likeButtonImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isItLiked) {
                    likeCounter--;
                    currentFanArtPost.setLikeCounter(likeCounter--);
                    holder.likeButtonImg.setImageResource(R.drawable.ic_heart);
                    holder.likeCounterTxt.setText(String.valueOf(likeCounter--));
                    notifyDataSetChanged();
                    dislikeFunction(likeCounter , imageID);


                } else {
                    likeCounter++;
                    holder.likeButtonImg.setImageResource(R.drawable.ic_heart_red);
                    currentFanArtPost.setLikeCounter(likeCounter);
                    holder.likeCounterTxt.setText(String.valueOf(likeCounter));
                    notifyDataSetChanged();
                    likeFunction(likeCounter , imageID);

                }
            }
        });

        holder.postImage.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick(View v) {
                Toast.makeText(context, isItLiked + "", Toast.LENGTH_SHORT).show();
                if (isItLiked) {
                    likeCounter++;
                    holder.likeButtonImg.setImageResource(R.drawable.ic_heart_red);
                    currentFanArtPost.setLikeCounter(likeCounter);
                    holder.likeCounterTxt.setText(String.valueOf(likeCounter));
                    notifyDataSetChanged();
                    Toast.makeText(context, String.valueOf(likeCounter), Toast.LENGTH_SHORT).show();
                    likeFunction(likeCounter , imageID);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return fansList.size();
    }

    public class FanArtViewHolder extends RecyclerView.ViewHolder {
        private TextView userNameTxt , likeCounterTxt;
        private ShapeableImageView userImage;
        private ImageView postImage;
        private final ImageView likeButtonImg;
        public FanArtViewHolder(@NonNull View itemView) {
            super(itemView);

            userNameTxt = itemView.findViewById(R.id.user_name_post);
            userImage = itemView.findViewById(R.id.user_image_post);
            postImage = itemView.findViewById(R.id.post_image);
            likeButtonImg = itemView.findViewById(R.id.post_like_btn);
            likeCounterTxt = itemView.findViewById(R.id.post_like_counter);
        }
    }

    public void likeFunction(int likeCounter , String imageID){
        String userUID = CurrentUserData.USER_UID;


        if (userUID != null && imageID != null && currentFanArtPost.getLikedUsers() != null){
            int numberOfLikedUsers = currentFanArtPost.getLikedUsers().size();
            FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
            DatabaseReference dataBaseReference = dataBase.getReference("users").child(userUID).child("posts").child(imageID);
            dataBaseReference.child("likedUsers").child("user" + numberOfLikedUsers).setValue(CurrentUserData.USER_DATA.getUserName());
            dataBaseReference.child("likeCounter").setValue(likeCounter);

            fetchFeed();

        }

    }

    public void updateList(ArrayList<FanArtPost> fanArtPosts){
        fansList.clear();
        fansList.addAll(fanArtPosts);
        notifyDataSetChanged();
    }

    public void dislikeFunction(int likeCounter , String imageID){
        String userUID = CurrentUserData.USER_UID;
        currentFanArtPost.getImageID();

        if (userUID != null && imageID != null && currentFanArtPost.getLikedUsers() != null){
            FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
            DatabaseReference dataBaseReference = dataBase.getReference("users").child(userUID).child("posts").child(imageID);
            dataBaseReference.child("likedUsers").orderByChild(CurrentUserData.USER_DATA.getUserName()).getRef().removeValue();
            dataBaseReference.child("likeCounter").setValue(likeCounter);


            fetchFeed();

        }

    }

    public boolean checkIfLiked() {
        if (currentFanArtPost.getLikedUsers() != null) {
            HashMap<String, String> likedUsers = currentFanArtPost.getLikedUsers();
                String userName = CurrentUserData.USER_DATA.getUserName();

                if (likedUsers.containsValue(userName)) {
                    System.out.println("true or false: " + true);
                    return true;
                } else if (!likedUsers.containsValue(userName)) {
                    System.out.println("true or false: " + false);
                    return false;
                }
                return false;

        }else{
            System.out.println(currentFanArtPost.getLikedUsers() == null);
        }
        return false;
    }

    public void fetchFeed() {
        ArrayList<User> userList = new ArrayList<>();
        ArrayList<FanArtPost> fansPosts = new ArrayList<>();
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
                                updateList(fansPosts);

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
}
