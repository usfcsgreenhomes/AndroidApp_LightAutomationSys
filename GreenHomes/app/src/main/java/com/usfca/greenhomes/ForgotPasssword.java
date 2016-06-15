package com.usfca.greenhomes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ForgotPasssword extends AppCompatActivity {

    static String remoteIP = "eclipse.umbc.edu";       //eclipse.umbc.edu  //127.0.0.1:8080
    EditText emailID;
    String email;
    Intent intent;
    Intent intent2;
    Intent intent3;
    Intent intent4;
    ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_passsword);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayShowTitleEnabled(true);
        ab.setTitle("forgot password?");
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF172604")));
        emailID = (EditText) findViewById(R.id.editText6);
        intent = new Intent(ForgotPasssword.this, PasswordSent.class);
        intent2 = new Intent(ForgotPasssword.this, AboutUs.class);
        intent3 = new Intent(ForgotPasssword.this, ContactUs.class);
        intent4 = new Intent(ForgotPasssword.this, Login.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInf = getMenuInflater();
        menuInf.inflate(R.menu.forgotpassword, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login:
                startActivity(intent4);
                Toast.makeText(getApplicationContext(), "Login to greenhomes!", Toast.LENGTH_LONG).show();
                break;
            case R.id.contactus:
                startActivity(intent3);
                Toast.makeText(getApplicationContext(), "We are eager to hear from you!", Toast.LENGTH_LONG).show();
                break;
            case R.id.aboutus:
                startActivity(intent2);
                Toast.makeText(getApplicationContext(), "Get to know about us!", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickForgotPassswordListener(View v){
        progressBar = ProgressDialog.show(this, "","Sending your password back...", true);     //Dialogue Title is kept blank
        email = emailID.getText().toString();
        if(email.equals("")){
            progressBar.hide();
            Toast.makeText(getApplicationContext(), "One of the input field(s) seems to be blank. Please try again!", Toast.LENGTH_LONG).show();
            return;
        }
        new MyHTTPPostRequestsForgotPassword().execute();
    }

    public class MyHTTPPostRequestsForgotPassword extends AsyncTask<String, String, String> {
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
                url = new URL("https://" + remoteIP + "/greenhome/forgotPassword");
                connection = (HttpURLConnection)url.openConnection();
                json = new JSONObject();
                json.put("email", email);
                urlParameters = "LoginInfo=" + json.toString();
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
                progressBar.hide();
                Toast.makeText(getApplicationContext(), "Incorrect Email ID/  Email ID does not exists in our records. Please try again!", Toast.LENGTH_LONG).show();
            }
            else{
                progressBar.hide();
                startActivity(intent);
            }
        }
    }
}
