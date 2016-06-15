package com.usfca.greenhomes;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class MyServices extends Service {

    public static Integer service;
    public static boolean started = false;
    public static String regToken;
    public static String remoteIP = "eclipse.umbc.edu";       //eclipse.umbc.edu  //127.0.0.1:8080

    class ServiceThread implements Runnable{
        ServiceThread(int serviceID){
            service = serviceID;
        }
        public void run(){
            try {
                InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
                String token = instanceID.getToken("918094878188", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                regToken = token;
                Log.i("Token: ", token);
                /*PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), 0);
                Notification noti = new Notification.Builder(getApplicationContext()).setTicker("You have opted for message service in greenhomes")
                                    .setContentTitle("Make your light dimmer")
                                    .setContentText("at " + new Date())
                                    .setOnlyAlertOnce(true)
                                    .setAutoCancel(true)
                                    .setSmallIcon(R.drawable.logo)
                                    .setContentIntent(pendingIntent).getNotification();
                noti.flags = Notification.FLAG_AUTO_CANCEL;
                NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0, noti);*/
                new MyHTTPPostRequestsSendToken().execute();
            }catch(Exception e){
                System.out.println(e);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        started = true;
        Thread thread = new Thread(new ServiceThread(startId));
        thread.start();
        //startService(new Intent(this, MyInstanceIDListenerService.class));
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        started = false;
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), 0);
        Notification noti = new Notification.Builder(getApplicationContext()).setTicker("You have left the message service option")
                            .setContentTitle("No more notifications now!")
                            .setOnlyAlertOnce(true)
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.logo)
                            .setContentIntent(pendingIntent).getNotification();
        noti.flags = Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, noti);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class MyHTTPPostRequestsSendToken extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... arg0) {
            BufferedReader buf = null;
            HttpURLConnection connection = null;
            URL url = null;
            JSONObject json = null;
            String urlParameters = null;
            DataOutputStream dStream = null;
            StringBuffer sbuf = new StringBuffer();
            String line = "";
            try{
                url = new URL("https://" + remoteIP + "/greenhome/registerToken");
                connection = (HttpURLConnection)url.openConnection();
                json = new JSONObject();
                json.put("email", ProfileData.emailID);
                json.put("token", regToken);
                urlParameters = "TokenDetails=" + json.toString();
                connection.setDoOutput(true);
                dStream = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(urlParameters); //Writes out the string to the underlying output stream as a sequence of bytes
                dStream.flush(); // Flushes the data output stream.
                dStream.close(); // Closing the output stream.
                buf = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while((line = buf.readLine()) != null){
                    sbuf.append(line);
                }
                return sbuf.toString();
            }catch(Exception e){
                return "Error";
            }finally{
                try {
                    buf.close();
                    if(connection != null)
                        connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("Error")) {
                stopSelf(service);
                Toast.makeText(getApplicationContext(), "We could not start the notification service due to issues at our end. Sorry for the inconvenience, please try again later!", Toast.LENGTH_LONG).show();
            }
            else{
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), 0);
                Notification noti = new Notification.Builder(getApplicationContext()).setTicker("You have opted for message service in greenhomes")
                                    .setContentTitle("Notification Service Started!")
                                    .setContentText("at " + new Date())
                                    .setOnlyAlertOnce(true)
                                    .setAutoCancel(true)
                                    .setSmallIcon(R.drawable.logo)
                                    .setContentIntent(pendingIntent).getNotification();
                noti.flags = Notification.FLAG_AUTO_CANCEL;
                NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0, noti);
            }
        }
    }

    /*@Override
    protected void onHandleIntent(Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
        Notification noti = new Notification.Builder(this).setTicker("You have opted for message service in greenhomes")
                .setContentTitle("Message service notification service is in progress..")
                .setContentText("contacting server in regular intervals")
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pendingIntent).getNotification();
        noti.flags = Notification.FLAG_GROUP_SUMMARY;
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, noti);
        Log.i("Push Notification", "handling intent");
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken("918094878188", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i("Token: ", token);
            wait(2000);
        }catch(Exception e){
            System.out.println(e);
        }
    }*/
}
