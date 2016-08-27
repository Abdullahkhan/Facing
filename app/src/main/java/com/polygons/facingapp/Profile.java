package com.polygons.facingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

public class Profile extends Fragment {

    Context context = getContext();
    View view;
    public static String TAG = "Profile";

    TextView textViewFirstNameProfile;
    TextView textViewLastNameProfile;
    String userid;
    JSONParser jsonParser = new JSONParser();
    String profileURL = Login.myURL + "view_profile";
    private static final String TAG_STATUS = "status";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userid = getArguments().getString("userid");
        view = inflater.inflate(R.layout.profile, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAllXMLReferences();
        new ViewProfile().execute(userid);
    }

    void setAllXMLReferences() {
        textViewFirstNameProfile = (TextView) view.findViewById(R.id.textViewFirstNameProfile);
        textViewLastNameProfile = (TextView) view.findViewById(R.id.textViewLastNameProfile);
    }

    class ViewProfile extends AsyncTask<String, String, Boolean>

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

                    firstName = json.getJSONObject("result").getString("Name");
                    lastName = json.getJSONObject("result").getString("Lname");


                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                Toast.makeText(getActivity(), "Profile showed ", Toast.LENGTH_SHORT).show();
                textViewFirstNameProfile.setText(firstName);
                textViewLastNameProfile.setText(lastName);


            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().finish();
    }
}
