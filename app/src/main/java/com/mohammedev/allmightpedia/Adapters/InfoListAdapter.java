package com.mohammedev.allmightpedia.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.InfoItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

public class InfoListAdapter extends ArrayAdapter<InfoItem> {
    private final ArrayList<InfoItem> infoItemList;

    public InfoListAdapter(@NonNull Context context, int resource, ArrayList<InfoItem> infoItemList) {
        super(context, resource, infoItemList);
        this.infoItemList = infoItemList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.info_list_layout, parent , false);
        InfoItem infoItem = infoItemList.get(position);
        TextView infoTitle = view.findViewById(R.id.info_title);
        GifImageView gifImageView = view.findViewById(R.id.info_image);
        infoTitle.setText(infoItem.getInfoTitle());
        Picasso.with(getContext()).load(infoItem.getInfoImageUrl()).into(gifImageView);

        return view;
    }
}
