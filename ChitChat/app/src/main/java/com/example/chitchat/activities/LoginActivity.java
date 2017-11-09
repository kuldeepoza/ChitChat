package com.example.chitchat.activities;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.chitchat.R;
import com.example.chitchat.utils.Constants;
import com.example.chitchat.utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by KD on 22-Jun-17.
 */

public class LoginActivity  extends AppCompatActivity {
    Button submit ;
    EditText etPass,etEmail;
     ProgressDialog dialog;
    FirebaseAuth auth;
    TextView forgPass;
    String strDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }
        if (Constants.auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, UsersActivity.class));
        }
        auth = FirebaseAuth.getInstance();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        strDate = sdf.format(c.getTime());
        submit = (Button) findViewById(R.id.loginBtnSubmit);
     //   otherSubmit = (Button) findViewById(R.id.btnOtherOption);
        etEmail = (EditText) findViewById(R.id.loginEtEmail);
        etPass = (EditText) findViewById(R.id.loginEtPass);
        forgPass = (TextView) findViewById(R.id.tvForgPass);
        dialog = new ProgressDialog(this);
        forgPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
            }
        });
        etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    Util.inputKeybordShow(LoginActivity.this, view);
                    etEmail.setText("@gmail.com");
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.inputKeybord(LoginActivity.this, view);
                if (Util.validationEditText(etEmail.getText().toString())) {
                    etEmail.setError("Please enter Email");
                } else if (Util.validationEditText(etPass.getText().toString())) {
                    etPass.setError("Please enter Password");
                } else {
                    NetworkInfo info = Util.isInternetConnection(LoginActivity.this);
                    if (info != null) {
                        dialog.setMessage(Constants.LOADING);
                        dialog.show();
                        auth.signInWithEmailAndPassword(etEmail.getText().toString(), etPass.getText().toString())
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    dialog.cancel();
                                } else {
                                    finish();
                                    dialog.cancel();
                                    Intent intent = new Intent(LoginActivity.this, UsersActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.intConnection), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    /*public void btnOtherSubmit(View v) {
        startActivity(new Intent(LoginActivity.this, OtherSignInActivity.class));
    }*/
    public void tvRegRedirect(View v) {
        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
    }
}
