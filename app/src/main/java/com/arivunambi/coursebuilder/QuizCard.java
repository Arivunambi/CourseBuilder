package com.arivunambi.coursebuilder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.ActionBarOverlayLayout;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by arivu on 6/3/2016.
 */
public class QuizCard extends LinearLayout {

    public String quiz_name="";
    public String quiz_id="";
    private ArrayList<String> quizArray = new ArrayList<String>( Arrays.asList("Quiz 1", "Quiz 2", "+"));

    public List<String> question1 = new ArrayList<String>( Arrays.asList("Graphical Interface","Commandline Interface"));
    public static HashMap<String, List<String>> question_map = new HashMap<>();
    public static List<String> choice_list;
    public List<String> binary_array = new ArrayList<String>(Arrays.asList("True","False"));

    public String question="";
    public String question_id="";
    public String question_type="";
    public String question_marks="";
    public JSONArray question_choice;
    public JSONArray question_answer;
    public JSONObject question_list_grouped = new JSONObject();
    public JSONArray question_id_list = new JSONArray();
    public DBHandler db;

    public int score=0;

    private int dpToPixels(int dps){
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;
    }

    public static String page = "Quiz";

    private void inflateLayout(final Context context) {
        if (HomeScreen.clearcache==1){
            clearSQLCache(context);
            HomeScreen.clearcache=0;
        }
        db = new DBHandler(context);
        if (page.equals("Quiz")){
            Log.d("MyLogger :=====", "inflate Quiz");
            if (CourseCard.editor.get("quiz_list")==null){ //to speedup
                backgroundVolley("GET", "http://auburn.edu/~azt0054/course_builder/user_control.php?fetch=quizzes&courseid=1","quiz_list");
            }
            Quiz(context);
            this.refreshDrawableState();
        }else if (page.equals("Question")){
            Log.d("MyLogger :=====", "inflate Question");
            Question(context);
            this.refreshDrawableState();
        }else if (page.equals("Score")) {
            Log.d("MyLogger :=====", "inflate Score");
            Score(context);
            this.refreshDrawableState();
        }
    }

    private void clearSQLCache(Context context){
        context.deleteDatabase("CourseBuilder");
    }

    private boolean checkCahce(String url, String key){
        String cacheRes = db.getURLCache(url);
        if (cacheRes != null && HomeScreen.useCache==1){
            Log.d("From cache:",cacheRes.toString());
            CourseCard.editor.put("APIResponse", "True");
            CourseCard.editor.put(key, cacheRes.toString());
            CourseCard.retry = 0;
            return true;
        }else{
            Log.d("From cache:","Not found");
            return false;
        }
    }

