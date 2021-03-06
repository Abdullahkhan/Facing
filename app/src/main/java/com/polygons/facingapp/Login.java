package com.polygons.facingapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.polygons.facingapp.tools.Constant;

public class Login extends Activity {
    String userid;
    EditText editTextEmail;
    EditText editTextPassword;
    Button buttonLogin;
    public static String baseUrl = "https://facing-app.herokuapp.com/";
//    public static String baseUrl = "http://192.168.8.100:3000/";
    public static String myURL = baseUrl+"api/";
    String loginURL = Login.myURL + "login";
    JSONParser jsonParser = new JSONParser();
    public static User user;
    Context context = this;
    SharedPreferences sp;
    public static ArrayList<AsyncTask<Object, Object, Boolean>> arrayListAsyncs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        setAllXMLReferences();
        setAllClickListner();
        setUserid();
        if (isLoggedIn()) {
            startActivity(new Intent(context, MainActivity.class));
        }
    }

    void setAllXMLReferences() {
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);

    }

    void setAllClickListner() {
        buttonLogin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

//                new LoginUser().execute(editTextEmail.getText().toString(),
//                        editTextPassword.getText().toString());
                if (MainActivity.isInternetPresent) {
                    arrayListAsyncs.add(new LoginUser());
                    arrayListAsyncs.get(arrayListAsyncs.size() - 1).execute(editTextEmail.getText().toString(),
                            editTextPassword.getText().toString());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class LoginUser extends AsyncTask<Object, Object, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Object... args) {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Constant.TAG_EMAIL, (String) args[0]);
            params.put(Constant.TAG_PASSWORD, (String) args[1]);


            try {
                JSONObject json = jsonParser.makeHttpRequest(loginURL, Constant.TAG_POST_METHOD,
                        params);
                boolean status = json.getBoolean(Constant.TAG_STATUS);
                if (status) {

                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean(Constant.TAG_ISLOGGEDIN, true);
                    editor.putString(Constant.TAG_USERID, json.getString(Constant.TAG_USERID));
                    editor.commit();

                    return true;
                }
            } catch (Exception e) {
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                startActivity(new Intent(context, MainActivity.class));
            }
        }
    }


    void setUserid() {
        sp = getSharedPreferences(Constant.TAG_USER, Activity.MODE_PRIVATE);
        userid = sp.getString(Constant.TAG_USERID, "0");

    }

    boolean isLoggedIn() {
        if (sp.getBoolean(Constant.TAG_ISLOGGEDIN, false)) {
            return true;
        } else
            return false;
    }
}
