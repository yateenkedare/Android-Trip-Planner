package com.example.yatee.hw9_a;

import android.content.Intent;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    long count;
    //New look variables
    private EditText messageET;
    private ListView messagesContainer;
    private ImageView sendBtn;
    private ImageView sendImage;
    private ChatAdapter adapter;
    private ArrayList<Message> chatHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_chat);
        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance();
        tripKey = getIntent().getStringExtra("KEY");
        rootRef=db.getReference("Chats");
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
                final User currentUser=dataSnapshot.getValue(User.class);
                rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
                        messageET = (EditText) findViewById(R.id.messageEdit);
                        sendBtn = (ImageView) findViewById(R.id.chatSendButton);
                        sendImage=(ImageView) findViewById(R.id.imageUpload);

                        TextView meLabel = (TextView) findViewById(R.id.meLbl);
                        TextView companionLabel = (TextView) findViewById(R.id.friendLabel);
                        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
                        companionLabel.setText("Others");// Hard Coded
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
                                chatMessage.setUserId(firebaseUser.getUid().toString());
                                chatMessage.setMessage(messageText);
                                chatMessage.setName(currentUser.getfName()+" "+currentUser.getlName());
                                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));

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
        chatHistory = new ArrayList<Message>();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    chatHistory.add(snapshot.getValue(Message.class));
                    adapter = new ChatAdapter(TripChatActivity.this, new ArrayList<Message>());
                    messagesContainer.setAdapter(adapter);

                    for(int i=0; i<chatHistory.size(); i++) {
                        Message message = chatHistory.get(i);
                        displayMessage(message);
                    }
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
                Intent intent = new Intent(TripChatActivity.this,LoginActivity.class);
                intent.putExtra("KEY",tripKey);
                startActivity(intent);
                return true;
            case R.id.leaveChatRoom:
                //TODO - delete trip from sub trips or my trips
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


}
