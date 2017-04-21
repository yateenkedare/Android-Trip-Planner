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

import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajdeeprao on 4/20/17.
 */

public class RequestAdapter extends ArrayAdapter<User> {
    Context context;
    int resource;
    List<User> objects;
    User currentUser,user;
    DatabaseReference rootRef;
    int mode;


    public RequestAdapter(Context context, int resource, List<User> objects, User currentUser, DatabaseReference rootRef, int mode) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.objects=objects;
        this.currentUser=currentUser;
        this.rootRef=rootRef;
        this.mode=mode;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(resource,parent,false);
        }
        final TextView tv1= (TextView) convertView.findViewById(R.id.requestName);
        final ImageView imageView= (ImageView) convertView.findViewById(R.id.displayPicture);
        final Button add= (Button) convertView.findViewById(R.id.accept);

        tv1.setText(objects.get(position).getfName()+" "+objects.get(position).getlName());
        Picasso.with(context)
                .load(objects.get(position).getPhotoURL())
                .into(imageView);
        if(mode==1)
            add.setText("Add Friend");
        else
            add.setText("Remove Friend");

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO mode 1=> ADD, mode 2=>Remove
                Log.d("userclick:",objects.get(position).toString());
                if(mode==1){
                    ArrayList<String> friends;
                    if(currentUser.getFriends()!=null)
                        friends=new ArrayList<String>(currentUser.getFriends());
                    else
                        friends=new ArrayList<String>();

                    friends.add(objects.get(position).getId());
                    currentUser.setFriends(friends);
                    Log.d("FRIENDSCURRENT:",friends.toString());

                    ArrayList<String> requested;
                    if(currentUser.getRequestsReceived()!=null)
                        requested=new ArrayList<String>(currentUser.getRequestsReceived());
                    else
                        requested=new ArrayList<String>();

                    requested.remove(position);
                    currentUser.setRequestsReceived(requested);
                    Log.d("ReqCURRENT:",requested.toString());

                    ArrayList<String> hisFriends;
                    if(objects.get(position).getFriends()!=null)
                        hisFriends=new ArrayList<String>(objects.get(position).getFriends());
                    else
                        hisFriends=new ArrayList<String>();

                    hisFriends.add(currentUser.getId());
                    objects.get(position).setFriends(hisFriends);
                    Log.d("FRIENDSHis:",hisFriends.toString());

                    ArrayList<String> sent;
                    if(objects.get(position).getRequests()!=null)
                        sent=new ArrayList<String>(objects.get(position).getRequests());
                    else
                        sent=new ArrayList<String>();

                    sent.remove(currentUser.getId());
                    objects.get(position).setRequests(sent);

                    rootRef.child(currentUser.getId()).setValue(currentUser);
                    rootRef.child(objects.get(position).getId()).setValue(objects.get(position));

                    objects.remove(position);
                    notifyDataSetChanged();

                }
                else{

                    ArrayList<String> friends;
                    if(currentUser.getFriends()!=null)
                        friends=new ArrayList<String>(currentUser.getFriends());
                    else
                        friends=new ArrayList<String>();

                    friends.remove(objects.get(position).getId());
                    currentUser.setFriends(friends);
                    Log.d("FRIENDSCURRENT:",friends.toString());


                    ArrayList<String> hisFriends;
                    if(objects.get(position).getFriends()!=null)
                        hisFriends=new ArrayList<String>(objects.get(position).getFriends());
                    else
                        hisFriends=new ArrayList<String>();

                    hisFriends.remove(currentUser.getId());
                    objects.get(position).setFriends(hisFriends);
                    Log.d("FRIENDSHis:",hisFriends.toString());

                    rootRef.child(currentUser.getId()).setValue(currentUser);
                    rootRef.child(objects.get(position).getId()).setValue(objects.get(position));

                    objects.remove(position);
                    notifyDataSetChanged();


                }
            }
        });



        return convertView;

    }
}