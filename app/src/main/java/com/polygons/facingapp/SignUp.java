package com.polygons.facingapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.polygons.facingapp.tools.Constant;

import org.json.JSONObject;

import java.util.HashMap;

public class SignUp extends Activity {
    String userid;
    EditText editTextFirstName;
    EditText editTextLastName;
    EditText editTextSignUpPassword;
    JSONParser jsonParser = new JSONParser();
    Button buttonSignUp;
    Button buttonGoToLogin;
    private ProgressDialog pDialog;
    Context context = this;
    String signUpURL = Login.myURL + "register";
    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        setAllXMLReferences();
        setAllClickListner();
        setUserid();
        if (isLoggedIn()) {
            startActivity(new Intent(context, MainActivity.class));
        }
    }

    void setAllXMLReferences() {
        editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextSignUpPassword = (EditText) findViewById(R.id.editTextSignUpPassword);
        buttonSignUp = (Button) findViewById(R.id.buttonSignUp);
        buttonGoToLogin = (Button) findViewById(R.id.buttonGoToLogin);
    }

    void setAllClickListner() {
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SignUpUser().execute(editTextFirstName.getText().toString(), editTextLastName.getText().toString(), editTextSignUpPassword.getText().toString());

            }
        });
        buttonGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Login.class));
            }
        });
    }

    class SignUpUser extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Please wait while we logged you in...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // String username = editTextUsername.getText().toString();
            // String password = editTextPassword.getText().toString();
            //
            // List<NameValuePair> params = new ArrayList<NameValuePair>();
            // params.add(new BasicNameValuePair("username", username));
            // params.add(new BasicNameValuePair("password", password));
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Constant.TAG_FIRST_NAME, args[0]);
            params.put(Constant.TAG_SECOND_NAME, args[1]);
            params.put(Constant.TAG_PASSWORD, args[2]);


            try {
                JSONObject json = jsonParser.makeHttpRequest(signUpURL, Constant.TAG_POST_METHOD,
                        params);
                boolean status = json.getBoolean(Constant.TAG_STATUS);
                if (status) {

                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean(Constant.TAG_ISLOGGEDIN, true);
                    editor.putString(Constant.TAG_USERID, json.getString(Constant.TAG_USERID));
                    editor.commit();

                    startActivity(new Intent(context, Suggestions.class));

                }
                //  Log.i("Login",""+ success);
//            try {
//                int success = json.getInt(TAG_STATUS);
//                switch (success) {
//                    case 1:
//                        user = new User();
//                        user.setUsername(json.getString("username"));
//                        user.setFirstName(json.getString("firstName"));
//                        user.setLastName(json.getString("lastName"));
//                        user.setEmail(json.getString("email"));
//
//                        startActivity(new Intent(context, NewsFeed.class));
//                        break;
//                    case 2:
//                        showAlert("Sorry! It looks like you are not registered: :/");
//                        break;
//                    case 0:
//                        showAlert("Sorry! We have a problem. Try again :/");
//                        break;
//                    default:
//                        break;
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            pDialog.dismiss();
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
