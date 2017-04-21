package com.example.yatee.hw9_a;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    static GoogleApiClient mGoogleApiClient;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private EditText editTextEmail , editTextFname, editTextLname;
    private EditText editTextPassword, editTextConfirmPassword;
    private Button btnSignUp, btnCancel;
    String name;
    FirebaseDatabase db;
    DatabaseReference rootRef;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        editTextEmail =(EditText)findViewById(R.id.et_emailSignUp);
        editTextPassword =(EditText)findViewById(R.id.et_passwordSignUp);
        editTextFname =(EditText)findViewById(R.id.et_fname_signup);
        editTextLname=(EditText)findViewById(R.id.et_lname_signup);
        editTextConfirmPassword =(EditText)findViewById(R.id.et_confirmpasswordSignUp);
        btnSignUp =(Button) findViewById(R.id.btn_signUp);
        btnCancel =(Button) findViewById(R.id.btn_cancel);


        db = FirebaseDatabase.getInstance();
        rootRef = db.getReference("Users");

        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id_string))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        findViewById(R.id.SignIn).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name).build();
                    user.updateProfile(profileUpdates);

                } else {
                    // User is signed out
                    Log.d("demo", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);



        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String fName = editTextFname.getText().toString().trim();
                final String lName = editTextLname.getText().toString().trim();
                final String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                name=fName+" "+lName;
                String confirmPassword = editTextConfirmPassword.getText().toString().trim();

                if(!(fName.equals("") || lName.equals("") || email.equals("") || password.equals("")) && password.equals(confirmPassword) ){
                    progressDialog.show();
                    mAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(SignupActivity.this, "User has been created", Toast.LENGTH_SHORT).show();
                                        FirebaseAuth temp=FirebaseAuth.getInstance();
                                        FirebaseUser firebaseUser=temp.getCurrentUser();
                                        User user=new User(fName,lName,"tempUrL","male");
                                        user.setId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        rootRef.child(firebaseUser.getUid()).setValue(user);
                                        progressDialog.dismiss();
                                        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                                        finish();

                                    }
                                }
                            }).addOnFailureListener(SignupActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignupActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Toast.makeText(SignupActivity.this, "Enter correct details", Toast.LENGTH_SHORT).show();
                }


            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();

            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.SignIn:
                progressDialog.show();
                signIn();
                break;


        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
        super.onStop();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void signIn(){
        Intent intent= Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                Log.d("ACTIVITYRESULT:","Success");
                firebaseAuthWithGoogle(account);
                //handleSignInResult(result);
            } else {
                progressDialog.dismiss();
                Log.d("ACTIVITYRESULT:","Fail"+ requestCode +"Result Code: "+resultCode);
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }


    }

    public void signOut(){
        mAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {

                Log.d("SIGNOUT:","SignedOut");
            }
        });

    }

    private void handleSignInResult(GoogleSignInResult result){
        Log.d("STATUS:",String.valueOf(result.isSuccess()));
        GoogleSignInAccount account=result.getSignInAccount();
        Log.d("SIGNIN:",account.getDisplayName());
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Demo", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Demo", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("Demo", "signInWithCredential", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String name = user.getDisplayName();
                            String email = user.getEmail();
                            Uri photoUrl = user.getPhotoUrl();
                            for (UserInfo userInfo : user.getProviderData()) {
                                if (name == null && userInfo.getDisplayName() != null) {
                                    name = userInfo.getDisplayName();
                                }
                            }
                            String[] names=name.split(" ");
                            String fName=names[0];
                            String lName=names[names.length-1];
                            Log.d("Demo:", "Name: "+fName+" "+lName+" email: "+email+" Photo:"+photoUrl.toString());
                            User thisuser=new User(fName,lName,photoUrl.toString(),"male");
                            thisuser.setId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            rootRef.child(user.getUid()).setValue(thisuser);
                            progressDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(),TabbedActivity.class));
                            finish();
                        }
                        // ...
                    }
                });
    }

}
