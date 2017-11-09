package com.example.chitchat.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chitchat.R;
import com.example.chitchat.activities.UsersActivity;
import com.example.chitchat.adapter.UserCallAdapter;
import com.example.chitchat.adapter.UserListAdapter;
import com.example.chitchat.models.CallBean;
import com.example.chitchat.models.ChannelBean;
import com.example.chitchat.models.ChatBean;
import com.example.chitchat.models.UserBean;
import com.example.chitchat.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by KD on 04-Jul-17.
 */

public class FragmentUserCallList  extends Fragment {
    RecyclerView rv;
    UserCallAdapter adapter;
    ArrayList<CallBean> callArray;
    CallBean bean;
    DatabaseReference myRef;
    View view;
    FirebaseUser user;
    DatabaseReference databaseReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_call_list, container, false);
        rv=(RecyclerView)view.findViewById(R.id.rvUserCall);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        callArray=new ArrayList<>();
        user= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        myRef = databaseReference.child("users/");
        myRef.child(user.getUid()).child("call").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                callArray.clear();
                for (com.google.firebase.database.DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                bean = new CallBean();
                                bean=singleSnapshot.getValue(CallBean.class);
                                bean.setName(bean.getName());
                                bean.setProfile_url(bean.getProfile_url());
                                bean.setTime(bean.getTime());
                                bean.setOnline(bean.getOnline());
                                bean.setMobile_no(bean.getMobile_no());
                                callArray.add(bean);
                }
                adapter = new UserCallAdapter(getContext(), callArray);
                rv.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }
}
