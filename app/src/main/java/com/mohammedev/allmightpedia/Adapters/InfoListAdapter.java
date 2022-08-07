package com.mohammedev.allmightpedia.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.mohammedev.allmightpedia.Activities.DetailsActivity;
import com.mohammedev.allmightpedia.Activities.InfoListActivity;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.InfoItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

public class InfoListAdapter extends RecyclerView.Adapter<InfoListAdapter.InfoListViewHolder> {
    private ArrayList<InfoItem> infoItemList;
    private String itemCategoryString;
    private Context context;

    public InfoListAdapter(ArrayList<InfoItem> infoItemList, String itemCategoryString, Context context) {
        this.infoItemList = infoItemList;
        this.itemCategoryString = itemCategoryString;
        this.context = context;
    }

    @NonNull
    @Override
    public InfoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.info_list_layout, parent , false);
        return new InfoListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoListViewHolder holder, int position) {
        InfoItem infoItem = infoItemList.get(position);
        holder.subTitle.setText(itemCategoryString);
        holder.infoTitle.setText(infoItem.getInfoTitle());
        Picasso.with(context).load(infoItem.getInfoImageUrl()).into(holder.gifImageView);

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InfoItem clickedItem = infoItemList.get(holder.getAdapterPosition());

                Intent detailsIntent = new Intent();
                detailsIntent.putExtra("infoItem" , clickedItem);
                detailsIntent.putExtra("Class" , "InfoListActivity");
                detailsIntent.setClass(context , DetailsActivity.class);
                context.startActivity(detailsIntent);
                Activity activity = (Activity) context;
                activity.overridePendingTransition(R.anim.slide_in_right , R.anim.slide_out_left);
            }
        });



    }

    @Override
    public int getItemCount() {
        return infoItemList.size();
    }

    static class InfoListViewHolder extends RecyclerView.ViewHolder{
        private final TextView infoTitle;
        private final TextView subTitle;
        private final ImageView gifImageView;
        private final ConstraintLayout constraintLayout;
        public InfoListViewHolder(@NonNull View itemView) {
            super(itemView);
            infoTitle = itemView.findViewById(R.id.info_title);
            subTitle = itemView.findViewById(R.id.info_subtitle);
            gifImageView = itemView.findViewById(R.id.info_image);
            constraintLayout = itemView.findViewById(R.id.info_item_constraint);
        }
    }
}
