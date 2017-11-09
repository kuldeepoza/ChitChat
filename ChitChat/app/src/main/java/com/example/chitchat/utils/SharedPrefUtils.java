package com.example.chitchat.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by KD on 22-Jun-17.
 */

public class SharedPrefUtils {
    SharedPreferences sp;
    SharedPreferences.Editor edit;
   public void setSharedPrefName(Context context)
   {
      sp=context.getSharedPreferences("Wallpaper",Context.MODE_PRIVATE);
       edit=sp.edit();
   }
public void setSharedPref(String name,String value)
{
    edit.putString(name,value);
}
    public String getSharedPref(String name)
    {
        String val=sp.getString(name,"null");
        return val;
    }

}
