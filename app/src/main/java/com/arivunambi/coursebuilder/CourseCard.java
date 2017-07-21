package com.arivunambi.coursebuilder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.EventLog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by arivu on 6/2/2016.
 */
public class CourseCard extends LinearLayout {

    private JSONArray course_list = new JSONArray();
    public static HashMap<String, String> editor = new HashMap<>();
    public static Integer retry = 0;
    public String course_name="";
    public String course_id="";

    public String article_name="";
    public String article_id="";

    public String vidAddress = "";
    public String imgAddress = "";
    public String articleText = "";

    public String downloadLink ="";
    public String downloadName ="";
    public String docframe = "";

    private VideoView a_videop;
    private int position = 0;
    private ProgressDialog progressDialog;
    private MediaController mediaControls;

    public String quiz_name="";

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
    public DBHandler db;


    private int dpToPixels(int dps){
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;
    }
    public static String page = "Course";

    private void inflateLayout(final Context context) {
        if (HomeScreen.clearcache==1){
            clearSQLCache(context);
            HomeScreen.clearcache=0;
        }
        db = new DBHandler(context);
        if (page.equals("Course")){
            Log.d("MyLogger :=====", "inflate Course");
            if (editor.get("course_list")==null){ //to speedup
                backgroundVolley("GET", "http://auburn.edu/~azt0054/course_builder/user_control.php?fetch=courses","course_list");
            }
            Course(context);
        }else if (page.equals("Loading")){
            Log.d("MyLogger :=====", "inflate Loading");
            Loading(context);
        }else if (page.equals("Article")){
            Log.d("MyLogger :=====", "inflate Article");
            Article(context);
        }else if (page.equals("Content")){
            Log.d("MyLogger :=====", "inflate Content");
            Content(context);
        }else if (page.equals("Document")){
            removeAllExcept();
            Log.d("MyLogger :=====", "inflate Document");
            Document(context);
            this.refreshDrawableState();
        }else if (page.equals("Quiz")){
            Log.d("MyLogger :=====", "inflate Quiz");
            if (editor.get("quiz_list")==null){
                backgroundVolley("GET", "http://auburn.edu/~azt0054/course_builder/user_control.php?fetch=quizzes&courseid=1","quiz_list");
            }
            Quiz(context);
            this.refreshDrawableState();
        }else if (page.equals("Question")){
            Log.d("MyLogger :=====", "inflate Question");
            Question(context);
            this.refreshDrawableState();
        }
    }

    public void removeAllExcept(){
        Log.d("MyLogger:::::::",Integer.toString(this.getChildCount()));
        for (int i = 0; i < this.getChildCount(); i++) {
            if (i==this.getChildCount()-1){

            }else{
                this.removeViewAt(i);
            }
        }
    }

