package com.example.yatee.hw9_a;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class AddTripActivity extends AppCompatActivity {
    private ImageView tripCoverPhoto;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;
    FirebaseDatabase db;
    DatabaseReference rootRefTrip, rootRefUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        mAuth = FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        db = FirebaseDatabase.getInstance();
        rootRefTrip = db.getReference("Trips");
        rootRefUser = db.getReference("Users");

        tripCoverPhoto = (ImageView) findViewById(R.id.tripCoverPhoto);
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fabTripAdded);
        final EditText mTripTitle = (EditText) findViewById(R.id.tripTitleTV);
        final EditText mTripLocation = (EditText) findViewById(R.id.tripLocationTV);

        tripCoverPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 2);//one can be replaced with any action code
            }
        });


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog=new ProgressDialog(AddTripActivity.this);
                progressDialog.setMessage("Loading..");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                final String key = rootRefTrip.push().getKey();
                StorageReference storageRef = storage.getReference(key);

                tripCoverPhoto.setDrawingCacheEnabled(true);
                tripCoverPhoto.buildDrawingCache();
                Bitmap bitmap = tripCoverPhoto.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data2 = baos.toByteArray();


                UploadTask uploadTask = storageRef.putBytes(data2);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Trip trip1 = new Trip(mTripTitle.getText().toString(),mTripLocation.getText().toString(),downloadUrl.toString(),key);
                        rootRefTrip.child(key).setValue(trip1);

                        rootRefUser.child(mAuth.getCurrentUser().getUid()).child("myTrips").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                                ArrayList<String> trips = new ArrayList<String>();
                                if(dataSnapshot != null) {
                                    trips = dataSnapshot.getValue(t);
                                }
                                Log.d("ARRAYLIST", key);
                                if(trips == null)
                                    trips = new ArrayList<String>();

                                trips.add(key);
                                rootRefUser.child(mAuth.getCurrentUser().getUid()).child("myTrips").setValue(trips);
                                progressDialog.dismiss();

                                Intent intent = new Intent(AddTripActivity.this,TripChatActivity.class);
                                intent.putExtra("KEY",key);
                                startActivity(intent);
                                finish();
                                Toast.makeText(AddTripActivity.this,"Trip Created", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                progressDialog.dismiss();
                                Toast.makeText(AddTripActivity.this,"Failed to add trip to your database", Toast.LENGTH_SHORT).show();
                            }
                        });
//
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddTripActivity.this,"Failed to Upload image to database", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2) {

            if(resultCode == RESULT_OK){
                Uri selectedImage = data.getData();
                tripCoverPhoto.setImageURI(selectedImage);
            }
        }

    }
}
