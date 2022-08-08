package com.mohammedev.allmightpedia.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mohammedev.allmightpedia.R;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import br.com.simplepass.loadingbutton.customViews.CircularProgressImageButton;

public class AboutMeFragment extends Fragment {
    CircularProgressImageButton circularProgressButton;
    EditText editText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.about_me_fragment , container , false);
        editText = view.findViewById(R.id.editTextTextPersonName);

        view.findViewById(R.id.insta).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.instagram.com/shaliakwt/"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


        view.findViewById(R.id.gmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "shalia.kwt@outlook.com", null));
                startActivity(Intent.createChooser(emailIntent, null));
            }
        });

        view.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.paypal.com/donate/?hosted_button_id=R46L55TZENPUC"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.imageView4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.paypal.com/donate/?hosted_button_id=R46L55TZENPUC"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });



        circularProgressButton = view.findViewById(R.id.circularProgressButton);

        circularProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                circularProgressButton.startAnimation();
                DatabaseReference  reference = FirebaseDatabase.getInstance().getReference().child("suggestions");
                String test = reference.push().getKey();

                reference.child(test).setValue(editText.getText().toString());

                circularProgressButton.stopAnimation();

                editText.setText("");

            }
        });

        return view;
    }
}
