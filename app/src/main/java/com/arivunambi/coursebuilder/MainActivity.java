package com.arivunambi.coursebuilder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    public static String userID;
    public static Integer privilege=1;
    public static Integer loadingScreen=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataAPI dataAPI = new DataAPI(this.getApplicationContext());
        dataAPI.get_courses();
        dataAPI.get_quizzes();
        SharedPreferences settings = getSharedPreferences(HomeScreen.PREFS_NAME, 0);
        //Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
        boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);
        if(hasLoggedIn){
            userID = settings.getString("userID", "1");
            Intent i = new Intent(MainActivity.this, HomeScreen.class);
            startActivity(i);
            finish();
        }else {
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }
    }

    public void onClickGetStarted(View v){
        if (v.getId()==R.id.getStarted ){
            SharedPreferences settings = getSharedPreferences(HomeScreen.PREFS_NAME, 0);
            //Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
            boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);

            if(hasLoggedIn)
            {
                //Go directly to main activity.
                Intent i = new Intent(MainActivity.this, HomeScreen.class);
                startActivity(i);
            }else {
                Intent i = new Intent(MainActivity.this, LoginScreen.class);
                startActivity(i);
            }
            finish();
        }
    }

}
