package com.usfca.greenhomes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class YesIntent extends BroadcastReceiver {
    static String remoteIP = "eclipse.umbc.edu";
    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.i("Option: ", context.toString());
        //Toast.makeText(context, "Yes button pressed", Toast.LENGTH_SHORT).show();
        new MyHTTPGetRequestYes().execute();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(intent.getExtras().getInt("id"));
    }
    public class MyHTTPGetRequestYes extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            BufferedReader buf = null;
            HttpURLConnection connection = null;
            //https://eclipse.umbc.edu/greenhome/set_light_from_rec?userid=<USER_ID_OF_PI>&group=<Name of the group the user is in>
            URL url = null;
            StringBuffer sbuf = new StringBuffer();
            String line = "";
            try {
                url = new URL("https://" + remoteIP + "/greenhome/set_light_from_rec?userid="+ProfileData.userID+"&group="+ProfileData.groups);
                connection = (HttpURLConnection) url.openConnection();
                buf = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = buf.readLine()) != null) {
                    sbuf.append(line);
                }
                return sbuf.toString();
            } catch (Exception e) {
                return "Error";
            } finally {
                try {
                    buf.close();
                    if (connection != null)
                        connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(String response) {
            Log.d("Response after request", response);
            //Toast.makeText(context, "Response after request: "+response, Toast.LENGTH_SHORT).show();
            if (response.equals("success :)")) {
                Toast.makeText(context, "Request for Yes Successful!!! ", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Request for Yes Failed!!!"+String.valueOf(response), Toast.LENGTH_LONG).show();
            }

        }
    }
}
