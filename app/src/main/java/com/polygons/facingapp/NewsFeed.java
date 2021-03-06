package com.polygons.facingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.polygons.facingapp.tools.CircleImageView;
import com.polygons.facingapp.tools.Constant;
import com.polygons.facingapp.tools.InteractiveScrollView;
import com.polygons.facingapp.tools.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;


public class NewsFeed extends android.support.v4.app.Fragment {
    View view;
    public static InteractiveScrollView scrollViewNewsFeed;
    LinearLayout linearLayoutPost;
    public static SwipeRefreshLayout swipeLayout;


    String userid;
    String offset = "0";
    String bucket = "5";
    Boolean isBottomReached = false;
    static ArrayList<ArrayList<String>> facingsUrlArrayList;
    ArrayList<String> singleFacingURLArraylist;
    public static int loader = R.drawable.ic_launcher;

    JSONArray allFacingsURL = null;
    JSONParser jsonparser = new JSONParser();
    JSONObject json;
    String refreshFacings = Login.myURL + "newsfeed";
    String likeThisPost = Login.myURL + "likepost";
    String unlikeThisPost = Login.myURL + "unlike_post";
    String shareThisPost = Login.myURL + "share_post";
   /* public static AsyncTask<String, String, Boolean> asyncTaskrefreshFacings;
    public static AsyncTask<String, String, Boolean> asyncTaskBottomrefreshFacings;
    public static AsyncTask<String, String, Boolean> asyncTasklikeThisPost;
    public static AsyncTask<String, String, Boolean> asyncTaskunlikeThisPost;
    public static AsyncTask<String, String, Boolean> asyncTaskshareThisPost;*/


    SharedPreferences sp;

    ArrayList<HashMap<String, String>> post;
    ArrayList<HashMap<String, String>> bottomPost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.newsfeed, container, false);
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    Toast.makeText(getActivity(), "refreshing ", Toast.LENGTH_SHORT).show();
                    if (MainActivity.isInternetPresent) {
                        MainActivity.cancelAllAsync();

                        Login.arrayListAsyncs.add(new RefreshFacings());
                        Login.arrayListAsyncs.get(Login.arrayListAsyncs.size() - 1).execute(userid, offset, bucket);
                    }
                    //   new RefreshFacings().execute(userid, offset, bucket);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setAllXMLReferences();
        setAllButtonOnClickListeners();
        sp = this.getActivity().getSharedPreferences(Constant.TAG_USER, Activity.MODE_PRIVATE);
        userid = sp.getString(Constant.TAG_USERID, "0");

        Log.i(Constant.TAG_USERID, userid);

        scrollViewNewsFeed.setOnBottomReachedListener(new InteractiveScrollView.OnBottomReachedListener() {
            @Override
            public void onBottomReached() {

                if (!isBottomReached) {
                    //  new RefreshBottomFacings().execute(userid, linearLayoutPost.getChildCount() + "", bucket);
                    if (MainActivity.isInternetPresent) {
                        Login.arrayListAsyncs.add(new RefreshBottomFacings());
                        Login.arrayListAsyncs.get(Login.arrayListAsyncs.size() - 1).execute(userid, linearLayoutPost.getChildCount() + "", bucket);
                    }
                    Toast.makeText(getActivity(), "Bottom reached", Toast.LENGTH_SHORT).show();
                    Log.i("Bottom", "Bottom");
                    isBottomReached = true;
                }

            }
        });


//        new RefreshFacings().execute(userid, "0", "20");


