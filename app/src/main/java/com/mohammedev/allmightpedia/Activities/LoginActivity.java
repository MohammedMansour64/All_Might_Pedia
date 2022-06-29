package com.mohammedev.allmightpedia.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.User;
import com.mohammedev.allmightpedia.utils.CurrentUserData;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "FireBase Authentication";
    private EditText emailEdt , passwordEdt;
    private FirebaseAuth mAuth;
    private final DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        emailEdt = findViewById(R.id.email_edt);
        passwordEdt = findViewById(R.id.password_edt);

        Button signBtn = findViewById(R.id.login_btn);
        signBtn.setOnClickListener(v -> signIn());

        findViewById(R.id.register_page_text).setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }
    private void signIn(){
        mAuth.signInWithEmailAndPassword(emailEdt.getText().toString().trim(), passwordEdt.getText().toString().trim())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        CurrentUserData.USER_UID = Objects.requireNonNull(user).getUid();
                        Toast.makeText(this, CurrentUserData.USER_UID, Toast.LENGTH_SHORT).show();

                        databaseReference.child("users").child(CurrentUserData.USER_UID).get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()){
                                User user1 = Objects.requireNonNull(task1.getResult()).getValue(User.class);
                                CurrentUserData.USER_DATA = user1;
                                Toast.makeText(LoginActivity.this, "Welcome " + Objects.requireNonNull(user1).getUserName() , Toast.LENGTH_SHORT).show();
                            }
                        });

                        Intent backToMain = new Intent(LoginActivity.this , MainActivity.class);
                        startActivity(backToMain);
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.v("signInWithEmail:failure " , String.valueOf(task.getException()));
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LoginActivity.this , MainActivity.class);
        startActivity(intent);
        finish();
    }
}