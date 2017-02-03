package com.usfca.greenhomes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.gcm.GcmListenerService;

import java.util.Date;

public class MyGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d("GcmListener Message: ", "Data: " + data.getString("gcm.notification.title"));
        String contentTitle = data.getString("gcm.notification.title");
        String contentText = data.getString("gcm.notification.body");
        //Intent yesIn = new Intent(this, YesIntent.class);
        Intent yesOrNoIn = new Intent(this, YesOrNoIntent.class);
        yesOrNoIn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent yesOrNoPin = PendingIntent.getActivity(getApplicationContext(), 0, yesOrNoIn, PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_ONE_SHOT);
        //PendingIntent yesIntent = PendingIntent.getBroadcast(this, 0, yesIn, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noti = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(new Date().toString())
                .setContentText(contentText)
                .setGroup("GreenHomes")
                .setGroupSummary(true)
                //.addAction(R.drawable.thumbpup, "", yesIntent)
                //.addAction(R.drawable.thumbpdown, "", yesIntent)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(yesOrNoPin);
        //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, noti.build());
    }

    /*@Override
    public void onDeletedMessages() {
        sendNotification("Deleted messages on server");
    }

    @Override
    public void onMessageSent(String msgId) {
        sendNotification("Upstream message sent. Id=" + msgId);
    }

    @Override
    public void onSendError(String msgId, String error) {
        sendNotification("Upstream message send error. Id=" + msgId + ", error" + error);
    }*/
}
