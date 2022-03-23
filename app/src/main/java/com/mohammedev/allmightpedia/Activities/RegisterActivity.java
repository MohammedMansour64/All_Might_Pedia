package com.mohammedev.allmightpedia.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.User;

//TODO: implicit intent with imageView, set data in onActivityResult

public class RegisterActivity extends AppCompatActivity {
    private static final int GET_IMAGE_STORAGE_CODE = 101;
    private static final int PERMISSIONS_READ_EXTERNAL_STORAGE = 123;
    ImageView userImage;
    EditText userName, userEmail, userPassword;
    Button registerBtn;
    TextView gotoLoginPage;

    StorageReference storageReference;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        userImage = findViewById(R.id.user_image_nav);
        userName = findViewById(R.id.user_name_edt);
        userEmail = findViewById(R.id.email_edt);
        userPassword = findViewById(R.id.password_edt);
        registerBtn = findViewById(R.id.login_btn);
        gotoLoginPage = findViewById(R.id.register_page_text);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toStorage();
                signUp();
            }
        });

        gotoLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginPageIntent = new Intent(RegisterActivity.this , LoginActivity.class);
                startActivity(loginPageIntent);
                finish();
            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndReadStorage();
            }
        });
    }


    private void checkPermissionAndReadStorage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(R.string.accessStorage).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(RegisterActivity.this, new String[] {
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                },
                                PERMISSIONS_READ_EXTERNAL_STORAGE);

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_IMAGE_STORAGE_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            userImage.setImageURI(imageUri);
        }
    }

    private void signUp() {
        String userEmailString = userEmail.getText().toString().trim();
        String userPasswordString = userPassword.getText().toString().trim();

        if (userEmailString.isEmpty() | userPasswordString.isEmpty()){
            Toast.makeText(getApplicationContext(), "Email or Password is empty", Toast.LENGTH_SHORT).show();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(userEmailString).matches()){
            userEmail.setError("this email is not valid.");
            userEmail.requestFocus();
        }
        else if (userPasswordString.length() < 6){
            userPassword.setError("this password is weak!");
            userPassword.requestFocus();

        }else{
            mAuth.createUserWithEmailAndPassword(userEmailString, userPasswordString)
                    .addOnCompleteListener(RegisterActivity.this , new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(RegisterActivity.this, "Authenticated Successfully", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            // ...
                        }
                    });
        }
    }

    private void toStorage() {
        if (imageUri != null || userName.getText().toString().isEmpty() || userPassword.getText().toString().isEmpty() || userEmail.getText().toString().isEmpty()){
            storageReference = FirebaseStorage.getInstance().getReference().child("userImages").child(userName.getText().toString() + "." + getExtension(imageUri));
            storageReference.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }else{
                        return storageReference.getDownloadUrl();
                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri finishedUri = task.getResult();
                        User userRegisteredData = new User (userName.getText().toString().trim(),
                                "",
                                finishedUri.toString(),
                                userEmail.getText().toString().trim());
                        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        databaseReference.setValue(userRegisteredData);

                        startActivity(new Intent(RegisterActivity.this , LoginActivity.class));
                        finish();
                    }
                }
            });
        }
    }


    private String getExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
}