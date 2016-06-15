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

public class ThankYou extends AppCompatActivity {

    Intent intent;
    Intent intent2;
    Intent intent3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayShowTitleEnabled(true);
        ab.setTitle("thanks!");
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF172604")));
        intent = new Intent(ThankYou.this, AboutUs.class);
        intent2 = new Intent(ThankYou.this, Login.class);
        intent3 = new Intent(ThankYou.this, Signup.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInf = getMenuInflater();
        menuInf.inflate(R.menu.thanks_contactus, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aboutus:
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Get to know about us!", Toast.LENGTH_LONG).show();
                break;
            case R.id.login:
                startActivity(intent2);
                Toast.makeText(getApplicationContext(), "Login to greenhomes!", Toast.LENGTH_LONG).show();
                break;
            case R.id.signup:
                startActivity(intent3);
                Toast.makeText(getApplicationContext(), "Sign up & enjoy our free services now!", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
