package com.mohammedev.allmightpedia.ui.gallery;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mohammedev.allmightpedia.Activities.LoginActivity;
import com.mohammedev.allmightpedia.Adapters.GalleryAdapter;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.Image;
import com.mohammedev.allmightpedia.utils.CurrentUserData;
import com.mohammedev.allmightpedia.utils.ViewSpaces;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;

public class GalleryFragmentList extends Fragment {

    FirebaseRecyclerAdapter adapter;
    ArrayList<Image> imagesArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    SkeletonScreen skeletonScreen;
    TextView noPostsText;
    GifImageView noPostsGifImage;
    TextView refreshFeedText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = LayoutInflater.from(getContext()).inflate(R.layout.fragment_gallery_list , container , false);

        recyclerView = root.findViewById(R.id.recycler_view_gallery);
        skeletonScreen = Skeleton.bind(recyclerView).load(R.layout.layout_img_skeleton).show();
        recyclerView.setLayoutManager(new LinearLayoutManager(GalleryFragmentList.this.getContext(), RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(new ViewSpaces(20));

        noPostsGifImage = root.findViewById(R.id.no_posts_gif);
        noPostsText = root.findViewById(R.id.no_result_txt);
        refreshFeedText = root.findViewById(R.id.refresh_feed_text);

        refreshFeedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshFeed();
            }
        });

        fetch();


        return root;
    }


    public void fetch(){
        FirebaseDatabase.getInstance().getReference().child("photos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    Image image = ds.getValue(Image.class);
                    imagesArrayList.add(image);
                }
                if (!imagesArrayList.isEmpty()){
                    GalleryAdapter galleryAdapter = new GalleryAdapter(imagesArrayList , getContext());
                    recyclerView.setAdapter(galleryAdapter);
                }else{
                    noPostsText.setVisibility(View.VISIBLE);
                    noPostsGifImage.setVisibility(View.VISIBLE);
                    refreshFeedText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                    skeletonScreen.hide();
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (imagesArrayList.isEmpty()){
                    noPostsText.setVisibility(View.VISIBLE);
                    noPostsGifImage.setVisibility(View.VISIBLE);
                    refreshFeedText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                    skeletonScreen.hide();
                }


            }
        }, 5000);
    }

    public void refreshFeed(){
        noPostsText.setVisibility(View.INVISIBLE);
        noPostsGifImage.setVisibility(View.INVISIBLE);
        refreshFeedText.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        skeletonScreen = Skeleton.bind(recyclerView).load(R.layout.layout_img_skeleton).show();
        fetch();
    }
}