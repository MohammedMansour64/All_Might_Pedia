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

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mohammedev.allmightpedia.Adapters.GalleryAdapter;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.Audio;
import com.mohammedev.allmightpedia.utils.ViewSpaces;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;

public class SoundPadFragment extends Fragment {

    private SoundPadViewModel mViewModel;
    private ArrayList<Audio> audioArrayList = new ArrayList<>();
    private FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
    FirebaseRecyclerAdapter adapter;
    RecyclerView recyclerView;
    MediaPlayer soundPlayer = new MediaPlayer();
    Uri audioUri;
    boolean isPlaying = false;
    TextView noPostsText;
    GifImageView noPostsGifImage;
    TextView refreshFeedText;
    SkeletonScreen skeletonScreen;
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
        skeletonScreen = Skeleton.bind(recyclerView).load(R.layout.layout_sound_skeleton).show();


        noPostsGifImage = root.findViewById(R.id.no_sounds_gif);
        noPostsText = root.findViewById(R.id.no_sounds_text);
        refreshFeedText = root.findViewById(R.id.refresh_sounds_text);

        refreshFeedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshFeed();
            }
        });

        fetch();
        return root;
    }

    public void refreshFeed(){
        noPostsText.setVisibility(View.INVISIBLE);
        noPostsGifImage.setVisibility(View.INVISIBLE);
        refreshFeedText.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        skeletonScreen = Skeleton.bind(recyclerView).load(R.layout.layout_sound_skeleton).show();
        fetch();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SoundPadViewModel.class);
        // TODO: Use the ViewModel

    }


    private void fetch() {
        audioArrayList.clear();
        FirebaseDatabase.getInstance().getReference().child("audio").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    audioArrayList.add(ds.getValue(Audio.class));
                }
                if (!audioArrayList.isEmpty()){
                    SoundAdapter soundAdapter = new SoundAdapter(audioArrayList);
                    recyclerView.setAdapter(soundAdapter);
                }else{
                    noPostsText.setVisibility(View.VISIBLE);
                    noPostsGifImage.setVisibility(View.VISIBLE);
                    refreshFeedText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                    skeletonScreen.hide();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (audioArrayList.isEmpty()){
                    noPostsText.setVisibility(View.VISIBLE);
                    noPostsGifImage.setVisibility(View.VISIBLE);
                    refreshFeedText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                    skeletonScreen.hide();
                }


            }
        }, 5000);

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

    class SoundAdapter extends RecyclerView.Adapter<SoundAdapter.SoundViewHolder>{
        ArrayList<Audio> audioArrayList;

        public SoundAdapter(ArrayList<Audio> audioArrayList) {
            this.audioArrayList = audioArrayList;
        }

        @NonNull
        @Override
        public SoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sound_button_layout, parent, false);

            return new SoundViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SoundViewHolder holder, int position) {
            Audio audio = audioArrayList.get(position);
            holder.audioName.setText(audio.getAudioName());

            holder.audioImageIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.audioImageIcon.setEnabled(false);
                    if (isPlaying){
                        holder.audioImageIcon.setImageResource(R.drawable.ic_sound);
                        soundPlayer.stop();
                        isPlaying = false;
                        holder.audioImageIcon.setEnabled(true);
                    }else if (!isPlaying){
                        isPlaying = true;
                        holder.audioImageIcon.setEnabled(true);
                        playAudioFromUrl(holder.audioImageIcon , holder.progressBar , audio.getAudioValue());
                        holder.audioImageIcon.setImageResource(R.drawable.ic_sound_off);
                    }


                }
            });

        }

        @Override
        public int getItemCount() {
            return audioArrayList.size();
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

    }
