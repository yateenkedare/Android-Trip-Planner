package com.example.yatee.hw9_a;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yatee on 4/20/2017.
 */

public class TripsAdapter extends ArrayAdapter<Trip> {
    Context context;
    int resource;
    List<Trip> objects;

    private static class ViewHolder {
        TextView tvTitle;
        TextView tvLocation;
        ImageView imageView;
        Button add;
    }

    public TripsAdapter(@NonNull Context context, @LayoutRes int resource, List<Trip> objects) {
        super(context, resource);
        this.context=context;
        this.resource=resource;
        this.objects=objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Trip trip = getItem(position);

        ViewHolder viewHolder;


        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(resource, parent, false);
            viewHolder.tvTitle= (TextView) convertView.findViewById(R.id.textViewTripTitle);
            viewHolder.tvLocation = (TextView) convertView.findViewById(R.id.textViewTripLocation);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageViewTrip);
            viewHolder.add = (Button) convertView.findViewById(R.id.tripButton);

            convertView.setTag(viewHolder);
        }
        else {
            // View is being recycled, retrieve the viewHolder object from tag

            viewHolder = (ViewHolder) convertView.getTag();
        }
        Log.d("TRIP List View", objects.toString());
        if(trip != null) {
            viewHolder.tvTitle.setText(trip.getTitle());
            viewHolder.tvLocation.setText(trip.getLocation());
            Picasso.with(context)
                    .load(trip.getCoverURL())
                    .into(viewHolder.imageView);
            viewHolder.add.setText("Chat");
            viewHolder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        return convertView;
    }
}
