package com.example.chitchat.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.chitchat.R;


public class OtherSignInActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_sign_in);
    }
}

/*
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.chitchat.R;
import com.example.chitchat.utils.Constants;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class OtherSignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient googleApiClient;
     CallbackManager callbackManager;
      LoginButton loginButton;
    GoogleSignInOptions googleSignInOptions;
    String strDate;
    FirebaseAuth auth;
    SignInButton signIn;
    AuthCredential credential,credentialFB;
    GoogleSignInResult result;
    Bundle parameter;
    Calendar c;
    SimpleDateFormat sdf;
    GraphRequest request;
    FirebaseAuth.AuthStateListener authStateListener;
    GoogleSignInAccount googleSignInAccount;
    FirebaseUser user,userFB;
    String moNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_sign_in);
        if (Constants.auth.getCurrentUser() != null) {
            startActivity(new Intent(OtherSignInActivity.this, UsersActivity.class));
        }
        c = Calendar.getInstance();
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        strDate = sdf.format(c.getTime());
        signIn = (SignInButton) findViewById(R.id.googleSignInBtn);
        loginButton = (LoginButton) findViewById(R.id.fbSignInBtn);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        auth = FirebaseAuth.getInstance();

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        signIn.setSize(SignInButton.SIZE_WIDE);
        signIn.setScopes(googleSignInOptions.getScopeArray());

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        loginButton.setReadPermissions("email", "public_profile");
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, 9001);
            }
        });
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            }
        };
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                Toast.makeText(OtherSignInActivity.this, loginResult.getAccessToken()+"", Toast.LENGTH_SHORT).show();
                handleFacebookAccessToken(loginResult.getAccessToken());
                request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                            handleFacebookAccessToken(loginResult.getAccessToken());
                    }
                });
                parameter = new Bundle();
                parameter.putString("fields", "id,name,email,picture");
                request.setParameters(parameter);
                request.executeAsync();
            }
            @Override
            public void onCancel() {
            }
            @Override
            public void onError(FacebookException error) {

            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 9001) {
            result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()) {
                googleSignInAccount = result.getSignInAccount();
                Toast.makeText(this, googleSignInAccount.getEmail()+ googleSignInAccount.getId()+"", Toast.LENGTH_SHORT).show();
              auth.createUserWithEmailAndPassword(googleSignInAccount.getEmail(), googleSignInAccount.getId());
                auth.signInWithEmailAndPassword(googleSignInAccount.getEmail(), googleSignInAccount.getId())
                        .addOnCompleteListener(OtherSignInActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(OtherSignInActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                } else {
                                    Constants.databaseReference.child("users").child(Constants.auth.getCurrentUser().getUid()).child("profile").child("name").setValue(googleSignInAccount.getDisplayName());
                                    Constants.databaseReference.child("users").child(Constants.auth.getCurrentUser().getUid()).child("profile").child("email").setValue(googleSignInAccount.getEmail());
                                    Constants.databaseReference.child("users").child(Constants.auth.getCurrentUser().getUid()).child("profile").child("password").setValue(Constants.auth.getCurrentUser().getUid());
                                    Constants.databaseReference.child("users").child(Constants.auth.getCurrentUser().getUid()).child("profile").child("profile_url").setValue(googleSignInAccount.getPhotoUrl().toString());
                                    Constants.databaseReference.child("users").child(Constants.auth.getCurrentUser().getUid()).child("profile").child("date").setValue(strDate);
                                    Constants.databaseReference.child("users").child(Constants.auth.getCurrentUser().getUid()).child("profile").child("online").setValue("true");
                                    startActivity(new Intent(OtherSignInActivity.this, UserMobileNo.class).putExtra("type","google"));
                                }
                            }
                        });

            }
            else
            {
                Toast.makeText(this, "Not Success", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void handleFacebookAccessToken(AccessToken token) {
         credentialFB = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credentialFB)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            userFB = auth.getCurrentUser();
                            Constants.databaseReference.child("users").child(userFB.getUid()).child("profile").child("name").setValue(userFB.getDisplayName());
                            Constants.databaseReference.child("users").child(userFB.getUid()).child("profile").child("email").setValue(userFB.getEmail());
                            Constants.databaseReference.child("users").child(userFB.getUid()).child("profile").child("password").setValue(userFB.getUid());
                            Constants.databaseReference.child("users").child(userFB.getUid()).child("profile").child("profile_url").setValue(userFB.getPhotoUrl().toString());
                            Constants.databaseReference.child("users").child(userFB.getUid()).child("profile").child("date").setValue(strDate);
                            Constants.databaseReference.child("users").child(userFB.getUid()).child("profile").child("online").setValue("true");
                            startActivity(new Intent(OtherSignInActivity.this, UserMobileNo.class).putExtra("type", "facebook"));

                        } else {
                            Toast.makeText(OtherSignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener!=null){
            auth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        googleApiClient.disconnect();
    }
}
*/