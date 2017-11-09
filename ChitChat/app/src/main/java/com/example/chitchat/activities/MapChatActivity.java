package com.example.chitchat.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.chitchat.R;
import com.example.chitchat.adapter.UserMessageAdapter;
import com.example.chitchat.models.ChannelBean;
import com.example.chitchat.models.ChatBean;
import com.example.chitchat.models.UserBean;
import com.example.chitchat.utils.Constants;
import com.example.chitchat.utils.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by KD on 22-Jun-17.
 */

public class MapChatActivity extends Activity  {
    EditText message;
    CircleImageView send;
    RecyclerView rv;
    UserMessageAdapter adapter;
    String key, time, channel, otherChannel,downloadUrl;
    int hour, minute;
    ChatBean bean;
    List<ChatBean> array;
    List<ChannelBean> channelList;
    ChannelBean channelBean;
    String prof_url,picturePath;
    Toolbar t;
    TextView userName;
    Bitmap bitmap,bmp;
    Boolean condition = false;
    DatabaseReference userRef;
    CircleImageView userImage;
    ImageView call,attach,camera, back, userRed,popup;
    DatabaseReference channelRef, profileRef,callRef;
    Calendar cal;
    FirebaseUser user;
    UserBean beanUser;
    ProgressDialog dialog;
    Intent intent;
    NetworkInfo info;
    String n;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_chat);
        info = Util.isInternetConnection(MapChatActivity.this);
        popup = (ImageView) findViewById(R.id.popupImage);
        call = (ImageView) findViewById(R.id.ivChatCall);
        userName = (TextView) findViewById(R.id.tvChatName);
        userImage = (CircleImageView) findViewById(R.id.cimChat);
        userRed = (ImageView) findViewById(R.id.ivChatUser);
        camera = (ImageView) findViewById(R.id.ivChatCamera);
        attach = (ImageView) findViewById(R.id.ivChatAttach);
        back = (ImageView) findViewById(R.id.ivChatBack);
        message = (EditText) findViewById(R.id.etUserTypeMsg);
        send = (CircleImageView) findViewById(R.id.btnUserSendMsg);
        rv = (RecyclerView) findViewById(R.id.rvUserMsgList);
        t = (Toolbar) findViewById(R.id.userToolbar);
        setActionBar(t);
        user = FirebaseAuth.getInstance().getCurrentUser();
        rv.setLayoutManager(new LinearLayoutManager(this));
        Bundle b = getIntent().getExtras();
        key = b.getString("key");
        array = new ArrayList<>();
        dialog=new ProgressDialog(this);
        channelList = new ArrayList<>();
        channel = user.getUid() + "_" + key;
        otherChannel = key + "_" + user.getUid();
        cal = Calendar.getInstance();
        hour = cal.get(Calendar.HOUR);
        minute = cal.get(Calendar.MINUTE);
        time = String.valueOf(hour + ":" + minute);
        userRef = Constants.databaseReference.child("chatting/");
        isChannelAvailable();
        profileRef = Constants.databaseReference.child("users/");
        callRef = Constants.databaseReference.child("users/");
        if (ContextCompat.checkSelfPermission(MapChatActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapChatActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 0);
            return;
        }
        profileRef.child(key).child("profile").child("profile_url").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                prof_url = dataSnapshot.getValue().toString();
                if (prof_url != null) {
                    Glide
                            .with(MapChatActivity.this)
                            .load(prof_url)
                            .into(userImage);
                } else {
                    Toast.makeText(MapChatActivity.this, Constants.Image_null, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });
        profileRef.child(key).child("profile").child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                n = dataSnapshot.getValue().toString();
                userName.setText(n);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        userRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapChatActivity.this, OtherUserProfile.class).putExtra("key",key));
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callRef.child(key).child("profile")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                beanUser = new UserBean();
                                beanUser = dataSnapshot.getValue(UserBean.class);
                                String pushKey = Constants.databaseReference.child("users").child(user.getUid()).child("call").push().getKey();
                                Constants.databaseReference.child("users").child(Constants.auth.getCurrentUser().getUid()).child("call").child(pushKey).child("name").setValue(beanUser.getName());
                                Constants.databaseReference.child("users").child(Constants.auth.getCurrentUser().getUid()).child("call").child(pushKey).child("profile_url").setValue(beanUser.getProfile_url());
                                Constants.databaseReference.child("users").child(Constants.auth.getCurrentUser().getUid()).child("call").child(pushKey).child("time").setValue(time);
                                Constants.databaseReference.child("users").child(Constants.auth.getCurrentUser().getUid()).child("call").child(pushKey).child("online").setValue(beanUser.getOnline());
                                Constants.databaseReference.child("users").child(Constants.auth.getCurrentUser().getUid()).child("call").child(pushKey).child("mobile_no").setValue(beanUser.getMobile_no());
                                intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + beanUser.getMobile_no()));
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkChannel();
            }

            private void createChannel(Boolean con) {
                if (con) {
                    final String pushKey = Constants.databaseReference.child("chatting").child("map").child(otherChannel).push().getKey();
                    Constants.databaseReference.child("chatting").child("map").child(otherChannel).child(pushKey).child("message").setValue(message.getText().toString());
                    Constants.databaseReference.child("chatting").child("map").child(otherChannel).child(pushKey).child("sender_id").setValue(key);
                    Constants.databaseReference.child("chatting").child("map").child(otherChannel).child(pushKey).child("time").setValue(time);
                    Constants.databaseReference.child("chatting").child("map").child(otherChannel).child(pushKey).child("profile_url").setValue(prof_url);
                    Constants.databaseReference.child("chatting").child("map").child(otherChannel).child(pushKey).child("name").setValue(n);
                    callEvent(otherChannel);
                } else {
                    final String pushKey = Constants.databaseReference.child("chatting").child("map").child(channel).push().getKey();
                    Constants.databaseReference.child("chatting").child("map").child(channel).child(pushKey).child("message").setValue(message.getText().toString());
                    Constants.databaseReference.child("chatting").child("map").child(channel).child(pushKey).child("sender_id").setValue(key);
                    Constants.databaseReference.child("chatting").child("map").child(channel).child(pushKey).child("time").setValue(time);
                    Constants.databaseReference.child("chatting").child("map").child(channel).child(pushKey).child("profile_url").setValue(prof_url);
                    Constants.databaseReference.child("chatting").child("map").child(channel).child(pushKey).child("name").setValue(n);
                    callEvent(channel);
                }
            }

            public void checkChannel() {
                createChannel(condition);
                message.setText("");

            }
        });
    }

    private void callEvent(String ch) {
        userRef.child("map").child(ch).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                array.clear();
                cal = Calendar.getInstance();
                hour = cal.get(Calendar.HOUR);
                minute = cal.get(Calendar.MINUTE);
                String timeN = String.valueOf(hour + ":" + minute);
                for(com.google.firebase.database.DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    bean=new ChatBean();
                    bean = singleSnapshot.getValue(ChatBean.class);
                    if(bean.getMessage()!=null) {
                        bean.setMessage(bean.getMessage());
                    }
                    else {
                        bean.setProfile_url(bean.getProfile_url());
                    }

                    if(bean.getSender_id()==null)
                    {
                        bean.setSender_id(key);
                    }
                    else {
                        bean.setSender_id(bean.getSender_id());
                    }

                    if(bean.getSender_id().equals(user.getUid())) {
                        bean.setType("left");
                    }
                    else {
                        bean.setType("right");
                    }
                    if(bean.getTime()==null) {
                        bean.setTime(timeN);
                    }
                    else {
                        bean.setTime(bean.getTime());
                    }
                    array.add(bean);
                }
                adapter=new UserMessageAdapter(getApplicationContext(),array);
                rv.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void isChannelAvailable()
    {
        channelRef = Constants.databaseReference.child("chatting/");
        channelRef.child("map").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                channelList.clear();
                for (com.google.firebase.database.DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    channelBean = new ChannelBean();
                    String kk = singleSnapshot.getKey();
                    channelBean.setKey(kk);
                    channelList.add(channelBean);
                }
                for (ChannelBean bb : channelList
                        ) {
                    if (otherChannel.equals(bb.getKey())) {
                        condition = true;
                    }
                }
                if(condition)
                {
                    callEvent(otherChannel);

                }
                else {
                    callEvent(channel);

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void cameraClick(View v)
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE);
    }
    public void attachment(View v)
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Constants.SELECT_PICTURE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bitmap = null;
        picturePath = null;
        bmp = null;
        if (requestCode == Constants.SELECT_PICTURE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor c = this.getContentResolver().query(selectedImage, filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            picturePath = c.getString(columnIndex);
            c.close();
            try {
                bmp = getBitmapFromUri(selectedImage);
                imageStoreInFirebase(condition);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == RESULT_OK && requestCode == Constants.REQUEST_IMAGE_CAPTURE) {
            bmp = (Bitmap)data.getExtras().get("data");
            String partFilename = currentDateFormat();
            picturePath = Environment.getExternalStorageDirectory()+"/DCIM/Camera/photo_"+partFilename+".jpg";
            storeCameraPhotoInSDCard(bmp, partFilename);
            getImageFileFromSDCard(picturePath);
            imageStoreInFirebase(condition);
        }
    }
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =this.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;

    }
    private String currentDateFormat(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String  currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }
    private void storeCameraPhotoInSDCard(Bitmap bitmap, String currentDate){
        File outputFile = new File(Environment.getExternalStorageDirectory(), "/DCIM/Camera/photo_" + currentDate + ".jpg");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Bitmap getImageFileFromSDCard(String filename){
        Bitmap bitmap = null;
        File imageFile = new File(filename);
        try {
            FileInputStream fis = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    private void imageStoreInFirebase(final Boolean con) {
        dialog.setMessage(Constants.LOADING);
        dialog.show();
        FirebaseStorage storage=FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();
        Uri file = Uri.fromFile(new File(picturePath));
        StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file,metadata);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(MapChatActivity.this, "Fail", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                downloadUrl = taskSnapshot.getDownloadUrl().toString();
                if(!con)
                {
                    String push= Constants.databaseReference.child("chatting").child("map").child(channel).push().getKey();
                    Constants.databaseReference
                            .child("chatting").child("map")
                            .child(channel)
                            .child(push)
                            .child("profile_url")
                            .setValue(downloadUrl);
                    Constants.databaseReference
                            .child("chatting").child("map")
                            .child(channel)
                            .child(push)
                            .child("sender_id")
                            .setValue(key);
                    Constants.databaseReference
                            .child("chatting").child("map")
                            .child(channel)
                            .child(push)
                            .child("time")
                            .setValue(time);
                    Constants.databaseReference.child("chatting").child("map").child(channel).child(push).child("name").setValue(n);
                    callEvent(channel);
                }
                else {
                    final String push= Constants.databaseReference.child("chatting").child("map").child(otherChannel).push().getKey();
                    Constants.databaseReference
                            .child("chatting")
                            .child("map")
                            .child(otherChannel)
                            .child(push)
                            .child("profile_url")
                            .setValue(downloadUrl);
                    Constants.databaseReference
                            .child("chatting")
                            .child("map")
                            .child(otherChannel)
                            .child(push)
                            .child("sender_id")
                            .setValue(key);
                    Constants.databaseReference
                            .child("chatting")
                            .child("map")
                            .child(otherChannel)
                            .child(push)
                            .child("time")
                            .setValue(time);
                    Constants.databaseReference
                            .child("chatting")
                            .child("map")
                            .child(otherChannel)
                            .child(push)
                            .child("name")
                            .setValue(n);

                    callEvent(otherChannel);

                }
                dialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                System.out.println("Upload is " + progress + "% done");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener ()
        {
            @Override
            public boolean onMenuItemClick (MenuItem item)
            {
                int id = item.getItemId();
                switch (id)
                {
                    case R.id.popupMenuClear:
                        if(condition) {
                            Constants.databaseReference.child("chatting").child("map").child(otherChannel).removeValue();
                        }
                        else
                        {
                            Constants.databaseReference.child("chatting").child("map").child(channel).removeValue();
                        }
                        break;
                }
                return true;
            }
        });
        popup.inflate(R.menu.popup_menu);
        popup.show();
    }
}
