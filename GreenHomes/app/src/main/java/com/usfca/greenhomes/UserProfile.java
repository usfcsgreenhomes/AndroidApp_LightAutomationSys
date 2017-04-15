package com.usfca.greenhomes;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpCookie;
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
    TextView emailID;
    TextView rgGroup;
    ProgressDialog progressBar;
    String user;
    String groups;
    String waitTime = "1800";
    String lightTime;
    String name;
    String phone;
    PopupWindow popupWindowSnooze;
    boolean snoozePopup;
    PopupWindow popupWindowGroup;
    boolean snoozeGroup;

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
        nickName = (EditText)findViewById(R.id.editText3) ;
        phoneNo = (EditText)findViewById(R.id.editText4);
        emailID = (TextView)findViewById(R.id.textView7);
        rgGroup = (TextView)findViewById(R.id.textView11);
        nickName.setText(ProfileData.nickname);
        phoneNo.setText(ProfileData.phone);
        emailID.setText(ProfileData.emailID);
        if(ProfileData.groups.equals("Group1")){
            rgGroup.setText("Manual Group");
            groups = "Group1";
        }
        if(ProfileData.groups.equals("Group3")){
            rgGroup.setText("Automatic Group");
            groups = "Group3";
        }
        if(ProfileData.groups.equals("Group2") || ProfileData.groups.equals("Group3") || ProfileData.groups.equals("Group4")){
            if(!ProfileData.waitInterval.equals("1800") || !ProfileData.lightInterval.equals("1800")){
                progressBar = ProgressDialog.show(this, "", "Saving your profile...", true);
                waitTime = "1800";
                lightTime = "1800";
                user = ProfileData.userID;
                phone = ProfileData.phone;
                name = ProfileData.nickname;
                new MyHTTPPostRequestsSave().execute();
            }
        }
        if(ProfileData.groups.equals("Group2")){
            rgGroup.setText("Automatic + Recommendation Group");
            groups = "Group2";
        }
        if(ProfileData.groups.equals("Group4")){
            rgGroup.setText("Manual + Recommendation Group");
            groups = "Group4";
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
        nickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() < 6 || s.length() > 20)
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
        intent4 = new Intent(UserProfile.this, RegistrationIntentService.class);
        if(ProfileData.groups.equals("Group2") || ProfileData.groups.equals("Group4")){            //Start the MyService class only if the user has selected Message Service
                startService(intent4);
        }

        /* Popup for Snooze Time */
        snoozePopup = false;
        LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popupsnooze, null);
        popupWindowSnooze = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        /* Popup for Group Information  */
        snoozeGroup = false;
        LayoutInflater layoutInflater3 = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView3 = layoutInflater3.inflate(R.layout.popupgroup, null);
        popupWindowGroup  = new PopupWindow(popupView3, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        //to hide the keyboard appearing automatically
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressBar != null)
            progressBar.dismiss();
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

    public void onBackgroundClickListener(View v){
        if(snoozePopup){
            popupWindowSnooze.dismiss();
            snoozePopup = false;
        }
        if(snoozeGroup){
            popupWindowGroup.dismiss();
            snoozeGroup = false;
        }
    }


    public void onGroupClickListener(View v){
        if(!snoozeGroup){
            popupWindowGroup.showAsDropDown(v, 50, -30);
            snoozeGroup = true;
        }
        else {
            popupWindowGroup.dismiss();
            snoozeGroup = false;
        }
    }

    public void onClickSaveButtonListener(View v) {
        progressBar = ProgressDialog.show(this, "", "Saving your profile...", true);     //Dialogue Title is kept blank
        user = ProfileData.userID;
        lightTime = ProfileData.lightInterval;
        phone = phoneNo.getText().toString();
        name = nickName.getText().toString();
        if (name.equals("") || phone.equals("")) {
            progressBar.hide();
            Toast.makeText(getApplicationContext(), "One of the input field(s) seems to be blank. Please try again!", Toast.LENGTH_LONG).show();
            return;
        }
        if (name.length() < 6 || name.length() > 20) {
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
        if(groups.equals("Group2") || groups.equals("Group3") || groups.equals("Group4")){

                waitTime = "1800";
                lightTime = "1800";

        }
        new MyHTTPPostRequestsSave().execute();
    }

    public class MyHTTPPostRequestsSave extends AsyncTask<String, String, String> {

        @SuppressLint("LongLogTag")
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
                ProfileData.pref = getSharedPreferences(ProfileData.PREF_FILE, MODE_PRIVATE);
                if(Login.msCookieManager.getCookieStore().getCookies().size() > 0){
                    connection.setRequestProperty("Cookie", TextUtils.join(";", Login.msCookieManager.getCookieStore().getCookies()));
                } else if (ProfileData.pref.getStringSet(ProfileData.PREF_COOKIES, null) != null){
                    for (String cookie : ProfileData.pref.getStringSet(ProfileData.PREF_COOKIES, null))
                        Login.msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                    connection.setRequestProperty("Cookie", TextUtils.join(";", Login.msCookieManager.getCookieStore().getCookies()));
                } else {
                    Toast.makeText(getApplicationContext(), "Session Expired.. Please Login!", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }
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
                    if (buf != null)
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
                    ProfileData.lightInterval = null;
                }
                else if(groups.equals("Group4")){
                    ProfileData.waitInterval = waitTime;
                    ProfileData.lightInterval = lightTime;
                }
            }
        }
    }
}
