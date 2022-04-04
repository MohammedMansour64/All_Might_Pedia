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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;

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
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.Image;
import com.mohammedev.allmightpedia.databinding.FragmentGalleryFavouritesBinding;
import com.mohammedev.allmightpedia.utils.CurrentUserData;
import com.mohammedev.allmightpedia.utils.ViewSpaces;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class GalleryFragmentFavourites extends Fragment {
    FragmentGalleryFavouritesBinding binding;
    FirebaseRecyclerAdapter adapter;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentGalleryFavouritesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(GalleryFragmentFavourites.this.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new ViewSpaces(20));

        if (CurrentUserData.USER_DATA != null){
            fetch();
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (CurrentUserData.USER_UID != "" || CurrentUserData.USER_DATA != null){
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (CurrentUserData.USER_UID != "" || CurrentUserData.USER_DATA != null){
            adapter.stopListening();
        }
    }


    private void fetch() {
        if (CurrentUserData.USER_UID != "" || CurrentUserData.USER_DATA != null) {
            Query query = FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUserData.USER_UID).child("galleryFavourites");

            FirebaseRecyclerOptions<Image> options = new FirebaseRecyclerOptions.Builder<Image>().setQuery(query, new SnapshotParser<Image>() {
                @NonNull
                @Override
                public Image parseSnapshot(@NonNull DataSnapshot snapshot) {
                    return new Image(snapshot.child("imageUrl").getValue().toString());
                }
            }).build();


            adapter = new FirebaseRecyclerAdapter<Image, GalleryFragmentFavourites.GalleryListViewHolder>(options) {

                @NonNull
                @Override
                public GalleryFragmentFavourites.GalleryListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.gallery_favourite_item, parent, false);

                    return new GalleryFragmentFavourites.GalleryListViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull GalleryFragmentFavourites.GalleryListViewHolder viewHolder, int i, @NonNull Image image) {
                    Picasso.with(GalleryFragmentFavourites.this.getContext()).load(image.getImageUrl()).into(viewHolder.image);
                    viewHolder.downloadBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GalleryFragmentFavourites.Downloading downloading = new GalleryFragmentFavourites.Downloading();
                            downloading.execute(image.getImageUrl());
                        }
                    });

                    viewHolder.unFavoriteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            unFavouriteFun(image.getImageUrl());
                            notifyDataSetChanged();
                        }
                    });
                }

            };
            recyclerView.setAdapter(adapter);

        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                    .setTitle(R.string.sign_in_required_to_show_favourites)
                    .setPositiveButton("Sign in", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent signInIntent = new Intent(getActivity() , LoginActivity.class);
                            startActivity(signInIntent);
                        }
                    }).show();

            alertDialog.create();
        }
    }

    private void unFavouriteFun(String imageUrl) {
        System.out.println(CurrentUserData.USER_UID);
        FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
        DatabaseReference dataBaseReference = dataBase.getReference("users").child(CurrentUserData.USER_UID).child("galleryFavourites");
        Query mQuery = dataBaseReference.orderByChild("imageUrl").equalTo(imageUrl);
        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    dataSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isFavourite(String imageUrl , ImageButton imageButton){
        FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
        DatabaseReference dataBaseReference = dataBase.getReference("users").child(CurrentUserData.USER_UID).child("galleryFavourites");
        Query mQuery = dataBaseReference.orderByChild("imageUrl").equalTo(imageUrl);
        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    imageButton.setImageResource(R.drawable.ic_heart_red);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    class GalleryListViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private ImageButton downloadBtn;
        private ImageButton unFavoriteButton;


        public GalleryListViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            downloadBtn = itemView.findViewById(R.id.download_btn);
            unFavoriteButton = itemView.findViewById(R.id.favourite_btn);
        }
    }

    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }

    public class Downloading extends AsyncTask<String, Integer, String> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... url) {
            File mydir = new File(Environment.getExternalStorageDirectory() + "/AllMightPedia");
            if (!mydir.exists()) {
                mydir.mkdirs();
            }

            DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(url[0]);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);

            SimpleDateFormat dateFormat = new SimpleDateFormat("mmddyyyyhhmmss");
            String date = dateFormat.format(new Date());

            request.setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle("Downloading")
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, date + "."+ getMimeType(GalleryFragmentFavourites.this.getContext(), downloadUri));

            request.setTitle("All Might Pedia");
            request.setDescription("Downloading your All Might image...");
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            manager.enqueue(request);
            return mydir.getAbsolutePath() + File.separator + date + "." + getMimeType(GalleryFragmentFavourites.this.getContext(), downloadUri);
        }



        @Override
        public void onPostExecute(String s) {
            super .onPostExecute(s);
        }
    }

}