package com.example.chitchat.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chitchat.R;
import com.example.chitchat.activities.ChatActivity;
import com.example.chitchat.models.CallBean;
import com.example.chitchat.models.UserBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KD on 04-Jul-17.
 */

public class UserCallAdapter extends RecyclerView.Adapter<UserCallAdapter.ListHandler> {
    Context c;
    List<CallBean> list;
Intent intent;
    public UserCallAdapter(Context c, List<CallBean> list) {
        this.c = c;
        this.list = list;
    }
    @Override
    public ListHandler onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_raw_call, parent, false);
        return new UserCallAdapter.ListHandler(v);
    }
    @Override
    public void onBindViewHolder(final UserCallAdapter.ListHandler holder, final int position) {
        final CallBean b = list.get(position);
        holder.name.setText(b.getName());
        holder.time.setText(b.getTime());
        if (b.getOnline().equals("true")) {
            holder.online.setTextColor(c.getColor(R.color.green_500));
        } else {
            holder.online.setTextColor(c.getColor(R.color.red_500));
        }
        Glide
                .with(c)
                .load(b.profile_url)
                .into(holder.profile);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:"+b.getMobile_no()));
                c.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ListHandler extends RecyclerView.ViewHolder {
        TextView name,time,online;
        ImageView profile;
        public ListHandler(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tvUserCallName);
            time = (TextView) itemView.findViewById(R.id.tvUserCallTime);
            online = (TextView) itemView.findViewById(R.id.tvOnlineCall);
            profile = (ImageView) itemView.findViewById(R.id.civUserCallProfile);
        }
    }
}