//        listViewNewsFeed.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                Intent intent = new Intent(context, PlayFacing.class);
//                intent.putExtra(TAG_POSITION, position);
//                startActivity(intent);
//            }
//        });

    }

    private void setAllXMLReferences() {

        linearLayoutPost = (LinearLayout) view.findViewById(R.id.linearLayoutPost);
        scrollViewNewsFeed = (InteractiveScrollView) view.findViewById(R.id.scrollViewNewsFeed);
    }

    private void setAllButtonOnClickListeners() {
//        buttonFace.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), PostText.class));
//            }
//        });
//        buttonSearchUser.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(context, SearchUser.class));
//            }
//        });


    }

    public static void refreshNewsFeed() {
    }

    private HashMap<String, String> addPostParameters(HashMap<String, String> eachPost, JSONObject postObject) throws JSONException {
        eachPost.put(Constant.TAG_POST_ID, postObject.getString(Constant.TAG_POST_ID));
        eachPost.put(Constant.TAG_POST_USERID, postObject.getString(Constant.TAG_POST_USERID));
        eachPost.put(Constant.TAG_POST, postObject.getString(Constant.TAG_POST));
        eachPost.put(Constant.TAG_TIME, postObject.getString(Constant.TAG_TIME));
        eachPost.put(Constant.TAG_USER_FIRST_NAME, postObject.getString(Constant.TAG_USER_FIRST_NAME));
        eachPost.put(Constant.TAG_USER_LAST_NAME, postObject.getString(Constant.TAG_USER_LAST_NAME));
        eachPost.put(Constant.TAG_USER_PROFILE_PICTURE_URL, postObject.getString(Constant.TAG_USER_PROFILE_PICTURE_URL));
        eachPost.put(Constant.TAG_TOTAL_LIKES, postObject.getString(Constant.TAG_TOTAL_LIKES));
        eachPost.put(Constant.TAG_TOTAL_SHARE, postObject.getString(Constant.TAG_TOTAL_SHARE));
        eachPost.put(Constant.TAG_ISLIKE, String.valueOf(postObject.getBoolean(Constant.TAG_ISLIKE)));
        eachPost.put(Constant.TAG_TOTAL_COMMENT, postObject.getString(Constant.TAG_TOTAL_COMMENT));
        eachPost.put(Constant.TAG_USER_COMMENT, postObject.getJSONArray(Constant.TAG_USER_COMMENT).toString());


        return eachPost;
    }

    class RefreshFacings extends AsyncTask<Object, Object, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Object... args) {
            post = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Constant.TAG_USERID, (String) args[0]);
            params.put(Constant.TAG_OFFSET, (String) args[1]);
            params.put(Constant.TAG_BUCKET, (String) args[2]);
            try {
                json = jsonparser.makeHttpRequest(refreshFacings, Constant.TAG_POST_METHOD, params);
                facingsUrlArrayList = new ArrayList<ArrayList<String>>();
                JSONArray jsonArray;
                Boolean success = json.getBoolean(Constant.TAG_STATUS);
                if (success) {
                    jsonArray = json.getJSONArray(Constant.TAG_MESSAGE);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        HashMap<String, String> eachPost = new HashMap<String, String>();
                        JSONObject postObject = jsonArray.getJSONObject(i);

                        post.add(addPostParameters(eachPost, postObject));

                    }
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }


        }

        @Override
        protected void onPostExecute(Boolean result) {
            //   pDialog.dismiss();
            if (result) {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // listViewNewsFeed.setAdapter(adapter);
                        removeDuplicatePosts();
                        CreateAndAppendPost();
                        swipeLayout.setRefreshing(false);
                        rearrangePosts();
                        scrollViewNewsFeed.fullScroll(ScrollView.FOCUS_UP);

                        // Toast.makeText(getActivity(), "Newsfeed updated ", Toast.LENGTH_SHORT).show();

                    }
                });
            }

        }
    }


    class RefreshBottomFacings extends AsyncTask<Object, Object, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(Object... args) {
            bottomPost = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Constant.TAG_USERID, (String) args[0]);
            params.put(Constant.TAG_OFFSET, (String) args[1]);
            params.put(Constant.TAG_BUCKET, (String) args[2]);
            json = jsonparser.makeHttpRequest(refreshFacings, Constant.TAG_POST_METHOD, params);
            facingsUrlArrayList = new ArrayList<ArrayList<String>>();
            JSONArray jsonArray;
            try {
                Boolean success = json.getBoolean(Constant.TAG_STATUS);
                if (success) {
                    jsonArray = json.getJSONArray(Constant.TAG_MESSAGE);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        HashMap<String, String> eachPost = new HashMap<String, String>();
                        JSONObject postObject = jsonArray.getJSONObject(i);
                        bottomPost.add(addPostParameters(eachPost, postObject));

                    }
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // listViewNewsFeed.setAdapter(adapter);
                        CreateAndAppendBottomPost();
                        Toast.makeText(getActivity(), "Bottom Newsfeed updated ", Toast.LENGTH_SHORT).show();
                        isBottomReached = false;
                        rearrangePosts();


                    }
                });
            }

        }
    }

    class LikeThisPost extends AsyncTask<Object, Object, Boolean> {

        Button like;
        Button unlike;

        @Override
        protected Boolean doInBackground(Object... args) {
            post = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Constant.TAG_USERID, (String) args[0]);
            params.put(Constant.TAG_POST_ID_SEND, (String) args[1]);
            like = (Button) args[2];
            unlike = (Button) args[3];

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    like.setVisibility(View.GONE);
                    unlike.setVisibility(View.VISIBLE);
                }
            });

            try {
                json = jsonparser.makeHttpRequest(likeThisPost, Constant.TAG_POST_METHOD, params);


                Boolean success = json.getBoolean(Constant.TAG_STATUS);
                if (success) {
                    Log.i("liked", json.toString());
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
                like.setVisibility(View.GONE);
                unlike.setVisibility(View.VISIBLE);
            } else {
                like.setVisibility(View.VISIBLE);
                unlike.setVisibility(View.GONE);
            }

        }
    }

    class UnLikeThisPost extends AsyncTask<Object, Object, Boolean> {

        Button like;
        Button unlike;

        @Override
        protected Boolean doInBackground(Object... args) {
            post = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Constant.TAG_USERID, (String) args[0]);
            params.put(Constant.TAG_POST_ID_SEND, (String) args[1]);
            like = (Button) args[2];
            unlike = (Button) args[3];

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    like.setVisibility(View.VISIBLE);
                    unlike.setVisibility(View.GONE);
                }
            });

            try {
                json = jsonparser.makeHttpRequest(unlikeThisPost, Constant.TAG_POST_METHOD, params);


                Boolean success = json.getBoolean(Constant.TAG_STATUS);
                if (success) {
                    Log.i("liked", json.toString());
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            //   pDialog.dismiss();
            if (result) {
                like.setVisibility(View.VISIBLE);
                unlike.setVisibility(View.GONE);
            } else {
                like.setVisibility(View.GONE);
                unlike.setVisibility(View.VISIBLE);
            }

        }
    }

    class ShareThisPost extends AsyncTask<Object, Object, Boolean> {


        @Override
        protected Boolean doInBackground(Object... args) {
            post = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Constant.TAG_USERID, (String) args[0]);
            params.put(Constant.TAG_POST_ID_SEND, (String) args[1]);

            try {
                json = jsonparser.makeHttpRequest(shareThisPost, Constant.TAG_POST_METHOD, params);


                Boolean success = json.getBoolean(Constant.TAG_STATUS);
                if (success) {
                    Log.i("liked", json.toString());
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
                Toast.makeText(getActivity(), "Shared Successfully", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getActivity(), "UnSuccessfull", Toast.LENGTH_SHORT).show();

        }
    }

    class SetUserProfilePicture extends AsyncTask<Object, Object, Boolean> {
        Bitmap profile_picture;
        CircleImageView circleImageView;

        @Override
        protected Boolean doInBackground(Object... args) {


            try {
                circleImageView = (CircleImageView) args[1];
                profile_picture = BitmapFactory.decodeStream(new URL((String) args[0]).openConnection().getInputStream());
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                circleImageView.setImageBitmap(profile_picture);
            }
//            else
            // Toast.makeText(getActivity(), "UnSuccessfull", Toast.LENGTH_SHORT).show();

        }
    }

    private class DownloadVideos extends AsyncTask<Object, Object, Boolean> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Object... arg) {
            return downloadFile((String) arg[0], (String) arg[1]);

        }

        protected void onPostExecute(Boolean result) {

        }

    }

    private Boolean downloadFile(String fileURL, String fileName) {
        try {
            String rootDir = Environment.getExternalStorageDirectory()
                    + File.separator + "VemeCircle";
            File rootFile = new File(rootDir);
            rootFile.mkdir();
            URL url = new URL(fileURL);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
            FileOutputStream f = new FileOutputStream(new File(rootFile,
                    fileName));
            InputStream in = c.getInputStream();
            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
            }
            f.close();
            return true;
        } catch (Exception e) {
            Log.d("Error....", e.toString());
            return false;
        }
    }

    private void rearrangePosts() {
        HashMap<Long, LinearLayout> hashMap = new HashMap<Long, LinearLayout>();
        //  final Tools tools = new Tools();

        for (int position = 0; position < linearLayoutPost.getChildCount(); position++) {
            ViewGroup linearLayoutEachPost = (ViewGroup) linearLayoutPost.getChildAt(position);

            LinearLayout linearLayout = (LinearLayout) linearLayoutEachPost.getChildAt(1);
            TextView textViewPostTime = (TextView) linearLayout.getChildAt(2);


            TextView textViewPostId = (TextView) linearLayoutEachPost.getChildAt(0);
            final String postid = textViewPostId.getText().toString();

            LinearLayout linearLayoutbuttons = (LinearLayout) linearLayoutEachPost.getChildAt(3);
            final Button like = (Button) linearLayoutbuttons.getChildAt(0);
            final Button unlike = (Button) linearLayoutbuttons.getChildAt(1);
            Button share = (Button) linearLayoutbuttons.getChildAt(3);

            TextView textViewProfilePicURL = (TextView) linearLayoutEachPost.getChildAt(4);
            String profilePicURL = textViewProfilePicURL.getText().toString();


            CircleImageView circleImageView = (CircleImageView) linearLayout.getChildAt(0);
            if (circleImageView.getDrawable().getConstantState() ==
                    getResources().getDrawable(R.drawable.thumbnail).getConstantState()) {
                //   new SetUserProfilePicture().execute("http://facing-app.herokuapp.com/"+profilePicURL, circleImageView);
                if (MainActivity.isInternetPresent) {
                    Login.arrayListAsyncs.add(new SetUserProfilePicture());
                    Login.arrayListAsyncs.get(Login.arrayListAsyncs.size() - 1).execute(Login.baseUrl + profilePicURL, circleImageView);
                    // Login.arrayListAsyncs.get(Login.arrayListAsyncs.size() - 1).execute("http://facing-app.herokuapp.com//" + profilePicURL, circleImageView);
                }
            }

            LinearLayout linearLayoutVideo = (LinearLayout) linearLayoutEachPost.getChildAt(2);
            final TextView postLink = (TextView) linearLayoutVideo.getChildAt(0);
            FrameLayout frameLayoutVideo = (FrameLayout) linearLayoutVideo.getChildAt(1);

            final LinearLayout linearLayoutVideos = (LinearLayout) frameLayoutVideo.getChildAt(0);
//            final VideoView videoView = (VideoView) linearLayoutVideos.getChildAt(0);
            final Button buttonPlay = (Button) frameLayoutVideo.getChildAt(1);
            final Button buttonReply = (Button) frameLayoutVideo.getChildAt(2);

            final TextView textViewComments = (TextView) linearLayoutEachPost.getChildAt(5);
            JSONArray jsonArrayComments = null;
            try {

                jsonArrayComments = new JSONArray(textViewComments.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            HashMap<String, String> hashMapComments = null;
            final ArrayList<HashMap<String, String>> arrayListComments = new ArrayList<>();
            for (int i = 0; i < jsonArrayComments.length(); i++) {

                try {

                    hashMapComments = new HashMap<>();
                    JSONObject jsonObjectEachComment = jsonArrayComments.getJSONObject(i);
                    hashMapComments.put(Constant.TAG_COMMENT, jsonObjectEachComment.getString(Constant.TAG_COMMENT));
                    //   hashMapComments.put(Constant.TAG_USER_COMMENT, jsonObjectEachComment.getString(Constant.TAG_USER_COMMENT));
                    hashMapComments.put(Constant.TAG_COMMENT_USER_ID, jsonObjectEachComment.getString(Constant.TAG_COMMENT_USER_ID));
                    hashMapComments.put(Constant.TAG_COMMENT_FIRST_NAME, jsonObjectEachComment.getString(Constant.TAG_COMMENT_FIRST_NAME));
                    hashMapComments.put(Constant.TAG_COMMENT_LAST_NAME, jsonObjectEachComment.getString(Constant.TAG_COMMENT_LAST_NAME));
                    hashMapComments.put(Constant.TAG_COMMENT_TIME, jsonObjectEachComment.getString(Constant.TAG_COMMENT_TIME));
                    hashMapComments.put(Constant.TAG_ENABLE, jsonObjectEachComment.getString(Constant.TAG_ENABLE));
                    hashMapComments.put(Constant.TAG_COMMENT_ID, jsonObjectEachComment.getString(Constant.TAG_COMMENT_ID));
                    hashMapComments.put(Constant.TAG_COMMENT_PROFILE_PIC_URL, jsonObjectEachComment.getString(Constant.TAG_COMMENT_PROFILE_PIC_URL));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                arrayListComments.add(hashMapComments);

            }

           /* final VideoView videoView = new VideoView(getActivity());
            videoView.setLayoutParams(new LinearLayout.LayoutParams(VideoCapture.getScreenWidth(), VideoCapture.getScreenWidth()));
            videoView.setVideoURI(Uri.parse(Login.baseUrl + postLink.getText().toString()));
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                   *//* buttonPlay.setVisibility(View.VISIBLE);
                    buttonReply.setVisibility(View.VISIBLE);
                    final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0, 0, 80, 0);
                    params.gravity = Gravity.CENTER;
                    buttonPlay.setLayoutParams(params);
                    buttonPlay.getLayoutParams().width = 120;
                    buttonPlay.getLayoutParams().height = 120;
                    buttonPlay.requestLayout();*//*

                }
            });*/

             final MediaPlayer  mediaPlayerFirstVideo=MediaPlayer.create(getActivity().getBaseContext(),Uri.parse(Login.baseUrl + postLink.getText().toString()));
            SurfaceView surfaceViewVideoEmpty=new SurfaceView(getActivity());
            surfaceViewVideoEmpty.setLayoutParams(new LinearLayout.LayoutParams(VideoCapture.getScreenWidth(), VideoCapture.getScreenWidth()));

            linearLayoutVideos.addView(surfaceViewVideoEmpty);
            SurfaceView surfaceViewVideo=(SurfaceView) linearLayoutVideos.getChildAt(linearLayoutVideos.getChildCount()-1);
            final SurfaceHolder surfaceHolderVideo=surfaceViewVideo.getHolder();
          

            surfaceHolderVideo.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {

                    final ArrayList<MediaPlayer> arrayListmediaPlayers=new ArrayList<MediaPlayer>();



                    try {
                        mediaPlayerFirstVideo.prepare();
                    }catch (Exception e){

                        e.printStackTrace();
                    }

                    mediaPlayerFirstVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mediaPlayerFirstVideo.setDisplay(surfaceHolderVideo);
                        }
                    });
                    mediaPlayerFirstVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {

                            mediaPlayerFirstVideo.release();
                            try {
                                arrayListmediaPlayers.get(1).setDisplay(surfaceHolderVideo);
                                arrayListmediaPlayers.get(1).start();
                            }catch (Exception e){

                            }
                        }
                    });

                    mediaPlayerFirstVideo.setAudioStreamType(AudioManager.STREAM_MUSIC);

                    arrayListmediaPlayers.add(mediaPlayerFirstVideo);
                    final Tools toolsComment=new Tools();
                    for (toolsComment.position=0;toolsComment.position<arrayListComments.size();) {
                        final Integer positionComment = new Integer(toolsComment.position);
                        MediaPlayer mediaPlayerComment = MediaPlayer.create(getActivity().getBaseContext(), Uri.parse(Login.baseUrl + "uploads/" + arrayListComments.get(positionComment).get(Constant.TAG_COMMENT)));
                        mediaPlayerComment.prepareAsync();

                        mediaPlayerComment.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                try {
                                    arrayListmediaPlayers.get(positionComment + 1).release();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    arrayListmediaPlayers.get(positionComment + 2).setDisplay(surfaceHolderVideo);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    arrayListmediaPlayers.get(positionComment + 2).start();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        });

                        toolsComment.position++;
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {

                }
            }
            );



