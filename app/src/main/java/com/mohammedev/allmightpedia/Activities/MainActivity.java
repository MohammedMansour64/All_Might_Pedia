package com.mohammedev.allmightpedia.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.FanArtPost;
import com.mohammedev.allmightpedia.data.User;
import com.mohammedev.allmightpedia.databinding.ActivityMainBinding;
import com.mohammedev.allmightpedia.utils.CurrentUserData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

    private AppBarConfiguration mAppBarConfiguration;
    private final DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference();

    private DrawerLayout drawer;

    ArrayList<FanArtPost> fanArtPostArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);

        com.mohammedev.allmightpedia.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        navigationView.getMenu().findItem(R.id.nav_signOut).setOnMenuItemClickListener(item -> {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }

            AuthUI.getInstance()
                    .signOut(MainActivity.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    CurrentUserData.USER_DATA = null;
                    CurrentUserData.USER_UID = "";
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    Toast.makeText(MainActivity.this, "Logged Out Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
            return true;
        });

        navigationView.getMenu().findItem(R.id.nav_profile).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (CurrentUserData.USER_DATA == null){
                    Intent intent = new Intent(MainActivity.this , LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                return false;
            }
        });

        Menu navMenu = navigationView.getMenu();
        MenuItem signOutMenu = navMenu.findItem(R.id.nav_signOut);
        MenuItem signOutTitle = navMenu.findItem(R.id.item_title_sign_out);


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_quiz
                , R.id.nav_sound , R.id.nav_profile , R.id.nav_art , R.id.nav_about_me , R.id.nav_chat)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();


        if (user != null && checkConnectivity()){
            CurrentUserData.USER_UID = user.getUid();
            signOutMenu.setEnabled(true);
            getUserData(user.getUid());
            drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

                }

                @Override
                public void onDrawerOpened(@NonNull View drawerView) {

                }

                @Override
                public void onDrawerClosed(@NonNull View drawerView) {

                }

                @Override
                public void onDrawerStateChanged(int newState) {
                    if (CurrentUserData.USER_DATA != null){
                        ImageView userImageNav = findViewById(R.id.user_image_nav);
                        TextView userNameNav = findViewById(R.id.user_name_nav);
                        userNameNav.setText("Welcome " + CurrentUserData.USER_DATA.getUserName() + "!");
                        Picasso.with(MainActivity.this).load(CurrentUserData.USER_DATA.getImageUrl()).into(userImageNav);

                        userNameNav.setVisibility(View.VISIBLE);
                        findViewById(R.id.cardView2).setVisibility(View.VISIBLE);
                    }
                }
            });
        }else{
            signOutTitle.setTitle("Sign In");
            signOutMenu.setTitle("Sign In");
            signOutMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Intent intent = new Intent(MainActivity.this , RegisterActivity.class);
                    startActivity(intent);
                    return true;
                }
            });
        }

    }

    private boolean checkConnectivity(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }
        else
            return false;
    }

    private void getUserData(String userUID) {
        databaseReference.child("users").child(userUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CurrentUserData.USER_DATA = snapshot.getValue(User.class);
                System.out.println(CurrentUserData.USER_DATA);
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
                        Map<String , Object> map = (Map<String, Object>) data.getValue();
                        Map<String , String> map2 = (Map<String, String>) map.get("likedUsers");

                        FanArtPost fanArtPost = new FanArtPost((String) map.get("postImageUrl")
                                                                , 0, (String) map.get("imageID")
                                                                , (String) map.get("userName") , (String) map.get("userImageUrl")
                                                                , userUID,(HashMap<String, String>) map2);
                        fanArtPostArrayList.add(fanArtPost);

                    }

                }
                CurrentUserData.USER_FAN_ARTS = fanArtPostArrayList;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error);
            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


}