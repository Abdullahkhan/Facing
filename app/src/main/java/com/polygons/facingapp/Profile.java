package com.polygons.facingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.polygons.facingapp.tools.CircleImageView;
import com.polygons.facingapp.tools.Constant;

import org.json.JSONObject;

import java.net.URL;
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

    HashMap<String, String> userData;

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
                Intent intent = new Intent(context, ImageCaptureCamera.class);
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
                    JSONObject result = json.getJSONObject(Constant.TAG_RESULT);
                    userData = new HashMap<String, String>();
                    userData.put(Constant.TAG_FIRST_NAME, result.getString(Constant.TAG_FIRST_NAME));
                    userData.put(Constant.TAG_LAST_NAME, result.getString(Constant.TAG_LAST_NAME));
                    userData.put(Constant.TAG_USERNAME, result.getString(Constant.TAG_USERNAME));
                    userData.put(Constant.TAG_PROFILE_PICTURE_URL, result.getString(Constant.TAG_PROFILE_PICTURE_URL));
                    userData.put(Constant.TAG_COVER_PICTURE_URL, result.getString(Constant.TAG_COVER_PICTURE_URL));
                    userData.put(Constant.TAG_COUNT_TOTAL_FOLLOWER, result.getString(Constant.TAG_COUNT_TOTAL_FOLLOWER));
                    userData.put(Constant.TAG_COUNT_TOTAL_FOLLOWING, result.getString(Constant.TAG_COUNT_TOTAL_FOLLOWING));


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
                try {

                    Log.i("ImageURL", userData.get(Constant.TAG_PROFILE_PICTURE_URL).substring(15));
                    URL imageURL = new URL(Login.myURL + "uploads/" + userData.get(Constant.TAG_PROFILE_PICTURE_URL.substring(15)));
                    imageViewProfilePictureProfile.setImageBitmap(BitmapFactory.decodeStream(imageURL.openConnection().getInputStream()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                textViewFirstNameProfile.setText(userData.get(Constant.TAG_FIRST_NAME));
                textViewLastNameProfile.setText(userData.get(Constant.TAG_LAST_NAME));
                textViewUsernameProfile.setText(userData.get(Constant.TAG_USERNAME));
                if (!userData.get(Constant.TAG_COUNT_TOTAL_FOLLOWER).equals("")) {
                    textViewCountFollowerProfile.setText(userData.get(Constant.TAG_COUNT_TOTAL_FOLLOWER));
                }
                if (!userData.get(Constant.TAG_COUNT_TOTAL_FOLLOWING).equals("")) {

                    textViewCountFollowingProfile.setText(userData.get(Constant.TAG_COUNT_TOTAL_FOLLOWING));
                }


            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().finish();
    }
}
