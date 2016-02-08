package com.patcomp.instagramclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by patrick on 2/7/16.
 */
public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {

    public InstagramPhotosAdapter(Context context, List<InstagramPhoto> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        InstagramPhoto photo = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo_xml,parent,false);

        TextView Caption = (TextView) convertView.findViewById(R.id.tvCaption);
        ImageView lvPhoto = (ImageView) convertView.findViewById(R.id.IvPhoto);

        Caption.setText(photo.caption);
        lvPhoto.setImageResource(0);

        Picasso.with(getContext()).load(photo.imageUrl).into(lvPhoto);

        return convertView;
    }
}
