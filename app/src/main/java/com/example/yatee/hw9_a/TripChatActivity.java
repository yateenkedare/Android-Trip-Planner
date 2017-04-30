package com.example.yatee.hw9_a;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

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
    LinearLayout linearLayout;
    long countID;
    //New look variables
    private EditText messageET;
    private ListView messagesContainer;
    private ImageView sendBtn;
    private ImageView sendImage;
    private ChatAdapter adapter;
    private ArrayList<Message> chatHistory;
    ArrayList<String> deletedMessages;
    FirebaseStorage storage;
    String path;
    ImageView imageViewTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_chat);
        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();
        storage=FirebaseStorage.getInstance();
        db = FirebaseDatabase.getInstance();
        tripKey = getIntent().getStringExtra("KEY");
        rootRef=db.getReference("Chats").child(tripKey);
        ref1 = db.getReference("Users").child(firebaseUser.getUid());

        driverFunction();


        /*ref2=db.getReference("Users");
        linearLayout= (LinearLayout) findViewById(R.id.messageContainer);

        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser=dataSnapshot.getValue(User.class);
                rootRef=db.getReference("Chats");//TODO: .child(tripKey);

                display();


                //TODO get all the friends from user: Just use this code right away when need arises

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



    }
    public void driverFunction(){
        initControls();
    }

    private void initControls() {
        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser=dataSnapshot.getValue(User.class);
                rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
                        messageET = (EditText) findViewById(R.id.messageEdit);
                        sendBtn = (ImageView) findViewById(R.id.chatSendButton);
                        sendImage=(ImageView) findViewById(R.id.imageUpload);

                        loadDummyHistory();

                        sendBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String messageText = messageET.getText().toString();
                                if (TextUtils.isEmpty(messageText)) {
                                    return;
                                }

                                Message chatMessage = new Message();
                                chatMessage.setId(dataSnapshot.getChildrenCount());
                                countID=dataSnapshot.getChildrenCount();
                                chatMessage.setUserId(firebaseUser.getUid().toString());
                                chatMessage.setMessage(messageText);
                                chatMessage.setType(0);//0 text,1 image
                                chatMessage.setName(currentUser.getfName()+" "+currentUser.getlName());
                                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                                Log.d("Count:","clicked");

                                messageET.setText("");
                                Log.d("Count:",String.valueOf(dataSnapshot.getChildrenCount()));
                                rootRef.child(String.valueOf(dataSnapshot.getChildrenCount())).setValue(chatMessage);
                                displayMessage(chatMessage);
                                initControls();


                            }
                        });

                        sendImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(pickPhoto , 1);//one can be replaced with any action code

                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void displayMessage(Message message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void loadDummyHistory(){
        Log.d("loadDummyHistory:","run");
        messagesContainer.setAdapter(null);
        chatHistory = new ArrayList<Message>();
        deletedMessages=new ArrayList<>();
        ref1.child("DeletedMessages").child(tripKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                if(dataSnapshot != null) {
                    deletedMessages= dataSnapshot.getValue(t);

                    rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                if(deletedMessages!=null){
                                    //Log.d("DeletedMessages:",deletedMessages.toString());
                                    if(!deletedMessages.contains(String.valueOf(String.valueOf(snapshot.getValue(Message.class).getId())))){
                                        chatHistory.add(snapshot.getValue(Message.class));
                                        adapter = new ChatAdapter(TripChatActivity.this, new ArrayList<Message>(),rootRef,ref1,tripKey);
                                        messagesContainer.setAdapter(adapter);
                                        for(int i=0; i<chatHistory.size(); i++) {
                                            Message message = chatHistory.get(i);
                                            displayMessage(message);
                                        }
                                    }
                                }else{
                                    chatHistory.add(snapshot.getValue(Message.class));
                                    adapter = new ChatAdapter(TripChatActivity.this, new ArrayList<Message>(),rootRef,ref1,tripKey);
                                    messagesContainer.setAdapter(adapter);
                                    for(int i=0; i<chatHistory.size(); i++) {
                                        Message message = chatHistory.get(i);
                                        displayMessage(message);
                                    }
                                }


                            }
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


    /*public void display(){
        linearLayout.removeAllViews();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Message:","Iterating Messages");
                count=dataSnapshot.getChildrenCount();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Message message=snapshot.getValue(Message.class);

                    RelativeLayout relativeLayout=new RelativeLayout(TripChatActivity.this);


                    TextView messageContent=new TextView(TripChatActivity.this);
                    TextView sender=new TextView(TripChatActivity.this);
                    TextView time=new TextView(TripChatActivity.this);

                    messageContent.setId(Integer.parseInt("1"));

                    RelativeLayout.LayoutParams parameter = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    parameter.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    parameter.addRule(RelativeLayout.BELOW, messageContent.getId());
                    sender.setLayoutParams(parameter);

                    RelativeLayout.LayoutParams parameter2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    parameter2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    parameter2.addRule(RelativeLayout.BELOW, messageContent.getId());
                    time.setLayoutParams(parameter2);

                    relativeLayout.addView(messageContent);
                    relativeLayout.addView(sender);
                    relativeLayout.addView(time);
                    Log.d("Message:","Added Views (Apparently)");

                    RelativeLayout.LayoutParams parameter3 = new RelativeLayout.LayoutParams(800, ViewGroup.LayoutParams.WRAP_CONTENT);
                    if(message.getSenderID().toString().equals(currentUser.getId().toString())) {
                        parameter3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        Log.d("Message:", "Aligned to the right");
                    }
                    else
                        parameter3.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    relativeLayout.setLayoutParams(parameter3);

                    messageContent.setText(message.getText());
                    sender.setText(message.getSender());
                    time.setText(message.getTime());

                    linearLayout.addView(relativeLayout);
                    Log.d("Message:",message.getText());

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Calendar c = Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        final String formattedDate = df.format(c.getTime());
        final EditText messageText= (EditText) findViewById(R.id.messageText);


        findViewById(R.id.addMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String messageValue=messageText.getText().toString();
                final Message messageObj=new Message();
                messageObj.setText(messageValue);
                messageObj.setSender(currentUser.getfName()+" "+currentUser.getlName());
                messageObj.setSenderID(currentUser.getId());
                messageObj.setTime(formattedDate);
                messageObj.setMessageID(String.valueOf(count));
                rootRef.child(String.valueOf(count)).setValue(messageObj);
                messageText.setText(" ");
                display();

            }
        });


    }*/

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
                Intent intent = new Intent(TripChatActivity.this,AddFriendsToTripActivity.class);
                intent.putExtra("KEY",tripKey);
                startActivity(intent);
                return true;
            case R.id.leaveChatRoom:
                leaveChatRoom();
                //TODO delete trip chat
                return true;
            case R.id.tripProfile:
                Intent intent1 = new Intent(TripChatActivity.this, TripProfileActivity.class);
                intent1.putExtra("KEY",tripKey);
                startActivity(intent1);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void leaveChatRoom(){
        if(currentUser.getMyTrips().contains(tripKey)){
            ArrayList<String> myTrips = currentUser.getMyTrips();
            myTrips.remove(tripKey);
            ref1.child("myTrips").setValue(myTrips);
            FirebaseDatabase.getInstance().getReference("Trips").child(tripKey).removeValue();
            FirebaseDatabase.getInstance().getReference("Chats").child(tripKey).removeValue();
        }
        else {
            if(currentUser.getSubTrips().contains(tripKey)){
                ArrayList<String> subTrips = currentUser.getSubTrips();
                subTrips.remove(tripKey);
                ref1.child("subTrips").setValue(subTrips);
            }
        }

        Intent intent = new Intent(TripChatActivity.this,TabbedActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final String[] downloadURL = new String[1];
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    imageViewTemp= (ImageView) findViewById(R.id.ImageViewTemp);
                    Uri selectedImage = data.getData();
                    Log.d("Getdata:",selectedImage.toString());
                    imageViewTemp.setImageURI(selectedImage);

                    path= UUID.randomUUID().toString();
                    StorageReference storageRef = storage.getReference(path);

                    imageViewTemp.setDrawingCacheEnabled(true);
                    imageViewTemp.buildDrawingCache();
                    //Bitmap bitmap = Bitmap.createBitmap(imageViewTemp.getMeasuredWidth(), imageViewTemp.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                    Bitmap bitmap = imageViewTemp.getDrawingCache();
                    //Log.d("TEMP",bitmap.toString());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data2 = baos.toByteArray();


                    UploadTask uploadTask = storageRef.putBytes(data2);
                    Log.d("CURRENT USER1:",mAuth.getCurrentUser().toString());

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(this,new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d("CURRENT USER2:",mAuth.getCurrentUser().toString());
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Log.d("URL",downloadUrl.toString());

                            Message chatMessage = new Message();
                            countID+=1;
                            chatMessage.setId(countID);
                            chatMessage.setUserId(firebaseUser.getUid().toString());
                            chatMessage.setMessage(downloadUrl.toString());
                            chatMessage.setType(1);//0 text,1 image
                            chatMessage.setName(currentUser.getfName()+" "+currentUser.getlName());
                            chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                            Log.d("Count:","clicked");

                            messageET.setText("");
                            Log.d("Count:",String.valueOf(countID));

                            rootRef.child(String.valueOf(countID)).setValue(chatMessage);
                            displayMessage(chatMessage);
                            initControls();



                        }
                    });

                    Log.d("UPLOAD:","Successsful");



                }
                break;

        }
    }
}
