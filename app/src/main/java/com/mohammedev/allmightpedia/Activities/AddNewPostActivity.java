package com.mohammedev.allmightpedia.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.FanArtPost;
import com.mohammedev.allmightpedia.data.User;
import com.mohammedev.allmightpedia.utils.CurrentUserData;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class AddNewPostActivity extends AppCompatActivity {
    private static final int PERMISSIONS_READ_EXTERNAL_STORAGE = 123;
    private static final int GET_IMAGE_STORAGE_CODE = 101;
    private ImageView postImageView;
    private Button uploadPostBtn;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private DatabaseReference reference;
    private StorageReference storageRef;
    private Uri imageUri;
    private FirebaseAuth mAUth;

    public AddNewPostActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_post);

        postImageView = findViewById(R.id.postImage);
        uploadPostBtn = findViewById(R.id.uploadPost);
        mAUth = FirebaseAuth.getInstance();

        uploadPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postArt();
            }
        });

        postImageView.setOnClickListener(v -> checkPermissionAndReadStorage());

    }

    private void postArt() {
        uploadPostBtn.setEnabled(false);
        reference = database.getReference().child("users").child(mAUth.getCurrentUser().getUid()).child("posts");
        String imageID = reference.push().getKey();
        if(CurrentUserData.USER_UID != null){
            storageRef = storage.getReference().child("users").child(mAUth.getCurrentUser().getUid()).child("posts").child(imageID);
            storageRef.putFile(imageUri).continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return storageRef.getDownloadUrl();
            }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Toast.makeText(AddNewPostActivity.this, "Success", Toast.LENGTH_SHORT).show();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        User user = CurrentUserData.USER_DATA;

                        HashMap<String,String> likedUsers = new HashMap<>();
                        likedUsers.put("test" , "test");
                        FanArtPost fanArtPost = new FanArtPost(task.getResult().toString() , 0 , imageID , user.getUserName() , user.getImageUrl(), user.getUserID() ,likedUsers);
                        System.out.println(imageID);
                        reference.child(imageID).setValue(fanArtPost);
                        finish();
                    }
                }

            });

        }else{
            Toast.makeText(this, "no user", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermissionAndReadStorage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(R.string.accessStorage).setPositiveButton("Yes", (dialog, which) -> ActivityCompat.requestPermissions(AddNewPostActivity.this, new String[] {
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        PERMISSIONS_READ_EXTERNAL_STORAGE)).setNegativeButton("No", (dialog, which) -> dialog.dismiss()).create();


                alertDialog.show();
            }
        }else{
            GalleryIntent();
        }
    }

    public void GalleryIntent() {
        Intent photoIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoIntent.setType("image/*");
        startActivityForResult(photoIntent , GET_IMAGE_STORAGE_CODE);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_IMAGE_STORAGE_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            postImageView.setImageURI(imageUri);
        }
    }
}