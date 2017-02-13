package com.usfca.greenhomes;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class YesOrNoIntent extends AppCompatActivity {
    static String remoteIP = "eclipse.umbc.edu";
    ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yes_or_no_intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInf = getMenuInflater();
        menuInf.inflate(R.menu.yes_or_no, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void onYesButtonListener(View v) {
        progressBar = ProgressDialog.show(this, "", "Sending the request...", true); //Dialogue Title is kept empty
        new MyHTTPGetRequestYes().execute();

    }

    public void onNoButtonListener(View v) {
        moveTaskToBack(true);
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
            Toast.makeText(getApplicationContext(), "Response after request: "+response, Toast.LENGTH_SHORT).show();
            if (response.equals("success :)")) {
                Toast.makeText(getApplicationContext(), "Request for Yes Successful!!! ", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Request for Yes Failed!!!"+String.valueOf(response), Toast.LENGTH_LONG).show();
            }
            progressBar.hide();
            moveTaskToBack(true);
        }
    }
}
