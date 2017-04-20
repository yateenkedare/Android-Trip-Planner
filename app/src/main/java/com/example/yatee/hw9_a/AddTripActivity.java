package com.example.yatee.hw9_a;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class AddTripActivity extends AppCompatActivity {
    private ImageView tripCoverPhoto;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        mAuth = FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();

        tripCoverPhoto = (ImageView) findViewById(R.id.tripCoverPhoto);
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fabTripAdded);
        EditText mTripTitle = (EditText) findViewById(R.id.tripTitleTV);
        EditText mTripLocation = (EditText) findViewById(R.id.tripLocationTV);

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
                //TODO - add trip to database with photo tripTitle tripLocation and chat thread
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

//                String path="tripCoverPhoto.png";
//                StorageReference storageRef = storage.getReference(path);
//
//                tripCoverPhoto.setDrawingCacheEnabled(true);
//                tripCoverPhoto.buildDrawingCache();
//                Bitmap bitmap = tripCoverPhoto.getDrawingCache();
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                byte[] data2 = baos.toByteArray();
//
//
//                UploadTask uploadTask = storageRef.putBytes(data2);
//                Log.d("CURRENT USER1:",mAuth.getCurrentUser().toString());
//
//                uploadTask.addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        // Handle unsuccessful uploads
//                    }
//                }).addOnSuccessListener(this,new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Log.d("CURRENT USER2:",mAuth.getCurrentUser().toString());
//                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//                        @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
//                        Log.d("URL",downloadUrl.toString());
//                        rootRef.child(firebaseUser.getUid()).child("photoURL").setValue(downloadUrl.toString());
//                        Picasso.with(AddTripActivity.this)
//                                .load(downloadUrl.toString())
//                                .into(tripCoverPhoto);
//
//
//                    }
//                });

                Log.d("UPLOAD:","Successsful");
            }
        }

    }
}
