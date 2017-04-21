package com.example.yatee.hw9_a;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by yatee on 4/19/2017.
 */

public class MyTripsFragment extends Fragment {
    DatabaseReference refUser, refTrips;
    FirebaseDatabase db;
    User user;
    FirebaseUser firebaseUser;
    ArrayList<Trip> trips;
    ListView listView;
    TripsAdapter tripsAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_trips, container, false);
        if (getUserVisibleHint()) {
            visibleActions();
        }
        return v;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            if(isResumed()){
                visibleActions();
            }
        }
    }

    private void visibleActions(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseDatabase.getInstance();
        refUser = db.getReference("Users").child(firebaseUser.getUid());
        refTrips = db.getReference("Trips");
        listView = (ListView) getActivity().findViewById(R.id.tripsListView);
        refUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                trips = new ArrayList<Trip>();
                listView = (ListView) getActivity().findViewById(R.id.tripsListView);
                tripsAdapter = new TripsAdapter(getActivity(),R.layout.trips_view,trips);
                listView.setAdapter(tripsAdapter);
                tripsAdapter.setNotifyOnChange(true);
                tripsAdapter.notifyDataSetChanged();
                if(currentUser.getMyTrips() != null){
                    for(String s: currentUser.getMyTrips()) {
                        refTrips.child(s).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Trip t = dataSnapshot.getValue(Trip.class);
                                Log.d("Trips", t.toString());
                                tripsAdapter.add(t);
                                tripsAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
                if(currentUser.getSubTrips() != null) {
                    for(String s: currentUser.getSubTrips()) {
                        refTrips.child(s).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Trip t = dataSnapshot.getValue(Trip.class);
                                Log.d("Trips", dataSnapshot.toString());
                                tripsAdapter.add(t);
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
