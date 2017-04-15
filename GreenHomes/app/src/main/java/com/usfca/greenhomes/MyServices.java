package com.usfca.greenhomes;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpCookie;
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

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        started = false;
        /*PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noti = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("No more notifications now!")
                .setGroup("GreenHomes")
                .setGroupSummary(true)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, noti.build());*/

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
                ProfileData.pref = getSharedPreferences(ProfileData.PREF_FILE, MODE_PRIVATE);
                if(Login.msCookieManager.getCookieStore().getCookies().size() > 0){
                    connection.setRequestProperty("Cookie", TextUtils.join(";", Login.msCookieManager.getCookieStore().getCookies()));
                } else if (ProfileData.pref.getStringSet(ProfileData.PREF_COOKIES, null) != null){
                    Login.msCookieManager.getCookieStore().removeAll();
                    for (String cookie : ProfileData.pref.getStringSet(ProfileData.PREF_COOKIES, null))
                        Login.msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                    connection.setRequestProperty("Cookie", TextUtils.join(";", Login.msCookieManager.getCookieStore().getCookies()));
                } else {
                    Toast.makeText(getApplicationContext(), "Session Expired.. Please Login!", Toast.LENGTH_LONG).show();
                }
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
                    if (buf != null)
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
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_ONE_SHOT);
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder noti = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle("Notification Service Stopped!")
                        .setContentText("Issues occurred at "+ service + new Date())
                        .setGroup("GreenHomes")
                        .setGroupSummary(true)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
                //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0, noti.build());
            }
        }
    }
}
