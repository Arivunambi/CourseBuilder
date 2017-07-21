package com.arivunambi.coursebuilder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class QuizScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static int recreateCount=0;

    @Override
    protected void onResume(){
        super.onResume();
        if (recreateCount==0){
            Intent intent = getIntent();
            startActivity(intent);
            Log.d("Mylogger :","restarting.......");
            recreateCount+=1;
            finish();
        }
        Log.d("Mylogger :","refreshing......."+ ((Integer) recreateCount).toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_screen);
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
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            /*Intent i = new Intent(getApplicationContext(), HomeScreen.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
            super.onBackPressed();*/
            if (CourseCard.page.equals("Course")) {
                //super.onBackPressed();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                System.exit(0);
            }else {
                if (CourseCard.page.equals("Quiz")){
                    CourseCard.page = "Course";
                    Intent intent = new Intent(QuizScreen.this,HomeScreen.class);
                    startActivity(intent);
                    finish();
                }else if (CourseCard.page.equals("Question")) {
                    CourseCard.page = "Quiz";
                    QuizCard.page = "Quiz";
                    Intent intent = new Intent(getIntent());
                    startActivity(intent);
                    finish();
                }else if (CourseCard.page.equals("Score")) {
                    CourseCard.page = "Quiz";
                    QuizCard.page = "Quiz";
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
        getMenuInflater().inflate(R.menu.quiz_screen, menu);
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
            Intent i = new Intent(QuizScreen.this, LoginScreen.class);
            startActivity(i);
        }
        else if (id == R.id.action_clear){
            HomeScreen.clearcache = 1;
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
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            CourseCard.page="Course";
            Intent i = new Intent(QuizScreen.this, HomeScreen.class);
            startActivity(i);
            finish();
        }/* else if (id == R.id.nav_activity) {
            Intent i = new Intent(QuizScreen.this, ActivityScreen.class);
            startActivity(i);
        }*/ else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        QuizScreen.this.finish();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
