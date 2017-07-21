package com.arivunambi.coursebuilder;

import android.annotation.TargetApi;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class CreateQuestion extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnTouchListener {

    boolean userSelect = false;
    private ArrayList<String> answerTypeArray = new ArrayList<>();
    private ArrayList<String> answerChoiceArray = new ArrayList<>();

    public String answerType="mcsa";
    public String answerChoice="True";
    private int dpToPixels(int dps){
        final float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner answer_type = (Spinner) findViewById(R.id.answer_type);

        Spinner correct_answer_TF = (Spinner) findViewById(R.id.correct_answer_TF);

        answer_type.setOnTouchListener(this);
        correct_answer_TF.setOnTouchListener(this);
        // Spinner click listener
        answer_type.setOnItemSelectedListener(this);
        correct_answer_TF.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        answerTypeArray.add("Select");
        answerTypeArray.add("Multiple Choice");
        answerTypeArray.add("Multiple Choice and Ans");
        answerTypeArray.add("True/False");
        answerTypeArray.add("Fill Ups");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, answerTypeArray);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        answer_type.setAdapter(dataAdapter);

        onAddQuestion();

    }


    private void onAddQuestion(){

        Button addQuestion = (Button) findViewById(R.id.add_question);
        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DataAPI dataAPI = new DataAPI(getApplicationContext());
                JSONArray answerChoiceList = new JSONArray();

                EditText question_text = (EditText) findViewById(R.id.question_text);
                String new_question_text = question_text.getText().toString();

                EditText question_mark = (EditText) findViewById(R.id.question_mark);
                String new_question_mark = question_mark.getText().toString();

                if (new_question_text != null && !new_question_text.isEmpty() && new_question_mark != null && !new_question_mark.isEmpty()) {
                    //update db

                    if (answerType=="mcsa"){
                        EditText correct_answer_MC = (EditText) findViewById(R.id.correct_answer_MC);
                        String new_correct_answer_MC = correct_answer_MC.getText().toString();
                        ArrayList<String> correct_answer_MC_position =
                                new  ArrayList<String>(Arrays.asList(new_correct_answer_MC.split(",")));
                        int count = new_correct_answer_MC.length() - new_correct_answer_MC.replace(",", "").length();
                        if (count>1){
                            answerType="mcma";
                        }

                        ArrayList<String> answer_choice_MC_list = new ArrayList<String>();
                        LinearLayout answer_choice_MC_layout = (LinearLayout) findViewById(R.id.answer_choice_MC_layout);
                        for( int i = 0; i < answer_choice_MC_layout.getChildCount(); i++ ) {
                            JSONObject jsonObject = new JSONObject();
                            if (answer_choice_MC_layout.getChildAt(i) instanceof EditText) {
                                answer_choice_MC_list.add((String) ((EditText) answer_choice_MC_layout.getChildAt(i)).getText().toString());
                                try {
                                    jsonObject.put("answer",(String) ((EditText) answer_choice_MC_layout.getChildAt(i)).getText().toString());
                                    for (int j=0;j<correct_answer_MC_position.size();j++){
                                        if (correct_answer_MC_position.get(j).toString().equals(((Integer)(i+1)).toString())){
                                            jsonObject.put("iscorrect","1");
                                            break;
                                        }else {
                                            jsonObject.put("iscorrect","0");
                                        }
                                    }
                                    answerChoiceList.put(jsonObject);
                                } catch (JSONException e) {
                                    Log.d("Mylogger :",e.toString());
                                }
                            }
                        }
                        //answerChoice = answer_choice_MC_list.toString();
                    } else if(answerType=="trueorfalse"){
                        answerType="mcsat";
                        try {
                            if (answerChoice.toLowerCase().equals("true")){
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("answer","true");
                                jsonObject.put("iscorrect","1");
                                answerChoiceList.put(jsonObject);
                                JSONObject jsonObject2 = new JSONObject();
                                jsonObject2.put("answer","false");
                                jsonObject2.put("iscorrect","0");
                                answerChoiceList.put(jsonObject2);
                            }else {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("answer","true");
                                jsonObject.put("iscorrect","0");
                                answerChoiceList.put(jsonObject);
                                JSONObject jsonObject2 = new JSONObject();
                                jsonObject2.put("answer","false");
                                jsonObject2.put("iscorrect","1");
                                answerChoiceList.put(jsonObject2);
                            }
                        } catch (JSONException e) {
                            Log.d("Mylogger :",e.toString());
                        }
                    } else if(answerType=="fillup"){
                        EditText correct_answer_FU = (EditText) findViewById(R.id.correct_answer_FU);
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("answer", correct_answer_FU.getText().toString());
                            jsonObject.put("iscorrect", "1");
                            answerChoiceList.put(jsonObject);
                        } catch (JSONException e) {
                            Log.d("Mylogger :",e.toString());
                        }
                    }

                    answerChoice = answerChoiceList.toString();

                    dataAPI.set_question(QuizContent.quiz_id, new_question_text, answerType, new_question_mark, answerChoice );
                    Intent intent = new Intent(CreateQuestion.this, LoadingScreen.class);
                    QuizContent.recreateCount =0;
                    startActivity(intent);
                    finish();
                }
            }
        });

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        userSelect = true;
        return false;
    }

    @Override
    public void onItemSelected(final AdapterView<?> parent, final View view, int position, long id) {

        if (userSelect) {

            if (parent.getId() == R.id.correct_answer_TF) {
                Log.d("Spinner name", "True");
                answerChoice = parent.getItemAtPosition(position).toString();
            }

            //RelativeLayout answer_group_layout = (RelativeLayout)findViewById(R.id.answer_group_layout);
            LinearLayout answer_choice_count_layout = (LinearLayout) findViewById(R.id.answer_choice_count_layout);
            LinearLayout correct_answer_TF_layout = (LinearLayout) findViewById(R.id.correct_answer_TF_layout);
            LinearLayout correct_answer_FU_layout = (LinearLayout) findViewById(R.id.correct_answer_FU_layout);
            LinearLayout correct_answer_MC_layout = (LinearLayout) findViewById(R.id.correct_answer_MC_layout);
            final LinearLayout answer_choice_MC_layout = (LinearLayout) findViewById(R.id.answer_choice_MC_layout);

            //answer_group_layout.setVisibility(View.VISIBLE);

            String item = parent.getItemAtPosition(position).toString();
            if (item.toLowerCase().contains("multiple choice")) {
                correct_answer_FU_layout.setVisibility(View.GONE);
                correct_answer_TF_layout.setVisibility(View.GONE);
                answer_choice_count_layout.setVisibility(View.VISIBLE);
                answer_choice_MC_layout.setVisibility(View.VISIBLE);
                correct_answer_MC_layout.setVisibility(View.VISIBLE);

                final EditText answer_choice_count = (EditText)answer_choice_count_layout.findViewById(R.id.answer_choice_count);
                answer_choice_count.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(!hasFocus){
                            Log.d("===================","Got Out of Focus");
                            answer_choice_MC_layout.removeAllViews();
                            Integer a_count= Integer.valueOf(answer_choice_count.getText().toString());
                            for(int i=0;i<a_count;i++){
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                layoutParams.setMargins(dpToPixels(0),dpToPixels(10),dpToPixels(0),dpToPixels(0));
                                EditText et = new EditText(CreateQuestion.this);
                                et.setHint("Choice " + (i + 1));
                                et.setTextSize(dpToPixels(10));
                                et.setPadding(dpToPixels(10), dpToPixels(10), dpToPixels(10), dpToPixels(10));
                                et.setBackgroundResource(R.drawable.ic_user_border);
                                //et.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
                                //et.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
                                et.setLayoutParams(layoutParams);
                                answer_choice_MC_layout.addView(et);
                            }
                        }
                    }
                });
            } else if (item.toLowerCase().contains("true")) {
                answerType = "trueorfalse";
                answer_choice_count_layout.setVisibility(View.GONE);
                correct_answer_FU_layout.setVisibility(View.GONE);
                answer_choice_MC_layout.setVisibility(View.GONE);
                correct_answer_MC_layout.setVisibility(View.GONE);
                correct_answer_TF_layout.setVisibility(View.VISIBLE);
            } else if (item.toLowerCase().contains("fill up")) {
                answerType = "fillup";
                answer_choice_count_layout.setVisibility(View.GONE);
                correct_answer_TF_layout.setVisibility(View.GONE);
                answer_choice_MC_layout.setVisibility(View.GONE);
                correct_answer_MC_layout.setVisibility(View.GONE);
                correct_answer_FU_layout.setVisibility(View.VISIBLE);
            }
            Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        }
        else {
            userSelect = false;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
