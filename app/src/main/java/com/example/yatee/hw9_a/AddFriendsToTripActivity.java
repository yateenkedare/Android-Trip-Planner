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
        rootRefUser = FirebaseDatabase.getInstance().getReference("Users");
        rootRefUser.child(firebaseUser.getUid()).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                friends = new ArrayList<String>();
                if(dataSnapshot != null) {
                    friends  = dataSnapshot.getValue(t);
                    rootRefUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            GenericTypeIndicator<ArrayList<User>> t = new GenericTypeIndicator<ArrayList<User>>() {};
                            ArrayList<User> friendUsers=new ArrayList<User>();
                            for(DataSnapshot f: dataSnapshot.getChildren()) {
                                User u = f.getValue(User.class);

                                if(friends.contains(u.getId())) {
                                    if(u.getMyTrips()== null) {
                                            friendUsers.add(u);
                                    }
                                    else{
                                        if (!u.getMyTrips().contains(tripKey)) {
                                            friendUsers.add(u);
                                        }
                                    }
                                }
                            }


                            AddFriendsToTripAdapter adapter = new AddFriendsToTripAdapter(AddFriendsToTripActivity.this,R.layout.friendsview,friendUsers,tripKey);
                            listView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
