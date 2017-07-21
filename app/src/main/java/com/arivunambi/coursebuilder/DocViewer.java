package com.arivunambi.coursebuilder;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DocViewer extends AppCompatActivity {

    private WebView webVw;
    private String docLink;
    private String docName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_viewer);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            docLink = extras.getString("downloadlink");
            docName = extras.getString("downloadname");
        }
        else{
            docLink = "http://www.tutorialspoint.com/android/android_tutorial.pdf";
            docName = "android_tutorial.pdf";
        }
        String docframe="<iframe src='http://docs.google.com/viewer?url="+ docLink +"&embedded=true' width='100%' height='100%'  style='border: none;'></iframe>";
        webVw = (WebView) findViewById(R.id.webViewDoc);
        webVw.getSettings().setJavaScriptEnabled(true);
        webVw.getSettings().setPluginState(WebSettings.PluginState.ON);
        //webVw.getSettings().setPluginsEnabled(true);
        webVw.getSettings().setAllowFileAccess(true);
        webVw.getSettings().setDomStorageEnabled(true);
        //webVw.loadUrl(docLink);
        //webVw.setWebViewClient(new Callback());
        Log.d("Mylogger:::::::::::",docframe);
        webVw.loadData(docframe, "text/html", "UTF-8");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabDoc);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DownloadFile().execute(docLink, docName);
                Toast.makeText(getApplicationContext(), "Downloading in /sdcard/coursebuilder" , Toast.LENGTH_SHORT).show();
                //Snackbar.make(view, "Not yet configured", Snackbar.LENGTH_LONG)
                  //      .setAction("Action", null).show();
            }
        });

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
            Toast.makeText(DocViewer.this.getApplicationContext(), "Download complete" , Toast.LENGTH_SHORT).show();
        }
    }
}
