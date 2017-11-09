package com.example.chitchat.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.chitchat.R;
import com.example.chitchat.utils.Constants;

/**
 * Created by KD on 23-Jun-17.
 */

public class RegistrationChoiceActivity  extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_choice);
        if(Constants.auth.getCurrentUser()!=null)
        {
            finish();
            startActivity(new Intent(RegistrationChoiceActivity.this, UsersActivity.class));
        }
    }
    public void btnGetStart(View v)
    {
        finish();
        startActivity(new Intent(RegistrationChoiceActivity.this,LoginActivity.class));
    }
}

