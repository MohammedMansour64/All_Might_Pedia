package com.mohammedev.allmightpedia.ui.gallery;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.Image;
import com.mohammedev.allmightpedia.databinding.FragmentGalleryFavouritesBinding;
import com.mohammedev.allmightpedia.databinding.FragmentGalleryListBinding;
import com.mohammedev.allmightpedia.utils.ViewSpaces;
import com.squareup.picasso.Picasso;

import java.util.Objects;


public class GalleryFragmentFavourites extends Fragment {
    FragmentGalleryFavouritesBinding binding;
    FirebaseRecyclerAdapter adapter;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentGalleryFavouritesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(GalleryFragmentFavourites.this.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new ViewSpaces(20));
        fetch();


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
        Query query = FirebaseDatabase.getInstance().getReference().child("users");

        FirebaseRecyclerOptions<Image> options = new FirebaseRecyclerOptions.Builder<Image>().setQuery(query, new SnapshotParser<Image>() {
            @NonNull
            @Override
            public Image parseSnapshot(@NonNull DataSnapshot snapshot) {
                return new Image(Objects.requireNonNull(snapshot.child("galleryFavourites").getValue()).toString());
            }
        }).build();


        adapter = new FirebaseRecyclerAdapter<Image, GalleryFragmentFavourites.GalleryListViewHolder>(options) {

            @NonNull
            @Override
            public GalleryFragmentFavourites.GalleryListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.gallery_recycler_view, parent, false);

                return new GalleryFragmentFavourites.GalleryListViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull GalleryFragmentFavourites.GalleryListViewHolder viewHolder, int i, @NonNull Image image) {
                Picasso.with(GalleryFragmentFavourites.this.getContext()).load(image.getImageUrl()).into(viewHolder.image);

            }


        };
        recyclerView.setAdapter(adapter);


    }

    class GalleryListViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private ImageButton downloadBtn;

        public GalleryListViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            downloadBtn = itemView.findViewById(R.id.download_btn);

        }
    }

}