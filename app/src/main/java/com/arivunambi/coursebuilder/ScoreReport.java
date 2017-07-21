package com.arivunambi.coursebuilder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScoreReport extends AppCompatActivity {

    public static String score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_report);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            score = extras.getString("score");
            TextView score_txt = (TextView)findViewById(R.id.score_txt);
            score_txt.setText(score);
        }
        //getActionBar().setTitle("Score Card");
        //getSupportActionBar().setTitle("Score Card");
        Button home = (Button)findViewById(R.id.score_home);
        home.setOnClickListener(backHome);

    }

    private View.OnClickListener backHome = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(ScoreReport.this, HomeScreen.class);
            HomeScreen.recreateCount =0;
            startActivity(i);
            finish();
        }
    };

}