    private void clearSQLCache(Context context){
        context.deleteDatabase("CourseBuilder");
    }
    private boolean checkCahce(String url, String key){
        String cacheRes = db.getURLCache(url);
        if (cacheRes != null && HomeScreen.useCache==1){
            Log.d("From cache:",cacheRes.toString());
            editor.put("APIResponse", "True");
            editor.put(key, cacheRes.toString());
            retry = 0;
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
                            editor.put("APIResponse", "True");
                            editor.put(key, response.toString());
                            db.setURLCache(urlApi,response.toString()); //update cache
                            retry = 0;
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("MyLogger ", error.toString());
                            NetworkResponse er_response = error.networkResponse;
                            if (er_response != null && er_response.statusCode==404) {
                                if (retry < 3) {
                                    retry = retry + 1;
                                    Log.d("MyLogger ","Retrying...");
                                    //requestResponse(method, urlApi, key, pg);
                                } else {
                                    retry = 0;
                                }
                            }else {
                                retry = 0;
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
        pg.setId(R.id.c_pg);
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
                        if (retry == 0) {
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

    private  void Loading(final Context context) {
        this.removeAllViews();
        ProgressBar pg = new ProgressBar(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.addView(pg,params);
    }

    public CourseCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateLayout(context);
    }

    public CourseCard(Context context) {
        super(context);
        inflateLayout(context);
    }

    private  void Course(final Context context){
        this.removeAllViews();
        try{
            String json_string = editor.get("course_list");
            if (json_string.contains("success")) {
                JSONObject jsonObject = new JSONObject(json_string);
                course_list = jsonObject.getJSONArray("success");
            }
        }catch (Exception e){
            Log.d("Mylogger ",e.toString());
        }

        Log.d("MyLogger course_list:",course_list.toString());
        this.setOrientation(LinearLayout.VERTICAL);
        Integer course_list_length = course_list.length();
        if (MainActivity.privilege<2) {
            course_list_length = course_list.length()-1;
        }
        for(int i = 0; i <= course_list_length; i++) {
            try{
                if (i<course_list.length()){
                    course_name = course_list.getJSONObject(i).getString("name");
                    course_id = course_list.getJSONObject(i).getString("id");
                }
            } catch (JSONException e) {
                Log.d("Mylogger :",e.toString());
            }


            LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, this.dpToPixels(100));
            params.setMargins(this.dpToPixels(5), this.dpToPixels(10), this.dpToPixels(5), this.dpToPixels(0));
            params.height = this.dpToPixels(100);

            CardView card = new CardView(context, null, 0);
            card.setLayoutParams(params);
            card.setCardBackgroundColor(Color.parseColor("#303F9F"));
            card.setId(i);

            LinearLayout cardInner = new LinearLayout(context);
            cardInner.setOrientation(LinearLayout.VERTICAL);

            final TextView card_title = new TextView(context);
            card_title.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
            ));
            card_title.setHeight(this.dpToPixels(45));
            card_title.setPadding(0, this.dpToPixels(0), this.dpToPixels(3), 0);//for edit icon
            card_title.setTextSize((float) this.dpToPixels(12));
            card_title.setGravity(Gravity.CENTER);
            card_title.setTextColor(Color.parseColor("#ffffff"));

            final String final_course_id =course_id;//specially for call

            if (i<course_list.length()){

                card_title.setText(course_name);

                card_title.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        course_id = final_course_id;
                        String urlApi = "http://auburn.edu/~azt0054/course_builder/user_control.php?fetch=articles&courseid="+course_id;
                        Log.d("Clicked: ","Article screen===");
                        page = "Article";
                        backgroundVolley("GET", urlApi, "article_list");
                        return true;
                    }
                });
            }else{// if (MainActivity.privilege>1) {
                card_title.setText("+");
            }

