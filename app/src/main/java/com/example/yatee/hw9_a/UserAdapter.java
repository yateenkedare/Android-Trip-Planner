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
    public View getView(int position, View convertView, ViewGroup parent) {
        pos=position;
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
        Button addFriend= (Button) convertView.findViewById(R.id.addFriend);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Demo:","Friend added");
                db = FirebaseDatabase.getInstance();
                ref2 = db.getReference("Users");
                ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            User user=snapshot.getValue(User.class);
                            Log.d("USER1:",user.toString());
                            Log.d("USER2:",snapshot.getKey());
                            if(snapshot.getKey().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid().toString())){
                                if(user.getFriends()!=null){
                                    ArrayList<User> friends=new ArrayList<User>(user.getFriends());
                                    friends.add(objects.get(pos));
                                    user.setFriends(friends);
                                }
                                else{
                                    ArrayList<User> friends=new ArrayList<User>();
                                    friends.add(objects.get(pos));
                                    user.setFriends(friends);
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