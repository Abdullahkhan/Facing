package com.polygons.facingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

public class Profile extends Activity {

    TextView textViewFirstNameProfile;
    TextView textViewLastNameProfile;
    String userid;
    Context context=this;
    JSONParser jsonParser = new JSONParser();
    String profileURL = Login.myURL + "view_profile";
    private static final String TAG_STATUS = "status";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        setAllXMLReferences();
        Intent intent=getIntent();
        userid=intent.getExtras().getString("userid");
        new ViewProfile().execute(userid);
    }

    void  setAllXMLReferences()
    {
        textViewFirstNameProfile=(TextView)findViewById(R.id.textViewFirstNameProfile);
        textViewLastNameProfile=(TextView)findViewById(R.id.textViewLastNameProfile);
    }

    class ViewProfile extends AsyncTask<String,String,Boolean>

    {
        String firstName;
        String lastName;
        @Override
        protected Boolean doInBackground(String... args) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid", args[0]);

            JSONObject json = jsonParser.makeHttpRequest(profileURL, "POST", params);

            try {
                boolean status = json.getBoolean(TAG_STATUS);
                if (status) {

                    firstName=json.getJSONObject("result").getString("Name");
                    lastName=json.getJSONObject("result").getString("Lname");


                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean)
            {
                Toast.makeText(context,"Profile showed ",Toast.LENGTH_SHORT).show();
                textViewFirstNameProfile.setText(firstName);
                textViewLastNameProfile.setText(lastName);


            }

        }
    }
}
