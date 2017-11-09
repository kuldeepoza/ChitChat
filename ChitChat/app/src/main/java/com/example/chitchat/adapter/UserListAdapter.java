package com.example.chitchat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.chitchat.R;
import com.example.chitchat.activities.ChatActivity;
import com.example.chitchat.activities.UsersActivity;
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
import java.util.Date;
import java.util.List;

/**
 * Created by KD on 24-Jun-17.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ListHandler> {
    Context c;
    List<UserBean> list;
    ArrayList<String> listKey;
    public UserListAdapter(Context c, List<UserBean> list,ArrayList<String> listKey)
    {
        this.c=c;
        this.list=list;
        this.listKey=listKey;
    }
    @Override
    public ListHandler onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_raw_users,parent,false);
        return new ListHandler(v);
    }
    @Override
    public void onBindViewHolder(final ListHandler holder, final int position) {
            UserBean b=list.get(position);
            holder.name.setText(b.getName());
            holder.status.setText(b.getEmail());
            holder.time.setText(b.getDate());
        holder.pb.setVisibility(View.VISIBLE);
                       if(b.getOnline().equals("true") )
                        {
                            holder.online.setTextColor(c.getColor(R.color.green_500));
                        }
                        else
                        {
                            holder.online.setTextColor(c.getColor(R.color.red_500));
                        }

            Glide
                    .with(c)
                    .load(b.profile_url)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.pb.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.pb.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(holder.profile);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  String key=listKey.get(position);
                chattingChannel(key);
            }
        });
    }
    private void chattingChannel(String key) {
              Intent i=new Intent(c, ChatActivity.class);
                i.putExtra("key",key);
               c.startActivity(i);
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ListHandler extends RecyclerView.ViewHolder{
TextView name,status,time,online;
        ImageView profile;
        ProgressBar pb;

        public ListHandler(View itemView) {

            super(itemView);
            pb=(ProgressBar)itemView.findViewById(R.id.pbUserProfile);
            name=(TextView)itemView.findViewById(R.id.userName);
            status=(TextView)itemView.findViewById(R.id.userEmail);
            time=(TextView) itemView.findViewById(R.id.userTime);
            online=(TextView) itemView.findViewById(R.id.online);
            profile=(ImageView) itemView.findViewById(R.id.userProfile);
        }
    }

}
