package com.arivunambi.coursebuilder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.widget.MediaController;
import android.widget.VideoView;

import android.provider.MediaStore.Video.Thumbnails;

import org.json.JSONArray;
import org.json.JSONException;

public class ArticleContent extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {

    private MediaPlayer mediaPlayer;
    private SurfaceHolder vidHolder;
    private SurfaceView vidSurface;

    String vidAddress = "";//https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
    String imgAddress = "";//http://designmodo.com/wp-content/uploads/2013/05/Player-Music-Player-App-by-Ilya-Boruhov.jpg";
    String articleText = "";/*"Android provides a rich application framework that allows you " +
            "to build innovative apps and games for mobile devices in a Java language " +
            "environment.The documents listed in the left navigation provide details about " +
            "how to build apps using Android's various APIs.";*/
    public static String article_id;
    public static String article_name;
    public static int recreateCount=0;




    @Override
    protected void onResume(){
        super.onResume();
        //DataAPI dataAPI = new DataAPI(getApplicationContext());
        //dataAPI.get_articles_content(article_id);
        if (recreateCount==0 && DataAPI.backgroundFlag!=2){
            Intent intent = getIntent();
            startActivity(intent);
            Log.d("Mylogger :","restarting.......");
            recreateCount+=1;
        }
        Log.d("Mylogger :","refreshing......."+ ((Integer) recreateCount).toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //String article_name = "";
        //ArrayList<String> article_doc = new ArrayList<String>();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            article_id = extras.getString("article_id");
            article_name = extras.getString("article_name");
        }

        final DataAPI dataAPI = new DataAPI(getApplicationContext());
        DataAPI.backgroundFlag = 2;
        JSONArray article_content_list = dataAPI.get_articles_content(article_id);

        //backgroundTask
        Log.d("=====MyLogger ","ARticle Content===");
        Intent loadInt = new Intent(ArticleContent.this, LoadingScreen.class);
        startActivityForResult(loadInt, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                ArrayList<String> article_doc = new ArrayList<String>();
                final DataAPI dataAPI = new DataAPI(getApplicationContext());
                JSONArray article_content_list = dataAPI.get_articles_content(article_id);
                Log.d("Mylogger :", article_content_list.toString());
                for (int i = 0; i < article_content_list.length(); i++) {
                    try {
                        String content_type = article_content_list.getJSONObject(i).getString("type");
                        String content_data = article_content_list.getJSONObject(i).getString("data");
                        Log.d("Mylogger :", content_data);
                        if (content_type.contains("mp4")) {
                            vidAddress = content_data.replace("\\", "");
                        } else if (content_type.contains("doc") || content_type.contains("ppt") || content_type.contains("pdf") || content_type.contains("xls")) {
                            article_doc.add(content_data.replace("\\", ""));
                        } else if (content_type.contains("jpg")) {
                            imgAddress = content_data.replace("\\", "");
                            new DownloadImageTask(R.id.article_image)
                                    .execute(imgAddress);
                        } else if (content_type.contains("text")) {
                            articleText = content_data.replace("\\", "");
                            new setArticleContent(R.id.article_text)
                                    .execute(articleText);
                        }
                    } catch (JSONException e) {
                        Log.d("Mylogger :", e.toString());
                    }
                }


                String docLink = "http://www.tutorialspoint.com/android/android_tutorial.pdf";
                if (!article_doc.isEmpty()) {
                    new setArticleDoc(R.id.article_doc)
                            .execute(article_doc);
                }
                //new setArticleContent(R.id.article_title)
                //      .execute("Android Interface Design");

                //new DownloadVideoTask(R.id.article_video,this)
                //      .execute("https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4");

                setContentView(R.layout.activity_article_content);

                TextView article_title = (TextView) findViewById(R.id.article_title);
                article_title.setText(article_name);

                try {
                    String url = vidAddress;//https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
                    LinearLayout article_videoLayout = (LinearLayout) findViewById(R.id.article_videoLayout);
                    if (url == "") {
                        article_videoLayout.setVisibility(View.GONE);
                    } else {
                        article_videoLayout.setVisibility(View.VISIBLE);
                        Bitmap videoThumb = retriveVideoFrameFromVideo(url);
                        BitmapDrawable videoThumbDrawable = new BitmapDrawable(getResources(), videoThumb);
                        ImageView imgThumb = (ImageView) findViewById(R.id.article_videoThumb);
                        //imgThumb.setImageBitmap(videoThumb);
                        imgThumb.setBackgroundDrawable(videoThumbDrawable);


                        imgThumb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(ArticleContent.this, VideoPlayer.class));
                                finish();
                            }
                        });
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }



    /*

            vidSurface = (SurfaceView) findViewById(R.id.article_videoSurface);
            vidHolder = vidSurface.getHolder();
            vidHolder.addCallback(this);

            try {
                Uri uri = Uri.parse("https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4");
                VideoView article_video = (VideoView)findViewById(R.id.article_video);
                article_video.setVideoURI(uri);

                MediaController vidControl = new MediaController(this);
                vidControl.setAnchorView(article_video);
                article_video.setMediaController(vidControl);
                article_video.start();
            }catch (Exception e){
                e.printStackTrace();
            }
    */

                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view, "Not yet configured", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.setDrawerListener(toggle);
                toggle.syncState();

                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(this);

                // addContentArticle();
                Log.d("Mylogger :", "completes");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent i = new Intent(getApplicationContext(), ArticleScreen.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.d("Mylogger con:","Back" );
            ArticleContent.recreateCount=0;
            startActivity(i);
            finish();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.article_content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            Intent i = new Intent(ArticleContent.this, HomeScreen.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDisplay(vidHolder);
            mediaPlayer.setDataSource(vidAddress);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        mediaPlayer.start();

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
            bitmap = mediaMetadataRetriever.getFrameAtTime();
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

    private class setArticleContent extends AsyncTask<String, Void, String> {
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
            textID.setText(result);
        }
    }

    private class setArticleDoc extends AsyncTask<ArrayList<String>, Void, String[][]> {
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
            LinearLayout llp = (LinearLayout)findViewById(R.id.article_doc_layout);
            llp.setOrientation(LinearLayout.VERTICAL);

            for (int i=0; i<result.length+1; i++)
            {
                if (i==0){
                    TextView textID = (TextView)findViewById(this.textId);
                    //textID.setClickable(true);
                    //textID.setMovementMethod(LinkMovementMethod.getInstance());
                    textID.setText("Related Docs");
                }else{
                    final String downloadLink = result[i-1][1];
                    final String downloadName = result[i-1][0];
                    TextView textID = new TextView(ArticleContent.this);
                    textID.setClickable(true);
                    textID.setMovementMethod(LinkMovementMethod.getInstance());
                    textID.setText(Html.fromHtml("<u>"+downloadName+"</u>"));
                    textID.setTextColor(Color.BLUE);
                    textID.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            Intent intent = new Intent(ArticleContent.this, DocViewer.class);
                            intent.putExtra("downloadlink", downloadLink);
                            intent.putExtra("downloadname", downloadName);
                            startActivity(intent);
                        }

                    });
                    llp.addView(textID);
                }
            }

        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        int bitId;

        public DownloadImageTask(int bitId) {
            this.bitId = bitId;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap bitImg = null;

            try {
                URL url = new URL(urlDisplay);
                InputStream is = new BufferedInputStream(url.openStream());
                bitImg = BitmapFactory.decodeStream(is);
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
            bmImage.setImageBitmap(result);
        }
    }

    private class DownloadVideoTask extends AsyncTask<String, Void, Uri> {
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


}
