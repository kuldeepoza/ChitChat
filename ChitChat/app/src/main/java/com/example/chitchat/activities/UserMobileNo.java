package com.example.chitchat.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.chitchat.R;
import com.example.chitchat.utils.Constants;
import com.example.chitchat.utils.Util;

public class UserMobileNo extends AppCompatActivity {
Button btn;
    EditText et;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_mobile_no);
        btn=(Button)findViewById(R.id.btnUserSign);
        dialog=new ProgressDialog(this);
        et=(EditText)findViewById(R.id.etOtherSignMob);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.inputKeybord(UserMobileNo.this,view);
                if (Util.validationEditText(et.getText().toString())) {
                    et.setError("Please enter Mobile No");
                }
                else {
                    dialog.setMessage("Loading...");
                    dialog.show();
                    putDataInFirebase();
                    dialog.dismiss();
                    startActivity(new Intent(UserMobileNo.this,UsersActivity.class));
                }
            }
        });
    }
    private void putDataInFirebase() {
        Constants.databaseReference.child("users").child(Constants.auth.getCurrentUser().getUid()).child("profile").child("mobile_no").setValue(et.getText().toString());
    }
}
