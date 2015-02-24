package com.instastuff.instaclient2;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;


/**
 * Created by avkadam on 2/18/15.
 */
public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {

    public InstagramPhotosAdapter(Context context, List<InstagramPhoto> objects) {
        super(context, 0, objects);
    }

    Transformation transformation = new RoundedTransformationBuilder()
            .borderColor(Color.BLACK)
            .borderWidthDp(3)
            .cornerRadiusDp(40)
            .oval(false)
            .build();


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data to be loaded
        InstagramPhoto photo = getItem(position);

        // Check if we are using recycled view
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.instaphoto, parent, false);
        }

        // Get views to be used
        TextView tvCaption = (TextView) convertView.findViewById(R.id.tvCaption);
        TextView tvUsername = (TextView) convertView.findViewById(R.id.tvUserName);
        ImageView ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);
        ImageView ivProfilePic = (ImageView) convertView.findViewById(R.id.ivProfilePic);

        // Date
        CharSequence relativeTimeSpan = DateUtils.getRelativeTimeSpanString(
                photo.timestamp * 1000,
                System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
        tvDate.setText(relativeTimeSpan);
        Log.i("f", "time is "+System.currentTimeMillis());

        // Set the caption now
        tvCaption.setText(photo.caption);
        tvUsername.setText(photo.username);

        // reset previous image in case recycled view is getting used
        ivPhoto.setImageResource(0);
        ivProfilePic.setImageResource(0);

        // Set image using picasso
        Picasso.with(getContext())
                .load(photo.imageURL)
                .placeholder(R.drawable.placeholder)
                .into(ivPhoto);
        Picasso.with(getContext()).load(photo.profilePicURL)
                .placeholder(R.drawable.ic_launcher)
                .transform(transformation)
                .into(ivProfilePic);

        // return the rendered view.
        return convertView;
    }
}
