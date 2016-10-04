package com.polygons.facingapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.polygons.facingapp.tools.Constant;

import org.json.JSONObject;

import java.util.HashMap;

public class SignUp extends Activity {
    String userid;
    EditText editTextFirstName;
    EditText editTextLastName;
    EditText editTextEmail;
    EditText editTextSignUpPassword;
    JSONParser jsonParser = new JSONParser();
    Button buttonSignUp;
    TextView textViewGoToLogin;
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
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextSignUpPassword = (EditText) findViewById(R.id.editTextSignUpPassword);
        buttonSignUp = (Button) findViewById(R.id.buttonSignUp);
        textViewGoToLogin = (TextView) findViewById(R.id.textViewGoToLogin);
    }

    void setAllClickListner() {
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.isInternetPresent) {

                    Login.arrayListAsyncs.add(new SignUpUser());
                    Login.arrayListAsyncs.get(Login.arrayListAsyncs.size() - 1).execute(editTextFirstName.getText().toString(), editTextLastName.getText().toString(), editTextEmail.getText().toString(), editTextSignUpPassword.getText().toString());
                }
            }
        });
        textViewGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Login.class));
            }
        });
    }

    class SignUpUser extends AsyncTask<Object, Object, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Object... args) {
            // String username = editTextUsername.getText().toString();
            // String password = editTextPassword.getText().toString();
            //
            // List<NameValuePair> params = new ArrayList<NameValuePair>();
            // params.add(new BasicNameValuePair("username", username));
            // params.add(new BasicNameValuePair("password", password));
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Constant.TAG_FIRST_NAME, (String) args[0]);
            params.put(Constant.TAG_LAST_NAME, (String) args[1]);
            params.put(Constant.TAG_EMAIL, (String) args[2]);
            params.put(Constant.TAG_PASSWORD, (String) args[3]);


            try {
                JSONObject json = jsonParser.makeHttpRequest(signUpURL, Constant.TAG_POST_METHOD,
                        params);
                boolean status = json.getBoolean(Constant.TAG_STATUS);
                if (status) {

                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean(Constant.TAG_ISLOGGEDIN, true);
                    editor.putString(Constant.TAG_USERID, json.getString(Constant.TAG_USERID));
                    editor.commit();


                    return true;

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
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                startActivity(new Intent(context, Suggestions.class));
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
