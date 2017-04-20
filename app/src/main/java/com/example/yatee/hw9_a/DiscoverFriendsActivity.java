package com.example.yatee.hw9_a;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DiscoverFriendsActivity extends AppCompatActivity {
    DatabaseReference ref2;
    FirebaseDatabase db;
    User user;
    ArrayList<User> usersList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_friends);
        db = FirebaseDatabase.getInstance();
        ref2 = db.getReference("Users");
        usersList=new ArrayList<User>();

        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    user=snapshot.getValue(User.class);
                    Log.d("Users:",user.toString());
                    if(!snapshot.getKey().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()))
                        usersList.add(user);
                }
                ListView lv= (ListView) findViewById(R.id.listView);
                //ArrayAdapter<Color> adapter=new ArrayAdapter<Color>(this,android.R.layout.simple_list_item_1,colors);
                UserAdapter adapter=new UserAdapter(DiscoverFriendsActivity.this,R.layout.friendsview,usersList);
                lv.setAdapter(adapter);
                adapter.setNotifyOnChange(true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Demo", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });



    }
}