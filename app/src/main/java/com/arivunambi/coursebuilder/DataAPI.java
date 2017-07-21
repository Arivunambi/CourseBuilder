package com.arivunambi.coursebuilder;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.util.AndroidException;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * Created by arivu on 10/18/2016.
 */


public class DataAPI extends LoadingScreen {

    private Context ctx;
    private String responseApi;
    final SharedPreferences settings;
    final SharedPreferences.Editor editor;
    private JSONArray course_list = new JSONArray();
    private JSONArray article_list = new JSONArray();
    private JSONArray article_content_list = new JSONArray();
    private JSONArray quiz_list = new JSONArray();
    private JSONArray question_list = new JSONArray();

    public static int backgroundFlag=0;

    public DataAPI(Context ctx) {
        this.ctx = ctx;
        settings = this.ctx.getSharedPreferences(HomeScreen.PREFS_NAME, 0);
        if (settings == null){
            editor = null;
        }else{
            editor = settings.edit();
        }
    }

    private void AsyncDataRequestor(String method, String urlApi) {
        RequestQueue asyncqueue = Volley.newRequestQueue(this.ctx);

        if (method == "GET") {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, urlApi,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Mylogger ", "That did work! " + response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Mylogger ", "That didn't work!");
                }
            });
            asyncqueue.add(stringRequest);
        } else {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, urlApi,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Mylogger ", "That did work!");
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Mylogger ", "That didn't work!");
                }
            });
            asyncqueue.add(stringRequest);
        }
    }

    private void SyncDataRequestor(String method, String urlApi, final String key) {
        RequestQueue syncqueue = Volley.newRequestQueue(this.ctx);
        //syncqueue.getCache().clear();
        //clearResponse(key);

        Log.d("MyLogger sync response ",((Integer) backgroundFlag).toString());

        if (method.equals("GET") && backgroundFlag==0) {
            StringRequest request = new StringRequest(Request.Method.GET, urlApi,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("MyLogger response ",response.toString());
                            editor.putBoolean("APIResponse", true);
                            editor.putString(key, response.toString());
                            editor.commit();
                            if (backgroundFlag==2 ){
                                backgroundFlag = 0;
                                //LoadingScreen.finisher.finish();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("MyLogger ", error.toString());
                        }
                    });
            request.setShouldCache(false);
            syncqueue.add(request);

        }else{
            Log.d("MyLogger : ", "sending for long operation");
            new LongOperation(method, urlApi, key, this.ctx).execute();
        }
        clearResponse(key);
    }

    private void SyncDataRequestor(String method, String urlApi, final HashMap postMap, final String key) {
        RequestQueue syncqueue = Volley.newRequestQueue(this.ctx);
        if (method == "POST") {
            StringRequest request = new StringRequest(Request.Method.POST, urlApi,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("MyLogger ",response.toString());
                            editor.putString(key, response.toString());
                            editor.commit();
                            if (backgroundFlag==0){
                                Log.d("Mylogger :", "background");
                                LoadingScreen.finisher.finish();
                            }
                            else {
                                backgroundFlag = 2;
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("MyLogger ",error.toString());
                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> hashMap = postMap;

                    return hashMap;
                }
            };
            request.setShouldCache(false);
            syncqueue.add(request);
        }

        //checkResponse();
        //String APIResponseData = settings.getString("APIResponseData", "");
        //Log.d("MyResponse ",APIResponseData);
        clearResponse(key);
    }

    public void checkResponse(){
        boolean hasAPIResponse = settings.getBoolean("APIResponse", false);
        long start_time = System.currentTimeMillis();
        long wait_time = 8000;
        long end_time = start_time + wait_time;
        while(System.currentTimeMillis()<end_time && !hasAPIResponse){
            hasAPIResponse = settings.getBoolean("APIResponse", false);
        }
    }

    public void clearResponse(String key){
        editor.putBoolean("APIResponse", false);
        //editor.putString(key, "");
        editor.commit();
    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        String method;
        String urlApi;
        String key;
        Context ctx;

        public LongOperation(String method, String urlApi, final String key, Context ctx) {
            this.method = method;
            this.urlApi = urlApi;
            this.key = key;
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d("Mylogger :", "Waiting for background");
            RequestQueue syncqueue = Volley.newRequestQueue(this.ctx);
            //Log.d("Mylogger :", " Post Executed");
            if (this.method == "GET") {
                StringRequest request = new StringRequest(Request.Method.GET, this.urlApi,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("MyLogger actual ",response.toString());
                                editor.putBoolean("APIResponse", true);
                                editor.putString(key, response.toString());
                                editor.commit();
                                Log.d("MyLogger actual ","before finishing........"+ ((Integer) backgroundFlag).toString());
                                if (backgroundFlag>0){
                                    if (backgroundFlag==2){
                                        LoadingScreen.finisher.finish();
                                    }
                                    backgroundFlag = 0;
                                    Log.d("MyLogger actual ","finishing........");
                                    //LoadingScreen.finisher.finish();
                                }
                                /*if (key=="course_list" && backgroundFlag>0 ){
                                    while (backgroundFlag==1) {
                                        Log.d("Mylogger :", "not finished");
                                    }
                                    backgroundFlag = 0;
                                    LoadingScreen.finisher.finish();
                                }*/

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("MyLogger ", error.toString());
                            }
                        });
                request.setShouldCache(false);
                syncqueue.add(request);

            }
            clearResponse(key);
            //if (backgroundFlag>0 ){
              //  while (backgroundFlag==1 || LoadingScreen.status==0) {
                    //Log.d("Mylogger :", "not finished");
               // }
            //}
            //SLoadingScreen.finisher.finish();
            Log.d("Mylogger :", "Executed");
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("Mylogger :", " Post Executed");
            //Log.d("Mylogger :", " closing background");
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {
            Log.d("Mylogger :", " Pre Executed=========");
            /*if (backgroundFlag==0 ) {
                LoadingScreen.finisher.finish();
            }*/


        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    public JSONArray get_courses() {
        String key = "course_list";
        backgroundFlag =0;
        Log.d("Mylogger DAPI",key);
        try {
            String json_string="";
            if (settings==null){
                json_string = "{\"success\":[{\"id\":\"1\",\"producer\":\"1\",\"name\":\"User Interface\"}," +
                        "{\"id\":\"2\",\"producer\":\"1\",\"name\":\"Database\"},{\"id\":\"3\",\"producer\":\"1\",\"name\":\"Algorithms\"},{\"id\":\"4\",\"producer\":\"1\",\"name\":\"Quality Assurance\"},{\"id\":\"5\",\"producer\":\"1\",\"name\":\"Operating System\"},{\"id\":\"6\",\"producer\":\"1\",\"name\":\"RFID\"}]}";
            }else{
                SyncDataRequestor("GET", "http://auburn.edu/~azt0054/course_builder/user_control.php?fetch=courses",key);
                json_string = settings.getString(key,"");
            }
            if (json_string.contains("success")){
                JSONObject jsonObject = new JSONObject(json_string);
                course_list = jsonObject.getJSONArray("success");
            }
        }catch (Exception e){
            Log.d("Mylogger ",e.toString());
        }
        return course_list;
    }

    public JSONArray get_articles(String course_id) {
        String key = "article_list";
        Log.d("Mylogger course id====",course_id.toString());
        backgroundFlag = 0;//1
        try {
            String json_string ="";
            if (settings==null) {
                json_string = "{\"success\":[{\"id\":\"1\",\"name\":\"Graphical Interface\",\"course\":\"1\",\"producer\":\"0\"}]}";
            }else {
                SyncDataRequestor("GET", "http://auburn.edu/~azt0054/course_builder/user_control.php?fetch=articles&courseid=" + course_id, key);
                json_string = settings.getString(key, "");
            }
            if (json_string.contains("success")){
                JSONObject jsonObject = new JSONObject(json_string);
                article_list = jsonObject.getJSONArray("success");
            }
        }catch (Exception e){
            Log.d("Mylogger ",e.toString());
        }
        return article_list;
    }

    public JSONArray get_articles_content(String article_id) {
        String key = "article_content_list";
        //backgroundFlag = 1;
        Log.d("MyloggerN in",key);
        try {
            String json_string ="";
            if (settings==null) {
                json_string = "{\"success\":[{\"id\":\"1\",\"data\":\"https:\\/\\/www.easychair.org\\/publications\\/easychair.docx\",\"type\":\"docx\",\"article\":\"1\"},{\"id\":\"2\",\"data\":\"http:\\/\\/www.tutorialspoint.com\\/android\\/android_tutorial.pdf\",\"type\":\"pdf\",\"article\":\"1\"},{\"id\":\"3\",\"data\":\"http:\\/\\/opendatakit.org\\/wp-content\\/uploads\\/static\\/sample.xls\",\"type\":\"xls\",\"article\":\"1\"},{\"id\":\"4\",\"data\":\"https:\\/\\/acdbio.com\\/sites\\/default\\/files\\/sample.ppt\",\"type\":\"ppt\",\"article\":\"1\"},{\"id\":\"5\",\"data\":\"https:\\/\\/archive.org\\/download\\/ksnn_compilation_master_the_internet\\/ksnn_compilation_master_the_internet_512kb.mp4\",\"type\":\"mp4\",\"article\":\"1\"},{\"id\":\"6\",\"data\":\"https:\\/\\/designmodo.com\\/wp-content\\/uploads\\/2013\\/05\\/Player-Music-Player-App-by-Ilya-Boruhov.jpg\",\"type\":\"jpg\",\"article\":\"1\"},{\"id\":\"7\",\"data\":\"Android provides a rich application framework that allows you to build innovative apps and games for mobile devices in a Java language environment.The documents listed in the left navigation provide details about how to build apps using Android's various APIs.\",\"type\":\"text\",\"article\":\"1\"}]}";
            }else {
                SyncDataRequestor("GET", "http://auburn.edu/~azt0054/course_builder/user_control.php?fetch=articlecontent&articleid="+article_id,key);
                json_string = settings.getString(key,"");
            }
            if (json_string.contains("success")){
                JSONObject jsonObject = new JSONObject(json_string);
                article_content_list = jsonObject.getJSONArray("success");
            }
        }catch (Exception e){
            Log.d("Mylogger ",e.toString());
        }
        return article_content_list;
    }

    public JSONArray get_quizzes() {
        String key = "quiz_list";
        Log.d("Mylogger ",key);
        try {
            String json_string ="";
            if (settings==null) {
                json_string = "{\"success\":[{\"id\":\"1\",\"course\":\"1\",\"name\":\"Quiz 1\"},{\"id\":\"2\",\"course\":\"1\",\"name\":\"Quiz 2\"},{\"id\":\"3\",\"course\":\"1\",\"name\":\"Quiz 3\"}]}";
            }else {
                SyncDataRequestor("GET", "http://auburn.edu/~azt0054/course_builder/user_control.php?fetch=quizzes&courseid=1",key);
                json_string = settings.getString(key,"");
            }
            if (json_string.contains("success")){
                JSONObject jsonObject = new JSONObject(json_string);
                quiz_list = jsonObject.getJSONArray("success");
            }
        }catch (Exception e){
            Log.d("Mylogger ",e.toString());
        }
        return quiz_list;
    }


    public JSONArray get_questions(String quiz_id) {
        String key = "question_list";
        try {
            String json_string ="";
            if (settings==null) {
                json_string = "{\"success\":[{\"id\":\"1\",\"quiz\":\"1\",\"data\":\"In which type of interface users provide commands?\",\"type\":\"mcsa\",\"marks\":\"1\",\"answer\":\"Graphical Interface\",\"iscorrect\":\"0\"}]}";
            }else {
                SyncDataRequestor("GET", "http://auburn.edu/~azt0054/course_builder/user_control.php?fetch=questions&quizid="+quiz_id,key);
                json_string = settings.getString(key,"");
            }
            if (json_string.contains("success")){
                JSONObject jsonObject = new JSONObject(json_string);
                question_list = jsonObject.getJSONArray("success");
            }
        }catch (Exception e){
            Log.d("Mylogger ",e.toString());
        }
        return question_list;
    }


    public String set_course(String course_name) {
        String userId = MainActivity.userID;
        String key = "set_course";
        String response ="";
        try {
            HashMap<String, String> postMap = new HashMap<String, String>();
            postMap.put("add", "course");
            postMap.put("course_name", course_name);
            postMap.put("user_id", userId);

            backgroundFlag = 1;
            SyncDataRequestor("POST", "http://auburn.edu/~azt0054/course_builder/user_control.php", postMap, key);
            Log.d("Mylogger bg ",((Integer) backgroundFlag).toString());
            get_courses();
            String json_string = settings.getString(key,"");
            if (json_string.contains("success")){
                //JSONObject jsonObject = new JSONObject(json_string);
                response = json_string;//jsonObject.getJSONArray("success").toString();
            }
        }catch (Exception e){
            Log.d("Mylogger ",e.toString());
        }
        return response;
    }

    public String set_article(String course_id, String article_name, String articlecontent ) {
        String userId = MainActivity.userID;
        String key = "set_article";
        String response ="";
        try {
            HashMap<String, String> postMap = new HashMap<String, String>();
            postMap.put("add", "article");
            postMap.put("article_name", article_name);
            postMap.put("user_id", userId);
            postMap.put("course_id", course_id);
            postMap.put("articlecontent", articlecontent);

            backgroundFlag = 1;
            SyncDataRequestor("POST", "http://auburn.edu/~azt0054/course_builder/user_control.php", postMap, key);
            Log.d("Mylogger bg ",((Integer) backgroundFlag).toString());
            get_articles(course_id);
            String json_string = settings.getString(key,"");
            if (json_string.contains("success")){
                //JSONObject jsonObject = new JSONObject(json_string);
                response = json_string;//jsonObject.getJSONArray("success").toString();
            }
        }catch (Exception e){
            Log.d("Mylogger ",e.toString());
        }
        return response;
    }


    public String set_quiz(String quiz_name ) {
        String userId = MainActivity.userID;
        String key = "set_quiz";
        String response ="";
        try {
            HashMap<String, String> postMap = new HashMap<String, String>();
            postMap.put("add", "quiz");
            postMap.put("quiz_name", quiz_name);
            postMap.put("course_id", "1");

            backgroundFlag = 1;
            SyncDataRequestor("POST", "http://auburn.edu/~azt0054/course_builder/user_control.php", postMap, key);
            Log.d("Mylogger bg ",((Integer) backgroundFlag).toString());
            get_quizzes();
            String json_string = settings.getString(key,"");
            if (json_string.contains("success")){
                //JSONObject jsonObject = new JSONObject(json_string);
                response = json_string;//jsonObject.getJSONArray("success").toString();
            }
        }catch (Exception e){
            Log.d("Mylogger ",e.toString());
        }
        return response;
    }

    public String set_question(String quiz_id, String question, String type, String marks, String answers) {
        String userId = MainActivity.userID;
        String key = "set_question";
        String response ="";
        try {
            HashMap<String, String> postMap = new HashMap<String, String>();
            postMap.put("add", "question");
            postMap.put("question", question);
            postMap.put("quiz_id", quiz_id);
            postMap.put("type", type);
            postMap.put("marks", marks);
            postMap.put("answers", answers);

            backgroundFlag = 1;
            SyncDataRequestor("POST", "http://auburn.edu/~azt0054/course_builder/user_control.php", postMap, key);
            Log.d("Mylogger bg ",((Integer) backgroundFlag).toString());
            get_questions(quiz_id);
            String json_string = settings.getString(key,"");
            if (json_string.contains("success")){
                //JSONObject jsonObject = new JSONObject(json_string);
                response = json_string;//jsonObject.getJSONArray("success").toString();
            }
        }catch (Exception e){
            Log.d("Mylogger ",e.toString());
        }
        return response;
    }


}