            TextView card_desc = new TextView(context);
            card_desc.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
            ));
            card_desc.setHeight(this.dpToPixels(55));
            card_desc.setPadding(0, this.dpToPixels(5), 0, 0);
            if (i==course_list.length()){
                //if (MainActivity.privilege>1){
                card_desc.setText("Add Course");
                //}
            }else {
                card_desc.setText("Description");
            }
            card_desc.setTextSize((float) this.dpToPixels(12));
            card_desc.setGravity(Gravity.CENTER);
            card_desc.setTextColor(Color.parseColor("#ffffff"));
            card_desc.setBackgroundColor(Color.parseColor("#dee0e2"));

            cardInner.addView(card_title);
            cardInner.addView(card_desc);
            card.addView(cardInner);

            if (i==course_list.length()){
                card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("Clicked: ", Integer.toString(v.getId()));
                        context.startActivity(new Intent(context.getApplicationContext(), CreateCourse.class));
                    }
                });
            }else {
                card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        course_id = final_course_id;
                        String urlApi = "http://auburn.edu/~azt0054/course_builder/user_control.php?fetch=articles&courseid="+course_id;
                        Log.d("Clicked: ","Article screen===");
                        page = "Article";
                        backgroundVolley("GET", urlApi, "article_list");
                    }
                });
            }

            this.addView(card);
        }
    }

    private void Article(final Context context){
        this.removeAllViews();

        JSONArray article_list = null;
        try{
            String json_string = editor.get("article_list");
            if (json_string.contains("success")) {
                JSONObject jsonObject = new JSONObject(json_string);
                article_list = jsonObject.getJSONArray("success");
            }
        }catch (Exception e){
            Log.d("Mylogger ",e.toString());
        }
        Log.d("MyLogger article_list :",article_list.toString());

        this.setOrientation(LinearLayout.VERTICAL);
        Integer article_list_length = article_list.length();
        if (MainActivity.privilege<2){
            article_list_length = article_list.length()-1;
        }
        for(int i = 0; i <= article_list_length; i++) {
            try{
                article_name = article_list.getJSONObject(i).getString("name");
                article_id = article_list.getJSONObject(i).getString("id");
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
            card_title.setPadding(0, this.dpToPixels(0), 0, 0);// was card_title.setPadding(0, this.dpToPixels(3), 0, 0);
            card_title.setTextSize((float) this.dpToPixels(12));// was 15
            card_title.setGravity(Gravity.CENTER);
            card_title.setTextColor(Color.parseColor("#ffffff"));

            TextView card_desc = new TextView(context);
            card_desc.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
            ));
            card_desc.setHeight(this.dpToPixels(55));
            card_desc.setPadding(0, this.dpToPixels(5), 0, 0);
            if (i==article_list.length()) {
                //if (MainActivity.privilege > 1) {
                card_title.setText("+");
                card_desc.setText("Add Article");
                //}
            }else {
                card_title.setText(article_name);
                card_desc.setText("Description");
            }
            card_desc.setTextSize((float) this.dpToPixels(12));
            card_desc.setGravity(Gravity.CENTER);
            card_desc.setTextColor(Color.parseColor("#ffffff"));
            card_desc.setBackgroundColor(Color.parseColor("#dee0e2"));

            cardInner.addView(card_title);
            cardInner.addView(card_desc);
            card.addView(cardInner);

            final String final_article_id =article_id;//specially for call
            final String final_article_name =article_name;//specially for call

            if (i==article_list.length()){
                card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("Clicked: ", Integer.toString(v.getId()));
                        context.startActivity(new Intent(context.getApplicationContext(), CreateArticle.class));
                    }
                });
            }else {
                card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        article_id = final_article_id;
                        article_name = final_article_name;
                        String urlApi = "http://auburn.edu/~azt0054/course_builder/user_control.php?fetch=articlecontent&articleid="+article_id;
                        Log.d("Clicked: ","Content screen===");
                        page = "Content";
                        backgroundVolley("GET", urlApi, "article_content_list");
                    }
                });
            }

            this.addView(card);
        }
    }


    private void Content(final Context context){
        this.removeAllViews();

        LayoutParams a_mparams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        LayoutParams a_aparams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        LayoutParams a_zeromparams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        a_zeromparams.setMargins(0,0,0,0);

        ScrollView a_sview = new ScrollView(context);
        a_sview.setId(R.id.a_sview);

        LinearLayout a_mlayout = new LinearLayout(context);
        a_mlayout.setId(R.id.a_mlayout);
        a_mlayout.setLayoutParams(a_aparams);
        a_mlayout.setOrientation(VERTICAL);

        ImageView a_image = new ImageView(context);
        a_image.setId(R.id.a_image);
        a_image.setPadding(0,0,0,15);
        a_image.setMinimumHeight(dpToPixels(200));
        a_image.setScaleType(ImageView.ScaleType.FIT_XY);
        a_mlayout.addView(a_image,a_aparams);

        TextView a_title = new TextView(context);
        a_title.setId(R.id.a_title);
        a_title.setTextSize(dpToPixels(10));
        a_mlayout.addView(a_title);

        TextView a_content = new TextView(context);
        a_content.setId(R.id.a_content);
        a_title.setTextSize(dpToPixels(6));
        a_mlayout.addView(a_content);

        TextView a_attach = new TextView(context);
        a_attach.setId(R.id.a_attach);
        a_title.setTextSize(dpToPixels(6));
        a_mlayout.addView(a_attach);

        LinearLayout a_alayout = new LinearLayout(context);
        a_alayout.setId(R.id.a_alayout);
        a_alayout.setLayoutParams(a_aparams);
        a_mlayout.addView(a_alayout);

        ImageView a_video = new ImageView(context);
        a_video.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
        a_video.setId(R.id.a_video);
        a_video.setPadding(dpToPixels(120),dpToPixels(65),dpToPixels(120),dpToPixels(65));
        a_video.setMinimumHeight(dpToPixels(210));
        a_video.setScaleType(ImageView.ScaleType.FIT_XY);
        a_video.setClickable(Boolean.TRUE);

        LinearLayout a_vlayout = new LinearLayout(context);
        a_vlayout.setId(R.id.a_vlayout);
        a_vlayout.setLayoutParams(a_mparams);
        a_vlayout.setPadding(dpToPixels(5),dpToPixels(5),dpToPixels(5),dpToPixels(5));
        a_vlayout.setBackgroundColor(Color.parseColor("#000000"));
        a_vlayout.addView(a_video,a_mparams);

        LinearLayout a_volayout = new LinearLayout(context);
        a_volayout.setId(R.id.a_volayout);
        a_volayout.setLayoutParams(a_aparams);
        a_volayout.setPadding(0,dpToPixels(16),0,0);
        a_volayout.addView(a_vlayout);

        a_mlayout.addView(a_volayout);

        a_sview.addView(a_mlayout);
        this.addView(a_sview,a_mparams);

        RelativeLayout webRL = new RelativeLayout(context);
        webRL.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        webRL.setId(R.id.webViewRL);
        webRL.setVisibility(VISIBLE);
        WebView webVw = new WebView(context);
        webVw.setId(R.id.webView);
        webVw.getSettings().setJavaScriptEnabled(true);
        webVw.getSettings().setPluginState(WebSettings.PluginState.ON);
        webVw.getSettings().setAllowFileAccess(true);
        webVw.getSettings().setDomStorageEnabled(true);
        webVw.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        webRL.addView(webVw);
        this.addView(webRL);

        ArrayList<String> article_doc = new ArrayList<String>();
        JSONArray article_content_list = null;

        try{
            String json_string = editor.get("article_content_list");
            if (json_string.contains("success")) {
                JSONObject jsonObject = new JSONObject(json_string);
                article_content_list = jsonObject.getJSONArray("success");
            }
        }catch (Exception e){
            Log.d("Mylogger ",e.toString());
        }

        if (article_content_list != null) {
            Log.d("Mylogger a_c_list:", article_content_list.toString());

            boolean hasVideo = false;
            for (int i = 0; i < article_content_list.length(); i++) {
                try {
                    String content_type = article_content_list.getJSONObject(i).getString("type");
                    String content_data = article_content_list.getJSONObject(i).getString("data");
                    Log.d("Mylogger :", content_data);
                    if (content_type.contains("mp4")) {
                        hasVideo = true;
                        vidAddress = content_data.replace("\\", "");
                    } else if (content_type.contains("doc") || content_type.contains("ppt") || content_type.contains("pdf") || content_type.contains("xls")) {
                        article_doc.add(content_data.replace("\\", ""));
                    } else if (content_type.contains("jpg")) {
                        imgAddress = content_data.replace("\\", "");
                        new DownloadImageTask(R.id.a_image)
                                .execute(imgAddress);
                    } else if (content_type.contains("text")) {
                        articleText = content_data.replace("\\", "");
                        new setArticleContent(R.id.a_content)
                                .execute(articleText);
                    }
                } catch (JSONException e) {
                    Log.d("Mylogger :", e.toString());
                }
            }


            String docLink = "http://www.tutorialspoint.com/android/android_tutorial.pdf";
            if (!article_doc.isEmpty()) {
                new setArticleDoc(R.id.a_attach)
                        .execute(article_doc);
            }

            TextView article_title = (TextView) findViewById(R.id.a_title);
            article_title.setText(Html.fromHtml("<b>" + article_name + "</b"));

            try {
                String url = vidAddress;//https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
                if (hasVideo) {
                    if (url.contains("youtube")) {
                        LinearLayout article_videoLayout = (LinearLayout) findViewById(R.id.a_volayout);
                        article_videoLayout.setVisibility(GONE);
                        WebView mWebView = new WebView(context);
                        mWebView.setPadding(0, 0, 0, 0);
                        WebSettings webSettings = mWebView.getSettings();
                        webSettings.setJavaScriptEnabled(true);

                        String vidHeight = ((Double) (getScreenHeight() / 7.5)).toString();

                        String frameVideo = "<body style=\"margin: 0; padding: 0\"><iframe width=\"100%\" height=\"" + vidHeight + "\" src=\"" + url + "\" frameborder=\"0\" allowfullscreen></iframe></body>";
                        mWebView.loadData(frameVideo, "text/html", "utf-8");

                        //mWebView.loadUrl("https://youtu.be/GcUlB-2V8-I");
                        mWebView.setWebViewClient(new WebViewClient());
                        this.addView(mWebView);

                    } else {
                        //new--article_videoLayout.setVisibility(View.VISIBLE);
                        LinearLayout article_videoLayout = (LinearLayout) findViewById(R.id.a_volayout);
                        article_videoLayout.setVisibility(VISIBLE);
                        new DownloadImageTask(R.id.a_video)
                                .execute(url, "vthumb");

                        ImageView imgThumb = (ImageView) findViewById(R.id.a_video);

                        imgThumb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent ni = new Intent(getContext(), VideoPlayer.class);
                                ni.putExtra("vidAddress", vidAddress);
                                getContext().startActivity(ni);
                            }
                        });
                    }
                } else {
                    LinearLayout article_videoLayout = (LinearLayout) findViewById(R.id.a_volayout);
                    article_videoLayout.setVisibility(GONE);
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            Log.d("Mylogger :", "completes");
        }

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        int bitId;
        String itype = "img";
        public DownloadImageTask(int bitId) {
            this.bitId = bitId;
        }

        private void setCache(String url, Bitmap bmp){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            db.setBlobCache(url, byteArray);
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            if (urls.length>1){
                itype = urls[1];
            }
            Bitmap bitImg = null;

            try {
                byte[] cache = db.getBlobCache(urlDisplay);
                if (cache !=null){
                    ByteArrayInputStream imageStream = new ByteArrayInputStream(cache);
                    bitImg = BitmapFactory.decodeStream(imageStream);
                }else if (itype.equals("img")){
                    URL url = new URL(urlDisplay);
                    InputStream is = new BufferedInputStream(url.openStream());
                    bitImg = BitmapFactory.decodeStream(is);
                    setCache(urlDisplay,bitImg);
                }else if (itype.equals("vthumb")){
                    try {
                        bitImg = retriveVideoFrameFromVideo(urlDisplay);
                        setCache(urlDisplay,bitImg);
                    }catch (Throwable throwable){
                        Log.e("Error", throwable.getMessage());
                        throwable.printStackTrace();
                    }
                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitImg;
        }

        protected void onPostExecute(Bitmap result) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ImageView bmImage = (ImageView)findViewById(this.bitId);
            bmImage.setLayoutParams(layoutParams);
            if (itype.equals("img")) {
                bmImage.setLayoutParams(layoutParams);
                bmImage.setImageBitmap(result);
            }else if (itype.equals("vthumb")) {
                BitmapDrawable videoThumbDrawable = new BitmapDrawable(getResources(), result);
                bmImage.setBackgroundDrawable(videoThumbDrawable);
            }

        }
    }

    public class DownloadVideoTask extends AsyncTask<String, Void, Uri> {
        int vidId;
        Context parent;

        public DownloadVideoTask(int vidId, Context context) {
            this.vidId = vidId;
            this.parent = context;
        }

        protected Uri doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Uri url = null;

            try {
                url = Uri.parse(urlDisplay);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return url;
        }

        protected void onPostExecute(Uri result) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            VideoView article_video = (VideoView)findViewById(this.vidId);
            article_video.setLayoutParams(layoutParams);
            article_video.setVideoURI(result);

            MediaController vidControl = new MediaController(this.parent);
            vidControl.setAnchorView(article_video);
            article_video.setMediaController(vidControl);

            article_video.start();
        }
    }

    public class setArticleContent extends AsyncTask<String, Void, String> {
        int textId;

        public setArticleContent(int textId) {
            this.textId = textId;
        }

        protected String doInBackground(String... aText) {
            String articleText = aText[0];
            return articleText;
        }

        protected void onPostExecute(String result) {
            TextView textID = (TextView)findViewById(this.textId);
            textID.setText(Html.fromHtml(result));
            textID.setTextSize(dpToPixels(6));
        }
    }

    public class setArticleDoc extends AsyncTask<ArrayList<String>, Void, String[][]> {
        int textId;

        public setArticleDoc(int textId) {
            this.textId = textId;
        }

        protected String[][] doInBackground(ArrayList<String>... aText) {
            String[][] docArray = new String[aText[0].size()][2];
            for (int i=0; i<aText[0].size(); i++)
            {
                String[] tempArray = aText[0].get(i).split("/");
                docArray[i][0] = tempArray[tempArray.length-1];
                docArray[i][1] = aText[0].get(i);//"<a href='" + aText[i] + "'> " + docArray[i][0] + " </a>";
            }
            return docArray;
        }

        protected void onPostExecute(String[][] result) {
            LinearLayout llp = (LinearLayout)findViewById(R.id.a_alayout);
            llp.setOrientation(LinearLayout.VERTICAL);

            for (int i=0; i<result.length+1; i++)
            {
                if (i==0){
                    TextView textID = (TextView)findViewById(this.textId);
                    textID.setText(Html.fromHtml("<b>Related Docs</b"));
                    textID.setTextSize(dpToPixels(6));
                }else{
                    final String f_downloadLink = result[i-1][1];
                    final String f_downloadName = result[i-1][0];
                    TextView textID = new TextView(getContext());
                    textID.setClickable(true);
                    textID.setMovementMethod(LinkMovementMethod.getInstance());
                    textID.setText(Html.fromHtml("<u>"+f_downloadName+"</u>"));
                    textID.setTextColor(Color.BLUE);
                    textID.setTextSize(dpToPixels(6));
                    textID.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            Intent intent = new Intent(getContext(), DocViewer.class);
                            intent.putExtra("downloadlink", f_downloadLink);
                            intent.putExtra("downloadname", f_downloadName);
                            getContext().startActivity(intent);
                        }

                    });
                    llp.addView(textID);
                }
            }

        }
    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath)
            throws Throwable
    {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try
        {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime(TimeUnit.MICROSECONDS.convert(73000, TimeUnit.MILLISECONDS));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Throwable(
                    "Exception in retriveVideoFrameFromVideo(String videoPath)"
                            + e.getMessage());

        }
        finally
        {
            if (mediaMetadataRetriever != null)
            {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }


    private void Document(final Context context){
        //this.removeAllViews();
        this.removeAllExcept();
        RelativeLayout webRL = (RelativeLayout)findViewById(R.id.webViewRL);
        webRL.setVisibility(VISIBLE);
        WebView webVw = (WebView) findViewById(R.id.webView);
        webVw.getSettings().setJavaScriptEnabled(true);
        webVw.getSettings().setPluginState(WebSettings.PluginState.ON);
        webVw.getSettings().setAllowFileAccess(true);
        webVw.getSettings().setDomStorageEnabled(true);
        webVw.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);


        webVw.loadData(docframe, "text/html", "UTF-8");

    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];
            String fileName = strings[1];
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "coursebuilder");
            folder.mkdir();

            File pdfFile = new File(folder, fileName);

            try{
                pdfFile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getContext(), "Download complete" , Toast.LENGTH_SHORT).show();
        }
    }

    private void Quiz(final Context context){
        this.removeAllViews();
        JSONArray quiz_list = null;//dataAPI.get_quizzes();
        try{
            String json_string = editor.get("quiz_list");
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
                        Log.d("Clicked: ", Integer.toString(v.getId()));
                        context.startActivity(new Intent(context.getApplicationContext(), CreateQuiz.class));
                    }
                });
            }else {
                card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("Clicked: ", Integer.toString(v.getId()));
                        quiz_id = final_quiz_id;
                        quiz_name = final_quiz_name;
                        String urlApi = "http://auburn.edu/~azt0054/course_builder/user_control.php?fetch=questions&quizid="+quiz_id;
                        Log.d("Clicked: ","Content screen===");
                        page = "Question";
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
            String json_string = editor.get("question_list");
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
                        Log.d("Clicked NoMatch: ", Integer.toString(card.getHeight()));
                    }
                });
            }
            this.addView(card);
        }
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}
