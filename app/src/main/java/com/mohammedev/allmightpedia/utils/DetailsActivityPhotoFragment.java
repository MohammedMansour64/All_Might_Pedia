package com.mohammedev.allmightpedia.utils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mohammedev.allmightpedia.R;
import com.squareup.picasso.Picasso;

import pl.droidsonroids.gif.GifImageView;

public class DetailsActivityPhotoFragment extends Fragment {

    String photoUrl;

    public DetailsActivityPhotoFragment(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.details_activity_photo_layout , container , false);

        GifImageView gifImageView = view.findViewById(R.id.details_activity_gif_image);

        Picasso.with(getContext()).load(photoUrl).into(gifImageView);

        return view;
    }
}
