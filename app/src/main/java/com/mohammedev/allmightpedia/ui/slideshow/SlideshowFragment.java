package com.mohammedev.allmightpedia.ui.slideshow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohammedev.allmightpedia.Activities.DetailsActivity;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.Moment;
import com.mohammedev.allmightpedia.databinding.FragmentSlideshowBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

public class SlideshowFragment extends Fragment {

    public ArrayList<Moment> highLightedMomentsArrayList = new ArrayList<>();
    public ArrayList<Moment> momentsArrayList = new ArrayList<>();

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference();

    private SlideshowViewModel slideshowViewModel;
    private FragmentSlideshowBinding binding;
    private RecyclerView highlightedMomentsRecycler;
    private RecyclerView momentsRecycler;
    SkeletonScreen momentsSkeletonScreen;
    SkeletonScreen momentsSkeletonScreen2;

    TextView noPostsText;
    GifImageView noPostsGifImage;
    TextView highLightedMomentsText , momentsText;
    TextView refreshFeedText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        highlightedMomentsRecycler = root.findViewById(R.id.highlighted_moments_recy);
        momentsRecycler = root.findViewById(R.id.other_moments_recy);
        momentsSkeletonScreen = Skeleton.bind(highlightedMomentsRecycler).load(R.layout.layout_img_skeleton).show();
        momentsSkeletonScreen2 = Skeleton.bind(momentsRecycler).load(R.layout.layout_highlighted_skeleton).show();
        fetchData();

        noPostsGifImage = root.findViewById(R.id.no_posts_gif);
        noPostsText = root.findViewById(R.id.no_result_txt);
        highLightedMomentsText = root.findViewById(R.id.text_slideshow);
        refreshFeedText = root.findViewById(R.id.refresh_feed_text);
        momentsText = root.findViewById(R.id.text_slideshow2);

        refreshFeedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshMoments();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public void fetchData(){
        momentsArrayList.clear();
        highLightedMomentsArrayList.clear();
        reference.child("moments").child("otherMoments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    Moment moment = data.getValue(Moment.class);
                    momentsArrayList.add(moment);
                }
                if (!momentsArrayList.isEmpty()){
                    MomentsAdapter momentsAdapter = new MomentsAdapter(momentsArrayList , getContext() , momentsSkeletonScreen2);
                    momentsRecycler.setLayoutManager(new GridLayoutManager(getContext() , 2));
                    momentsRecycler.setAdapter(momentsAdapter);
                }else{
                    noPostsText.setVisibility(View.VISIBLE);
                    noPostsGifImage.setVisibility(View.VISIBLE);
                    refreshFeedText.setVisibility(View.VISIBLE);
                    momentsText.setVisibility(View.INVISIBLE);
                    highlightedMomentsRecycler.setVisibility(View.INVISIBLE);
                    momentsRecycler.setVisibility(View.INVISIBLE);
                    highLightedMomentsText.setVisibility(View.INVISIBLE);
                    momentsSkeletonScreen.hide();
                    momentsSkeletonScreen2.hide();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (momentsArrayList.isEmpty()){
                    noPostsText.setVisibility(View.VISIBLE);
                    noPostsGifImage.setVisibility(View.VISIBLE);
                    refreshFeedText.setVisibility(View.VISIBLE);
                    momentsText.setVisibility(View.INVISIBLE);
                    momentsRecycler.setVisibility(View.INVISIBLE);
                    highlightedMomentsRecycler.setVisibility(View.INVISIBLE);
                    highLightedMomentsText.setVisibility(View.INVISIBLE);
                    momentsSkeletonScreen.hide();
                    momentsSkeletonScreen2.hide();
                }


            }
        }, 5000);


        reference.child("moments").child("highLightedMoments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    Moment moment = data.getValue(Moment.class);
                    highLightedMomentsArrayList.add(moment);
                }
                HighlightedMomentsAdapter highlightedMomentsAdapter = new HighlightedMomentsAdapter(highLightedMomentsArrayList , getContext() , momentsSkeletonScreen);
                highlightedMomentsRecycler.setLayoutManager(new LinearLayoutManager(getContext() , RecyclerView.VERTICAL , false));
                highlightedMomentsRecycler.setAdapter(highlightedMomentsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void refreshMoments(){
        noPostsText.setVisibility(View.INVISIBLE);
        noPostsGifImage.setVisibility(View.INVISIBLE);
        refreshFeedText.setVisibility(View.INVISIBLE);
        momentsText.setVisibility(View.VISIBLE);
        momentsRecycler.setVisibility(View.VISIBLE);
        highlightedMomentsRecycler.setVisibility(View.VISIBLE);
        highLightedMomentsText.setVisibility(View.VISIBLE);
        momentsSkeletonScreen = Skeleton.bind(highlightedMomentsRecycler).load(R.layout.layout_img_skeleton).show();
        momentsSkeletonScreen2 = Skeleton.bind(momentsRecycler).load(R.layout.layout_highlighted_skeleton).show();
        fetchData();
    }


    public static class HighlightedMomentsAdapter extends RecyclerView.Adapter<HighlightedMomentsAdapter.HighLightedMomentsViewHolder>{
        ArrayList<Moment> highLightedMomentsArrayList;
        Context context;
        SkeletonScreen momentsSkeletonScreen;

        public HighlightedMomentsAdapter(ArrayList<Moment> highLightedMomentsAdapter , Context context , SkeletonScreen momentsSkeletonScreen) {
            this.highLightedMomentsArrayList = highLightedMomentsAdapter;
            this.context = context;
            this.momentsSkeletonScreen = momentsSkeletonScreen;
        }

        @NonNull
        @Override
        public HighLightedMomentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.highlighted_moments_list_item , parent , false);

            return new HighLightedMomentsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull HighLightedMomentsViewHolder holder, int position) {
            Moment highLightedMoment = highLightedMomentsArrayList.get(position);

            holder.highLightedMomentTextView.setText(highLightedMoment.getMomentTitle());
            Picasso.with(context).load(highLightedMoment.getMomentImage()).into(holder.highLightedMomentImageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                }
            });

            holder.highLightedMomentTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent detailsIntent = new Intent();
                    detailsIntent.putExtra("MomentTitle" , highLightedMoment.getMomentTitle());
                    detailsIntent.putExtra("MomentDesc" , highLightedMoment.getMomentDesc());
                    detailsIntent.putExtra("MomentVideo" , highLightedMoment.getMomentVideo());
                    detailsIntent.putExtra("MomentImage" , highLightedMoment.getMomentImage());
                    detailsIntent.putExtra("Class" , "SlideShowFragment");
                    detailsIntent.setClass(context , DetailsActivity.class);
                    context.startActivity(detailsIntent);
                    Activity activity = (Activity) context;
                    activity.overridePendingTransition(R.anim.slide_in_right , R.anim.slide_out_left);
                }
            });

            holder.highLightedMomentImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent detailsIntent = new Intent();
                    detailsIntent.putExtra("MomentTitle" , highLightedMoment.getMomentTitle());
                    detailsIntent.putExtra("MomentDesc" , highLightedMoment.getMomentDesc());
                    detailsIntent.putExtra("MomentVideo" , highLightedMoment.getMomentVideo());
                    detailsIntent.putExtra("MomentImage" , highLightedMoment.getMomentImage());
                    detailsIntent.putExtra("Class" , "SlideShowFragment");
                    detailsIntent.setClass(context , DetailsActivity.class);
                    context.startActivity(detailsIntent);

                    Activity activity = (Activity) context;
                    activity.overridePendingTransition(R.anim.slide_in_right , R.anim.slide_out_left);

                }
            });

        }

        @Override
        public int getItemCount() {
            return highLightedMomentsArrayList.size();
        }

        public static class HighLightedMomentsViewHolder extends RecyclerView.ViewHolder{
            private ImageView highLightedMomentImageView;
            private TextView highLightedMomentTextView;

            public HighLightedMomentsViewHolder(@NonNull View itemView) {
                super(itemView);
                highLightedMomentImageView = itemView.findViewById(R.id.highlighted_moment_img);
                highLightedMomentTextView = itemView.findViewById(R.id.highlighted_moment_name);
            }
        }


    }

    public static class MomentsAdapter extends RecyclerView.Adapter<MomentsAdapter.MomentsViewHolder>{
        ArrayList<Moment> momentsArrayList;
        Context context;
        SkeletonScreen momentsSkeletonScreen;

        public MomentsAdapter(ArrayList<Moment> momentsArrayList , Context context , SkeletonScreen momentsSkeletonScreen) {
            this.momentsArrayList = momentsArrayList;
            this.context = context;
            this.momentsSkeletonScreen = momentsSkeletonScreen;
        }

        @NonNull
        @Override
        public MomentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.moments_list_item , parent , false);

            return new MomentsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MomentsViewHolder holder, int position) {
            Moment highLightedMoment = momentsArrayList.get(position);

            holder.momentTextView.setText(highLightedMoment.getMomentTitle());
            Picasso.with(context).load(highLightedMoment.getMomentImage()).into(holder.momentImageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                }
            });
            holder.momentTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent detailsIntent = new Intent();
                    detailsIntent.putExtra("MomentTitle" , highLightedMoment.getMomentTitle());
                    detailsIntent.putExtra("MomentDesc" , highLightedMoment.getMomentDesc());
                    detailsIntent.putExtra("MomentVideo" , highLightedMoment.getMomentVideo());
                    detailsIntent.putExtra("MomentImage" , highLightedMoment.getMomentImage());
                    detailsIntent.putExtra("Class" , "SlideShowFragment");
                    detailsIntent.setClass(context , DetailsActivity.class);
                    context.startActivity(detailsIntent);
                    Activity activity = (Activity) context;
                    activity.overridePendingTransition(R.anim.slide_in_right , R.anim.slide_out_left);
                }
            });

            holder.momentImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent detailsIntent = new Intent();
                    detailsIntent.putExtra("MomentTitle" , highLightedMoment.getMomentTitle());
                    detailsIntent.putExtra("MomentDesc" , highLightedMoment.getMomentDesc());
                    detailsIntent.putExtra("MomentVideo" , highLightedMoment.getMomentVideo());
                    detailsIntent.putExtra("MomentImage" , highLightedMoment.getMomentImage());
                    detailsIntent.putExtra("Class" , "SlideShowFragment");
                    detailsIntent.setClass(context , DetailsActivity.class);
                    context.startActivity(detailsIntent);
                    Activity activity = (Activity) context;
                    activity.overridePendingTransition(R.anim.slide_in_right , R.anim.slide_out_left);
                }
            });

        }

        @Override
        public int getItemCount() {
            return momentsArrayList.size();
        }

        public static class MomentsViewHolder extends RecyclerView.ViewHolder{
            private ImageView momentImageView;
            private TextView momentTextView;

            public MomentsViewHolder(@NonNull View itemView) {
                super(itemView);
                momentImageView = itemView.findViewById(R.id.highlighted_moment_img);
                momentTextView = itemView.findViewById(R.id.highlighted_moment_name);
            }
        }


    }
}