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
import android.widget.Toast;

public class PasswordSent extends AppCompatActivity {

    Intent intent2;
    Intent intent3;
    Intent intent4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_sent);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayShowTitleEnabled(true);
        ab.setTitle("password sent!");
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF172604")));
        intent2 = new Intent(PasswordSent.this, AboutUs.class);
        intent3 = new Intent(PasswordSent.this, ContactUs.class);
        intent4 = new Intent(PasswordSent.this, Login.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInf = getMenuInflater();
        menuInf.inflate(R.menu.password_sent, menu);
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
}
