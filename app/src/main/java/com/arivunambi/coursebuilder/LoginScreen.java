package com.arivunambi.coursebuilder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginScreen extends AppCompatActivity {

    private EditText loginEmail,loginPassword;
    private Button signUpButton;
    private Button loginButton;
    private RequestQueue requestQueue;
    private static final String URL = "http://auburn.edu/~azt0054/course_builder/user_control.php";
    private StringRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginEmail = (EditText) findViewById(R.id.loginEmail);
        loginPassword = (EditText) findViewById(R.id.loginPassword);
        loginButton = (Button) findViewById(R.id.loginButton);

        signUpButton = (Button) findViewById(R.id.signUpbutton);

        requestQueue = Volley.newRequestQueue(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("MyLogger",response);
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.names().get(0).equals("success")) {
                                //User has successfully logged in, save this information
                                // We need an Editor object to make preference changes.
                                SharedPreferences settings = getSharedPreferences(HomeScreen.PREFS_NAME, 0); // 0 - for private mode
                                SharedPreferences.Editor editor = settings.edit();
                                //Set "hasLoggedIn" to true
                                editor.putBoolean("hasLoggedIn", true);
                                String userId = jsonObject.getString("user_id");
                                Integer privilege = Integer.parseInt(jsonObject.getString("privilege"));
                                MainActivity.userID = userId;
                                MainActivity.privilege = privilege;
                                editor.putString("userId",userId);
                                // Commit the edits!
                                editor.commit();
                                Toast.makeText(getApplicationContext(), "SUCCESS " + jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), HomeScreen.class));
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error" + jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String,String> hashMap = new HashMap<String, String>();
                        hashMap.put("email",loginEmail.getText().toString());
                        hashMap.put("password", loginPassword.getText().toString());

                        return hashMap;
                    }
                };
                request.setShouldCache(false);
                requestQueue.add(request);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginScreen.this, SignupUser.class);
                startActivity(i);
                LoginScreen.this.finish();
            }
        });

    }

}
