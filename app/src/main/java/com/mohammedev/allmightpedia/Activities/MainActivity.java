package com.mohammedev.allmightpedia.Activities;

import android.content.Intent;
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
import androidx.appcompat.app.ActionBarDrawerToggle;
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
import com.mohammedev.allmightpedia.data.User;
import com.mohammedev.allmightpedia.databinding.ActivityMainBinding;
import com.mohammedev.allmightpedia.utils.CurrentUserData;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity{

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private NavController navController;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference();

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private MenuItem menuItem;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        drawer = binding.drawerLayout;
        navigationView = binding.navView;
        navigationView.getMenu().findItem(R.id.nav_signOut).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
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
            }
        });

        Menu navMenu = navigationView.getMenu();
        menuItem = navMenu.findItem(R.id.nav_signOut);


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_quiz , R.id.nav_sound)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user !=null){
            menuItem.setEnabled(true);
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
            //TODO: if user is not logged in make the nav_signOut menu item disabled.
            menuItem.setEnabled(false);
        }

    }



    private void getUserData(String userUID) {
        databaseReference.child("users").child(userUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                CurrentUserData.USER_DATA = user;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this , RegisterActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}