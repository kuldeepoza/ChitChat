package com.example.chitchat.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.chitchat.R;
import com.example.chitchat.activities.UsersActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by KD on 20-Jun-17.
 */

public class FCMPushReceiverService  extends FirebaseMessagingService{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendNotification(remoteMessage.getNotification().getBody());
    }
    private void sendNotification(String message) {
        Intent intent = new Intent(FCMPushReceiverService.this, UsersActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("ChitChat")
                .setSmallIcon(R.mipmap.app_icon)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(sound)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noBuilder.build());

    }
}