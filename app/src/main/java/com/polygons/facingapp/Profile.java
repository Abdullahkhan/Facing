package com.polygons.facingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.polygons.facingapp.tools.CircleImageView;
import com.polygons.facingapp.tools.Constant;

import org.json.JSONObject;

import java.util.HashMap;

public class Profile extends Fragment {

    Context context = getContext();
    View view;
    public static String TAG = "Profile";

    TextView textViewFirstNameProfile;
    TextView textViewLastNameProfile;
    TextView textViewUsernameProfile;
    CircleImageView imageViewProfilePictureProfile;
    TextView textViewCountFollowerProfile;
    TextView textViewCountFollowingProfile;
    String userid;
    JSONParser jsonParser = new JSONParser();
    String profileURL = Login.myURL + "view_profile";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userid = getArguments().getString(Constant.TAG_USERID);
        view = inflater.inflate(R.layout.profile, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAllXMLReferences();
        setAllClickListner();
        new ViewProfile().execute(userid);
    }

    void setAllClickListner() {
        imageViewProfilePictureProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ImageCptureCamera.class);
                startActivity(intent);
            }
        });
    }

    void setAllXMLReferences() {
        textViewFirstNameProfile = (TextView) view.findViewById(R.id.textViewFirstNameProfile);
        textViewLastNameProfile = (TextView) view.findViewById(R.id.textViewLastNameProfile);
        textViewUsernameProfile = (TextView) view.findViewById(R.id.textViewUsernameProfile);
        imageViewProfilePictureProfile = (CircleImageView) view.findViewById(R.id.imageViewProfilePictureProfile);
        textViewCountFollowerProfile = (TextView) view.findViewById(R.id.textViewCountFollowerProfile);
        textViewCountFollowingProfile = (TextView) view.findViewById(R.id.textViewCountFollowingProfile);

    }

    class ViewProfile extends AsyncTask<String, String, Boolean>

    {
        String firstName;
        String lastName;
        String userName;
        String profilePictureURL;
        String coverPictureURL;
        String totalCountFollower;
        String totalCountFollowing;

        @Override
        protected Boolean doInBackground(String... args) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Constant.TAG_USERID, args[0]);


            JSONObject json = jsonParser.makeHttpRequest(profileURL, Constant.TAG_POST_METHOD, params);

            try {
                boolean status = json.getBoolean(Constant.TAG_STATUS);
                if (status) {

                    Log.i("Profile", json.toString());
                    firstName = json.getJSONObject(Constant.TAG_RESULT).getString(Constant.TAG_FIRST_NAME);
                    lastName = json.getJSONObject(Constant.TAG_RESULT).getString(Constant.TAG_LAST_NAME);
                    userName = json.getJSONObject(Constant.TAG_RESULT).getString(Constant.TAG_USERNAME);
                    profilePictureURL = json.getJSONObject(Constant.TAG_RESULT).getString(Constant.TAG_PROFILE_PICTURE_URL);
                    coverPictureURL = json.getJSONObject(Constant.TAG_RESULT).getString(Constant.TAG_COVER_PICTURE_URL);
                    totalCountFollower = json.getJSONObject(Constant.TAG_RESULT).getString(Constant.TAG_COUNT_TOTAL_FOLLOWER);
                    totalCountFollowing = json.getJSONObject(Constant.TAG_RESULT).getString(Constant.TAG_COUNT_TOTAL_FOLLOWING);


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
                textViewFirstNameProfile.setText(firstName);
                textViewLastNameProfile.setText(lastName);
                textViewUsernameProfile.setText(userName);
                if (!totalCountFollower.equals("")) {
                    textViewCountFollowerProfile.setText(totalCountFollower);
                }
                if (!totalCountFollowing.equals("")) {

                    textViewCountFollowingProfile.setText(totalCountFollowing);
                }


            }

        }
    }

}
