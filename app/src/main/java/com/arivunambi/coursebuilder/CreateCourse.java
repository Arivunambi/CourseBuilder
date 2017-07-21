package com.arivunambi.coursebuilder;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateCourse extends AppCompatActivity {

    final ArrayList<String> userList = new ArrayList<String>( Arrays.asList("Admin", "User1", "User2"));
    //private PopupWindow pw;
    //private boolean expanded;
    public static boolean[] owner_checkSelected;
    public static boolean[] user_checkSelected;

    //public static boolean[] checkSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ViewGroup dropdown_popup = (ViewGroup)findViewById(R.id.dropdown_popup);

        RelativeLayout dropdown_container_course_owner = (RelativeLayout)findViewById(R.id.dropdown_container_course_owner);
        TextView dropdown_textView_course_owner = (TextView)findViewById(R.id.dropdown_textView_course_owner);
        owner_checkSelected = new boolean[userList.size()];
        MultiSelectDropDown addOwner = new MultiSelectDropDown(this, userList, dropdown_container_course_owner,
                dropdown_textView_course_owner, dropdown_popup, owner_checkSelected );
        addOwner.initialize();

        RelativeLayout dropdown_container_course_user = (RelativeLayout)findViewById(R.id.dropdown_container_course_user);
        TextView dropdown_textView_course_user = (TextView)findViewById(R.id.dropdown_textView_course_user);
        user_checkSelected = new boolean[userList.size()];
        MultiSelectDropDown addUser = new MultiSelectDropDown(this, userList, dropdown_container_course_user, dropdown_textView_course_user, dropdown_popup, user_checkSelected );
        addUser.initialize();

        //initialize();
        onAddCourse();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .7));


    }

    private void onAddCourse(){
        Button addCourse = (Button)findViewById(R.id.add_course);
        addCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DataAPI dataAPI = new DataAPI(getApplicationContext());

                EditText courseName = (EditText)findViewById(R.id.course_name);
                String new_courseName = courseName.getText().toString();

                String selected = "Course name is " + new_courseName;
                selected += " and owners are ";
                List<String> owners = new ArrayList<String>();
                for (int i = 0; i < userList.size(); i++) {
                    if (owner_checkSelected[i] == true) {
                        selected += userList.get(i);
                        selected += ";";
                        owners.add(userList.get(i));
                    }
                }
                selected +=" and users are ";
                List<String> users = new ArrayList<String>();
                for (int i = 0; i < userList.size(); i++) {
                    if (user_checkSelected[i] == true) {
                        selected += userList.get(i);
                        selected += ";";
                        users.add(userList.get(i));
                    }
                }

                if (new_courseName != null && !new_courseName.isEmpty() && owners.size()>0 && users.size()>0){
                    //Important//CourseCard.courseArray.add(CourseCard.courseArray.size()-2, new_courseName);
                    dataAPI.set_course(new_courseName);
                    Intent i = new Intent(CreateCourse.this, LoadingScreen.class);
                    HomeScreen.recreateCount =0;
                    startActivity(i);
                    //Intent intent = new Intent(CreateCourse.this, HomeScreen.class);
                    //startActivity(intent);
                    finish();
                }
                //Log.d("Add Course: ",selected);
            }
        });
    }

    /*
    private void initialize(){
        checkSelected = new boolean[userList.size()];
        for (int i = 0; i < checkSelected.length; i++) {
            checkSelected[i] = false;
        }

        final TextView tv = (TextView) findViewById(R.id.dropdown_textView);
        tv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!expanded) {
                    //display all selected values
                    String selected = "";
                    int flag = 0;
                    for (int i = 0; i < userList.size(); i++) {
                        if (checkSelected[i] == true) {
                            selected += userList.get(i);
                            selected += ", ";
                            flag = 1;
                        }
                    }
                    if (flag == 1)
                        tv.setText(selected);
                    expanded = true;
                } else {
                    //display shortened representation of selected values
                    tv.setText(DropDownListAdapter.getSelected());
                    expanded = false;
                }
            }
        });

        //onClickListener to initiate the dropDown list - For initiator see below the definition
        View.OnClickListener dropdown_icon = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePopUp(userList, tv);
            }
        };

        RelativeLayout createButton = (RelativeLayout)findViewById(R.id.dropdown_container);
        createButton.setOnClickListener(dropdown_icon);
        TextView extendButton = (TextView)findViewById(R.id.dropdown_textView);
        extendButton.setOnClickListener(dropdown_icon);

        Button addCourse = (Button)findViewById(R.id.add_course);
        addCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selected = "";
                for (int i = 0; i < userList.size(); i++) {
                    if (checkSelected[i] == true) {
                        selected += userList.get(i);
                        selected += ";";
                    }
                }

                Log.d("Add Course: ",selected);
            }
        });
    }

    private void initiatePopUp(ArrayList<String> userList, TextView tv){
        LayoutInflater inflater = (LayoutInflater)CreateCourse.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.dropdown_popup, (ViewGroup) findViewById(R.id.dropdown_popup));

        pw = new PopupWindow(layout, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pw.setBackgroundDrawable(new BitmapDrawable());
        pw.setTouchable(true);
        //inform pop-up the touch event outside its window
        pw.setOutsideTouchable(true);
        //the pop-up will be dismissed if touch event occurs anywhere outside its window
        pw.setTouchInterceptor(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    pw.dismiss();
                    return true;
                }
                return false;
            }
        });

        // Set the content of pop-up window as layout and anchor the pop-up to the bottom-left corner of the desired view.
        pw.setContentView(layout);
        RelativeLayout layout1 = (RelativeLayout)findViewById(R.id.dropdown_container);
        pw.showAsDropDown(layout1);

        // finally set the drop-down listview with the data source items:
        final ListView list = (ListView) layout.findViewById(R.id.dropdown_listView);
        DropDownListAdapter adapter = new DropDownListAdapter(this, userList, tv, checkSelected);
        //â€™itemsâ€™ is the valuesâ€™ list and â€˜tvâ€™ is the textview where the selected values are displayed
        list.setAdapter(adapter);
    }
    */
}
