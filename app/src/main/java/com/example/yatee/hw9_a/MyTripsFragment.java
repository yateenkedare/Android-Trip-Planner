package com.example.yatee.hw9_a;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by yatee on 4/19/2017.
 */

public class MyTripsFragment extends Fragment {
    DatabaseReference refUser;
    FirebaseDatabase db;
    User user;
    FirebaseUser firebaseUser;
    ArrayList<User> requested;
    ArrayList<User> received;

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
        db = FirebaseDatabase.getInstance();
        refUser = db.getReference("Users").child(firebaseUser.getUid());
        refUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                if(currentUser.getMyTrips() != null){

                }
                if(currentUser.getSubTrips() != null) {

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
