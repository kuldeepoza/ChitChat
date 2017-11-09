package com.example.chitchat.utils;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by KD on 24-Jun-17.
 */

public class Util {
    public static Typeface SetCustomFont(String fontName, Context context) {
        return Typeface.createFromAsset(context.getAssets(), fontName);
    }
    public static Boolean validationEditText(String fieldName) {
        if (TextUtils.isEmpty(fieldName)) {
            return true;
        }
        else
        {
            return  false;
        }
    }
    public static String currentDateFormat(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String  currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }
    public static NetworkInfo isInternetConnection(Context context)
    {
        ConnectivityManager connectivityManager =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info=connectivityManager.getActiveNetworkInfo();
        return info;
    }
    public static int validationMobile(String fieldName)
    {
        if(TextUtils.isEmpty(fieldName))
        {
            return 0;
        }
        else if(fieldName.length()==10)
        {
            return  1;
        }
        else {
            return 2;
        }
    }
    public static void inputKeybord(Context c,View v) {
        InputMethodManager im = (InputMethodManager)c.getSystemService(INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
    public static void inputKeybordShow(Context c,View v) {
        InputMethodManager im = (InputMethodManager)c.getSystemService(INPUT_METHOD_SERVICE);
        im.showSoftInputFromInputMethod(v.getWindowToken(), 0);
    }
}
