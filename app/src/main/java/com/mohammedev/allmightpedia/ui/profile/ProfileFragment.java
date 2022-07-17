package com.mohammedev.allmightpedia.ui.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {
    private DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference();

    private ShapeableImageView userImage;
    private TextView userName , userBio , userEmail;
    private Button editButton;
    private RecyclerView recyclerView;
    private ConstraintLayout constraintLayout;
    private ViewStub viewStub;
    private EditText editProfileNameEditText, editProfileBioEditText;
    private ImageView editProfileImageView;
    public PostsAdapter postsAdapter;
    ArrayList<FanArtPost> fanArtPostArrayList = new ArrayList<>();



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_profile, container, false);

        userImage = view.findViewById(R.id.user_image_profile);
        userName = view.findViewById(R.id.user_name_profile);
        userBio = view.findViewById(R.id.user_bio_profile);
        userEmail = view.findViewById(R.id.user_email_profile);
        editButton = view.findViewById(R.id.edit_profile_btn);
        recyclerView = view.findViewById(R.id.posts_recycler);
        constraintLayout = view.findViewById(R.id.profile_fragment_layout);

        // included layout
        viewStub = view.findViewById(R.id.edit_profile_view_stub);
        viewStub.setLayoutResource(R.layout.edit_profile_layout);
        View inflated = viewStub.inflate();
        inflated.setVisibility(View.INVISIBLE);
        editProfileNameEditText = inflated.findViewById(R.id.user_name_edit_profile);
        editProfileImageView = inflated.findViewById(R.id.user_edit_image_profile);
        editProfileBioEditText = inflated.findViewById(R.id.user_bio_profile);
        /**--------------------------------------------------------------------------**/

        setUserData();

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfileNameEditText.setText(userName.getText().toString());
                editProfileBioEditText.setText(userBio.getText().toString());
                editProfileImageView.setImageDrawable(userImage.getDrawable());
                inflated.setVisibility(View.VISIBLE);
                constraintLayout.setVisibility(View.INVISIBLE);
            }
        });

        inflated.findViewById(R.id.edit_profile_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUserNameString = editProfileNameEditText.getText().toString();
                String newUserBioString = editProfileBioEditText.getText().toString();
                editProfile(CurrentUserData.USER_UID , newUserNameString , newUserBioString , "");

                inflated.setVisibility(View.INVISIBLE);
                constraintLayout.setVisibility(View.VISIBLE);
            }
        });
        return view;
    }

    private void editProfile(String userUID , String newUserName , String newUserBio , String newUserImageUrl){
     DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
     reference.child("users").child(userUID).child("userName").setValue(newUserName);
     reference.child("users").child(userUID).child("userBio").setValue(newUserBio);

     getUserData(userUID);
     setUserData();
    }
    
    private void setUserData() {
        fanArtPostArrayList = CurrentUserData.USER_FAN_ARTS;

        User user = CurrentUserData.USER_DATA;
        if (user != null || !CurrentUserData.USER_UID.equals("")) {
            Picasso.with(getContext()).load(user.getImageUrl()).into(userImage);

            userName.setText(user.getUserName());
            userBio.setText(user.getUserBio());
            userEmail.setText(user.getEmail());
        }

        if (fanArtPostArrayList != null){
            postsAdapter = new PostsAdapter(fanArtPostArrayList , getContext());
            
            recyclerView.setAdapter(postsAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext() , 3));
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (fanArtPostArrayList != null){
            getUserData(CurrentUserData.USER_UID);

        }
    }

    private void getUserData(String userUID) {
        databaseReference.child("users").child(userUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CurrentUserData.USER_DATA = snapshot.getValue(User.class);
                User user = CurrentUserData.USER_DATA;
                userName.setText(user.getUserName());
                userBio.setText(user.getUserBio());
                userEmail.setText(user.getEmail());
                for (int i = 0; i < fanArtPostArrayList.size(); i++){
                    fanArtPostArrayList.get(i).setUserName(CurrentUserData.USER_DATA.getUserName());
                    fanArtPostArrayList.get(i).setUserImageUrl(CurrentUserData.USER_DATA.getImageUrl());

                }
                CurrentUserData.USER_FAN_ARTS = fanArtPostArrayList;


                postsAdapter.updateList(fanArtPostArrayList);
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
}