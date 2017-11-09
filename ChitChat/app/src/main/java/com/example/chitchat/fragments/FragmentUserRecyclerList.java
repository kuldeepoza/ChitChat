package com.example.chitchat.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chitchat.R;
import com.example.chitchat.adapter.UserListAdapter;
import com.example.chitchat.models.UserBean;
import com.example.chitchat.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentUserRecyclerList extends Fragment {
    RecyclerView rv;
    UserListAdapter adapter;
    ArrayList<UserBean> array;
    UserBean bean;
    ArrayList<String> listKey;
    DatabaseReference myRef;
    View view;
    FirebaseUser user;
    Cursor phones;
    DatabaseReference databaseReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_user_recycler_list, container, false);
        rv=(RecyclerView)view.findViewById(R.id.userListRv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        array=new ArrayList<>();
        listKey=new ArrayList<>();
        user=FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        myRef = databaseReference.child("users/");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                array.clear();
                listKey.clear();
                for (com.google.firebase.database.DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    String key = singleSnapshot.getKey();
                    if (key.equals(user.getUid())) {
                    } else {
                        listKey.add(key);
                        for (com.google.firebase.database.DataSnapshot doubleSnapshot : singleSnapshot.getChildren()) {
                          if(doubleSnapshot.getKey().equals("profile")) {
                              if(getActivity()!=null) {
                                  phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                                  bean = new UserBean();
                                  bean = doubleSnapshot.getValue(UserBean.class);
                                  bean.setName(bean.getName());
                                  bean.setEmail(bean.getEmail());
                                  bean.setMobile_no(bean.getMobile_no());
                                  bean.setProfile_url(bean.getProfile_url());
                                  bean.setDate(bean.getDate());
                                  bean.setOnline(bean.getOnline());
                                  array.add(bean);
                                  while (phones.moveToNext()) {
                                      //    String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                      String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                      if (bean.getMobile_no().equals(phoneNumber)) {

                                          break;
                                      }
                                  }
                              }

                              phones.close();
                          }
                        }
                    }
                }
                adapter = new UserListAdapter(getContext(), array, listKey);
                rv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }




}

