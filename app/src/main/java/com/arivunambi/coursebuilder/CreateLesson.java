package com.arivunambi.coursebuilder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CreateLesson extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lesson);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        onAddLesson();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .7));


    }

    private void onAddLesson(){
        Button addCourse = (Button)findViewById(R.id.add_lesson);
        addCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText lessonName = (EditText)findViewById(R.id.lesson_name);
                String new_lessonName = lessonName.getText().toString();

                EditText lessonDesc = (EditText)findViewById(R.id.lesson_desc);
                String new_lessonDesc = lessonDesc.getText().toString();

                if (new_lessonName != null && !new_lessonName.isEmpty() && new_lessonDesc != null && !new_lessonDesc.isEmpty()){
                    LessonCard.lessonArray.add(LessonCard.lessonArray.size() - 2, new_lessonName);
                    Intent intent = new Intent(CreateLesson.this, LessonScreen.class);
                    startActivity(intent);
                    finish();

                }
            }
        });
    }

}
