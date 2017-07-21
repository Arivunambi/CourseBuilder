package com.arivunambi.coursebuilder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by arivu on 6/10/2016.
 */
public class QuestionCard extends LinearLayout {

    public static ArrayList<String> questionArray = new ArrayList<String>( Arrays.asList("In which type of interface users provide commands selecting from a menu?",
            "Select the type of interface you have used?",
            "Have you used form based interface?",
            "Give an example of menu based interface?",
            "+"));

    public List<String> choice_array = new ArrayList<String>( Arrays.asList("Graphical Interface","Commandline Interface", "Touch Input"));
    public List<String> question1 = new ArrayList<String>( Arrays.asList("Graphical Interface","Commandline Interface"));
    public static HashMap<String, List<String>> question_map = new HashMap<>();
    public static List<String> choice_list;
    public List<String> binary_array = new ArrayList<String>(Arrays.asList("True","False"));

    public String quiz_id="";
    public String question="";
    public String question_id="";
    public String question_type="";
    public String question_marks="";
    public JSONArray question_choice;
    public JSONArray question_answer;
    public JSONObject question_list_grouped = new JSONObject();
    public JSONArray question_id_list = new JSONArray();

    private int dpToPixels(int dps){
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;
    }

    private void inflateLayout(final Context context) {

        quiz_id = QuizContent.quiz_id;
        final DataAPI dataAPI = new DataAPI(context.getApplicationContext());
        JSONArray question_list = dataAPI.get_questions(quiz_id);
        Log.d("MyLogger article_list :",question_list.toString());

        for (int j =0; j < question_list.length(); j++){
            try {
                String q_id = ((JSONObject) question_list.get(j)).getString("id").toString();
                if (question_list_grouped.has(q_id)){
                    Log.d("MyLogger ","appending");
                    JSONObject jsonObject = question_list_grouped.getJSONObject(q_id);
                    JSONArray choice_list = jsonObject.getJSONArray("choice_list");
                    choice_list.put(((JSONObject) question_list.get(j)).getString("answer").toString());
                    jsonObject.remove("choice_list");
                    jsonObject.put("choice_list", choice_list);
                    JSONArray answer_list = jsonObject.getJSONArray("answer_list");
                    answer_list.put(((JSONObject) question_list.get(j)).getString("iscorrect").toString());
                    jsonObject.remove("answer_list");
                    jsonObject.put("answer_list", answer_list);
                    question_list_grouped.remove(q_id);
                    question_list_grouped.put(q_id, jsonObject);
                }else {
                    question_id_list.put(q_id);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("question_id", q_id);
                    jsonObject.put("question", ((JSONObject) question_list.get(j)).getString("data").toString());
                    jsonObject.put("question_type", ((JSONObject) question_list.get(j)).getString("type").toString());
                    jsonObject.put("question_marks", ((JSONObject) question_list.get(j)).getString("marks").toString());
                    JSONArray choice_list = new JSONArray();
                    choice_list.put(((JSONObject) question_list.get(j)).getString("answer").toString());
                    jsonObject.put("choice_list", choice_list);
                    JSONArray answer_list = new JSONArray();
                    answer_list.put(((JSONObject) question_list.get(j)).getString("iscorrect").toString());
                    jsonObject.put("answer_list", answer_list);
                    question_list_grouped.put(q_id, jsonObject);
                }
            } catch (JSONException e) {
                Log.d("MyLogger article_list :", e.toString());
            }
        }

        Log.d("MyLogger proccessed :",question_list_grouped.toString());


        this.setOrientation(LinearLayout.VERTICAL);
        final Integer question_id_list_length = question_id_list.length();
        /*if (MainActivity.privilege<2) {
            question_id_list_length = question_id_list.length()-1;
        }*/
        for(int i = 0; i <= question_id_list_length; i++) {
            try{
                JSONObject questionInfo = (JSONObject) question_list_grouped.getJSONObject(question_id_list.getString(i));
                question_id = question_id_list.getString(i);
                question = questionInfo.getString("question");
                question_type = questionInfo.getString("question_type");
                question_marks = questionInfo.getString("question_marks");
                question_choice = questionInfo.getJSONArray("choice_list");
                question_answer = questionInfo.getJSONArray("answer_list");
            } catch (JSONException e) {
                Log.d("Mylogger :",e.toString());
            }

            //LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, this.dpToPixels(100));//Test
            LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
            params.setMargins(this.dpToPixels(5), this.dpToPixels(10), this.dpToPixels(5), this.dpToPixels(0));
            //params.height = this.dpToPixels(100);//Test

            final CardView card = new CardView(context, null, 0);
            card.setLayoutParams(params);
            card.setCardBackgroundColor(Color.parseColor("#303F9F"));
            //card.setId(i);

            final LinearLayout cardInner = new LinearLayout(context);
            cardInner.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            cardInner.setOrientation(LinearLayout.VERTICAL);

            final TextView card_title = new TextView(context);
            card_title.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
            ));
            card_title.setHeight(this.dpToPixels(45));
            card_title.setPadding(0, this.dpToPixels(0), this.dpToPixels(5), 0);
            //card_title.setText(question_id_list.get(i));
            if (i!=question_id_list.length()){
                card_title.setText("Question "+Integer.toString(i+1));
                card_title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
            }else if (MainActivity.privilege>1) {
                card_title.setText("+");
            }else {
                card_title.setText("Submit");
                card_title.setBackgroundColor(Color.parseColor("#FF4081"));
            }
            card_title.setTextSize((float) this.dpToPixels(12));
            card_title.setGravity(Gravity.CENTER);
            card_title.setTextColor(Color.parseColor("#ffffff"));


