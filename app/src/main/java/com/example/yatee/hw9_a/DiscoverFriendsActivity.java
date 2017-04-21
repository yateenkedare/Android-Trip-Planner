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
    DatabaseReference ref3;
    FirebaseDatabase db;
    User user,currentUser;
    ArrayList<User> usersList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_friends);
        db = FirebaseDatabase.getInstance();
        ref2 = db.getReference("Users");
        ref3 = db.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        usersList=new ArrayList<User>();

        ref3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser=dataSnapshot.getValue(User.class);

                ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            user=snapshot.getValue(User.class);
                            Log.d("Users:",user.toString());
                            int flag=0;

                            if(!snapshot.getKey().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid().toString())){
                                ArrayList<String> friends=currentUser.getFriends();
                                if(friends != null)
                                    for(int i=0;i<friends.size();i++){
                                        if(user.getId().equals(friends.get(i))) {
                                            flag = 1;
                                            break;
                                        }
                                    }
                                if(flag!=1)
                                    usersList.add(user);

                            }

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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}