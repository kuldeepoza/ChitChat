package com.example.chitchat.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chitchat.R;
import com.example.chitchat.adapter.UserListAdapter;
import com.example.chitchat.models.UserBean;
import com.example.chitchat.utils.Constants;
import com.example.chitchat.utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KD on 24-Jun-17.
 */

public class OtherUserProfile extends AppCompatActivity{
    Toolbar tb;
    TextView name,email,mo;
    ImageView profile,back;
    String profile_url,userName;
    UserBean bean;
    DatabaseReference myRef;
    FirebaseUser user;
    CollapsingToolbarLayout collapsingToolbarLayout;
    NetworkInfo info;
    Context c;
    Bundle bundle;
    String key;
    String userMobile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);
        info=Util.isInternetConnection(OtherUserProfile .this);
        tb = (Toolbar) findViewById(R.id.toolbarOther);
        profile = (ImageView) findViewById(R.id.ivUserDetailOther);
        back = (ImageView) findViewById(R.id.ivUserProfBackOther);
        name = (TextView) findViewById(R.id.tvUserDeatilNameOther);
        email = (TextView) findViewById(R.id.tvUserDetailEmailOther);
        mo = (TextView) findViewById(R.id.tvUserDetailMoOther);
        bundle=getIntent().getExtras();
        key=bundle.getString("key");
        user = FirebaseAuth.getInstance().getCurrentUser();
        myRef = Constants.databaseReference.child("users/");
        if(info!=null) {
            tb.setTitleTextColor(getResources().getColor(R.color.white_1000));
            setSupportActionBar(tb);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            myRef.child(key).child("profile").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                    bean = new UserBean();
                    bean = dataSnapshot.getValue(UserBean.class);
                    name.setText(bean.getName());
                    email.setText(bean.getEmail());
                    mo.setText(bean.getMobile_no());
                    if(bean.getProfile_url()!=null) {
                        Glide
                                .with(getApplicationContext())
                                .load(bean.getProfile_url())
                                .into(profile);
                    }
                    profile_url=bean.getProfile_url();
                    userName=bean.getName();
                    userMobile = bean.getMobile_no();
                    setCollapsingProperty();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (profile_url != null) {
                        Intent intent = new Intent(OtherUserProfile .this, UserProfEditActivity.class);
                        intent.putExtra("profile_url", profile_url);
                        intent.putExtra("user_name", userName);
                        startActivity(intent);
                    } else {
                        Toast.makeText(OtherUserProfile .this, Constants.Image_null, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            Toast.makeText(OtherUserProfile .this, getString(R.string.intConnection), Toast.LENGTH_LONG).show();
        }
    }
    private void setCollapsingProperty() {
        collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.collspacing);
        collapsingToolbarLayout.setTitle(userName+"("+userMobile+")");
        c=this;
        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(c,R.color.colorPrimary));
        collapsingToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(c,android.R.color.white));
        collapsingToolbarLayout.setExpandedTitleTextColor(ColorStateList.valueOf(ContextCompat.getColor(c,android.R.color.white)));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
