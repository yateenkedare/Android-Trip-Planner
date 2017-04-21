package com.example.yatee.hw9_a;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DiscoverTripsActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    DatabaseReference rootRefUsers, rootRefTrips;
    FirebaseUser firebaseUser;
    FirebaseDatabase db;
    ListView listView;
    TripsAdapter tripsAdapter;
    ArrayList<Trip> trips;
    User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_trips);

        db = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        rootRefUsers = db.getReference("Users");
        rootRefTrips = db.getReference("Trips");

        listView = (ListView) findViewById(R.id.listViewDiscoverTrips);
        fab = (FloatingActionButton) findViewById(R.id.fabAddNewTrip);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DiscoverTripsActivity.this,AddTripActivity.class);
                startActivity(intent);
            }
        });
        trips = new ArrayList<Trip>();
        tripsAdapter = new TripsAdapter(DiscoverTripsActivity.this,R.layout.trips_view,trips, 1);
        listView.setAdapter(tripsAdapter);
        tripsAdapter.setNotifyOnChange(true);
        tripsAdapter.notifyDataSetChanged();
        rootRefUsers.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                if(currentUser.getFriends() != null) {
                    for(String s: currentUser.getFriends()){
                        rootRefUsers.child(s).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User friendUser = dataSnapshot.getValue(User.class);
                                if(friendUser.getMyTrips() != null) {
                                    for(String f: friendUser.getMyTrips()){
                                        if(currentUser.getSubTrips() != null) {
                                            if (!currentUser.getSubTrips().contains(f)) {
                                                rootRefTrips.child(f).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        Trip t = dataSnapshot.getValue(Trip.class);
                                                        tripsAdapter.add(t);
                                                        tripsAdapter.notifyDataSetChanged();
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        }
                                        else{
                                            rootRefTrips.child(f).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    Trip t = dataSnapshot.getValue(Trip.class);
                                                    tripsAdapter.add(t);
                                                    tripsAdapter.notifyDataSetChanged();
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
