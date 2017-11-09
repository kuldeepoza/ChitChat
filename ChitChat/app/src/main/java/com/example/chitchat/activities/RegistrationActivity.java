package com.example.chitchat.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chitchat.R;
import com.example.chitchat.utils.Constants;
import com.example.chitchat.utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by KD on 22-Jun-17.
 */

public class RegistrationActivity extends Activity {
    Button submit;
    CircleImageView regUserProfile;
    private FirebaseAuth auth;
    EditText etName,etEmail,etPass,etMo;
    private ProgressDialog dialog;
    TextView regR;
    String picturePath;
     String downloadUrl;
    Bitmap bitmap,bmp;
    Date date;
    CharSequence dateChar;
    String profile_url,name,email,password,mo_no;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        auth=FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null)
        {
            startActivity(new Intent(RegistrationActivity.this, UsersActivity.class));
        }
        regUserProfile =(CircleImageView)findViewById(R.id.regUserProfile);
        etName = (EditText) findViewById(R.id.regEtName);
        etEmail = (EditText) findViewById(R.id.regEtEmail);
        etPass= (EditText) findViewById(R.id.regEtPass);
        etMo= (EditText) findViewById(R.id.regEtMobNo);
        submit=(Button)findViewById(R.id.regBtnSubmit);
        regR = (TextView) findViewById(R.id.loginRedirect);
        dialog=new ProgressDialog(this);
        regR = (TextView) findViewById(R.id.regRedirect);
        date = new Date();
        dateChar  = DateFormat.format("MMMM d, yyyy ", date.getTime());
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.inputKeybord(RegistrationActivity.this,view);

                CheckConditionField();
    }
    private void CheckConditionField() {
        if (Util.validationEditText(etName.getText().toString())) {
            etName.setError("Please enter Name");
        } else if (Util.validationEditText(etEmail.getText().toString())) {
            etEmail.setError("Please enter Email");
        } else if (Util.validationEditText(etPass.getText().toString())) {
            etPass.setError("Please enter Password");
        } else if (Util.validationMobile(etMo.getText().toString()) == 0) {
            etMo.setError("Please enter Mobile No");
        } else if (Util.validationMobile(etMo.getText().toString()) == 2) {
            etMo.setError("Please enter 10 Digit No");
        } else {
            NetworkInfo info=Util.isInternetConnection(RegistrationActivity.this);
            if(info!=null) {
                dialog.setMessage(Constants.LOADING);
                dialog.show();
                name = etName.getText().toString();
                email = etEmail.getText().toString();
                password = etPass.getText().toString();
                mo_no = etMo.getText().toString();
                createUserInFirebase();
            }
            else
            {
                Toast.makeText(RegistrationActivity.this, getString(R.string.intConnection), Toast.LENGTH_LONG).show();

            }
        }

    }});
    }
    private void openDialogImage() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(RegistrationActivity.this);
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
            profile_url=picturePath;
            c.close();
            try {
                bmp = getBitmapFromUri(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            regUserProfile.setImageBitmap(bmp);
        } else if (resultCode == RESULT_OK && requestCode == Constants.REQUEST_IMAGE_CAPTURE) {
            bmp = (Bitmap)data.getExtras().get("data");
            String partFilename = currentDateFormat();
            picturePath = Environment.getExternalStorageDirectory()+"/DCIM/Camera/photo_"+partFilename+".jpg";
            storeCameraPhotoInSDCard(bmp, partFilename);
            profile_url=picturePath;
            Bitmap b=getImageFileFromSDCard(picturePath);
            regUserProfile.setImageBitmap(b);
        }
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
    private void createUserInFirebase() {
        auth.createUserWithEmailAndPassword(etEmail.getText().toString(),etPass.getText().toString()).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete( Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(RegistrationActivity.this, getString(R.string.authFailre) + task.getException(),
                            Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                } else {
                    imageStoreInFirebase();
                    }
                }
        });
    }
    private void imageStoreInFirebase() {
        FirebaseStorage storage=FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();
        Uri file = Uri.fromFile(new File(profile_url));
        StorageReference riversRef = storageRef.child("profile/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file,metadata);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(RegistrationActivity.this, "Fail", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                downloadUrl = taskSnapshot.getDownloadUrl().toString();
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                String strDate = sdf.format(c.getTime());
                Constants.databaseReference
                        .child("users")
                        .child(auth.getCurrentUser().getUid())
                        .child("profile")
                        .child("profile_url")
                        .setValue(downloadUrl);
                Constants.databaseReference.child("users").child(auth.getCurrentUser().getUid()).child("profile").child("email").setValue(email);
                Constants.databaseReference.child("users").child(auth.getCurrentUser().getUid()).child("profile").child("password").setValue(password);
                Constants.databaseReference.child("users").child(auth.getCurrentUser().getUid()).child("profile").child("mobile_no").setValue(mo_no);
                Constants.databaseReference.child("users").child(auth.getCurrentUser().getUid()).child("profile").child("name").setValue(name);
                Constants.databaseReference.child("users").child(auth.getCurrentUser().getUid()).child("profile").child("date").setValue(strDate);
                Constants.databaseReference.child("users").child(auth.getCurrentUser().getUid()).child("profile").child("online").setValue("true");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dialog.cancel();
                startActivity(new Intent(RegistrationActivity.this, UsersActivity.class));
                finish();
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
    protected void onResume() {
        super.onResume();
    }
    public void civUserProfileClick(View v)
    {
        openDialogImage();

    }
    public void tvLogRedirect(View v)
    {
        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
    }

}