            question_map.put("In which type of interface users provide commands for operations ?", question1);
            //question_map.put("Which is the common interface type ?", question2);
            choice_list = new ArrayList<String>(question_map.keySet());

            ExpandableListView expandableListView = new ExpandableListView(context);
            expandableListView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            expandableListView.setIndicatorBounds(expandableListView.getRight() - 40, expandableListView.getWidth());
            expandableListView.setBackgroundColor(Color.parseColor("#dee0e2"));
            expandableListView.setPadding(this.dpToPixels(5),0,this.dpToPixels(5),0);

            QuestionAdapter qAdapter = new QuestionAdapter(context,question_map,choice_list);
            expandableListView.setAdapter(qAdapter);

            final TextView card_desc = new TextView(context);
            card_desc.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
            ));
            card_desc.setHeight(this.dpToPixels(55));
            card_desc.setPadding(0, this.dpToPixels(5), 0, 0);
            card_desc.setTextSize((float) this.dpToPixels(5));//was 8
            card_desc.setGravity(Gravity.CENTER);
            card_desc.setTextColor(Color.parseColor("#000000"));//ffffff"));
            card_desc.setBackgroundColor(Color.parseColor("#dee0e2"));

            if (question_id_list_length==0 && MainActivity.privilege<2) {
                //pass;
            }else{
                cardInner.addView(card_title);
            }

            //final TextView answer_choices = new TextView(context);
            final LinearLayout answer_choices = new LinearLayout(context);
            Integer answer_choices_length = 0;

            if (i==question_id_list.length()){
                if (MainActivity.privilege>1) {
                    card_desc.setText("Add Question");
                    cardInner.addView(card_desc);//Test
                }
            }else {
                card_desc.setText(question);//Need to replace with dictionary that varies for each question//Test
                cardInner.addView(card_desc);//(expandableListView);
                answer_choices.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
                ));

                if (question_type.equals("mcsa")){
                    final RadioButton[] rb = new RadioButton[question_choice.length()];
                    RadioGroup rg = new RadioGroup(context); //create the RadioGroup
                    rg.setId(i+1000);
                    rg.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL
                    for(int k=0; k<question_choice.length(); k++){
                        try{
                            rb[k]  = new RadioButton(context);
                            rb[k].setText(question_choice.getString(k));
                            answer_choices_length += (rb[k].getLineCount() * rb[k].getLineHeight());
                            rb[k].setId(k + 100);
                            rg.addView(rb[k]);
                        } catch (JSONException e) {
                            Log.d("Mylogger :",e.toString());
                        }
                    }
                    answer_choices.addView(rg);
                }
                else if (question_type.equals("mcma")){
                    LinearLayout checkGroup = new LinearLayout(context);
                    checkGroup.setOrientation(VERTICAL);
                    checkGroup.setId(i+1000);
                    for(int j = 0; j < question_choice.length(); j++) {
                        try {
                            CheckBox choices = new CheckBox(context);
                            choices.setText(question_choice.getString(j));
                            checkGroup.addView(choices);
                            answer_choices_length += (choices.getLineCount() * choices.getLineHeight());
                            //answer_choices.addView(choices);
                        } catch (JSONException e) {
                            Log.d("Mylogger :",e.toString());
                        }
                    }
                    answer_choices.addView(checkGroup);

                }
                else if (question_type.equals("mcsat")){
                    final RadioButton[] rb = new RadioButton[binary_array.size()];
                    RadioGroup rg = new RadioGroup(context); //create the RadioGroup
                    rg.setId(i+1000);
                    rg.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL
                    for(int k=0; k<binary_array.size(); k++){
                        try {
                            rb[k]  = new RadioButton(context);
                            //rb[k].setText(binary_array.get(k));
                            rb[k].setText(question_choice.getString(k));
                            answer_choices_length += (rb[k].getLineCount() * rb[k].getLineHeight());
                            rb[k].setId(k + 100);
                            rg.addView(rb[k]);
                        } catch (JSONException e) {
                            Log.d("Mylogger :",e.toString());
                        }
                    }
                    answer_choices.addView(rg);
                }
                else if (question_type.equals("fillup")){
                    LinearLayout.LayoutParams text_box_params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
                    text_box_params.setMargins(this.dpToPixels(5), this.dpToPixels(10), this.dpToPixels(5), this.dpToPixels(5));
                    text_box_params.height = this.dpToPixels(80);//Test

                    EditText textbox = new EditText(context);
                    textbox.setId(i+1000);
                    textbox.setLayoutParams(text_box_params);
                    textbox.setBackgroundResource(R.drawable.ic_user_border);
                    textbox.setPadding(5,5,5,5);
                    textbox.setTextSize((float) this.dpToPixels(5));
                    answer_choices.addView(textbox);
                }

                /*
                //answer_choices.setHeight(this.dpToPixels(55));
                answer_choices.setPadding(this.dpToPixels(3), this.dpToPixels(5), this.dpToPixels(3), this.dpToPixels(3));
                answer_choices.setTextSize((float) this.dpToPixels(8));
                answer_choices.setGravity(Gravity.CENTER);
                answer_choices.setTextColor(Color.parseColor("#000000"));//ffffff"));
                answer_choices.setBackgroundColor(Color.parseColor("#dee0e2"));
                answer_choices.setText("Option1\nOption2\nOption3");
                answer_choices.setVisibility(GONE);
                */

                answer_choices.setPadding(this.dpToPixels(3), this.dpToPixels(5), this.dpToPixels(3), this.dpToPixels(3));
                answer_choices.setGravity(Gravity.LEFT);
                answer_choices.setOrientation(VERTICAL);
                answer_choices.setBackgroundColor(Color.parseColor("#dee0e2"));
                answer_choices.setVisibility(GONE);

                cardInner.addView(answer_choices);
            }


            //cardInner.addView(card_title);//Test
            //cardInner.addView(card_desc);//Test
            //cardInner.addView(expandableListView);//Test
            card.addView(cardInner);

            if (i==question_id_list.length()){
                card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // item clicked
                        if (MainActivity.privilege>1){
                            Log.d("Clicked count: ", Integer.toString(v.getId()));
                            context.startActivity(new Intent(context.getApplicationContext(), CreateQuestion.class));
                        }else{
                            int score=0;
                            for(int k=0;k<question_id_list_length;k++){
                                int cid = k+1000;
                                try{
                                    JSONObject questionInfo = (JSONObject) question_list_grouped.getJSONObject(question_id_list.getString(k));
                                    String q_id = question_id_list.getString(k);
                                    String q_type = questionInfo.getString("question_type");
                                    String q_marks = questionInfo.getString("question_marks");
                                    JSONArray q_choice = questionInfo.getJSONArray("choice_list");
                                    JSONArray q_answer = questionInfo.getJSONArray("answer_list");
                                    if (q_type.equals("mcsat") || q_type.equals("mcsa")){
                                        RadioGroup radioGroup = (RadioGroup)findViewById(cid);
                                        int radioButtonID = radioGroup.getCheckedRadioButtonId();
                                        View radioButton = radioGroup.findViewById(radioButtonID);
                                        int idx = radioGroup.indexOfChild(radioButton);
                                        if (q_answer.getString(idx).equals("1")){
                                            score+=1;
                                        }
                                        //Log.d("Mylogger :::::::",q_answer.toString());
                                    }else if (q_type.equals("fillup")) {
                                        EditText mEdit = (EditText) findViewById(cid);
                                        String fAns = mEdit.getText().toString().toLowerCase();
                                        if (q_choice.getString(0).toLowerCase().equals(fAns)) {
                                            score += 1;
                                        }
                                    }else if (q_type.equals("mcma")) {
                                        LinearLayout checkBoxAns = (LinearLayout)findViewById(cid);
                                        final int childcount = checkBoxAns.getChildCount();
                                        int wrongFlag =0;
                                        for (int i = 0; i < childcount; i++) {
                                            CheckBox cB = (CheckBox)checkBoxAns.getChildAt(i);
                                            if ((cB.isChecked() && q_answer.getString(i).equals("0")) ||
                                                    (!cB.isChecked() && q_answer.getString(i).equals("1"))) {
                                                wrongFlag = 1;
                                                break;
                                            }
                                            // Do something with v.
                                        }
                                        if (wrongFlag==0){
                                            score+=1;
                                        }
                                    }
                                } catch (JSONException e) {
                                    Log.d("Mylogger :",e.toString());
                                }

                            }
                            //Toast.makeText(context, "Your Quiz has been sumbitted", Toast.LENGTH_SHORT).show();
                            //Toast.makeText(context, "Your Score is "+Integer.toString(score),Toast.LENGTH_SHORT).show();
                            //TextView score_txt = (TextView)findViewById(R.id.score_txt);
                            //score_txt.setText(Integer.toString(score));
                            Intent intent = new Intent(context.getApplicationContext(), ScoreReport.class);
                            intent.putExtra("score",Integer.toString((score*100/question_id_list_length)));
                            context.startActivity(intent);

                        }
                    }
                });
            }else {
                final Integer finalAnswer_choices_length = answer_choices_length;
                card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (card.getHeight() >300){//was 200
                            card_title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
                            card.setMinimumHeight(dpToPixels(100));
                            card_desc.setMinimumHeight(dpToPixels(55));
                            answer_choices.setVisibility(GONE);

                        }
                        else {
                            int height_px_card_desc = card_desc.getLineCount() * card_desc.getLineHeight();
                            int height_px_card_choices = finalAnswer_choices_length;

                            card_title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up, 0);
                            card.setMinimumHeight(height_px_card_desc + height_px_card_choices);
                            card_desc.setMinimumHeight(height_px_card_desc);
                            answer_choices.setVisibility(VISIBLE);
                        }
                        // Dropdown tabs for question
                        Log.d("Clicked NoMatch: ", Integer.toString(card.getHeight()));
                       // context.startActivity(new Intent(context.getApplicationContext(), QuizContent.class));
                    }
                });
            }
            this.addView(card);
        }



        /*
        for(int i = 0; i < questionArray.size(); i++) {

            /*
            try{
                question_name = question_list.getJSONObject(i).getString("data");
                question_id = question_list.getJSONObject(i).getString("id");
            } catch (JSONException e) {
                Log.d("Mylogger :",e.toString());
            }
            /**
            //LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, this.dpToPixels(100));//Test
            LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
            params.setMargins(this.dpToPixels(5), this.dpToPixels(10), this.dpToPixels(5), this.dpToPixels(0));
            //params.height = this.dpToPixels(100);//Test

            final CardView card = new CardView(context, null, 0);
            card.setLayoutParams(params);
            card.setCardBackgroundColor(Color.parseColor("#303F9F"));
            //card.setId(i);

            final LinearLayout cardInner = new LinearLayout(context);
            cardInner.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            cardInner.setOrientation(LinearLayout.VERTICAL);

            final TextView card_title = new TextView(context);
            card_title.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
            ));
            card_title.setHeight(this.dpToPixels(45));
            card_title.setPadding(0, this.dpToPixels(3), this.dpToPixels(5), 0);
            card_title.setText(questionArray.get(i));
            if (i!=questionArray.size()-1){
                card_title.setText("Question "+Integer.toString(i+1));
                card_title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
            }
            card_title.setTextSize((float) this.dpToPixels(15));
            card_title.setGravity(Gravity.CENTER);
            card_title.setTextColor(Color.parseColor("#ffffff"));


            question_map.put("In which type of interface users provide commands for operations ?", question1);
            //question_map.put("Which is the common interface type ?", question2);
            choice_list = new ArrayList<String>(question_map.keySet());

            ExpandableListView expandableListView = new ExpandableListView(context);
            expandableListView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            expandableListView.setIndicatorBounds(expandableListView.getRight() - 40, expandableListView.getWidth());
            expandableListView.setBackgroundColor(Color.parseColor("#dee0e2"));
            expandableListView.setPadding(this.dpToPixels(5),0,this.dpToPixels(5),0);

            QuestionAdapter qAdapter = new QuestionAdapter(context,question_map,choice_list);
            expandableListView.setAdapter(qAdapter);

            final TextView card_desc = new TextView(context);
            card_desc.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
            ));
            card_desc.setHeight(this.dpToPixels(55));
            card_desc.setPadding(0, this.dpToPixels(5), 0, 0);
            card_desc.setTextSize((float) this.dpToPixels(8));
            card_desc.setGravity(Gravity.CENTER);
            card_desc.setTextColor(Color.parseColor("#000000"));//ffffff"));
            card_desc.setBackgroundColor(Color.parseColor("#dee0e2"));

            cardInner.addView(card_title);

            //final TextView answer_choices = new TextView(context);
            final LinearLayout answer_choices = new LinearLayout(context);
            Integer answer_choices_length = 0;

            if (i==questionArray.size()-1){
                card_desc.setText("Add Question");
                cardInner.addView(card_desc);//Test
            }else {
                card_desc.setText(questionArray.get(i));//Need to replace with dictionary that varies for each question//Test
                cardInner.addView(card_desc);//(expandableListView);
                answer_choices.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
                ));

                if (i==0){
                    final RadioButton[] rb = new RadioButton[choice_array.size()];
                    RadioGroup rg = new RadioGroup(context); //create the RadioGroup
                    rg.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL
                    for(int k=0; k<choice_array.size(); k++){
                        rb[k]  = new RadioButton(context);
                        rb[k].setText(choice_array.get(k));
                        answer_choices_length += (rb[k].getLineCount() * rb[k].getLineHeight());
                        rb[k].setId(k + 100);
                        rg.addView(rb[k]);
                    }
                    answer_choices.addView(rg);
                }
                else if (i==1){
                    for(int j = 0; j < choice_array.size(); j++) {
                        CheckBox choices = new CheckBox(context);
                        choices.setText(choice_array.get(j));
                        answer_choices_length += (choices.getLineCount() * choices.getLineHeight());
                        answer_choices.addView(choices);
                    }
                }
                else if (i==2){
                    final RadioButton[] rb = new RadioButton[binary_array.size()];
                    RadioGroup rg = new RadioGroup(context); //create the RadioGroup
                    rg.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL
                    for(int k=0; k<binary_array.size(); k++){
                        rb[k]  = new RadioButton(context);
                        rb[k].setText(binary_array.get(k));
                        answer_choices_length += (rb[k].getLineCount() * rb[k].getLineHeight());
                        rb[k].setId(k + 100);
                        rg.addView(rb[k]);
                    }
                    answer_choices.addView(rg);
                }
                else if (i==3){
                    LinearLayout.LayoutParams text_box_params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
                    text_box_params.setMargins(this.dpToPixels(5), this.dpToPixels(10), this.dpToPixels(5), this.dpToPixels(5));
                    text_box_params.height = this.dpToPixels(80);//Test

                    EditText textbox = new EditText(context);
                    textbox.setLayoutParams(text_box_params);
                    textbox.setBackgroundResource(R.drawable.ic_user_border);
                    textbox.setPadding(5,5,5,5);
                    answer_choices.addView(textbox);
                }

                /*
                //answer_choices.setHeight(this.dpToPixels(55));
                answer_choices.setPadding(this.dpToPixels(3), this.dpToPixels(5), this.dpToPixels(3), this.dpToPixels(3));
                answer_choices.setTextSize((float) this.dpToPixels(8));
                answer_choices.setGravity(Gravity.CENTER);
                answer_choices.setTextColor(Color.parseColor("#000000"));//ffffff"));
                answer_choices.setBackgroundColor(Color.parseColor("#dee0e2"));
                answer_choices.setText("Option1\nOption2\nOption3");
                answer_choices.setVisibility(GONE);
                /**

                answer_choices.setPadding(this.dpToPixels(3), this.dpToPixels(5), this.dpToPixels(3), this.dpToPixels(3));
                answer_choices.setGravity(Gravity.LEFT);
                answer_choices.setOrientation(VERTICAL);
                answer_choices.setBackgroundColor(Color.parseColor("#dee0e2"));
                answer_choices.setVisibility(GONE);

                cardInner.addView(answer_choices);
            }


            //cardInner.addView(card_title);//Test
            //cardInner.addView(card_desc);//Test
            //cardInner.addView(expandableListView);//Test
            card.addView(cardInner);

            if (i==questionArray.size()-1){
                card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // item clicked
                        Log.d("Clicked: ", Integer.toString(v.getId()));
                        context.startActivity(new Intent(context.getApplicationContext(), CreateQuestion.class));
                    }
                });
            }else {
                final Integer finalAnswer_choices_length = answer_choices_length;
                card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (card.getHeight() >200){
                            card_title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
                            card.setMinimumHeight(dpToPixels(100));
                            card_desc.setMinimumHeight(dpToPixels(55));
                            answer_choices.setVisibility(GONE);

                        }
                        else {
                            int height_px_card_desc = card_desc.getLineCount() * card_desc.getLineHeight();
                            int height_px_card_choices = finalAnswer_choices_length;

                            card_title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up, 0);
                            card.setMinimumHeight(height_px_card_desc + height_px_card_choices);
                            card_desc.setMinimumHeight(height_px_card_desc);
                            answer_choices.setVisibility(VISIBLE);
                        }
                        // Dropdown tabs for question
                        Log.d("Clicked: ", Integer.toString(v.getId()));
                       // context.startActivity(new Intent(context.getApplicationContext(), QuizContent.class));
                    }
                });
            }

            this.addView(card);
        }*/
    }

    public QuestionCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateLayout(context);
    }


    public QuestionCard(Context context) {
        super(context);
        inflateLayout(context);
    }
}
