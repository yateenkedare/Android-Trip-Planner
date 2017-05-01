package com.example.yatee.hw9_a;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.example.yatee.hw9_a.LoginActivity.mGoogleApiClient;

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
    LocationManager mLocationManager;
    Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_profile);
        tripKey = getIntent().getStringExtra("KEY");

        name = (TextView) findViewById(R.id.tvProfileName);
        picture = (ImageView) findViewById(R.id.tripProfileImageView);
        placesListView = (ListView) findViewById(R.id.tripPlacesListView);
        fab = (FloatingActionButton) findViewById(R.id.tripProfileFAB);

        db = FirebaseDatabase.getInstance();
        refTrips = db.getReference("Trips").child(tripKey);
        refPlaces = db.getReference("Places").child(tripKey);

        currentplaces = new ArrayList<Places>();
        adapter = new ArrayAdapter<Places>(TripProfileActivity.this,
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
                Log.d("TestLatLang:",dataSnapshot.getValue().toString());
                Places p = dataSnapshot.getValue(Places.class);
                currentplaces.add(p);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("CHILD ", "REMOVED");
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
                if (currentplaces.size() > 1 || position == 0) {
                    ArrayList<Places> n = new ArrayList<>(currentplaces);
                    n.remove(position);
                    refPlaces.setValue(n);
                    return true;
                } else {
                    Toast.makeText(TripProfileActivity.this, "Trip requires atleast one place", Toast.LENGTH_SHORT).show();
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
                LatLng pos=place.getLatLng();
                Places p = new Places(place.getName().toString(), place.getId(),pos.latitude,pos.longitude);

                if (!currentplaces.contains(p)) {
                    ArrayList<Places> n = new ArrayList<>(currentplaces);
                    n.add(p);
                    refPlaces.setValue(n);
                } else {
                    Toast.makeText(TripProfileActivity.this, "Place already present in currnet trip", Toast.LENGTH_SHORT).show();
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
        Location lastKnownLocation=getLastKnownLocation();

        int id = item.getItemId();
        switch (id) {
            case R.id.getRoute:
                //https://maps.googleapis.com/maps/api/directions/json?origin=place_id:ChIJF5NfdwgcVIgRuRgluxZnNWc&destination=place_id:ChIJOUywjxUcVIgR1jBAhv2xWW0&waypoints=place_id:ChIJc7nFnYceVIgRyWtpZZiynrU|ChIJgRo4_MQfVIgRZNFDv-ZQRogv&key=AIzaSyBzsFTY-zCFL-DpmwnAEZaNKgunVMjNDrQ
                StringBuilder routeURL = new StringBuilder();
                routeURL.append("https://maps.googleapis.com/maps/api/directions/json?origin=" + lastKnownLocation.getLatitude()+","+lastKnownLocation.getLongitude()+"&destination=" + lastKnownLocation.getLatitude()+","+lastKnownLocation.getLongitude()+ "&waypoints=");
                for (int i = 0; i < currentplaces.size(); i++) {
                    routeURL.append("place_id:");
                    routeURL.append(currentplaces.get(i).getId());
                    //if(i<=currentplaces.size()-2)
                    routeURL.append("|");

                }
                routeURL.append("&key=AIzaSyBzsFTY-zCFL-DpmwnAEZaNKgunVMjNDrQ");
                Log.d("RouteURL", routeURL.toString());
                Intent intent = new Intent(TripProfileActivity.this, MapsActivity.class);
                intent.putExtra("URL", routeURL.toString());
                intent.putExtra("KEY",tripKey);
                startActivity(intent);

                break;
            case R.id.navigation:
                //TODO marker for each waypoint
                //TODO Progress dialogs wherever needed

                String navUrl="";
                Log.d("Currentplaces:",currentplaces.toString());
                for(int i=0;i<currentplaces.size();i++){
                    navUrl = navUrl+"+to:"+currentplaces.get(i).toString();
                }
                navUrl= navUrl+"+to:"+lastKnownLocation.getLatitude()+","+lastKnownLocation.getLongitude();
                StringBuilder source=new StringBuilder();
                source.append(lastKnownLocation.getLatitude());
                source.append(",");
                source.append(lastKnownLocation.getLongitude());

                final String uri = "http://maps.google.com/maps?daddr="+source+navUrl;
                Log.d("demo","NAV URL "+navUrl.toString());
                Log.d("demo",uri.toString());
                final Intent intent1 = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(uri));
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.setClassName("com.google.android.apps.maps",
                        "com.google.android.maps.MapsActivity");

                startActivity(intent1);


                break;

        }
        return super.onOptionsItemSelected(item);
    }
    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_route_trip, menu);
        return true;
    }

}