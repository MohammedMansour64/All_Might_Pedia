package com.mohammedev.allmightpedia.ui.gallery;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mohammedev.allmightpedia.MainActivity;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.Image;
import com.mohammedev.allmightpedia.databinding.FragmentGalleryBinding;
import com.mohammedev.allmightpedia.utils.ViewSpaces;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class GalleryFragment extends Fragment {
    private static final int STORAGE_PERMISSION_CODE = 101;
    private FragmentGalleryBinding binding;
    private FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
    FirebaseRecyclerAdapter adapter;
    RecyclerView recyclerView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(GalleryFragment.this.getContext(), RecyclerView.VERTICAL, false));
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


        adapter = new FirebaseRecyclerAdapter<Image, GalleryViewHolder>(options) {

            @NonNull
            @Override
            public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.gallery_recycler_view, parent, false);

                return new GalleryViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull GalleryViewHolder viewHolder, int i, @NonNull Image image) {
                Picasso.with(GalleryFragment.this.getContext()).load(image.getImageUrl()).into(viewHolder.image);
                viewHolder.downloadBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        download(image.getImageUrl());
                        Downloading downloading = new Downloading();
                        downloading.execute(image.getImageUrl());
                    }
                });
            }


        };
        recyclerView.setAdapter(adapter);


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
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, date + "."+ getMimeType(GalleryFragment.this.getContext(), downloadUri));

            request.setTitle("All Might Pedia");
            request.setDescription("Downloading your All Might image...");
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            manager.enqueue(request);
            return mydir.getAbsolutePath() + File.separator + date + "." + getMimeType(GalleryFragment.this.getContext(), downloadUri);
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

    class GalleryViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private ImageButton downloadBtn;

        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            downloadBtn = itemView.findViewById(R.id.download_btn);

        }
    }



}