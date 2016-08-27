package com.polygons.facingapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PostText extends Activity {
    EditText editTextPostText;
    Button buttonPostText;
    SharedPreferences sp;
    String userid;
    ProgressDialog pDialog;
    Context context = this;
    JSONObject json;
    JSONParser jsonparser = new JSONParser();
    String postURL = Login.myURL + "post";
    String TAG_SUCCESS = "status";
    String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.posttext);
        sp = getSharedPreferences("user", Activity.MODE_PRIVATE);
        userid = sp.getString("userid", "0");
        editTextPostText = (EditText) findViewById(R.id.editTextPostText);
        buttonPostText = (Button) findViewById(R.id.buttonPostText);

        buttonPostText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PostMyText().execute(userid, editTextPostText.getText().toString());
            }
        });
    }

    class PostMyText extends AsyncTask<String, String, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Posting");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected Boolean doInBackground(String... args) {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid", args[0]);
            params.put("post", args[1]);

            json = jsonparser.makeHttpRequest(postURL, "POST", params);
            try {
                Boolean success = json.getBoolean(TAG_SUCCESS);
                if (success) {

                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            pDialog.dismiss();

            if (result) {
                Toast.makeText(context, "Posted successfully ", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(context, "Posting failed ", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
