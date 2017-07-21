package com.arivunambi.coursebuilder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

/**
 * Created by arivu on 4/5/2017.
 */
public class DataSource extends AsyncTask<String, Void, String> {

    String method;
    String urlApi;
    String key;
    Context ctx;
    private String responseApi;
    final SharedPreferences settings;
    final SharedPreferences.Editor editor;
    private JSONArray course_list = new JSONArray();

    public DataSource(String method, String urlApi, final String key, Context ctx) {
        this.method = method;
        this.urlApi = urlApi;
        this.key = key;
        this.ctx = ctx;
        settings = this.ctx.getSharedPreferences(HomeScreen.PREFS_NAME, 0);
        editor = settings.edit();
    }

    @Override
    protected String doInBackground(String... params) {
            Log.d("Mylogger :", "Waiting for background");
            RequestQueue syncqueue = Volley.newRequestQueue(this.ctx);
            if (this.method == "GET") {
                StringRequest request = new StringRequest(Request.Method.GET, this.urlApi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                                Log.d("MyLogger actual ",response.toString());
                                editor.putBoolean("APIResponse", true);
                                editor.putString(key, response.toString());
                                editor.commit();
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
                syncqueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
                    @Override
                    public void onRequestFinished(Request<String> request) {
                        //if (progressDialog !=  null && progressDialog.isShowing())
                         //   progressDialog.dismiss();
                    }
                });

            }

        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {
            Log.d("Mylogger :", " Post Executed=========");
    }

    @Override
    protected void onPreExecute() {
            Log.d("Mylogger :", " Pre Executed=========");

    }

    @Override
    protected void onProgressUpdate(Void... values) {}

}
