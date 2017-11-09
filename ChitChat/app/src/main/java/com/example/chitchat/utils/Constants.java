package com.example.chitchat.utils;

import android.content.Context;
import android.graphics.Typeface;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by KD on 22-Jun-17.
 */

public class Constants {
    public static final String firebase_url="https://chitchat-3e268.firebaseio.com/";
    public static final int REQUEST_IMAGE_CAPTURE=1;
    public static final int SELECT_PICTURE=0;
    public static final String Image_null="Image path not found";
    public static final int REQUEST_LOCATION = 1000;
    public static  FirebaseAuth auth= FirebaseAuth.getInstance();
  //  public static   FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
    public static DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
    public static final String LOADING = "Loading...";
    public static final int Wallpaper_Image=201;
    public static final int Wallpaper_Image_caputure=202;
}