//            final ArrayList<VideoView> arrayListVideoView = new ArrayList<>();
//            VideoView videoView = new VideoView(getActivity());
//            videoView.setVideoURI(Uri.parse(Login.baseUrl + postLink.getText().toString()));
//            arrayListVideoView.add(videoView);
//
//
//            for (int i = 0; i < arrayListComments.size(); i++) {
//                VideoView videoViewComment = new VideoView(getActivity());
//                videoViewComment.setVideoPath(Login.baseUrl + "uploads/" + arrayListComments.get(i).get(Constant.TAG_COMMENT));
//                videoViewComment.setVisibility(View.GONE);
//                arrayListVideoView.add(videoViewComment);
//            }
//            final Tools toolComment = new Tools();
//            Log.i("Size", arrayListComments.size() + "" + arrayListVideoView.size());
//            for (toolComment.position = 0; toolComment.position < arrayListVideoView.size(); ) {
//                arrayListVideoView.get(toolComment.position).seekTo(100);
//                arrayListVideoView.get(toolComment.position).start();
//                arrayListVideoView.get(toolComment.position).pause();
//                arrayListVideoView.get(toolComment.position).setLayoutParams(new LinearLayout.LayoutParams(VideoCapture.getScreenWidth(), VideoCapture.getScreenWidth()));
//                arrayListVideoView.get(toolComment.position).setOnErrorListener(new MediaPlayer.OnErrorListener() {
//                    @Override
//                    public boolean onError(MediaPlayer mp, int what, int extra) {
////                        arrayListVideoView.get(toolComment.position).setBackgroundDrawable(getResources().getDrawable(R.drawable.thumbnail));
//                        buttonPlay.setVisibility(View.INVISIBLE);
//                        return true;
//                    }
//                });
//                final Integer positionComment = new Integer(toolComment.position);
//                Log.i("listener", positionComment + "");
//                if (toolComment.position == arrayListVideoView.size() - 1 || arrayListVideoView.size() == 0) {
//
//
//                    arrayListVideoView.get(toolComment.position).setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mp) {
//                            buttonPlay.setVisibility(View.VISIBLE);
//                            buttonReply.setVisibility(View.VISIBLE);
//                            final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
//                                    FrameLayout.LayoutParams.WRAP_CONTENT,
//                                    FrameLayout.LayoutParams.WRAP_CONTENT
//                            );
//                            params.setMargins(0, 0, 80, 0);
//                            params.gravity = Gravity.CENTER;
//                            buttonPlay.setLayoutParams(params);
//                            buttonPlay.getLayoutParams().width = 120;
//                            buttonPlay.getLayoutParams().height = 120;
//                            buttonPlay.requestLayout();
//                            for (int i = 0; i < arrayListVideoView.size(); i++) {
//                                arrayListVideoView.get(i).setVisibility(View.GONE);
//                            }
//                            arrayListVideoView.get(0).setVisibility(View.VISIBLE);
//
//                        }
//                    });
//                } else {
//                    arrayListVideoView.get(toolComment.position).setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mp) {
//
//                            try {
//                                arrayListVideoView.get(positionComment - 1).setVisibility(View.GONE);
//                                Log.i("listener", "pre");
//                                Log.i("listener", positionComment + "");
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            try {
//                                arrayListVideoView.get(positionComment).setVisibility(View.GONE);
//                                Log.i("listener", "cur");
//                                Log.i("listener", positionComment + "");
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            try {
//                                arrayListVideoView.get(positionComment + 1).setVisibility(View.VISIBLE);
//                                arrayListVideoView.get(positionComment + 1).start();
//                                Log.i("listener", "next");
//                                Log.i("listener", positionComment + "");
//
//                            } catch (Exception e) {
//
//                                e.printStackTrace();
//                            }
//
//                        }
//
//                    });
//                }
//                toolComment.position++;
//            }
//            for (int i = 0; i < arrayListVideoView.size(); i++) {
//                linearLayoutVideos.addView(arrayListVideoView.get(i));
//            }


            buttonPlay.setVisibility(View.VISIBLE);
            buttonReply.setVisibility(View.GONE);
            final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(80, 0, 0, 0);
            params.gravity = Gravity.CENTER;
            buttonReply.setLayoutParams(params);


            buttonPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonPlay.setVisibility(View.GONE);
                    buttonReply.setVisibility(View.GONE);
                    mediaPlayerFirstVideo.start();
                    //arrayListVideoView.get(0).start();

                }
            });
            buttonReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ReplyVideoCapture.class);
                    intent.putExtra(Constant.TAG_POST_ID_SEND, postid);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.no_animation);

                }
            });


            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.i("postid", postid);
                    //   new LikeThisPost().execute(userid, postid, like, unlike);
                    if (MainActivity.isInternetPresent) {
                        Login.arrayListAsyncs.add(new LikeThisPost());
                        Login.arrayListAsyncs.get(Login.arrayListAsyncs.size() - 1).execute(userid, postid, like, unlike);
                    }

                }
            });

            unlike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("postid", postid + " Unlike");
                    //    new UnLikeThisPost().execute(userid, postid, like, unlike);
                    if (MainActivity.isInternetPresent) {
                        Login.arrayListAsyncs.add(new UnLikeThisPost());
                        Login.arrayListAsyncs.get(Login.arrayListAsyncs.size() - 1).execute(userid, postid, like, unlike);
                    }
                }
            });

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //  new ShareThisPost().execute(userid, postid);
                    if (MainActivity.isInternetPresent) {
                        Login.arrayListAsyncs.add(new ShareThisPost());
                        Login.arrayListAsyncs.get(Login.arrayListAsyncs.size() - 1).execute(userid, postid);
                    }
                }
            });


            hashMap.put(Long.parseLong(textViewPostTime.getText().toString()), (LinearLayout) linearLayoutPost.getChildAt(position));


        }

        linearLayoutPost.removeAllViews();

        Map<Long, LinearLayout> map = new TreeMap<Long, LinearLayout>(hashMap);
        System.out.println("After Sorting:");
        Set set2 = map.entrySet();

        Iterator iterator2 = set2.iterator();
        while (iterator2.hasNext()) {
            try {

                Map.Entry me2 = (Map.Entry) iterator2.next();
//            System.out.print(me2.getKey() + ": ");
//            System.out.println(me2.getValue());
                linearLayoutPost.addView(hashMap.get(me2.getKey()), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        setLikeShareButtonListener();
    }

    public void setLikeShareButtonListener() {

    }

    private View creatingPost(View tempView, ArrayList<HashMap<String, String>> post,
                              int position) {

        TextView textViewItemListViewPostIDPostNewsFeed = (TextView) tempView.findViewById(R.id.textViewItemListViewPostIDPostNewsFeed);
        TextView textViewItemListViewNamePostNewsFeed = (TextView) tempView.findViewById(R.id.textViewItemListViewFullNamePostNewsFeed);
        TextView textViewItemListViewPostNewsFeed = (TextView) tempView.findViewById(R.id.textViewItemListViewPostNewsFeed);
        TextView textViewItemListViewTimePostGoneNewsFeed = (TextView) tempView.findViewById(R.id.textViewItemListViewTimePostGoneNewsFeed);
        TextView textViewItemListViewTimePostNewsFeed = (TextView) tempView.findViewById(R.id.textViewItemListViewTimePostNewsFeed);
        TextView textViewProfilePicURL = (TextView) tempView.findViewById(R.id.textViewProfilePicURL);
        Button buttonPostLike = (Button) tempView.findViewById(R.id.buttonPostLike);
        Button buttonPostUnLike = (Button) tempView.findViewById(R.id.buttonPostUnLike);
        TextView textViewPostTotalLikes = (TextView) tempView.findViewById(R.id.textViewPostTotalLikes);
        TextView textViewPostTotalShares = (TextView) tempView.findViewById(R.id.textViewPostTotalShares);

        TextView textViewComments = (TextView) tempView.findViewById(R.id.textViewComments);


        textViewComments.setText(post.get(position).get(Constant.TAG_USER_COMMENT));
        //  videoView.setVideoPath(post.get(position).get(Constant.TAG_POST));


        //  videoView.start();


//            Button buttonLike = (Button) tempView.findViewById(R.id.buttonLike);
//            Button buttonUnLike = (Button) tempView.findViewById(R.id.buttonUnLike);
//            Button buttonShare = (Button) tempView.findViewById(R.id.buttonShare);
//
//
//            String post_id=post.get(position).get(Constant.TAG_POST_ID);
//
//            buttonLike.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    new LikeThisPost.execute(post);
//                }
//            });


        textViewItemListViewPostIDPostNewsFeed.setText(post.get(position).get(Constant.TAG_POST_ID));
        textViewItemListViewNamePostNewsFeed.setText(post.get(position).get(Constant.TAG_USER_FIRST_NAME) + " " + post.get(position).get(Constant.TAG_USER_LAST_NAME));
        textViewItemListViewPostNewsFeed.setText(post.get(position).get(Constant.TAG_POST));
        textViewItemListViewTimePostNewsFeed.setText(toDuration(System.currentTimeMillis() - Long.parseLong(post.get(position).get(Constant.TAG_TIME))));
        textViewItemListViewTimePostGoneNewsFeed.setText(String.valueOf(Long.parseLong(post.get(position).get(Constant.TAG_TIME))));
        textViewProfilePicURL.setText(post.get(position).get(Constant.TAG_USER_PROFILE_PICTURE_URL));
        int noLikes = 0;
        try {

            noLikes = Integer.parseInt(post.get(position).get(Constant.TAG_TOTAL_LIKES));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (noLikes <= 1) {

            textViewPostTotalLikes.setText(noLikes + " Like");
        } else {
            textViewPostTotalLikes.setText(post.get(position).get(Constant.TAG_TOTAL_LIKES) + " Likes");

        }
        int noShares = 0;
        try {

            noShares = Integer.parseInt(post.get(position).get(Constant.TAG_TOTAL_SHARE));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (noShares <= 1) {

            textViewPostTotalShares.setText(noShares + " Share");
        } else {
            textViewPostTotalShares.setText(post.get(position).get(Constant.TAG_TOTAL_SHARE) + " Shares");

        }

        if (Boolean.parseBoolean(post.get(position).get(Constant.TAG_ISLIKE))) {
            buttonPostLike.setVisibility(View.GONE);
            buttonPostUnLike.setVisibility(View.VISIBLE);
        } else {
            buttonPostLike.setVisibility(View.VISIBLE);
            buttonPostUnLike.setVisibility(View.GONE);
        }
        return tempView;
    }

    public void CreateAndAppendPost() {
        LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        for (int position = post.size() - 1; position >= 0; position--) {
        for (int position = 0; position < post.size(); position++) {
            View tempView = li.inflate(R.layout.linearlayout_post, null);
            try {

                linearLayoutPost.addView(creatingPost(tempView, post, position), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void CreateAndAppendBottomPost() {
        LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int position = 0; position < bottomPost.size(); position++) {
            View tempView = li.inflate(R.layout.linearlayout_post, null);
            try {

                linearLayoutPost.addView(creatingPost(tempView, bottomPost, position), linearLayoutPost.getChildCount());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




    /*public void startTheVideo(ArrayList<String> comments) {

        videoViewFacing.setVideoPath(clonedfacingsUrlArrayList.get(position)
                .get(i));
        videoViewFacing.setMediaController(new MediaController(this));
        videoViewFacing.start();
        videoViewFacing
                .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.setDisplay(null);
                        mp.reset();
                        mp.setDisplay(videoViewFacing.getHolder());
                        i = (i + 1)
                                % clonedfacingsUrlArrayList.get(position)
                                .size();
                        videoViewFacing
                                .setVideoPath(clonedfacingsUrlArrayList.get(
                                        position).get(i));
                        videoViewFacing.start();
                    }
                });
    }*/


    void removeDuplicatePosts() {
        for (int position = 0; position < linearLayoutPost.getChildCount(); position++) {
            ViewGroup linearLayoutEachPost = (ViewGroup) linearLayoutPost.getChildAt(position);
            TextView textViewPostId = (TextView) linearLayoutEachPost.getChildAt(0);
            String postId = textViewPostId.getText().toString();

            for (int position2 = 0; position2 < post.size(); position2++) {
                if (postId.equals(post.get(position2).get(Constant.TAG_POST_ID))) {

//                    post.remove(position2);
                    linearLayoutPost.removeViewAt(position);
                }


            }


        }
    }

    ;

    private BaseAdapter adapter = new BaseAdapter() {
        //        ImageView imageView;
//        TextView username;
//        Bitmap bmImg;
        TextView textViewItemListViewNamePostNewsFeed;
        TextView textViewItemListViewPostNewsFeed;
        TextView textViewItemListViewTimePostNewsFeed;
        TextView textViewItemListViewPostIDPostNewsFeed;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View retval = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_listview_newsfeed, null);


//            imageView = (ImageView) retval.findViewById(R.id.imageButtonItemListViewNewsFeed);
//            username = (TextView) retval.findViewById(R.id.textViewItemListViewNewsFeedUsername);


            textViewItemListViewPostIDPostNewsFeed = (TextView) retval.findViewById(R.id.textViewItemListViewPostIDPostNewsFeed);
            textViewItemListViewNamePostNewsFeed = (TextView) retval.findViewById(R.id.textViewItemListViewFullNamePostNewsFeed);
            textViewItemListViewPostNewsFeed = (TextView) retval.findViewById(R.id.textViewItemListViewPostNewsFeed);
            textViewItemListViewTimePostNewsFeed = (TextView) retval.findViewById(R.id.textViewItemListViewTimePostNewsFeed);

            textViewItemListViewPostIDPostNewsFeed.setText(post.get(position).get(0));
            textViewItemListViewNamePostNewsFeed.setText(post.get(position).get(1));
            textViewItemListViewPostNewsFeed.setText(post.get(position).get(2));
            textViewItemListViewTimePostNewsFeed.setText(toDuration(System.currentTimeMillis() - Long.parseLong(post.get(position).get(3))));


//            ImageLoader imgLoader = new ImageLoader(getApplicationContext());
//            int loader = R.drawable.ic_launcher;
            // whenever you want to load an image from url
            // call DisplayImage function
            // url - image url to load
            // loader - loader image, will be displayed before getting image
            // image - ImageView
//            imgLoader.DisplayImage(facingsUrlArrayList.get(position).get(0), loader, imageView);
//            imageView.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context, PlayFacing.class);
//                    intent.putExtra(TAG_POSITION, position);
//                    startActivity(intent);
//
//                }
//            });
//            username.setText(facingsUrlArrayList.get(position).get(0).split("_")[1].substring(0, facingsUrlArrayList.get(position).get(0).split("_")[1].length() - 4));
//

            return retval;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return post.get(position).get(0);
        }

        @Override
        public int getCount() {
            return post.size();
        }
    };


    public static final List<Long> times = Arrays.asList(
            TimeUnit.DAYS.toMillis(365),
            TimeUnit.DAYS.toMillis(30),
            TimeUnit.DAYS.toMillis(1),
            TimeUnit.HOURS.toMillis(1),
            TimeUnit.MINUTES.toMillis(1),
            TimeUnit.SECONDS.toMillis(1));
    public static final List<String> timesString = Arrays.asList("year", "month", "d", "h", "m", "s");

    public static String toDuration(long duration) {

        StringBuffer res = new StringBuffer();
        for (int i = 0; i < NewsFeed.times.size(); i++) {
            Long current = NewsFeed.times.get(i);
            long temp = duration / current;
            if (temp > 0) {
                res.append(temp).append(" ")
                        .append(NewsFeed.timesString.get(i))
                        .append(timesString.get(i).equals("year") || timesString.get(i).equals("month") ? "s" : "");
                break;
            }
        }
        if ("" .equals(res.toString()))
            return "0 second ago";
        else
            return res.toString();
    }


    @Override
    public void onResume() {
        super.onResume();
        //   new RefreshFacings().execute(userid, offset, bucket);
        if (MainActivity.isInternetPresent) {
            try {

                Login.arrayListAsyncs.add(new RefreshFacings());
                Login.arrayListAsyncs.get(Login.arrayListAsyncs.size() - 1).execute(userid, offset, bucket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
