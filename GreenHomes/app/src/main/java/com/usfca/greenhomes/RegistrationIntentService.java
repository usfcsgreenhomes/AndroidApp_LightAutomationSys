package com.usfca.greenhomes;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
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


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class RegistrationIntentService extends IntentService {

    public static String remoteIP = "eclipse.umbc.edu";       //eclipse.umbc.edu  //127.0.0.1:8080
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_FOO = "com.usfca.greenhomes.action.FOO";
    public static final String ACTION_BAZ = "com.usfca.greenhomes.action.BAZ";
    public static String regToken;

    // TODO: Rename parameters
    public static final String EXTRA_PARAM1 = "com.usfca.greenhomes.extra.PARAM1";
    public static final String EXTRA_PARAM2 = "com.usfca.greenhomes.extra.PARAM2";

    public RegistrationIntentService() {
        super("RegistrationIntentService");
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
        try{
            InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
            String token = instanceID.getToken("918094878188", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            regToken = token;
            Log.d("RegIntentService", "GCM Registration Token: " + token);
            sendRegistrationToServer();

        } catch (Exception e){
            Log.d("RegIntentService", "Failed to complete token refresh", e);

        }

    }

    private void sendRegistrationToServer() {
        new MyHTTPPostRequestsSendToken().execute();
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
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
                stopSelf();
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_ONE_SHOT);
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder noti = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle("Notification Service Stopped!")
                        .setContentText("Issues occurred at "+ new Date())
                        .setGroup("GreenHomes")
                        .setGroupSummary(true)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0, noti.build());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
