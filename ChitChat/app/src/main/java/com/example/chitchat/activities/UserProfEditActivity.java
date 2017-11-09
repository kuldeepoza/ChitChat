package com.example.chitchat.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chitchat.R;
import com.example.chitchat.utils.Constants;
import com.example.chitchat.utils.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by KD on 01-Jul-17.
 */

public class UserProfEditActivity  extends AppCompatActivity {
    ImageView profile,back,edit;
    TextView name;
    String downloadUrl;
    String profile_url,userName;
    Toolbar tb;
    Bundle bundle;
     ProgressDialog dialog;
    String picturePath;
    Bitmap bitmap,bmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_prof_edit);
        profile = (ImageView) findViewById(R.id.ivUserProfEdit);
        back = (ImageView) findViewById(R.id.ivUserProfEditBack);
        edit = (ImageView) findViewById(R.id.ivUserProfEditIco);
        name = (TextView) findViewById(R.id.tvUserProfEditName);
        tb = (Toolbar) findViewById(R.id.toolbarUserProfEdit);
        setSupportActionBar(tb);
        bundle=getIntent().getExtras();
        dialog=new ProgressDialog(this);
        profile_url= bundle.getString("profile_url");
        userName= bundle.getString("user_name");
        name.setText(userName);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogImage();
            }
        });
                if(profile_url!=null) {
                    Glide
                            .with(UserProfEditActivity.this)
                            .load(profile_url)
                            .into(profile);
                }
                else
                {
                    Toast.makeText(UserProfEditActivity.this, Constants.Image_null, Toast.LENGTH_SHORT).show();
                }
    }
    private void openDialogImage() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(UserProfEditActivity.this);
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");
        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, Constants.SELECT_PICTURE);
                    }
                });
        myAlertDialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE);
                    }
                });
        myAlertDialog.show();
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

        } else if (resultCode == RESULT_OK && requestCode == Constants.REQUEST_IMAGE_CAPTURE) {
            bmp = (Bitmap)data.getExtras().get("data");
            String partFilename = Util.currentDateFormat();
            picturePath = Environment.getExternalStorageDirectory()+"/DCIM/Camera/photo_"+partFilename+".jpg";
            storeCameraPhotoInSDCard(bmp, partFilename);
        }
        dialog.setMessage(Constants.LOADING);
        dialog.show();
   imageStoreInFirebase();

    }
    private void imageStoreInFirebase() {
        FirebaseStorage storage=FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();
        Uri file = Uri.fromFile(new File(picturePath));
        StorageReference riversRef = storageRef.child("profile/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file,metadata);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(UserProfEditActivity.this, "Fail", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                downloadUrl = taskSnapshot.getDownloadUrl().toString();
                Constants.databaseReference
                        .child("users")
                        .child(Constants.auth.getCurrentUser().getUid())
                        .child("profile")
                        .child("profile_url")
                        .setValue(downloadUrl);
                dialog.dismiss();
                finish();
                startActivity(new Intent(UserProfEditActivity.this, UsersActivity.class));
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                System.out.println("Upload is " + progress + "% done");
            }
        });
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
}

