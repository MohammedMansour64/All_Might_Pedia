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

import java.util.ArrayList;

public class AdditionalInfoAdapter extends ArrayAdapter<String> {

    private final String[] additionalInfoList;
    public AdditionalInfoAdapter(@NonNull Context context, int resource, String[] additionalInfoList) {
        super(context, resource, additionalInfoList);
        this.additionalInfoList = additionalInfoList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.additional_info_layout, null, true);
        TextView infoName = view.findViewById(R.id.info_name);

        infoName.setText(additionalInfoList[position]);

        return view;

    }
}
