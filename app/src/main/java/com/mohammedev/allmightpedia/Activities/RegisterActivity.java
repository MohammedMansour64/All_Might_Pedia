package com.mohammedev.allmightpedia.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.view.View;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mohammedev.allmightpedia.Adapters.FanArtAdapter;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.FanArtPost;
import com.mohammedev.allmightpedia.data.User;
import com.mohammedev.allmightpedia.utils.OnGetDataListener;

import java.util.ArrayList;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;


public class RegisterActivity extends AppCompatActivity {
    private static final int GET_IMAGE_STORAGE_CODE = 101;
    private static final int PERMISSIONS_READ_EXTERNAL_STORAGE = 123;
    ImageView userImage;
    EditText userName, userEmail, userPassword;
    CircularProgressButton registerBtn;
    TextView gotoLoginPage;
    boolean test;

    StorageReference storageReference;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    FirebaseUser user;

    Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        userImage = findViewById(R.id.user_image_nav);
        userName = findViewById(R.id.user_name_edt);
        userEmail = findViewById(R.id.email_edt_register);
        userPassword = findViewById(R.id.password_edt_register);
        registerBtn = findViewById(R.id.login_btn);
        gotoLoginPage = findViewById(R.id.register_page_text);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerBtn.startAnimation();

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
        String userNameString = userName.getText().toString().trim();
        String userEmailString = userEmail.getText().toString().trim();
        String userPasswordString = userPassword.getText().toString().trim();

        mReadDataOnce(new OnGetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(DataSnapshot data) {
                if (userNameString != null && userEmailString != null && userPasswordString != null && imageUri != null){
                    if (userEmailString.isEmpty() | userPasswordString.isEmpty()){
                        Toast.makeText(getApplicationContext(), "Email or Password is empty", Toast.LENGTH_SHORT).show();
                        registerBtn.revertAnimation();
                    }
                    else if (!Patterns.EMAIL_ADDRESS.matcher(userEmailString).matches()){
                        userEmail.setError("this email is not valid.");
                        userEmail.requestFocus();
                        registerBtn.revertAnimation();
                    }
                    else if (userPasswordString.length() < 6){
                        userPassword.setError("this password is weak!");
                        userPassword.requestFocus();
                        registerBtn.revertAnimation();

                    }else if (checkUserNames(data , userName)){
                        userName.setError("Username is already in use, try another");
                        userName.requestFocus();
                        registerBtn.revertAnimation();
                    }else{
                        mAuth.createUserWithEmailAndPassword(userEmailString, userPasswordString)
                                .addOnCompleteListener(RegisterActivity.this , new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            user = mAuth.getCurrentUser();
                                            toStorage();

                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                            System.out.println(task.getException());
                                            userEmail.setError(task.getException().getMessage());
                                            userEmail.requestFocus();
                                            registerBtn.revertAnimation();

                                        }
                                        // ...
                                    }
                                });
                    }
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {

            }
        });

    }

    private void toStorage() {
        String userNameString = userName.getText().toString().trim();
        String userEmailString = userEmail.getText().toString().trim();
        String userPasswordString = userPassword.getText().toString().trim();

        if (userNameString != null && userEmailString != null && userPasswordString != null && imageUri != null){
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

            }else {
                String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                storageReference = FirebaseStorage.getInstance().getReference().child("userImages").child(userUID);
                storageReference.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        } else {
                            return storageReference.getDownloadUrl();
                        }
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {


                            Uri finishedUri = task.getResult();
                            User userRegisteredData = new User(userName.getText().toString().trim(),
                                    "",
                                    finishedUri.toString(),
                                    userEmail.getText().toString().trim()
                                    , userUID);


                            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userUID);
                            databaseReference.setValue(userRegisteredData);

                            Toast.makeText(RegisterActivity.this, "Authenticated Successfully", Toast.LENGTH_SHORT).show();
                            registerBtn.revertAnimation();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        }
                    }
                });
            }
        }
    }



    public boolean checkUserNames(DataSnapshot data , EditText userNameEdt){
        ArrayList<User> userList = new ArrayList<>();
        ArrayList<String> userNamesList = new ArrayList<>();
        for (DataSnapshot snapshot1 : data.getChildren()){
            userList.add(snapshot1.getValue(User.class));
        }

        for (int i = 0; i < userList.size(); i++){
            userNamesList.add(userList.get(i).getUserName());
        }

        if (userNamesList.contains(userNameEdt.getText().toString())){
            return true;
        }
        return false;
    }

    public void mReadDataOnce(final OnGetDataListener listener) {
        listener.onStart();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }

}