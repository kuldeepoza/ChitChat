package com.example.chitchat.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.chitchat.fragments.FragmentUserCallList;
import com.example.chitchat.fragments.FragmentUserMapChatList;
import com.example.chitchat.fragments.FragmentUserMapList;
import com.example.chitchat.fragments.FragmentUserRecyclerList;

/**
 * Created by KD on 23-Jun-17.
 */

public class UserPagerAdapter extends FragmentPagerAdapter {
    Context c;
    private static final int item=4;
    public UserPagerAdapter(FragmentManager fm, Context c) {
        super(fm);
        this.c=c;
    }
    @Override
    public Fragment getItem(int position) {
       if(position==0)
       {
           FragmentUserRecyclerList rec=new FragmentUserRecyclerList();
           return  rec;
       }
       else if(position==1)
       {
           FragmentUserMapList rec=new FragmentUserMapList();
           return  rec;
       }
       else if(position==2)
       {
           FragmentUserCallList rec=new FragmentUserCallList();
           return  rec;
       }
       else if(position==3)
       {
           FragmentUserMapChatList rec=new FragmentUserMapChatList();
           return  rec;
       }
        return null;
    }
    @Override
    public int getCount() {
        return item;
    }
    public CharSequence getPageTitle(int position)
    {
        switch(position)
        {
            case 0:
                return "List";
            case 1:
                return "Map";
            case 2:
                return "Call";
            case 3:
                return "History";
        }
        return  null;
    }
}
