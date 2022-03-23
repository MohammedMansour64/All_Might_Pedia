package com.mohammedev.allmightpedia.ui.gallery;

import android.app.DownloadManager;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.Image;
import com.mohammedev.allmightpedia.databinding.FragmentGalleryBinding;
import com.mohammedev.allmightpedia.databinding.FragmentGalleryListBinding;
import com.mohammedev.allmightpedia.utils.CurrentUserData;
import com.mohammedev.allmightpedia.utils.ViewSpaces;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class GalleryFragmentList extends Fragment {

    private FragmentGalleryListBinding binding;
    FirebaseRecyclerAdapter adapter;
    RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentGalleryListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(GalleryFragmentList.this.getContext(), RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(new ViewSpaces(20));
        fetch();


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
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void fetch() {
        Query query = FirebaseDatabase.getInstance().getReference().child("photos");

        FirebaseRecyclerOptions<Image> options = new FirebaseRecyclerOptions.Builder<Image>().setQuery(query, new SnapshotParser<Image>() {
            @NonNull
            @Override
            public Image parseSnapshot(@NonNull DataSnapshot snapshot) {
                return new Image(Objects.requireNonNull(snapshot.child("imageUrl").getValue()).toString());
            }
        }).build();


        adapter = new FirebaseRecyclerAdapter<Image, GalleryFragmentList.GalleryListViewHolder>(options) {

            @NonNull
            @Override
            public GalleryFragmentList.GalleryListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.gallery_recycler_view, parent, false);

                return new GalleryFragmentList.GalleryListViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull GalleryFragmentList.GalleryListViewHolder viewHolder, int i, @NonNull Image image) {

                Picasso.with(GalleryFragmentList.this.getContext()).load(image.getImageUrl()).into(viewHolder.image);
                viewHolder.downloadBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        download(image.getImageUrl());
                        GalleryFragmentList.Downloading downloading = new GalleryFragmentList.Downloading();
                        downloading.execute(image.getImageUrl());
                    }
                });
                isFavourite(image.getImageUrl(), viewHolder.favoriteButton);
                final boolean[] isFavourite = {false};
                viewHolder.favoriteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isFavourite[0]){
                            unFavouriteFun(image.getImageUrl());
                            viewHolder.favoriteButton.setImageResource(R.drawable.ic_heart);
                        }else {
                            Image image1 = new Image(image.getImageUrl());
                            FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
                            DatabaseReference dataBaseReference = dataBase.getReference("users").child(CurrentUserData.USER_UID).child("galleryFavourites");
                            dataBaseReference.push().setValue(image1);
                            viewHolder.favoriteButton.setImageResource(R.drawable.ic_heart_red);
                        }
                        isFavourite[0] = !isFavourite[0];
                    }
                });
            }


        };
        recyclerView.setAdapter(adapter);


    }

    private void unFavouriteFun(String imageUrl) {
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

    public class Downloading extends AsyncTask<String, Integer, String> {

        @Override
        public void onPreExecute() {
            super .onPreExecute();
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
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, date + "."+ getMimeType(GalleryFragmentList.this.getContext(), downloadUri));

            request.setTitle("All Might Pedia");
            request.setDescription("Downloading your All Might image...");
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            manager.enqueue(request);
            return mydir.getAbsolutePath() + File.separator + date + "." + getMimeType(GalleryFragmentList.this.getContext(), downloadUri);
        }



        @Override
        public void onPostExecute(String s) {
            super .onPostExecute(s);
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

    class GalleryListViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private ImageButton downloadBtn;
        private ImageButton favoriteButton;

        public GalleryListViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            downloadBtn = itemView.findViewById(R.id.download_btn);
            favoriteButton = itemView.findViewById(R.id.favourite_btn);

        }
    }
}