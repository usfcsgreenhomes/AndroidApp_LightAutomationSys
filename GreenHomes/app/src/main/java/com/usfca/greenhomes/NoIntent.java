package com.usfca.greenhomes;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by bvenky27 on 2/16/17.
 */

public class NoIntent extends BroadcastReceiver {
    private String remoteIP = "eclipse.umbc.edu";
    private Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        //Toast.makeText(context, " Rejected the Recommendation", Toast.LENGTH_LONG).show();
        new MyHTTPGetRequestNo().execute();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }

    public class MyHTTPGetRequestNo extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            BufferedReader buf = null;
            HttpURLConnection connection = null;
            //https://eclipse.umbc.edu/greenhome/set_light_from_rec?userid=<USER_ID_OF_PI>&group=<Name of the group the user is in>
            URL url = null;
            StringBuffer sbuf = new StringBuffer();
            String line = "";
            try {
                url = new URL("https://" + remoteIP + "/greenhome/set_light_from_rec?userid="+ProfileData.pref.getString(ProfileData.PREF_USERID, null)+"&group="+ProfileData.pref.getString(ProfileData.PREF_GROUPS, null)+"&accept=no");
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
                    if (buf != null)
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
            super.onPostExecute(response);
            Log.d("Response after request", response);
            if (response.equals("success :)")) {
                Toast.makeText(context, "Request for No Successful!!! ", Toast.LENGTH_LONG).show();
            } else if(response.equals("error :(")) {
                Toast.makeText(context, "Request for No Failed. "+String.valueOf(response), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Request for No Failed. Check Internet Connection!!!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
