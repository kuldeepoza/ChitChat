package com.example.chitchat.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chitchat.R;
import com.example.chitchat.adapter.UserCallAdapter;
import com.example.chitchat.adapter.UserMapChatAdapter;
import com.example.chitchat.models.CallBean;
import com.example.chitchat.models.ChatBean;
import com.example.chitchat.models.UserBean;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by KD on 11-Jul-17.
 */

public class FragmentUserMapChatList extends Fragment {
    RecyclerView rv;
    UserMapChatAdapter adapter;
    ArrayList<ChatBean> callArray;
    ChatBean bean;
    DatabaseReference myRef;
    FirebaseUser user;
    DatabaseReference databaseReference;
    RecyclerView.LayoutManager mLayoutManager;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.fragment_user_mapchat_list, container, false);
        rv=(RecyclerView)view.findViewById(R.id.rvChatHistory);
         mLayoutManager = new GridLayoutManager(getContext(), 3);
        rv.setLayoutManager(mLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        callArray=new ArrayList<>();
        user= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        myRef = databaseReference.child("chatting/");
        myRef.child("map").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                callArray.clear();
                int m;
                for (com.google.firebase.database.DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                    m=0;
                    for (com.google.firebase.database.DataSnapshot doubleSnapShot : singleSnapshot.getChildren()) {
                        if(m==0) {
                            bean = new ChatBean();
                            bean = doubleSnapShot.getValue(ChatBean.class);
                            bean.setName(bean.getName());
                            bean.setSender_id(bean.getSender_id());
                            bean.setProfile_url(bean.getProfile_url());
                            bean.setTime(bean.getTime());
                            if(singleSnapshot.getKey().equals(user.getUid()+"_"+bean.getSender_id())) {
                                callArray.add(bean);
                            }
                            m=1;
                        }
                    }
                }
                adapter = new UserMapChatAdapter(getContext(), callArray);
                rv.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return view;

    }

}
