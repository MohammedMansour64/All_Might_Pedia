package com.mohammedev.allmightpedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mohammedev.allmightpedia.data.Image;

import java.io.File;

public class AddImageActivity extends AppCompatActivity {

    public static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
    private DatabaseReference dataBaseReference = dataBase.getReference("photos");
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef;
    private Uri imageUri;
    private ImageView imageView;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addimage);

        imageView = findViewById(R.id.imageStorage);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readStorage();
            }
        });

        submitButton = findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendImagesFun();
            }
        });
    }

    public void readStorage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // When an Image is picked
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && null != data) {
            // Get the Image from data

            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            super.onActivityResult(requestCode, resultCode, data);

        }
    }

    private void sendImagesFun() {
        storageRef = storage.getReference().child("images").child(System.currentTimeMillis() + "." +getMimeType(AddImageActivity.this, imageUri));
        storageRef.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();

                }
                return storageRef.getDownloadUrl();

            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Image imageUrl = new Image(task.getResult().toString());
                    String imageID = dataBaseReference.push().getKey();
                    submitButton.setEnabled(false);
                    dataBaseReference.child(imageID).setValue(imageUrl);
                    finish();
                }
            }

        });
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
}
