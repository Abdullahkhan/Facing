package com.polygons.facingapp;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.polygons.facingapp.tools.CircleImageView;
import com.polygons.facingapp.tools.Constant;
import com.polygons.facingapp.tools.ImageLoader;

public class ShowFollowers extends Activity {
    Context context = this;
    LinearLayout linearLayoutShowFollowers;
    JSONParser jsonparser = new JSONParser();

    public static ArrayList<HashMap<String, String>> arrayListFollowers;

    String showFollowersURL = Login.myURL + "see_following";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_followers);
        setAllXMLReferences();
        setAllClickListner();
        new AllFollowers().execute(getIntent().getStringExtra(Constant.TAG_USERID));
    }

    void setAllXMLReferences() {
        linearLayoutShowFollowers = (LinearLayout) findViewById(R.id.linearLayoutShowFollowers);
    }

    void setAllClickListner() {
    }

    class AllFollowers extends AsyncTask<String, String, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(String... args) {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Constant.TAG_USERID, args[0]);
            JSONObject json = jsonparser.makeHttpRequest(showFollowersURL, Constant.TAG_POST_METHOD, params);

            try {
                arrayListFollowers = new ArrayList<HashMap<String, String>>();

                Boolean success = json.getBoolean(Constant.TAG_STATUS);
                JSONArray jsonArray;
                if (success) {
                    jsonArray = json.getJSONArray(Constant.TAG_RESULT);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        HashMap<String, String> hashMap = new HashMap<String, String>();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        hashMap.put(Constant.TAG_FOLLOWER_ID, jsonObject.getString(Constant.TAG_FOLLOWER_ID));
                        hashMap.put(Constant.TAG_FOLLOWER_FIRST_NAME, jsonObject.getString(Constant.TAG_FOLLOWER_FIRST_NAME));
                        hashMap.put(Constant.TAG_FOLLOWER_LAST_NAME, jsonObject.getString(Constant.TAG_FOLLOWER_LAST_NAME));
                        hashMap.put(Constant.TAG_FOLLOWER_PROFILE_PICTURE_URL, jsonObject.getString(Constant.TAG_FOLLOWER_PROFILE_PICTURE_URL));


                        arrayListFollowers.add(hashMap);
                    }
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                CreateAndAppendFollowers();
            }

        }
    }

    public void CreateAndAppendFollowers() {
        LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int position = arrayListFollowers.size() - 1; position >= 0; position--) {
            View tempView = li.inflate(R.layout.linearlayout_follower_following, null);


            CircleImageView circleImageViewItemLinearLayoutFollowerFollowing = (CircleImageView) tempView.findViewById(R.id.circleImageViewItemLinearLayoutFollowerFollowing);
            TextView textViewItemLinearLayoutFullNameFollowerFollowing = (TextView) tempView.findViewById(R.id.textViewItemLinearLayoutFullNameFollowerFollowing);
            Button buttonItemLinearLayoutFollowFollowerFollowing = (Button) tempView.findViewById(R.id.buttonItemLinearLayoutFollowFollowerFollowing);

            try {


                ImageLoader imgLoader = new ImageLoader(getApplicationContext());
                int loader = R.drawable.ic_launcher;
                // whenever you want to load an image from url
                // call DisplayImage function
                // url - image url to load
                // loader - loader image, will be displayed before getting image
                // image - ImageView
                imgLoader.DisplayImage("https://facing-app.herokuapp.com/" + "uploads/" + arrayListFollowers.get(position).get(Constant.TAG_PROFILE_PICTURE_URL), loader, circleImageViewItemLinearLayoutFollowerFollowing);
            } catch (Exception e) {
                e.printStackTrace();
            }


            textViewItemLinearLayoutFullNameFollowerFollowing.setText(arrayListFollowers.get(position).get(Constant.TAG_FOLLOWER_FIRST_NAME) + " " + arrayListFollowers.get(position).get(Constant.TAG_FOLLOWER_LAST_NAME));
            linearLayoutShowFollowers.addView(tempView, 0);
        }
    }

}
