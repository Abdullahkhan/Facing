package com.polygons.facingapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.polygons.facingapp.tools.CircleImageView;
import com.polygons.facingapp.tools.Constant;
import com.soundcloud.android.crop.Crop;

import org.json.JSONObject;

import java.io.File;
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

    TextView textViewFollowerProfile;
    TextView textViewFollowingProfile;
    String userid;
    JSONParser jsonParser = new JSONParser();
    String profileURL = Login.myURL + "view_profile";
    SwipeRefreshLayout swipeLayout;

    HashMap<String, String> userData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userid = getArguments().getString(Constant.TAG_USERID);
        view = inflater.inflate(R.layout.profile, container, false);

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getActivity(), "refreshing ", Toast.LENGTH_SHORT).show();
                if (MainActivity.isInternetPresent) {
                    Login.arrayListAsyncs.add(new ViewProfile());
                    Login.arrayListAsyncs.get(Login.arrayListAsyncs.size() - 1).execute(userid);
                }   else swipeLayout.setRefreshing(false);             }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAllXMLReferences();
        setAllClickListner();
        if (MainActivity.isInternetPresent) {

            Login.arrayListAsyncs.add(new ViewProfile());
            Login.arrayListAsyncs.get(Login.arrayListAsyncs.size() - 1).execute(userid);
        }    }



    void setAllClickListner() {
        imageViewProfilePictureProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Choose your desired option")
                        .setCancelable(true)
                        .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(getActivity(), ImageCaptureCamera.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Intent intent=new Intent(getActivity(),ProfilePicturePreview.class);
                                startActivity(intent);

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();


            }
        });




        textViewFollowerProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowFollowers.class);
                intent.putExtra(Constant.TAG_USERID, userid);
                startActivity(intent);

            }
        });
        textViewFollowingProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowFollowing.class);
                intent.putExtra(Constant.TAG_USERID, userid);
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
        textViewFollowerProfile = (TextView) view.findViewById(R.id.textViewFollowerProfile);
        textViewFollowingProfile = (TextView) view.findViewById(R.id.textViewFollowingProfile);

    }

    class ViewProfile extends AsyncTask<Object, Object, Boolean>

    {

        Bitmap profile_picture;

        @Override
        protected Boolean doInBackground(Object... args) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Constant.TAG_USERID, (String) args[0]);


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
                    try {

                        Log.i("ImageURL", userData.get(Constant.TAG_PROFILE_PICTURE_URL));
                       // URL imageURL = new URL("https://facing-app.herokuapp.com/" + userData.get(Constant.TAG_PROFILE_PICTURE_URL));
                        URL imageURL = new URL("http://192.168.8.106:3000/" + userData.get(Constant.TAG_PROFILE_PICTURE_URL));
                        Log.i("ImageURL", imageURL.toString());
                        profile_picture = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

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

                    imageViewProfilePictureProfile.setImageBitmap(profile_picture);
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
                swipeLayout.setRefreshing(false);

            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().finish();
    }
}
