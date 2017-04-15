package com.usfca.greenhomes;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class YesIntent extends BroadcastReceiver {
    static String remoteIP = "eclipse.umbc.edu";
    static public boolean yesintent = false;

    public static boolean isYesintent() {
        return yesintent;
    }

    public static void setYesintent(boolean yesintent) {
        YesIntent.yesintent = yesintent;
    }

    private static Context context;

    public static void onLoginYes(){
        new MyHTTPGetRequestYes().execute();
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        ProfileData.pref = context.getSharedPreferences(ProfileData.PREF_FILE, Context.MODE_PRIVATE);
        if (!ProfileData.pref.getBoolean(ProfileData.PREF_LOGGEDIN, false)){
            yesintent = true;
            Intent intentLogin = new Intent(context.getApplicationContext(), MainActivity.class);
            intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentLogin);
        } else {
            new MyHTTPGetRequestYes().execute();
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);

    }
    public static class MyHTTPGetRequestYes extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            BufferedReader buf = null;
            HttpURLConnection connection = null;
            //https://eclipse.umbc.edu/greenhome/set_light_from_rec?userid=<USER_ID_OF_PI>&group=<Name of the group the user is in>
            URL url = null;
            StringBuffer sbuf = new StringBuffer();
            String line = "";
            try {
                url = new URL("https://" + remoteIP + "/greenhome/set_light_from_rec?userid="+ProfileData.pref.getString(ProfileData.PREF_USERID, null)+"&group="+ProfileData.pref.getString(ProfileData.PREF_GROUPS, null)+"&accept=yes");
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
            if (response.equals("success :)")) {
                Toast.makeText(context, "Request for Yes Successful!!! ", Toast.LENGTH_LONG).show();
            } else if (response.equals("error :(")){
                Toast.makeText(context, "Request for Yes Failed. "+String.valueOf(response), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Request for Yes Failed. Check Internet Connection!!!", Toast.LENGTH_LONG).show();
            }

        }
    }
}
