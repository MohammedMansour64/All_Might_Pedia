package com.mohammedev.allmightpedia.utils;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mohammedev.allmightpedia.R;

public class DetailsActivityVideoFragment extends Fragment {
    String videoUrl;

    public DetailsActivityVideoFragment(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.details_activity_video_layout , container , false);
        VideoView videoView = view.findViewById(R.id.videoView);

        videoView.setVideoURI(Uri.parse(videoUrl));
        videoView.setMediaController(new MediaController(getContext()));

        ProgressBar progressBar = view.findViewById(R.id.details_video_progress_bar);

        progressBar.setVisibility(View.VISIBLE);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {



                mp.start();

                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {

                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int arg1, int arg2) {
                        // TODO Auto-generated method stub
                        progressBar.setVisibility(View.GONE);
                        mp.start();
                    }
                });


            }
        });
        return view;
    }
}
