package com.example.chitchat.receiver;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by KD on 20-Jun-17.
 */

public class FCMTokenRefreshListenerService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIDService";
    public static final String REGISTRATION_SUCCESS = "RegistrationSuccess";
    public static final String REGISTRATION_ERROR = "RegistrationError";
    @Override
    public void onTokenRefresh() {
        Intent registrationComplete;
        String token = null;
        try {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.d(TAG, "Refreshed token: " + refreshedToken);
            registrationComplete = new Intent(REGISTRATION_SUCCESS);
            registrationComplete.putExtra("token", token);

        }
        catch (Exception e) {
            Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            registrationComplete = new Intent(REGISTRATION_ERROR);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
}