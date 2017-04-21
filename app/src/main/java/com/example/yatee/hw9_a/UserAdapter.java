package com.example.yatee.hw9_a;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class UserAdapter extends ArrayAdapter<User> {
    Context context;
    int resource;
    List<User> objects;

    DatabaseReference ref2;
    FirebaseDatabase db;
    int pos;

    public UserAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.objects=objects;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(resource,parent,false);
        }
        TextView tv1= (TextView) convertView.findViewById(R.id.textView);
        tv1.setText(objects.get(position).getfName()+" "+objects.get(position).getlName());
        ImageView imageView= (ImageView) convertView.findViewById(R.id.imageView);
        Picasso.with(context)
                .load(objects.get(position).getPhotoURL())
                .resize(140,140)
                .into(imageView);
        pos=position;
        final Button addFriend= (Button) convertView.findViewById(R.id.addFriend);
        addFriend.setText("Add Friend");
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Demo:","Friend added");
                addFriend.setClickable(false);
                addFriend.setEnabled(false);
                db = FirebaseDatabase.getInstance();
                ref2 = db.getReference("Users");
                ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            User user=snapshot.getValue(User.class);
                            Log.d("USER1:",user.toString());
                            Log.d("USER2:",snapshot.getKey());
                            Log.d("POS:",String.valueOf(position));
                            if(snapshot.getKey().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid().toString())){
                                if(user.getRequests()!=null){
                                    Log.d("IF:","Entered");
                                    Log.d("IF:",user.getRequests().toString());
                                    ArrayList<String> requests=new ArrayList<>(user.getRequests());
                                    Log.d("REQ:",requests.toString());
                                    requests.add(objects.get(position).getId());
                                    Log.d("REQEDIT:",requests.toString());
                                    user.setRequests(requests);
                                    if(objects.get(position).getRequestsReceived()!=null){
                                        Log.d("FULL-IF:","working");
                                        ArrayList<String> receivedReq=new ArrayList<String>(objects.get(position).getRequestsReceived());
                                        receivedReq.add(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                                        objects.get(position).setRequestsReceived(receivedReq);
                                        ref2.child(objects.get(position).getId()).setValue(objects.get(position));
                                    }
                                    else{
                                        Log.d("FULL-ELSE:","working");
                                        ArrayList<String> receivedReq=new ArrayList<String>();
                                        receivedReq.add(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                                        objects.get(position).setRequestsReceived(receivedReq);
                                        ref2.child(objects.get(position).getId()).setValue(objects.get(position));
                                    }

                                }
                                else{

                                    ArrayList<String> requests=new ArrayList<String>();
                                    requests.add(objects.get(position).getId());
                                    user.setRequests(requests);
                                    if(objects.get(position).getRequestsReceived()!=null){
                                        Log.d("EMPTY-IF:","working");
                                        ArrayList<String> receivedReq=new ArrayList<String>(objects.get(position).getRequestsReceived());
                                        receivedReq.add(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                                        objects.get(position).setRequestsReceived(receivedReq);
                                        ref2.child(objects.get(position).getId()).setValue(objects.get(position));
                                    }
                                    else{
                                        Log.d("EMPTY-ELSE:","working");
                                        ArrayList<String> receivedReq=new ArrayList<String>();
                                        receivedReq.add(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                                        objects.get(position).setRequestsReceived(receivedReq);
                                        ref2.child(objects.get(position).getId()).setValue(objects.get(position));
                                    }
                                }
                                ref2.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);

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
        });

        return convertView;

    }
}