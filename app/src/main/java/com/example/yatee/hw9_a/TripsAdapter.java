package com.example.yatee.hw9_a;

import android.content.Context;
import android.content.Intent;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
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
    int mode;
    DatabaseReference rootRefCurrentUsers;
    FirebaseUser firebaseUser;
    FirebaseDatabase db;

    private static class ViewHolder {
        TextView tvTitle;
        TextView tvLocation;
        ImageView imageView;
        Button add;
    }

    public TripsAdapter(@NonNull Context context, @LayoutRes int resource, List<Trip> objects, int mode) {
        super(context, resource);
        this.context=context;
        this.resource=resource;
        this.objects=objects;
        this.mode = mode;

        db = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        rootRefCurrentUsers = db.getReference("Users").child(firebaseUser.getUid());

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Trip trip = getItem(position);

        final ViewHolder viewHolder;


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
            if(0 == mode) {
                viewHolder.add.setText("Chat");
                viewHolder.add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(),TripChatActivity.class);
                        intent.putExtra("KEY",trip.getKey());
                        context.startActivity(intent);
                    }
                });
            }
            else{
                viewHolder.add.setText("Join");
                viewHolder.add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        viewHolder.add.setEnabled(false);

                        rootRefCurrentUsers.child("subTrips").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                                ArrayList<String> subTrips = new ArrayList<String>();
                                if(dataSnapshot != null) {
                                    subTrips  = dataSnapshot.getValue(t);
                                }

                                if(subTrips  == null)
                                    subTrips  = new ArrayList<String>();

                                subTrips.add(trip.getKey());
                                rootRefCurrentUsers.child("subTrips").setValue(subTrips);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });
            }
        }
        return convertView;
    }
}
