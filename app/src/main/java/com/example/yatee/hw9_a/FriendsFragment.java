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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by yatee on 4/19/2017.
 */

public class FriendsFragment extends Fragment {
    DatabaseReference ref2;
    DatabaseReference ref3;
    FirebaseDatabase db;
    User user;
    FirebaseUser firebaseUser;
    ArrayList<User> requested;
    ArrayList<User> received;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_friends, container, false);
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

        requested=new ArrayList<User>();
        received=new ArrayList<User>();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        ref2 = db.getReference("Users").child(firebaseUser.getUid());
        ref3 = db.getReference("Users");

        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("UserIS:",snapshot.getValue(User.class).toString());
                user=snapshot.getValue(User.class);
                final ArrayList<String> requestReceivedString=user.getRequestsReceived();
                ref3.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot1: dataSnapshot.getChildren()){
                            if(requestReceivedString!=null)
                            for(int i=0;i<requestReceivedString.size();i++){
                                Log.d("Testing:",requestReceivedString.get(i));
                                if(snapshot1.getKey().toString().equals(requestReceivedString.get(i))){
                                    Log.d("Testing:","Entered");
                                    received.add(snapshot1.getValue(User.class));
                                }
                                Log.d("ReceivedList:",received.toString());
                                ListView lv= (ListView) getActivity().findViewById(R.id.received);
                                //ListView lv2= (ListView) findViewById(R.id.sent);
                                //ArrayAdapter<Color> adapter=new ArrayAdapter<Color>(this,android.R.layout.simple_list_item_1,colors);
                                RequestAdapter adapter=new RequestAdapter(getActivity(),R.layout.requests,received,user,ref3);
                                lv.setAdapter(adapter);
                                adapter.setNotifyOnChange(true);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

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
