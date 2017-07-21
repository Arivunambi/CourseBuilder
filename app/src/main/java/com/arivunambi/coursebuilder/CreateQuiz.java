package com.arivunambi.coursebuilder;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CreateQuiz extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        onAddQuiz();
    }

    public void onStart(){
        super.onStart();
        final TextView publish_from_text=(TextView)findViewById(R.id.publish_from_text);
        ImageView publish_from_datePicker = (ImageView)findViewById(R.id.publish_from_datePicker);
        //RelativeLayout pub_from_container = (RelativeLayout)findViewById(R.id.publish_from_datePicker_container);

        View.OnClickListener pub_from_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDialog dialog = new DateDialog(publish_from_text);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "DatePicker");
            }
        };

        publish_from_datePicker.setOnClickListener(pub_from_listener);
        publish_from_text.setOnClickListener(pub_from_listener);

        final TextView publish_to_text=(TextView)findViewById(R.id.publish_to_text);
        ImageView publish_to_datePicker = (ImageView)findViewById(R.id.publish_to_datePicker);

        View.OnClickListener pub_to_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDialog dialog = new DateDialog(publish_to_text);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "DatePicker");
            }
        };

        publish_to_datePicker.setOnClickListener(pub_to_listener);
        publish_to_text.setOnClickListener(pub_to_listener);
    }

    private void onAddQuiz(){
        Button addCourse = (Button) findViewById(R.id.add_quiz);
        addCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DataAPI dataAPI = new DataAPI(getApplicationContext());

                EditText quizName = (EditText) findViewById(R.id.quiz_name);
                String new_quizName = quizName.getText().toString();

                TextView publish_from_text = (TextView) findViewById(R.id.publish_from_text);
                String new_publish_from_text = publish_from_text.getText().toString();

                if (new_quizName != null && !new_quizName.isEmpty() && new_publish_from_text != null && !new_publish_from_text.isEmpty()) {
                    //update db

                    dataAPI.set_quiz(new_quizName);
                    Intent intent = new Intent(CreateQuiz.this, LoadingScreen.class);
                    QuizScreen.recreateCount =0;
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

}