    private StringRequest requestResponse(final String method, final String urlApi, final String key, final ProgressBar pg){
        if (method == "GET") {
            StringRequest request = new StringRequest(Request.Method.GET, urlApi,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("MyLogger actual ",response.toString());
                            CourseCard.editor.put("APIResponse", "True");
                            CourseCard.editor.put(key, response.toString());
                            db.setURLCache(urlApi,response.toString()); //update cache
                            CourseCard.retry = 0;
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("MyLogger ", error.toString());
                            NetworkResponse er_response = error.networkResponse;
                            if (er_response != null && er_response.statusCode==404) {
                                if (CourseCard.retry < 3) {
                                    CourseCard.retry = CourseCard.retry + 1;
                                    Log.d("MyLogger ","Retrying...");
                                    //requestResponse(method, urlApi, key, pg);
                                } else {
                                    CourseCard.retry = 0;
                                }
                            }else {
                                CourseCard.retry = 0;
                            }
                        }
                    }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("User-Agent", "Mozilla/5.0");
                    params.put("Accept-Language", "en");
                    return params;
                }
            };

            request.setShouldCache(false);   //*****turn false on prod
            return request;
        }
        return null;
    }

    private void backgroundVolley(final String method, final String urlApi, final String key){

        this.removeAllViews();
        final ProgressBar pg = new ProgressBar(getContext());
        pg.setId(R.id.q_pg);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.addView(pg,params);
        boolean cached = checkCahce(urlApi,key);
        if (!cached) {
            final RequestQueue syncqueue = Volley.newRequestQueue(getContext());
            StringRequest request = requestResponse(method, urlApi, key, pg);
            if (request != null) {
                syncqueue.add(request);
                syncqueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
                    @Override
                    public void onRequestFinished(Request<String> request) {
                        if (CourseCard.retry == 0) {
                            inflateLayout(getContext());
                            pg.setVisibility(GONE);
                            Log.d("MyLogger actual ", "finished");
                        } else {
                            request = requestResponse(method, urlApi, key, pg);
                            syncqueue.add(request);
                        }

                    }
                });
            }
        }else{
            inflateLayout(getContext());
            pg.setVisibility(GONE);
        }
    }

    private void Quiz(final Context context){
        this.removeAllViews();
        JSONArray quiz_list = null;//dataAPI.get_quizzes();
        try{
            String json_string = CourseCard.editor.get("quiz_list");
            if (json_string.contains("success")) {
                JSONObject jsonObject = new JSONObject(json_string);
                quiz_list = jsonObject.getJSONArray("success");
            }
        }catch (Exception e){
            Log.d("Mylogger ",e.toString());
        }

        this.setOrientation(LinearLayout.VERTICAL);
        Integer quiz_list_length;
        if (quiz_list != null) {
            Log.d("MyLogger quiz_list:", quiz_list.toString());
            if (MainActivity.privilege<2){
                quiz_list_length = quiz_list.length()-1;
            }
            quiz_list_length = quiz_list.length();
        }else{
            Log.d("MyLogger quiz_list:", "null");
            quiz_list_length = -1;
        }
        for(int i = 0; i <= quiz_list_length; i++) {
            try{
                quiz_name = quiz_list.getJSONObject(i).getString("name");
                quiz_id = quiz_list.getJSONObject(i).getString("id");
            } catch (JSONException e) {
                Log.d("Mylogger :",e.toString());
            }
            LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, this.dpToPixels(100));
            params.setMargins(this.dpToPixels(5), this.dpToPixels(10), this.dpToPixels(5), this.dpToPixels(0));
            params.height = this.dpToPixels(100);

            CardView card = new CardView(context, null, 0);
            card.setLayoutParams(params);
            card.setCardBackgroundColor(Color.parseColor("#303F9F"));

            LinearLayout cardInner = new LinearLayout(context);
            cardInner.setOrientation(LinearLayout.VERTICAL);

            TextView card_title = new TextView(context);
            card_title.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
            ));
            card_title.setHeight(this.dpToPixels(45));
            card_title.setPadding(0, this.dpToPixels(0), 0, 0);
            card_title.setTextSize((float) this.dpToPixels(12));
            card_title.setGravity(Gravity.CENTER);
            card_title.setTextColor(Color.parseColor("#ffffff"));

            TextView card_desc = new TextView(context);
            card_desc.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
            ));
            card_desc.setHeight(this.dpToPixels(55));
            card_desc.setPadding(0, this.dpToPixels(5), 0, 0);
            if (i==quiz_list.length()){
                //if (MainActivity.privilege>1) {
                    card_title.setText("+");
                    card_desc.setText("Add Quiz");
                //}
            }else {
                card_title.setText(quiz_name);
                card_desc.setText("Description");
            }
            card_desc.setTextSize((float) this.dpToPixels(12));
            card_desc.setGravity(Gravity.CENTER);
            card_desc.setTextColor(Color.parseColor("#ffffff"));
            card_desc.setBackgroundColor(Color.parseColor("#dee0e2"));

            cardInner.addView(card_title);
            cardInner.addView(card_desc);
            card.addView(cardInner);

            final String final_quiz_id =quiz_id;//specially for call
            final String final_quiz_name =quiz_name;//specially for call

            if (i==quiz_list.length()){
                card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // item clicked
                        Log.d("Clicked: ", Integer.toString(v.getId()));
                        context.startActivity(new Intent(context.getApplicationContext(), CreateQuiz.class));
                    }
                });
            }else {
                card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        quiz_id = final_quiz_id;
                        quiz_name = final_quiz_name;
                        String urlApi = "http://auburn.edu/~azt0054/course_builder/user_control.php?fetch=questions&quizid="+quiz_id;
                        Log.d("Clicked: ","Quiz screen===");
                        page = "Question";
                        CourseCard.page = "Question";
                        backgroundVolley("GET", urlApi, "question_list");
                    }
                });
            }

            this.addView(card);
        }
    }


    private void Question(final Context context){
        this.removeAllViews();
        JSONArray question_list = null;//dataAPI.get_questions(quiz_id);
        try{
            String json_string = CourseCard.editor.get("question_list");
            if (json_string.contains("success")) {
                JSONObject jsonObject = new JSONObject(json_string);
                question_list = jsonObject.getJSONArray("success");
            }
        }catch (Exception e){
            Log.d("Mylogger ",e.toString());
        }

        if (question_list != null) {
            Log.d("MyLogger que_list :", question_list.toString());
        }
        else {
            question_list = new JSONArray();
        }
        for (int j =0; j < question_list.length(); j++){
            try {
                String q_id = ((JSONObject) question_list.get(j)).getString("id").toString();
                if (question_list_grouped.has(q_id)){
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

            LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
            params.setMargins(this.dpToPixels(5), this.dpToPixels(10), this.dpToPixels(5), this.dpToPixels(0));

            final CardView card = new CardView(context, null, 0);
            card.setLayoutParams(params);
            card.setCardBackgroundColor(Color.parseColor("#303F9F"));

            final LinearLayout cardInner = new LinearLayout(context);
            cardInner.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            cardInner.setOrientation(LinearLayout.VERTICAL);

            final TextView card_title = new TextView(context);
            card_title.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
            ));
            card_title.setHeight(this.dpToPixels(45));
            card_title.setPadding(0, this.dpToPixels(0), this.dpToPixels(5), 0);
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

                answer_choices.setPadding(this.dpToPixels(3), this.dpToPixels(5), this.dpToPixels(3), this.dpToPixels(3));
                answer_choices.setGravity(Gravity.LEFT);
                answer_choices.setOrientation(VERTICAL);
                answer_choices.setBackgroundColor(Color.parseColor("#dee0e2"));
                answer_choices.setVisibility(GONE);

                cardInner.addView(answer_choices);
            }


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
                            score=0;
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
                            page="Score";
                            CourseCard.page = "Score";
                            //backgroundVolley("GET", "", "score_list");
                            inflateLayout(context);

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
                    }
                });
            }
            this.addView(card);
        }
    }


    private void Score(final Context context){
        this.removeAllViews();
        LayoutParams s_mparams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        LayoutParams s_aparams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);

        LinearLayout s_mlayout = new LinearLayout(context);
        s_mlayout.setId(R.id.s_mlayout);
        s_mlayout.setLayoutParams(s_mparams);
        s_mlayout.setGravity(Gravity.CENTER);
        s_mlayout.setOrientation(VERTICAL);

        TextView s_slable = new TextView(context);
        s_slable.setId(R.id.s_slable);
        s_slable.setGravity(Gravity.CENTER);
        s_slable.setText("Your Score");
        s_slable.setPadding(0,dpToPixels(20),0,dpToPixels(20));
        s_slable.setTextSize(dpToPixels(10));
        s_mlayout.addView(s_slable);

        TextView s_stxt = new TextView(context);
        s_stxt.setBackgroundResource(R.drawable.ic_circle);
        s_stxt.setGravity(Gravity.CENTER);
        s_stxt.setId(R.id.s_stxt);
        s_stxt.setWidth(dpToPixels(180));
        s_stxt.setHeight(dpToPixels(180));
        s_stxt.setTextSize(78);
        s_stxt.setTextColor(Color.parseColor("#FFFFFF"));
        s_stxt.setText(Integer.toString(score));
        s_mlayout.addView(s_stxt,s_aparams);

        TextView s_rate = new TextView(context);
        s_rate.setId(R.id.s_rate);
        s_rate.setGravity(Gravity.CENTER);
        s_rate.setPadding(0,dpToPixels(40),0,0);
        s_rate.setText("Rate the Quiz");
        s_rate.setTextSize(dpToPixels(10));
        s_mlayout.addView(s_rate);

        RatingBar s_ratebar = new RatingBar(context);
        s_ratebar.setId(R.id.s_ratebar);
        s_ratebar.setNumStars(4);
        s_ratebar.setStepSize((float) 0.5);
        s_ratebar.setRating(0);
        s_mlayout.addView(s_ratebar,s_aparams);

        this.addView(s_mlayout,s_mparams);

    }

    public QuizCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateLayout(context);
    }

    public QuizCard(Context context) {
        super(context);
        inflateLayout(context);
    }

}

