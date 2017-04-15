package com.usfca.greenhomes;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.fabric.sdk.android.Fabric;

public class YesOrNoIntent extends AppCompatActivity {
    static String remoteIP = "eclipse.umbc.edu";
    static public boolean yesornointent = false;
    private Context context = this;

    public static boolean isYesornointent() {
        return yesornointent;
    }

    public static void setYesornointent(boolean yesornointent) {
        YesOrNoIntent.yesornointent = yesornointent;
    }


    ProgressDialog progressBar;
    Intent intent;
    Intent intent2;
    Intent intent3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yes_or_no_intent);
        intent = new Intent(YesOrNoIntent.this, MainActivity.class);
        intent2 = new Intent(YesOrNoIntent.this, ContactUs.class);
        intent3 = new Intent(YesOrNoIntent.this, AboutUs.class);
        Fabric.with(this, new Crashlytics());
        ProfileData.pref = getSharedPreferences(ProfileData.PREF_FILE, MODE_PRIVATE);
        if(!ProfileData.pref.getBoolean(ProfileData.PREF_LOGGEDIN, false)){
            yesornointent = true;
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInf = getMenuInflater();
        menuInf.inflate(R.menu.yes_or_no, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.logout:
                ProfileData.loggedin = false;
                getSharedPreferences(ProfileData.PREF_FILE, MODE_PRIVATE)
                        .edit()
                        .putBoolean(ProfileData.PREF_LOGGEDIN, false)
                        .apply();
                Login.msCookieManager.getCookieStore().removeAll();
                Toast.makeText(getApplicationContext(), "You have been logged out successfully!", Toast.LENGTH_LONG).show();
                startActivity(intent);
                break;
            case R.id.contactus:
                startActivity(intent2);
                Toast.makeText(getApplicationContext(), "We are eager to hear from you!", Toast.LENGTH_LONG).show();
                break;
            case R.id.aboutus:
                startActivity(intent3);
                Toast.makeText(getApplicationContext(), "Get to know about us!", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onYesButtonListener(View v) {
        progressBar = ProgressDialog.show(this, "", "Sending the request...", true); //Dialogue Title is kept empty
        new MyHTTPGetRequestYes().execute();
        /*MyHTTPGetRequestYes myHTTPGetRequestYes = new MyHTTPGetRequestYes(context);
        myHTTPGetRequestYes.execute();*/

    }

    public void onNoButtonListener(View v) {
        startActivity(intent);
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
                    if(buf != null)
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
                Toast.makeText(getApplicationContext(), "Request for Yes Successful!!! ", Toast.LENGTH_LONG).show();
            } else if (response.equals("error :(")){
                Toast.makeText(getApplicationContext(), "Request for Yes Failed. Check Internet Connection!!!"+String.valueOf(response), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Request for Yes Failed. Check Internet Connection!!!", Toast.LENGTH_LONG).show();
            }
            progressBar.hide();
            startActivity(intent);
        }
    }
}
