package com.example.yatee.hw9_a;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AddFriendsToTripAdapter extends ArrayAdapter<User> {
    private Context context;
    private String tripKey;
    private static class ViewHolder {
        TextView tv_Name;
        Button button_add;
        ImageView iv_pic;
        DatabaseReference rootRefUser;
    }

    public AddFriendsToTripAdapter(@NonNull Context context, @LayoutRes int resource, List<User> objects, String tripKey) {
        super(context, resource,objects);
        this.context = context;
        this.tripKey = tripKey;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final User user = getItem(position);

        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.friendsview, parent, false);
            viewHolder.tv_Name= (TextView) convertView.findViewById(R.id.textView);
            viewHolder.button_add= (Button) convertView.findViewById(R.id.addFriend);
            viewHolder.iv_pic= (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.rootRefUser =FirebaseDatabase.getInstance().getReference("Users").child(user.getId());
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (user != null) {
            viewHolder.tv_Name.setText(user.getfName()+" "+ user.getlName());
            Picasso.with(context)
                    .load(user.getPhotoURL())
                    .resize(140,140)
                    .into(viewHolder.iv_pic);

            viewHolder.button_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.rootRefUser.child("subTrips").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                            ArrayList<String> subTrips = new ArrayList<String>();
                            if(dataSnapshot != null) {
                                subTrips  = dataSnapshot.getValue(t);
                            }

                            if(subTrips  == null)
                                subTrips  = new ArrayList<String>();
                            if(!subTrips.contains(tripKey))
                                subTrips.add(tripKey);
                            viewHolder.rootRefUser.child("subTrips").setValue(subTrips);
                            viewHolder.button_add.setEnabled(false);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
        }

        return convertView;
    }
}
