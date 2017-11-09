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

public class UserProfile extends AppCompatActivity{
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
    Button btnN,btnE,btnM;
    EditText e1,e2,e3;
    LinearLayout nameEdit,emailEdit,noEdit;
    String userMobile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        info=Util.isInternetConnection(UserProfile.this);
        tb = (Toolbar) findViewById(R.id.toolbar);
        profile = (ImageView) findViewById(R.id.ivUserDetail);
        back = (ImageView) findViewById(R.id.ivUserProfBack);
        name = (TextView) findViewById(R.id.tvUserDeatilName);
        email = (TextView) findViewById(R.id.tvUserDetailEmail);
        mo = (TextView) findViewById(R.id.tvUserDetailMo);
        e1 = (EditText) findViewById(R.id.etUserEditName);
        e2 = (EditText) findViewById(R.id.etUserEditEmail);
        e3 = (EditText) findViewById(R.id.etUserEditMobile);
        btnN = (Button) findViewById(R.id.btnUSerEditName);
        btnE = (Button) findViewById(R.id.btnUSerEditEmail);
        btnM = (Button) findViewById(R.id.btnUserEditMob);
        nameEdit = (LinearLayout) findViewById(R.id.llEditUserName);
        emailEdit = (LinearLayout) findViewById(R.id.llEditUserEmail);
        noEdit = (LinearLayout) findViewById(R.id.llEditUserMobile);
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
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                name.setVisibility(View.GONE);
                nameEdit.setVisibility(View.VISIBLE);
                }
            });

            email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    email.setVisibility(View.GONE);
                    emailEdit.setVisibility(View.VISIBLE);
                }
            });
            mo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mo.setVisibility(View.GONE);
                    noEdit.setVisibility(View.VISIBLE);
                }
            });
            btnN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Util.validationEditText(e1.getText().toString())) {
                        e1.setError("Please enter Name");
                    }
                    else {
                        Constants.databaseReference.child("users").child(Constants.auth.getCurrentUser().getUid()).child("profile").child("name").setValue(e1.getText().toString());
                        nameEdit.setVisibility(View.GONE);
                        name.setVisibility(View.VISIBLE);
                    }
                }
            });

            btnE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Util.validationEditText(e2.getText().toString())) {
                        e2.setError("Please enter Email");
                    }
                    else {
                      Constants.auth.getCurrentUser().updateEmail(e2.getText().toString())
                              .addOnCompleteListener(new OnCompleteListener<Void>() {
                          @Override
                          public void onComplete(Task<Void> task) {
                              if(task.isComplete()) {
                                  Constants.databaseReference.child("users").child(Constants.auth.getCurrentUser().getUid()).child("profile").child("email").setValue(e2.getText().toString());
                              }
                              else
                              {
                                  Toast.makeText(c, "Some Error in update Email", Toast.LENGTH_SHORT).show();
                              }
                          }
                      });
                        emailEdit.setVisibility(View.GONE);
                        email.setVisibility(View.VISIBLE);
                    }

                }
            });
            btnM.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Util.validationEditText(e3.getText().toString())) {
                        e3.setError("Please enter Mobile No");
                    }
                    else {
                        Constants.databaseReference.child("users").child(Constants.auth.getCurrentUser().getUid()).child("profile").child("mobile_no").setValue(e3.getText().toString());
                        noEdit.setVisibility(View.GONE);
                        mo.setVisibility(View.VISIBLE);
                    }

                }
            });
            myRef.child(user.getUid()).child("profile").addValueEventListener(new ValueEventListener() {
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
                        Intent intent = new Intent(UserProfile.this, UserProfEditActivity.class);
                        intent.putExtra("profile_url", profile_url);
                        intent.putExtra("user_name", userName);
                        startActivity(intent);
                    } else {
                        Toast.makeText(UserProfile.this, Constants.Image_null, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
            {
                Toast.makeText(UserProfile.this, getString(R.string.intConnection), Toast.LENGTH_LONG).show();
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
