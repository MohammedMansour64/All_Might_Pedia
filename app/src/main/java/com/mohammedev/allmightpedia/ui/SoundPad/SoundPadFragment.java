package com.mohammedev.allmightpedia.ui.SoundPad;

import androidx.lifecycle.ViewModelProvider;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.Audio;
import com.mohammedev.allmightpedia.utils.ViewSpaces;

import java.io.IOException;
import java.util.Objects;

public class SoundPadFragment extends Fragment {

    private SoundPadViewModel mViewModel;
    private FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
    FirebaseRecyclerAdapter adapter;
    RecyclerView recyclerView;
    MediaPlayer soundPlayer = new MediaPlayer();
    Uri audioUri;
    boolean isPlaying = false;
    public static SoundPadFragment newInstance() {
        return new SoundPadFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.sound_pad_fragment, container, false);
        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 2));
        recyclerView.addItemDecoration(new ViewSpaces(20));
        fetch();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SoundPadViewModel.class);
        // TODO: Use the ViewModel

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
        Query query = FirebaseDatabase.getInstance().getReference().child("audio");

        FirebaseRecyclerOptions<Audio> options = new FirebaseRecyclerOptions.Builder<Audio>().setQuery(query, new SnapshotParser<Audio>() {
            @NonNull
            @Override
            public Audio parseSnapshot(@NonNull DataSnapshot snapshot) {
                return new Audio(Objects.requireNonNull(
                        snapshot.child("audioValue").getValue()).toString(),
                        Objects.requireNonNull(snapshot.child("audioName").getValue()).toString());
            }
        }).build();


        adapter = new FirebaseRecyclerAdapter<Audio, SoundViewHolder>(options) {

            @NonNull
            @Override
            public SoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.sound_button_layout, parent, false);

                return new SoundViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull SoundViewHolder viewHolder, int i, @NonNull Audio audio) {
                viewHolder.audioName.setText(audio.getAudioName());
                viewHolder.audioImageIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (isPlaying){
                            viewHolder.audioImageIcon.setImageResource(R.drawable.ic_sound);
                            soundPlayer.stop();
                            isPlaying = false;
                        }else if (!isPlaying){
                            isPlaying = true;
                            playAudioFromUrl(viewHolder.audioImageIcon , viewHolder.progressBar , audio.getAudioValue());
                            viewHolder.audioImageIcon.setImageResource(R.drawable.ic_sound_off);
                        }


                    }
                });
            }


        };
        recyclerView.setAdapter(adapter);

    }

    public void playAudioFromUrl(ImageView imageView  , ProgressBar progressBar , String url)  {

        imageView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        try {
            audioUri = Uri.parse(url);
            soundPlayer.reset();
            soundPlayer.setDataSource(getContext(), audioUri);
            soundPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            soundPlayer.prepareAsync();
            soundPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    imageView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            });

            soundPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    imageView.setImageResource(R.drawable.ic_sound);
                    isPlaying = false;
                }
            });




        } catch (IOException e){
            e.printStackTrace();
        }
    }

    class SoundViewHolder extends RecyclerView.ViewHolder {
        private TextView audioName;
        private ImageView audioImageIcon;
        private ProgressBar progressBar;
        public SoundViewHolder(@NonNull View itemView) {
            super(itemView);
            audioName = itemView.findViewById(R.id.textView32);
            audioImageIcon = itemView.findViewById(R.id.imageView4);
            progressBar = itemView.findViewById(R.id.progressBar2);

        }
    }
}