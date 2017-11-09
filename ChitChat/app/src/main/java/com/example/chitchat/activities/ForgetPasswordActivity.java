package com.example.chitchat.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chitchat.R;
import com.example.chitchat.utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by KD on 04-Jul-17.
 */

public class ForgetPasswordActivity extends Activity{
    Button submit;
    EditText etEmail;
    ProgressDialog dialog;
    FirebaseAuth auth;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        auth=FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
        submit = (Button) findViewById(R.id.btnForgUser);
        etEmail = (EditText) findViewById(R.id.etForgUserEmail);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.inputKeybord(ForgetPasswordActivity.this,view);
                if (Util.validationEditText(etEmail.getText().toString())) {
                    etEmail.setError("Please enter Email");
                }
                else {
                    dialog.setMessage("Loading...");
                    dialog.show();
                    checkForgetPassword();
                }
            }
        });
    }
    private void checkForgetPassword() {
        auth.sendPasswordResetEmail(etEmail.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete( Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgetPasswordActivity.this, "Please check your email id...", Toast.LENGTH_SHORT).show();
                            Log.d("sent", "Email sent.");
                            dialog.dismiss();
                            startActivity(new Intent(ForgetPasswordActivity.this,LoginActivity.class));

                        }
                        else {
                            dialog.dismiss();
                            Toast.makeText(ForgetPasswordActivity.this, "Some Problem", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
