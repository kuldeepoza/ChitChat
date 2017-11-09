package com.example.chitchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.chitchat.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by KD on 22-Jun-17.
 */

public class SplashActivity extends AppCompatActivity{
    private static  final int SPLASH_TIME=2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                startActivity(new Intent(SplashActivity.this,RegistrationChoiceActivity.class));
            }
        },SPLASH_TIME);
    }
}
