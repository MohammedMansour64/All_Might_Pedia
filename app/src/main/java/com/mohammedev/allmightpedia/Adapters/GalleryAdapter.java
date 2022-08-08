package com.mohammedev.allmightpedia.Adapters;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mohammedev.allmightpedia.Activities.LoginActivity;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.Image;
import com.mohammedev.allmightpedia.ui.gallery.GalleryFragmentList;
import com.mohammedev.allmightpedia.utils.CurrentUserData;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {
    ArrayList<Image> galleryImages;
    Context context;

    public GalleryAdapter(ArrayList<Image> galleryImages, Context context) {
        this.galleryImages = galleryImages;
        this.context = context;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_recycler_view, parent, false);

        return new GalleryViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder viewHolder, int i) {
        Image image = galleryImages.get(i);
        Picasso.with(context).load(image.getImageUrl()).into(viewHolder.galleryImage);
        viewHolder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Downloading downloading = new Downloading();
                downloading.execute(image.getImageUrl());
            }
        });
        isFavourite(image.getImageUrl(), viewHolder.favoriteButton);
        final boolean[] isFavourite = {false};
        viewHolder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CurrentUserData.USER_DATA != null) {


                    if (isFavourite[0]) {
                        unFavouriteFun(image.getImageUrl());
                        viewHolder.favoriteButton.setImageResource(R.drawable.ic_heart);
                    } else {
                        Image image1 = new Image(image.getImageUrl());
                        FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
                        DatabaseReference dataBaseReference = dataBase.getReference("users").child(CurrentUserData.USER_UID).child("galleryFavourites");
                        dataBaseReference.push().setValue(image1);
                        viewHolder.favoriteButton.setImageResource(R.drawable.ic_heart_red);
                    }
                    isFavourite[0] = !isFavourite[0];
                }else{
                    AlertDialog alertDialog = new AlertDialog.Builder(context)
                            .setTitle(R.string.sign_in_required_to_favourite)
                            .setPositiveButton("Sign in", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent signInIntent = new Intent(context , LoginActivity.class);
                                    context.startActivity(signInIntent);
                                }
                            }).show();

                    alertDialog.create();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return galleryImages.size();
    }

    public static class GalleryViewHolder extends RecyclerView.ViewHolder{
        private ImageView galleryImage;
        private ImageButton downloadButton;
        private ImageButton favoriteButton;
        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);

            galleryImage = itemView.findViewById(R.id.image);
            downloadButton = itemView.findViewById(R.id.download_btn);
            favoriteButton = itemView.findViewById(R.id.favourite_btn);
        }
    }

     class Downloading extends AsyncTask<String, Integer, String> {

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

            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(url[0]);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);

            SimpleDateFormat dateFormat = new SimpleDateFormat("mmddyyyyhhmmss");
            String date = dateFormat.format(new Date());

            request.setAllowedNetworkTypes(
                            DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle("Downloading")
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, date + "."+ getMimeType(context, downloadUri));

            request.setTitle("All Might Pedia");
            request.setDescription("Downloading your All Might image...");
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            manager.enqueue(request);
            return mydir.getAbsolutePath() + File.separator + date + "." + getMimeType(context, downloadUri);
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
}
