package com.example.yatee.hw9_a;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TripChatActivity extends AppCompatActivity {
    //TODO add Friends from Friends List
    //TODO add Trip Details
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase db;
    DatabaseReference rootRef;
    DatabaseReference ref1;
    DatabaseReference ref2;
    User currentUser;
    ArrayList<User> friends;
    String tripKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_chat);
        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance();
        tripKey = getIntent().getStringExtra("KEY");

        //TODO get all the friends from user: Just use this code right away when need arises

        /*ref1 = db.getReference("Users").child(firebaseUser.getUid());
        ref2=db.getReference("Users");


        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser=dataSnapshot.getValue(User.class);

                ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            User user=snapshot.getValue(User.class);
                            Log.d("Users:",user.toString());
                            int flag=0;
                            if(!snapshot.getKey().toString().equals(firebaseUser.getUid().toString())){
                                ArrayList<String> friendString=currentUser.getFriends();
                                if(friendString!= null)
                                    for(int i=0;i<friendString.size();i++){
                                        if(user.getId().equals(friendString.get(i))) {
                                            flag = 1;
                                            break;
                                        }
                                    }
                                if(flag!=1)
                                    friends.add(user);

                            }

                        }



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
        });*/

        rootRef=db.getReference("Chats");
        EditText messageText= (EditText) findViewById(R.id.messageText);
        String messageValue=messageText.getText().toString();
        Message messageObj=new Message();
        messageObj.setText(messageValue);
        messageObj.setSender(currentUser.getfName()+" "+currentUser.getlName());

        findViewById(R.id.addMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.addMembersToTripChat:
                Intent intent = new Intent(TripChatActivity.this,LoginActivity.class);
                startActivity(intent);
                return true;
            case R.id.leaveChatRoom:
                //TODO - delete trip from sub trips or my trips
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
