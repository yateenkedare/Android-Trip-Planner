package com.example.yatee.hw9_a;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

        currentplaces = new ArrayList<Places>();
        adapter =  new ArrayAdapter<Places>(TripProfileActivity.this,
                android.R.layout.simple_list_item_1, currentplaces);
        placesListView.setAdapter(adapter);

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


        refPlaces.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Places p = dataSnapshot.getValue(Places.class);
                currentplaces.add(p);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("CHILD ","REMOVED");
                Places p = dataSnapshot.getValue(Places.class);
                currentplaces.remove(p);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

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

        placesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentplaces.size()>1||position==0){
                    ArrayList<Places> n = new ArrayList<>(currentplaces);
                    n.remove(position);
                    refPlaces.setValue(n);
                    return true;
                }
                else{
                    Toast.makeText(TripProfileActivity.this, "Trip requires atleast one place",Toast.LENGTH_SHORT).show();
                    return false;
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
                    ArrayList<Places> n = new ArrayList<>(currentplaces);
                    n.add(p);
                    refPlaces.setValue(n);
                }
                else{
                    Toast.makeText(TripProfileActivity.this,"Place already present in currnet trip", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.getRoute:
                //https://maps.googleapis.com/maps/api/directions/json?origin=place_id:ChIJF5NfdwgcVIgRuRgluxZnNWc&destination=place_id:ChIJOUywjxUcVIgR1jBAhv2xWW0&waypoints=place_id:ChIJc7nFnYceVIgRyWtpZZiynrU|ChIJgRo4_MQfVIgRZNFDv-ZQRogv&key=AIzaSyBzsFTY-zCFL-DpmwnAEZaNKgunVMjNDrQ
                StringBuilder routeURL=new StringBuilder();
                routeURL.append("https://maps.googleapis.com/maps/api/directions/json?origin=place_id:"+currentplaces.get(0).getId()+"&destination=place_id:"+currentplaces.get(currentplaces.size()-1).getId()+"&waypoints=");
                for(int i=1;i<currentplaces.size()-1;i++){
                    routeURL.append("place_id:");
                    routeURL.append(currentplaces.get(i).getId());
                    //if(i<=currentplaces.size()-2)
                        routeURL.append("|");
                }
                routeURL.append("&key=AIzaSyBzsFTY-zCFL-DpmwnAEZaNKgunVMjNDrQ");
                Log.d("RouteURL",routeURL.toString());
                Intent intent = new Intent(TripProfileActivity.this,RouteActivity.class);
                intent.putExtra("URL",routeURL.toString());
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_route_trip,menu);
        return true;
    }
}