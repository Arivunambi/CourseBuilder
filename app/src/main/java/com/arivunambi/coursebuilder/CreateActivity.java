package com.arivunambi.coursebuilder;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class CreateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        onAddActivity();
    }

    public void onStart(){
        super.onStart();
        final TextView activity_publish_from_text=(TextView)findViewById(R.id.activity_publish_from_text);
        ImageView activity_publish_from_datePicker = (ImageView)findViewById(R.id.activity_publish_from_datePicker);
        //RelativeLayout pub_from_container = (RelativeLayout)findViewById(R.id.publish_from_datePicker_container);

        View.OnClickListener pub_from_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDialog dialog = new DateDialog(activity_publish_from_text);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "DatePicker");
            }
        };

        activity_publish_from_datePicker.setOnClickListener(pub_from_listener);
        activity_publish_from_text.setOnClickListener(pub_from_listener);

        final TextView activity_publish_to_text=(TextView)findViewById(R.id.activity_publish_to_text);
        ImageView activity_publish_to_datePicker = (ImageView)findViewById(R.id.activity_publish_to_datePicker);

        View.OnClickListener pub_to_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDialog dialog = new DateDialog(activity_publish_to_text);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "DatePicker");
            }
        };

        activity_publish_to_datePicker.setOnClickListener(pub_to_listener);
        activity_publish_to_text.setOnClickListener(pub_to_listener);
    }

    private void onAddActivity(){
        Button addActivity = (Button) findViewById(R.id.add_activity);
        addActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText activityName = (EditText) findViewById(R.id.activity_name);
                String new_activityName = activityName.getText().toString();

                TextView activity_publish_from_text = (TextView) findViewById(R.id.activity_publish_from_text);
                String new_activity_publish_from_text = activity_publish_from_text.getText().toString();

                if (new_activityName != null && !new_activityName.isEmpty() && new_activity_publish_from_text != null && !new_activity_publish_from_text.isEmpty()) {
                    //update db
                    Intent intent = new Intent(CreateActivity.this, ActivityScreen.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
}
