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

public class ContactUs extends AppCompatActivity {

    static String remoteIP = "eclipse.umbc.edu";       //eclipse.umbc.edu  //127.0.0.1:8080
    String name;
    String email;
    String usercomments;
    EditText username;
    EditText emailID;
    EditText comments;
    Intent intent;
    ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayShowTitleEnabled(true);
        ab.setTitle("contact us!");
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF172604")));
        intent = new Intent(ContactUs.this, ThankYou.class);
        username = (EditText) findViewById(R.id.username);
        emailID = (EditText) findViewById(R.id.useremail);
        comments = (EditText) findViewById(R.id.comments);
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() < 1 || s.length() > 100)
                    username.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.warning,0);
                else
                    username.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkcircle,0);
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
        comments.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() < 1 || s.length() > 500)
                    comments.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.warning,0);
                else
                    comments.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkcircle,0);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });
    }

    public void onSendCommentsButtonClickListener(View v) {
        progressBar = ProgressDialog.show(this, "","Sending your message...", true);     //Dialogue Title is kept blank
        name = username.getText().toString();
        email = emailID.getText().toString();
        usercomments = comments.getText().toString();
        if(name.equals("") || email.equals("") || usercomments.equals("")){
            progressBar.hide();
            Toast.makeText(getApplicationContext(), "One of the input field(s) seems to be blank. Please try again!", Toast.LENGTH_LONG).show();
            return;
        }
        if(name.length() > 100){
            progressBar.hide();
            username.setError("Too long");
            username.setSelection(0);
            return;
        }
        if(email.length() < 8 || email.length() > 100){
            progressBar.hide();
            emailID.setError("Too short or too long");
            emailID.setSelection(0);
            return;
        }
        if(usercomments.length() > 500){
            progressBar.hide();
            comments.setError("Too long");
            comments.setSelection(0);
            return;
        }
        new MyHTTPPostRequestsSendComments().execute();
    }

    public class MyHTTPPostRequestsSendComments extends AsyncTask<String, String, String>{
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
                url = new URL("https://" + remoteIP + "/greenhome/send");
                connection = (HttpURLConnection)url.openConnection();
                json = new JSONObject();
                json.put("username", name);
                json.put("email", email);
                json.put("comments", usercomments);
                urlParameters = "MessageDetails=" + json.toString();
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
                Toast.makeText(getApplicationContext(), "We could not send your feedback right now. Please try again!", Toast.LENGTH_LONG).show();
            }
            else{
                progressBar.hide();
                startActivity(intent);
            }
        }
    }
}
