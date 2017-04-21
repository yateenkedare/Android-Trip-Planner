package com.example.yatee.hw9_a;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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


    public TripsAdapter(@NonNull Context context, @LayoutRes int resource, List<Trip> objects) {
        super(context, resource);
        this.context=context;
        this.resource=resource;
        this.objects=objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(resource,parent,false);
        }

        final TextView tvTitle= (TextView) convertView.findViewById(R.id.textViewTripTitle);
        final TextView tvLocation= (TextView) convertView.findViewById(R.id.textViewTripLocation);
        final ImageView imageView= (ImageView) convertView.findViewById(R.id.imageViewTrip);
        final Button add= (Button) convertView.findViewById(R.id.tripButton);

        Picasso.with(context)
                .load(objects.get(position).getCoverURL())
                .into(imageView);

        tvTitle.setText(objects.get(position).getTitle());
        tvLocation.setText(objects.get(position).getLocation());

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });

        return convertView;
    }
}
