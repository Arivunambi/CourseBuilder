package com.arivunambi.coursebuilder;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Layout;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class HomeScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private boolean _doubleBackToExitPressedOnce = false;
    public static final String PREFS_NAME = "MyPrefsFile";
    public static Activity refreshCard;
    public static int recreateCount=0;
    public static String pageLayout = "home";
    public static Integer clearcache = 0;
    public static Integer useCache = 1;

    @Override
    protected void onResume(){
        super.onResume();
        //DataAPI dataAPI = new DataAPI(getApplicationContext());
        //dataAPI.get_courses();
        if (recreateCount==0){
            Intent intent = getIntent();
            startActivity(intent);
            Log.d("Mylogger :","restarting Home.......");
            recreateCount+=1;
            finish();
        }
        Log.d("Mylogger :::::::::","refreshing Home......."+ ((Integer) recreateCount).toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Not yet configured", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        refreshCard = this;

    }

    @Override
    public void onBackPressed() {
        //int backButtonCount =0;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (_doubleBackToExitPressedOnce || CourseCard.page.equals("Course")) {
                //super.onBackPressed();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                _doubleBackToExitPressedOnce = false;
                System.exit(0);
            }else {
                this._doubleBackToExitPressedOnce = true;
                //Toast.makeText(this, "Press again to quit", Toast.LENGTH_SHORT).show();
                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        _doubleBackToExitPressedOnce = false;
                    }
                }, 3000);*/
                //super.onBackPressed();
                if (CourseCard.page.equals("Article")){
                    CourseCard.page = "Course";
                    Intent intent = new Intent(getIntent());
                    startActivity(intent);
                    finish();
                }else if (CourseCard.page.equals("Content")) {
                    CourseCard.page = "Article";
                    Intent intent = new Intent(getIntent());
                    startActivity(intent);
                    finish();
                }else if (CourseCard.page.equals("Document")) {
                    CourseCard.page = "Content";
                    Intent intent = new Intent(getIntent());
                    startActivity(intent);
                    finish();
                }else if (CourseCard.page.equals("Quiz")) {
                    CourseCard.page = "Course";
                    Intent intent = new Intent(getIntent());
                    startActivity(intent);
                    finish();
                }else if (CourseCard.page.equals("Question")) {
                    CourseCard.page = "Quiz";
                    Intent intent = new Intent(getIntent());
                    startActivity(intent);
                    finish();
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu){
        if (HomeScreen.useCache==0) {
            MenuItem item = menu.getItem(2);
            if (item!=null) {
                item.setTitle("Turn On Cache");
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_logOut){
            Intent i = new Intent(HomeScreen.this, LoginScreen.class);
            startActivity(i);
            finish();
        }else if (id == R.id.action_clear){
            HomeScreen.clearcache = 1;
            /*Intent intent = new Intent(getIntent());
            startActivity(intent);
            finish();*/
        }else if (id == R.id.action_cache){
            if (HomeScreen.useCache == 1){
                HomeScreen.useCache = 0;
                CourseCard.editor.put("quiz_list",null);
                CourseCard.editor.put("course_list",null);
                item.setTitle("Turn On Cache");
            }else{
                HomeScreen.useCache = 1;
                item.setTitle("Turn Off Cache");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        int id = item.getItemId();

        if (id == R.id.nav_course) {
            // Handle the camera action
            //final DataAPI dataAPI = new DataAPI(getApplicationContext());
            //dataAPI.get_courses();
            Intent i = new Intent(HomeScreen.this, HomeScreen.class);
            CourseCard.page="Course";
            startActivity(i);
            finish();
        } /*else if (id == R.id.nav_lesson) {
            Intent i = new Intent(HomeScreen.this, HomeScreen.class);
            startActivity(i);
        } */else if (id == 0){ //R.id.nav_article) {
            Intent i = new Intent(HomeScreen.this, HomeScreen.class);
            startActivity(i);
        }/*else if (id == R.id.nav_activity) {
            Intent i = new Intent(HomeScreen.this, ActivityScreen.class);
            startActivity(i);
        } */else if (id == R.id.nav_quiz) {
            final DataAPI dataAPI = new DataAPI(getApplicationContext());
            dataAPI.get_quizzes();
            //DataAPI.backgroundFlag=0;
            //QuizScreen.recreateCount=0;
            Intent i = new Intent(HomeScreen.this, QuizScreen.class);
            i.putExtra("course_id","1");
            CourseCard.page="Quiz";
            QuizCard.page="Quiz";
            startActivity(i);
            finish();
            /*CourseCard.page="quiz";
            //HomeScreen.this.finish();
            Intent intent = this.getIntent();
            finish();
            startActivity(intent);
            Log.d("Mylogger:::::::","Drawer");
            drawer.closeDrawers();
            return true;*/

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        HomeScreen.this.finish();

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
