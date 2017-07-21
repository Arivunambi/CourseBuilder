package com.arivunambi.coursebuilder;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;


public class LoadingScreen extends AppCompatActivity {

    public static Activity finisher;
    public static int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        finisher = this;
        status =1;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        //autoFinish();
    }

    @Override
    protected void onDestroy(){
        status = 0;
        Intent resultIntent = new Intent();
        resultIntent.putExtra("done","done");  // put data that you want returned to activity A
        setResult(Activity.RESULT_OK, resultIntent);
        super.onDestroy();
        //finish();
    }

    private void autoFinish(){
        Handler handler = new Handler();
        Runnable r=new Runnable() {
            @Override
            public void run() {
                finish();
            }
        };
        handler.postDelayed(r, 3000);
    }
}

