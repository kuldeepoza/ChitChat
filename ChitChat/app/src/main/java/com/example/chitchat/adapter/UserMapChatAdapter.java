package com.example.chitchat.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chitchat.R;
import com.example.chitchat.activities.ChatActivity;
import com.example.chitchat.activities.MapChatActivity;
import com.example.chitchat.models.ChatBean;

import java.util.List;

/**
 * Created by KD on 11-Jul-17.
 */

public class UserMapChatAdapter extends RecyclerView.Adapter<UserMapChatAdapter.ListHandler> {
        Context c;
    List<ChatBean> list;
        public UserMapChatAdapter(Context c,List<ChatBean> list)
        {
        this.c=c;
        this.list=list;
        }
@Override
public ListHandler onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_raw_mapchat_history,parent,false);
        return new ListHandler(v);
        }
@Override
public void onBindViewHolder(final ListHandler holder, final int position) {
    final ChatBean b=list.get(position);
        holder.name.setText(b.getName());
        holder.time.setText(b.getTime());
        Glide
        .with(c)
        .load(b.getProfile_url())
        .into(holder.profile);
    holder.cv.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i=new Intent(c, MapChatActivity.class);
            i.putExtra("key",b.getSender_id());
            c.startActivity(i);
        }
    });
}
@Override
public int getItemCount() {
        return list.size();
        }
public class ListHandler extends RecyclerView.ViewHolder{
    TextView name,time;
    ImageView profile;
    CardView cv;

    public ListHandler(View itemView) {
        super(itemView);
        cv=(CardView)itemView.findViewById(R.id.cvChatMap);
        name=(TextView)itemView.findViewById(R.id.tvChatNameHis);
        time=(TextView) itemView.findViewById(R.id.tvChatTimeHis);
        profile=(ImageView) itemView.findViewById(R.id.civChatHis);
    }
}

}
