package com.usfca.greenhomes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UserProfile extends AppCompatActivity {

    static String remoteIP = "eclipse.umbc.edu";       //eclipse.umbc.edu  //127.0.0.1:8080
    Intent intent;
    Intent intent2;
    Intent intent3;
    Intent intent4;
    EditText nickName;
    EditText phoneNo;
    EditText userID;
    TextView emailID;
    RadioGroup rgGroup;
    RadioGroup rgWait;
    RadioGroup rgLight;
    ImageView waitInt;
    ImageView lightInt;
    ProgressDialog progressBar;
    String user;
    String groups;
    String waitTime;
    String lightTime;
    String name;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setLogo(R.drawable.logo);
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayShowTitleEnabled(true);
        ab.setTitle("welcome!");
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF172604")));
        waitInt = (ImageView)findViewById(R.id.imageView8);
        lightInt = (ImageView)findViewById(R.id.imageView9);
        nickName = (EditText)findViewById(R.id.editText3) ;
        phoneNo = (EditText)findViewById(R.id.editText4);
        userID = (EditText)findViewById(R.id.editText5);
        emailID = (TextView)findViewById(R.id.textView7);
        rgGroup = (RadioGroup)findViewById(R.id.radioGroup);
        rgWait = (RadioGroup)findViewById(R.id.radioGroup3);
        rgLight = (RadioGroup)findViewById(R.id.radioGroup2);
        rgWait.check(R.id.wait1800);
        rgLight.check(R.id.light3600);
        nickName.setText(ProfileData.nickname);
        phoneNo.setText(ProfileData.phone);
        userID.setText(ProfileData.userID);
        emailID.setText(ProfileData.emailID);
        if(ProfileData.groups.equals("Group1")){
            rgWait.setVisibility(View.GONE);
            rgLight.setVisibility(View.GONE);
            waitInt.setVisibility(View.GONE);
            lightInt.setVisibility(View.GONE);
            rgGroup.check(R.id.Group1);
        }
        if(ProfileData.groups.equals("Group3")){
            rgLight.setVisibility(View.GONE);
            lightInt.setVisibility(View.GONE);
            rgGroup.check(R.id.Group3);
        }
        if(ProfileData.groups.equals("Group2") || ProfileData.groups.equals("Group3")){
            if(ProfileData.waitInterval.equals("1800")){
                rgWait.check(R.id.wait1800);
            }
            else if(ProfileData.waitInterval.equals("3600")){
                rgWait.check(R.id.wait3600);
            }
            else if(ProfileData.waitInterval.equals("7200")){
                rgWait.check(R.id.wait7200);
            }
            else if(ProfileData.waitInterval.equals("10800")){
                rgWait.check(R.id.wait10800);
            }
            else{
                rgWait.check(R.id.wait21600);
            }
        }
        if(ProfileData.groups.equals("Group2")){
            rgGroup.check(R.id.Group2);
            if(ProfileData.lightInterval.equals("1800")){
                rgLight.check(R.id.light1800);
            }
            else if(ProfileData.lightInterval.equals("3600")){
                rgLight.check(R.id.light3600);
            }
            else if(ProfileData.lightInterval.equals("5400")){
                rgLight.check(R.id.light5400);
            }
            else if(ProfileData.lightInterval.equals("7200")){
                rgLight.check(R.id.light7200);
            }
            else{
                rgLight.check(R.id.light9000);
            }
        }
        phoneNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 10)
                    phoneNo.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkcircle,0);
                else
                    phoneNo.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.warning,0);
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
        nickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() < 6 || s.length() > 10)
                    nickName.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.warning,0);
                else
                    nickName.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkcircle,0);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });
        intent = new Intent(UserProfile.this, MainActivity.class);
        intent2 = new Intent(UserProfile.this, ContactUs.class);
        intent3 = new Intent(UserProfile.this, AboutUs.class);
        intent4 = new Intent(UserProfile.this, MyServices.class);
        if(ProfileData.groups.equals("Group2")){            //Start the MyService class only if the user has selected Message Service
            if(!MyServices.started){
                startService(intent4);
            }
        }
        else{
            if(MyServices.started){
                stopService(intent4);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInf = getMenuInflater();
        menuInf.inflate(R.menu.profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.logout:
                ProfileData.loggedin = false;
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

    public void onGroup1RadioButtonListener(View v){
        rgWait.setVisibility(View.GONE);
        rgLight.setVisibility(View.GONE);
        waitInt.setVisibility(View.GONE);
        lightInt.setVisibility(View.GONE);
    }

    public void onGroup2RadioButtonListener(View v){
        rgWait.setVisibility(View.VISIBLE);
        rgLight.setVisibility(View.VISIBLE);
        waitInt.setVisibility(View.VISIBLE);
        lightInt.setVisibility(View.VISIBLE);
    }

    public void onGroup3RadioButtonListener(View v){
        rgWait.setVisibility(View.VISIBLE);
        waitInt.setVisibility(View.VISIBLE);
        rgLight.setVisibility(View.GONE);
        lightInt.setVisibility(View.GONE);
    }

    public void onClickSaveButtonListener(View v) {
        progressBar = ProgressDialog.show(this, "", "Saving your profile...", true);     //Dialogue Title is kept blank
        user = userID.getText().toString();
        phone = phoneNo.getText().toString();
        name = nickName.getText().toString();
        if (user.equals("") || name.equals("") || phone.equals("")) {
            progressBar.hide();
            Toast.makeText(getApplicationContext(), "One of the input field(s) seems to be blank. Please try again!", Toast.LENGTH_LONG).show();
            return;
        }
        if (user.length() < 8 || user.length() > 100) {
            progressBar.hide();
            userID.setError("Too short or too long");
            userID.setSelection(0);
            return;
        }
        if (name.length() < 6 || name.length() > 10) {
            progressBar.hide();
            nickName.setError("Too short or too long");
            nickName.setSelection(0);
            return;
        }
        if (phone.length() != 10) {
            progressBar.hide();
            phoneNo.setError("Too short or too long");
            phoneNo.setSelection(0);
            return;
        }
        RadioButton grp1 = (RadioButton) findViewById(R.id.Group1);
        RadioButton grp2 = (RadioButton) findViewById(R.id.Group2);
        RadioButton grp3 = (RadioButton) findViewById(R.id.Group3);
        if (grp1.isChecked())
            groups = "Group1";
        else if(grp2.isChecked())
            groups = "Group2";
        else if(grp3.isChecked())
            groups = "Group3";
        if(groups.equals("Group2") || groups.equals("Group3")){
            RadioButton wait1 = (RadioButton) findViewById(R.id.wait1800);
            RadioButton wait2 = (RadioButton) findViewById(R.id.wait3600);
            RadioButton wait3 = (RadioButton) findViewById(R.id.wait7200);
            RadioButton wait4 = (RadioButton) findViewById(R.id.wait10800);
            RadioButton wait5 = (RadioButton) findViewById(R.id.wait21600);
            if(wait1.isChecked())
                waitTime = "1800";
            else if(wait2.isChecked())
                waitTime = "3600";
            else if(wait3.isChecked())
                waitTime = "7200";
            else if(wait4.isChecked())
                waitTime = "10800";
            else if(wait5.isChecked())
                waitTime = "21600";
        }
        if(groups.equals("Group2")){
            RadioButton light1 = (RadioButton) findViewById(R.id.light1800);
            RadioButton light2 = (RadioButton) findViewById(R.id.light3600);
            RadioButton light3 = (RadioButton) findViewById(R.id.light5400);
            RadioButton light4 = (RadioButton) findViewById(R.id.light7200);
            RadioButton light5 = (RadioButton) findViewById(R.id.light9000);
            if(light1.isChecked())
                lightTime = "1800";
            else if(light2.isChecked())
                lightTime = "3600";
            else if(light3.isChecked())
                lightTime = "5400";
            else if(light4.isChecked())
                lightTime = "7200";
            else if(light5.isChecked())
                lightTime = "9000";
        }
        new MyHTTPPostRequestsSave().execute();
        if (grp1.isChecked()){
            if(MyServices.started){
                stopService(intent4);
            }
        }
        else if(grp2.isChecked()){
            if(!MyServices.started){
                startService(intent4);
            }
        }
        else if(grp3.isChecked()){
            if(MyServices.started){
                stopService(intent4);
            }
        }
    }

    public class MyHTTPPostRequestsSave extends AsyncTask<String, String, String> {

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
                url = new URL("https://" + remoteIP + "/greenhome/save");
                connection = (HttpURLConnection)url.openConnection();
                json = new JSONObject();
                json.put("phone", phone);
                json.put("nickname", name);
                json.put("email", ProfileData.emailID);             //EmailID of user never changes
                json.put("group", groups);
                json.put("wait", waitTime);
                json.put("lightdata", lightTime);
                json.put("user", user);
                urlParameters = "Update=" + json.toString();
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
                Toast.makeText(getApplicationContext(), "We could not save your information right now. Please try again!", Toast.LENGTH_LONG).show();
            }
            else{
                progressBar.hide();
                Toast.makeText(getApplicationContext(), "We have saved your new profile successfully!", Toast.LENGTH_LONG).show();
                ProfileData.groups = groups;
                ProfileData.phone = phone;
                ProfileData.nickname = name;
                ProfileData.userID = user;
                if(groups.equals("Group1")){
                    ProfileData.waitInterval = null;
                    ProfileData.lightInterval = null;
                }
                else if(groups.equals("Group2")){
                    ProfileData.waitInterval = waitTime;
                    ProfileData.lightInterval = lightTime;
                }
                else if(groups.equals("Group3")){
                    ProfileData.waitInterval = waitTime;
                }
            }
        }
    }
}
