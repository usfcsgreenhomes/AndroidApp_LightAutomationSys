package com.usfca.greenhomes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Login extends AppCompatActivity {

    static String remoteIP = "eclipse.umbc.edu";       //eclipse.umbc.edu  //127.0.0.1:8080
    EditText password;
    EditText emailID;
    CheckBox checkBox;
    Intent intent;
    Intent intent2;
    Intent intent3;
    Intent intent4;
    ProgressDialog progressBar;
    boolean imageSwitch;
    String user;
    String pass;
    SharedPreferences pref;
    static final String PREF_USERNAME = "username";
    static final String PREF_PASSWORD = "password";
    static final String PREF_FILE = "GreenHomes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setLogo(R.drawable.logo);
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayShowTitleEnabled(true);
        ab.setTitle("login!");
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF172604")));
        intent = new Intent(Login.this, Signup.class);
        intent2 = new Intent(Login.this, AboutUs.class);
        intent3 = new Intent(Login.this, ContactUs.class);
        intent4 = new Intent(Login.this, UserProfile.class);
        password = (EditText) findViewById(R.id.editText2);
        emailID = (EditText) findViewById(R.id.editText);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        imageSwitch = true;
        pref = getSharedPreferences(PREF_FILE, MODE_PRIVATE);
        if(ProfileData.loggedin){
            intent4 = new Intent(Login.this, UserProfile.class);
            startActivity(intent4);
        }
        emailID.setText(pref.getString(PREF_USERNAME, null));
        password.setText(pref.getString(PREF_PASSWORD, null));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInf = getMenuInflater();
        menuInf.inflate(R.menu.login_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.contactus:
                startActivity(intent3);
                Toast.makeText(getApplicationContext(), "We are eager to hear from you!", Toast.LENGTH_LONG).show();
                break;
            case R.id.aboutus:
                startActivity(intent2);
                Toast.makeText(getApplicationContext(), "Get to know about us!", Toast.LENGTH_LONG).show();
                break;
            case R.id.signup:
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Sign up & enjoy our free services now!", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSignupLink(View v){
        startActivity(intent);
        Toast.makeText(getApplicationContext(), "Sign up & enjoy our free services now!", Toast.LENGTH_LONG).show();
    }

    public void onForgotPasswordLink(View v){
        intent = new Intent(Login.this, ForgotPasssword.class);
        startActivity(intent);
    }

    public void onPasswordClickListener(View v){

        ImageView imageView = (ImageView) findViewById(R.id.imageSwitcher);
        if(imageSwitch) {
            imageView.setImageResource(R.drawable.visibility_off);
            password.setTransformationMethod(null);
            imageSwitch = false;
        }
        else{
            imageView.setImageResource(R.drawable.visibility);
            password.setTransformationMethod(new PasswordTransformationMethod());
            imageSwitch = true;
        }
    }

    public void onLoginButtonListener(View v){
        progressBar = ProgressDialog.show(this, "","Logging you in...", true);     //Dialogue Title is kept blank
        user = emailID.getText().toString();
        pass = password.getText().toString();
        if(user.equals("") || pass.equals("")){
            progressBar.hide();
            Toast.makeText(getApplicationContext(), "One of the input field(s) seems to be blank. Please try again!", Toast.LENGTH_LONG).show();
            return;
        }
        if(checkBox.isChecked()){
            getSharedPreferences(PREF_FILE, MODE_PRIVATE)
                    .edit()
                    .putString(PREF_USERNAME, emailID.getText().toString())
                    .putString(PREF_PASSWORD, password.getText().toString())
                    .commit();
        }
        new MyHTTPPostRequestsLogin().execute();
    }

    public class MyHTTPPostRequestsLogin extends AsyncTask<String, String, String> {
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
                url = new URL("https://" + remoteIP + "/greenhome/loginMobile");
                connection = (HttpURLConnection)url.openConnection();
                json = new JSONObject();
                json.put("email", user);
                json.put("pass", pass);
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
                Toast.makeText(getApplicationContext(), "Incorrect User/ Password. Please try again!", Toast.LENGTH_LONG).show();
            }
            else{
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    ProfileData.emailID = jsonObject.getString("email");
                    ProfileData.userID = jsonObject.getString("user");
                    ProfileData.nickname = jsonObject.getString("nickname");
                    ProfileData.phone = jsonObject.getString("phone");
                    ProfileData.groups = jsonObject.getString("Groups");
                    ProfileData.waitInterval = jsonObject.getString("Wait");
                    ProfileData.lightInterval = jsonObject.getString("LightInterval");
                    ProfileData.loggedin = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressBar.hide();
                startActivity(intent4);
            }
        }
    }
}
