package com.usfca.greenhomes;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class SuccessfullyRegistered extends AppCompatActivity {

    Intent intent;
    Intent intent2;
    Intent intent3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successfully_registered);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayShowTitleEnabled(true);
        ab.setTitle("congrats!");
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF172604")));
        intent = new Intent(SuccessfullyRegistered.this, Login.class);
        intent2 = new Intent(SuccessfullyRegistered.this, AboutUs.class);
        intent3 = new Intent(SuccessfullyRegistered.this, ContactUs.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInf = getMenuInflater();
        menuInf.inflate(R.menu.success_registered, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.contactus:
                startActivity(intent3);
                Toast.makeText(getApplicationContext(), "We are eager to hear from you!", Toast.LENGTH_LONG).show();
                break;
            case R.id.aboutus:
                startActivity(intent2);
                Toast.makeText(getApplicationContext(), "Get to know about us!", Toast.LENGTH_LONG).show();
                break;
            case R.id.login:
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Login to greenhomes!", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onLoginClickListener(View v){
        startActivity(intent);
        Toast.makeText(getApplicationContext(), "Login to greenhomes!", Toast.LENGTH_LONG).show();
    }
}
