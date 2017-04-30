package com.example.yatee.hw9_a;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TripProfileActivity extends AppCompatActivity {
    private static final String TAG = "Trip-Profile-Activity";
    String tripKey;
    TextView name;
    ListView placesListView;
    ImageView picture;
    FloatingActionButton fab;
    DatabaseReference refTrips, refPlaces;
    FirebaseDatabase db;
    Trip currentTrip;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 0x05;
    ArrayAdapter<Places> adapter;
    ArrayList<Places> currentplaces;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_profile);
        tripKey = getIntent().getStringExtra("KEY");

        name = (TextView) findViewById(R.id.tvProfileName);
        picture = (ImageView) findViewById(R.id.tripProfileImageView);
        placesListView= (ListView) findViewById(R.id.tripPlacesListView);
        fab = (FloatingActionButton) findViewById(R.id.tripProfileFAB);

        db = FirebaseDatabase.getInstance();
        refTrips = db.getReference("Trips").child(tripKey);
        refPlaces = db.getReference("Places").child(tripKey);

        refTrips.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentTrip = dataSnapshot.getValue(Trip.class);
                name.setText(currentTrip.getTitle());
                Picasso.with(TripProfileActivity.this)
                        .load(currentTrip.getCoverURL())
                        .into(picture);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        refPlaces.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<Places>> t = new GenericTypeIndicator<ArrayList<Places>>() {};
                currentplaces = new ArrayList<Places>();
                if(dataSnapshot != null) {
                    currentplaces = dataSnapshot.getValue(t);
                }

                if(currentplaces== null){
                    currentplaces = new ArrayList<Places>();
                }
                adapter =  new ArrayAdapter<Places>(TripProfileActivity.this,
                        android.R.layout.simple_list_item_1, currentplaces);
                placesListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(TripProfileActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.toString());
                Places p = new Places(place.getName().toString(), place.getId());

                if(!currentplaces.contains(p)){
                    currentplaces.add(p);
                    adapter.notifyDataSetChanged();
                    refPlaces.setValue(currentplaces);
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}
