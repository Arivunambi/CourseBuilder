package com.arivunambi.coursebuilder;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

public class VideoPlayer extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_player);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width), (int) (height*.8));

        Intent intent = getIntent();
        String vidAddress = intent.getExtras().getString("vidAddress");
        new DownloadVideoTask(R.id.article_videoPop,VideoPlayer.this)
                .execute(vidAddress);

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

            FrameLayout.LayoutParams vidControlParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            vidControlParams.gravity = Gravity.CENTER_VERTICAL;
            vidControlParams.bottomMargin=40;
            MediaController vidControl = new MediaController(this.parent);
            vidControl.setLayoutParams(vidControlParams);
            vidControl.setAnchorView(article_video);

            article_video.setMediaController(vidControl);

            article_video.start();
        }
    }

}
