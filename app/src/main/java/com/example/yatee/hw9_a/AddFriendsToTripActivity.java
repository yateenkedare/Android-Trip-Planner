package com.example.yatee.hw9_a;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class AddFriendsToTripActivity extends AppCompatActivity {
    private String tripKey;
    private ListView listView;
    private DatabaseReference rootRefUser;
    private FirebaseUser firebaseUser;
    ArrayList<String> friends;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends_to_trip);
        tripKey = getIntent().getStringExtra("KEY");
        listView = (ListView) findViewById(R.id.lvAddFriendsToTrip);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        rootRefUser = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        rootRefUser = FirebaseDatabase.getInstance().getReference("Trips").child(tripKey);
        rootRefUser.child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                friends = new ArrayList<String>();
                if(dataSnapshot != null) {
                    friends  = dataSnapshot.getValue(t);
//                    AddFriendsToTripAdapter adapter = new AddFriendsToTripAdapter(AddFriendsToTripActivity.this,R.layout.friendsview,friends,tripKey);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
