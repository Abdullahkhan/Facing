package com.polygons.facingapp;

import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class Login extends Activity {
    String userid;
    EditText editTextUsername;
    EditText editTextPassword;
    Button buttonLogin;
    public static String myURL = "http://facing-app.herokuapp.com/api/";
    String loginURL = Login.myURL + "login";
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    private static final String TAG_STATUS = "status";
    public static User user;
    Context context = this;
    SharedPreferences sp;
    String TAG_ISLOGGEDIN = "isLoggedIn";


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
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);

    }

    void setAllClickListner() {
        buttonLogin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new LoginUser().execute(editTextUsername.getText().toString(),
                        editTextPassword.getText().toString());
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

    class LoginUser extends AsyncTask<String, String, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Please wait while we logged you in...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... args) {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("first_name", args[0]);
            params.put("password", args[1]);


            JSONObject json = jsonParser.makeHttpRequest(loginURL, "POST",
                    params);
            try {
                boolean status = json.getBoolean(TAG_STATUS);
                if (status) {

                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean(TAG_ISLOGGEDIN, true);
                    editor.putString("userid", json.getString("userid"));
                    editor.commit();

                }
                return true;
            } catch (Exception e) {
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            pDialog.dismiss();
            if (result) {
                startActivity(new Intent(context, MainActivity.class));
            }
        }
    }

    private void showAlert(final String message) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(message).setTitle("Response from Servers")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // do nothing
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    void setUserid() {
        sp = getSharedPreferences("user", Activity.MODE_PRIVATE);
        userid = sp.getString("userid", "0");

    }

    boolean isLoggedIn() {
        if (sp.getBoolean(TAG_ISLOGGEDIN, false)) {
            return true;
        } else
            return false;
    }
}
