package com.example.chitchat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.chitchat.R;
import com.example.chitchat.activities.ChatActivity;
import com.example.chitchat.activities.UserProfEditActivity;
import com.example.chitchat.models.ChatBean;
import com.example.chitchat.models.UserBean;
import com.example.chitchat.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KD on 26-Jun-17.
 */
public class UserMessageAdapter extends RecyclerView.Adapter<UserMessageAdapter.ListHandler> {
    Context c;
    List<ChatBean> list;
    View v;
    public UserMessageAdapter(Context c,List<ChatBean> list)
    {
        this.c=c;
        this.list=list;
    }
    @Override
    public ListHandler onCreateViewHolder(ViewGroup parent, int viewType) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_raw_message, parent, false);
        return new UserMessageAdapter.ListHandler(v);
    }
    @Override
    public void onBindViewHolder(final UserMessageAdapter.ListHandler holder, final int position) {
         final ChatBean bean=list.get(position);
        if(bean.getType().equals("left")) {
            if(bean.getMessage()!=null) {
                holder.left.setText(bean.getMessage());
                holder.timeL.setText(bean.getTime());
                holder.layoutLeft.setVisibility(View.VISIBLE);
                holder.layoutRight.setVisibility(View.GONE);
               holder.layoutLeftImage.setVisibility(View.GONE);
                holder.layoutRightImage.setVisibility(View.GONE);
            }
            else {
                holder.pbLeft.setVisibility(View.VISIBLE);
                holder.layoutLeftImage.setVisibility(View.VISIBLE);
                Glide
                        .with(c)
                        .load(bean.getProfile_url())
                       .listener(new RequestListener<Drawable>() {
                           @Override
                           public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                               holder.pbLeft.setVisibility(View.GONE);
                               return false;
                           }
                           @Override
                           public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                               holder.pbLeft.setVisibility(View.GONE);
                               return false;
                           }
                       })
                        .into(holder.ivLeft);
                holder.timeLImage.setText(bean.getTime());
                holder.layoutRightImage.setVisibility(View.GONE);
                holder.layoutLeft.setVisibility(View.GONE);
                holder.layoutRight.setVisibility(View.GONE);
            }
        }
        else
        {
            if(bean.getMessage()!=null) {
                holder.right.setText(bean.getMessage());
                holder.timeR.setText(bean.getTime());
                holder.layoutRight.setVisibility(View.VISIBLE);
                holder.layoutLeft.setVisibility(View.GONE);
               holder.layoutRightImage.setVisibility(View.GONE);
                holder.layoutLeftImage.setVisibility(View.GONE);
            }
            else {
                holder.layoutRightImage.setVisibility(View.VISIBLE);
                holder.pbRight.setVisibility(View.VISIBLE);

                Glide
                        .with(c)
                        .load(bean.getProfile_url())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                holder.pbRight.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                holder.pbRight.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(holder.ivRight);
                holder.timeRImage.setText(bean.getTime());
                holder.layoutLeftImage.setVisibility(View.GONE);
                holder.layoutRight.setVisibility(View.GONE);
                holder.layoutLeft.setVisibility(View.GONE);
            }
        }
        holder.ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bean.getProfile_url() != null) {
                    Intent intent = new Intent(c, UserProfEditActivity.class);
                    intent.putExtra("profile_url", bean.getProfile_url());
                    c.startActivity(intent);
                } else {
                    Toast.makeText(c, Constants.Image_null, Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bean.getProfile_url() != null) {
                    Intent intent = new Intent(c, UserProfEditActivity.class);
                    intent.putExtra("profile_url", bean.getProfile_url());
                    c.startActivity(intent);
                } else {
                    Toast.makeText(c, Constants.Image_null, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    public class ListHandler extends RecyclerView.ViewHolder{
        TextView left,right,timeL,timeR,timeLImage,timeRImage;
        ProgressBar pbLeft,pbRight;
        ImageView ivLeft,ivRight;
        LinearLayout layoutLeft,layoutRight,layoutLeftImage,layoutRightImage;
        public ListHandler(View itemView) {
            super(itemView);
            pbLeft=(ProgressBar)itemView.findViewById(R.id.pbChatLeft);
            pbRight=(ProgressBar)itemView.findViewById(R.id.pbChatRight);
            ivLeft=(ImageView)itemView.findViewById(R.id.ivChatLeft);
            ivRight=(ImageView)itemView.findViewById(R.id.ivChatRight);
            layoutLeftImage=(LinearLayout)itemView.findViewById(R.id.llLeftImage);
            layoutRightImage=(LinearLayout)itemView.findViewById(R.id.llRightImage);
            timeLImage=(TextView)itemView.findViewById(R.id.tvLefttymImage);
            timeRImage=(TextView)itemView.findViewById(R.id.tvRighttymImage);

            left=(TextView)itemView.findViewById(R.id.tvLeft);
            right=(TextView)itemView.findViewById(R.id.tvRight);
            timeL=(TextView)itemView.findViewById(R.id.tvLefttym);
            timeR=(TextView)itemView.findViewById(R.id.tvRighttym);
            layoutLeft=(LinearLayout)itemView.findViewById(R.id.llLeft);
            layoutRight=(LinearLayout)itemView.findViewById(R.id.llRight);


        }
    }}
