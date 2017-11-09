package com.example.chitchat.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chitchat.R;
import com.example.chitchat.adapter.UserPagerAdapter;
import com.example.chitchat.models.UserBean;
import com.example.chitchat.receiver.FCMTokenRefreshListenerService;
import com.example.chitchat.utils.Constants;
import com.example.chitchat.utils.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by KD on 23-Jun-17.
 */
public class UsersActivity extends AppCompatActivity{
    ViewPager vp;
    TabLayout tl;
    ImageView logout,profile,profileRedirect;
    TextView name;
    Toolbar t;
    String prof_url;
    ArrayList<UserBean>  array;
    DatabaseReference myRef;
    FirebaseUser user;
    LocationManager locationManager;
    TextView tvOnline;
    NetworkInfo info;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        info=Util.isInternetConnection(UsersActivity.this);
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
            t = (Toolbar) findViewById(R.id.userToolbar);
            setSupportActionBar(t);
            logout = (ImageView) findViewById(R.id.userLogout);
            profile = (ImageView) findViewById(R.id.userHeaderprofile);
            name = (TextView) findViewById(R.id.userHeaderName);
            tvOnline=(TextView)findViewById(R.id.online);
            profileRedirect = (ImageView) findViewById(R.id.userProfileRedirect);
            array = new ArrayList<>();
            vp = (ViewPager) findViewById(R.id.userViewpager);
            tl = (TabLayout) findViewById(R.id.usersTab);
            user = FirebaseAuth.getInstance().getCurrentUser();
            myRef = Constants.databaseReference.child("users/");
        addPermission();

            checkBroadcastToken();
            myRef.child(user.getUid()).child("profile").child("profile_url")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            prof_url = dataSnapshot.getValue().toString();
                            if (prof_url != null) {
                                Glide
                                        .with(UsersActivity.this)
                                        .load(prof_url)
                                        .into(profile);
                            } else {
                                Toast.makeText(UsersActivity.this, Constants.Image_null, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            profileRedirect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(UsersActivity.this, UserProfile.class));
                }
            });
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Constants.auth.signOut();
                    startActivity(new Intent(UsersActivity.this, LoginActivity.class));
                    finish();
                }
            });
            setViewPagerTab();


    }

    private void checkBroadcastToken() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(FCMTokenRefreshListenerService.REGISTRATION_SUCCESS)){
                    String token = intent.getStringExtra("token");
                    Log.w("+@$---#$^^^^^*&", "token:" + token);
                } else if(intent.getAction().equals(FCMTokenRefreshListenerService.REGISTRATION_ERROR)){
                    Toast.makeText(getApplicationContext(), "GCM registration error!", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
                }
            }
        };
        Intent itent = new Intent(this, FCMTokenRefreshListenerService.class);
        startService(itent);
    }

    private void addPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UsersActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 0);
        }
    }
    private void setViewPagerTab() {
        vp.setAdapter(new UserPagerAdapter(getSupportFragmentManager(), getApplicationContext()));
        tl.setupWithViewPager(vp);
    }
    @Override
    public void onResume() {
        super.onResume();
        if(info!=null) {
            myRef.child(user.getUid()).child("profile").child("online").setValue("true");
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(FCMTokenRefreshListenerService.REGISTRATION_SUCCESS));
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(FCMTokenRefreshListenerService.REGISTRATION_ERROR));
        }
        else {
        Toast.makeText(UsersActivity.this, getString(R.string.intConnection)+" or GPS connection", Toast.LENGTH_LONG).show();
    }
    }
    @Override
    public void onStop() {
        super.onStop();
        if(info!=null) {
            myRef.child(user.getUid()).child("profile").child("online").setValue("false");
        }
    }


}

