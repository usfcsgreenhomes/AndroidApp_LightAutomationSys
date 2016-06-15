package com.usfca.greenhomes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Signup extends AppCompatActivity {

    boolean error = false;
    static String remoteIP = "eclipse.umbc.edu";       //eclipse.umbc.edu  //127.0.0.1:8080
    String user;
    String email;
    String password1;
    String password2;
    String name;
    String phoneNo;
    EditText userID;
    EditText emailID;
    EditText nickname;
    EditText phone;
    EditText pass1;
    EditText pass2;
    Intent intent;
    Intent intent2;
    Intent intent3;
    Intent intent4;
    ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayShowTitleEnabled(true);
        ab.setTitle("sign up!");
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF172604")));
        userID = (EditText) findViewById(R.id.userID);
        emailID = (EditText) findViewById(R.id.email);
        nickname = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.phone);
        pass1 = (EditText) findViewById(R.id.pass1);
        pass2 = (EditText) findViewById(R.id.pass2);
        intent = new Intent(Signup.this, SuccessfullyRegistered.class);
        intent2 = new Intent(Signup.this, AboutUs.class);
        intent3 = new Intent(Signup.this, ContactUs.class);
        intent4 = new Intent(Signup.this, Login.class);
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 10)
                    phone.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkcircle,0);
                else
                    phone.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.warning,0);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });
        userID.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() < 8 || s.length() > 100)
                    userID.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.warning,0);
                else
                    userID.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkcircle,0);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });
        emailID.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() < 8 || s.length() > 100)
                    emailID.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.warning,0);
                else
                    emailID.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkcircle,0);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });
        nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() < 6 || s.length() > 10)
                    nickname.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.warning,0);
                else
                    nickname.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkcircle,0);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });
        pass1.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() < 6 || s.length() > 100)
                    pass1.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.warning,0);
                else
                    pass1.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkcircle,0);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });
        pass2.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String e = pass1.getText().toString();
                if(!(s.toString().equals(e.toString())))
                    pass2.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.warning,0);
                else
                    pass2.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkcircle,0);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInf = getMenuInflater();
        menuInf.inflate(R.menu.signup_main_activity, menu);
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

    public void onButtonClickListener(View v) {
        progressBar = ProgressDialog.show(this, "","Registering you...", true);     //Dialogue Title is kept blank
        user = userID.getText().toString();
        email = emailID.getText().toString();
        name = nickname.getText().toString();
        phoneNo = phone.getText().toString();
        password1 = pass1.getText().toString();
        password2 = pass2.getText().toString();
        if(user.equals("") || email.equals("") || name.equals("") || phoneNo.equals("") || password1.equals("") || password2.equals("")){
            progressBar.hide();
            Toast.makeText(getApplicationContext(), "One of the input field(s) seems to be blank. Please try again!", Toast.LENGTH_LONG).show();
            return;
        }
        if(user.length() < 8 || user.length() > 100){
            progressBar.hide();
            userID.setError("Too short or too long");
            userID.setSelection(0);
            return;
        }
        if(email.length() < 8 || email.length() > 100){
            progressBar.hide();
            emailID.setError("Too short or too long");
            emailID.setSelection(0);
            return;
        }
        if(name.length() < 6 || name.length() > 10){
            progressBar.hide();
            nickname.setError("Too short or too long");
            nickname.setSelection(0);
            return;
        }
        if(phoneNo.length() != 10){
            progressBar.hide();
            phone.setError("Too short or too long");
            phone.setSelection(0);
            return;
        }
        if(password1.length() < 6 || password1.length() > 100){
            progressBar.hide();
            pass1.setError("Too short or too long");
            pass1.setSelection(0);
            return;
        }
        if(!password1.equals(password2)){
            progressBar.hide();
            pass2.setError("Password does not matches");
            pass2.setSelection(0);
            return;
        }
        new MyHTTPPostRequests().execute();
    }
    public class MyHTTPPostRequests extends AsyncTask<String, String, String> {

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
                url = new URL("https://" + remoteIP + "/greenhome/userId");
                connection = (HttpURLConnection)url.openConnection();
                json = new JSONObject();
                json.put("user", user);
                urlParameters = "UserID=" + json.toString();
                connection.setDoOutput(true);
                dStream = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(urlParameters); //Writes out the string to the underlying output stream as a sequence of bytes
                dStream.flush(); // Flushes the data output stream.
                dStream.close(); // Closing the output stream.
                buf = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while((line = buf.readLine()) != null){
                    sbuf.append(line);
                }
                if(sbuf.toString().equals("Error"))
                    return "UserID already exists";
                else{
                    sbuf.setLength(0);
                    line = "";
                    url = new URL("https://" + remoteIP + "/greenhome/emailId");
                    connection = (HttpURLConnection)url.openConnection();
                    json = new JSONObject();
                    json.put("email", email);
                    urlParameters = "EmailID=" + json.toString();
                    connection.setDoOutput(true);
                    dStream = new DataOutputStream(connection.getOutputStream());
                    dStream.writeBytes(urlParameters);
                    dStream.flush();
                    dStream.close();
                    buf = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while((line = buf.readLine()) != null){
                        sbuf.append(line);
                    }
                    if(sbuf.toString().equals("Error"))
                        return "EmailID already exists";
                    else{
                        sbuf.setLength(0);
                        line = "";
                        url = new URL("https://" + remoteIP + "/greenhome/signup");
                        connection = (HttpURLConnection)url.openConnection();
                        json = new JSONObject();
                        json.put("user", user);
                        json.put("nickname", name);
                        json.put("phone", phoneNo);
                        json.put("pass", password1);
                        json.put("email", email);
                        urlParameters = "SignUpInfo=" + json.toString();
                        connection.setDoOutput(true);
                        dStream = new DataOutputStream(connection.getOutputStream());
                        dStream.writeBytes(urlParameters);
                        dStream.flush();
                        dStream.close();
                        buf = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while((line = buf.readLine()) != null){
                            sbuf.append(line);
                        }
                        return sbuf.toString();
                    }
                }
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
            if(result.equals("UserID already exists")) {
                progressBar.hide();
                error = true;
                userID.setSelection(0, user.length());
                userID.setError("User ID is already in use. Choose a new one.");
            }
            else if (result.equals("EmailID already exists")) {
                progressBar.hide();
                error = true;
                emailID.setSelection(0, email.length());
                emailID.setError("Email ID is already in use. Choose a new one.");
            }
            else if (result.equals("Error")) {
                progressBar.hide();
                error = true;
                Toast.makeText(getApplicationContext(), "We could not register you now. Please try again!", Toast.LENGTH_LONG).show();
            }
            else{
                progressBar.hide();
                startActivity(intent);
            }
        }
    }
}
