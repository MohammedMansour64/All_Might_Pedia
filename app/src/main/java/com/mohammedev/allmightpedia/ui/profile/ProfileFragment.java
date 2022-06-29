package com.mohammedev.allmightpedia.ui.profile;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mohammedev.allmightpedia.Activities.ArtViewActivity;
import com.mohammedev.allmightpedia.Activities.LoginActivity;
import com.mohammedev.allmightpedia.Activities.MainActivity;
import com.mohammedev.allmightpedia.Adapters.PostsAdapter;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.FanArtPost;
import com.mohammedev.allmightpedia.data.User;
import com.mohammedev.allmightpedia.utils.CurrentUserData;
import com.mohammedev.allmightpedia.utils.ViewSpaces;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;


public class ProfileFragment extends Fragment {
    private DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference();

    private ShapeableImageView userImage;
    private TextView userName , userBio , userEmail;
    private Button editButton;
    private ArrayList<FanArtPost> postList = new ArrayList<>();
    private RecyclerView recyclerView;


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


        setUserData();
        return view;

    }
    
    private void setUserData() {
        ArrayList<FanArtPost> test = new ArrayList<>();
        test = CurrentUserData.USER_FAN_ARTS;
        Toast.makeText(getContext(), test.get(0).getUserImageUrl(), Toast.LENGTH_SHORT).show();

        User user = CurrentUserData.USER_DATA;
        if (user != null || !CurrentUserData.USER_UID.equals("")) {
            Picasso.with(getContext()).load(user.getImageUrl()).into(userImage);

            userName.setText(user.getUserName());
            userBio.setText(user.getUserBio());
            userEmail.setText(user.getEmail());
        }

        if (test != null){
            PostsAdapter postsAdapter = new PostsAdapter(test , getContext());
            
            recyclerView.setAdapter(postsAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext() , 3));
        }
    }
}