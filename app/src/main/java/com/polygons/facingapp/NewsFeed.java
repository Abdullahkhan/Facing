package com.polygons.facingapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.polygons.facingapp.tools.CircleImageView;
import com.polygons.facingapp.tools.Constant;
import com.polygons.facingapp.tools.InteractiveScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    InteractiveScrollView scrollViewNewsFeed;
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
            }

            return false;
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
            } else
                Toast.makeText(getActivity(), "UnSuccessfull", Toast.LENGTH_SHORT).show();

        }
    }


    private void rearrangePosts() {
        HashMap<Long, LinearLayout> hashMap = new HashMap<Long, LinearLayout>();

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
                    //Login.arrayListAsyncs.get(Login.arrayListAsyncs.size() - 1).execute("http://192.168.8.101/:3000" + profilePicURL, circleImageView);
                    Login.arrayListAsyncs.get(Login.arrayListAsyncs.size() - 1).execute("http://facing-app.herokuapp.com//" + profilePicURL, circleImageView);
                }
            }

            LinearLayout linearLayoutVideo = (LinearLayout) linearLayoutEachPost.getChildAt(2);
            final TextView postLink = (TextView) linearLayoutVideo.getChildAt(0);
            FrameLayout frameLayoutVideo = (FrameLayout) linearLayoutVideo.getChildAt(1);

            final VideoView videoView = (VideoView) frameLayoutVideo.getChildAt(0);
            final Button buttonPlay = (Button) frameLayoutVideo.getChildAt(1);

            try {

                videoView.setVideoURI(Uri.parse("http://facing-app.herokuapp.com/" + postLink.getText().toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            // videoView.setVideoURI(Uri.parse("http://192.168.8.107:3000/" + postLink.getText().toString()));
            // videoView.setVideoURI(Uri.parse("http://192.168.8.107:3000/uploads/video.mp4"));
            //   videoView.setVideoURI(Uri.parse("http://portal.ekniazi.com/The%20Giver%20(2014)/The.Giver.2014.720p.BluRay.x264.YIFY.mp4"));
//                    videoView.setVideoPath("http://192.168.8.101/facing/upload/video.mp4");
            // videoView.setVideoPath("http://192.168.8.106:3000/uploads/110716_video.mp4");
            //android.widget.MediaController mc = new android.widget.MediaController(getActivity());
//            videoView.setMediaController(mc);
            buttonPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoView.start();
                    buttonPlay.setVisibility(View.INVISIBLE);
                }
            });
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    buttonPlay.setVisibility(View.VISIBLE);
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

    private View creatingPost(View tempView, ArrayList<HashMap<String, String>> post, int position) {

        TextView textViewItemListViewPostIDPostNewsFeed = (TextView) tempView.findViewById(R.id.textViewItemListViewPostIDPostNewsFeed);
        TextView textViewItemListViewNamePostNewsFeed = (TextView) tempView.findViewById(R.id.textViewItemListViewFullNamePostNewsFeed);
        TextView textViewItemListViewPostNewsFeed = (TextView) tempView.findViewById(R.id.textViewItemListViewPostNewsFeed);
        TextView textViewItemListViewTimePostGoneNewsFeed = (TextView) tempView.findViewById(R.id.textViewItemListViewTimePostGoneNewsFeed);
        TextView textViewItemListViewTimePostNewsFeed = (TextView) tempView.findViewById(R.id.textViewItemListViewTimePostNewsFeed);
        TextView textViewProfilePicURL = (TextView) tempView.findViewById(R.id.textViewProfilePicURL);
        Button buttonPostLike = (Button) tempView.findViewById(R.id.buttonPostLike);
        Button buttonPostUnLike = (Button) tempView.findViewById(R.id.buttonPostUnLike);


        final VideoView videoView = (VideoView) tempView.findViewById(R.id.videoView);

        final ViewGroup.LayoutParams params = videoView.getLayoutParams();
        videoView.post(new Runnable() {
            @Override
            public void run() {

                params.height = videoView.getWidth();
                videoView.setLayoutParams(params);
            }
        });
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

            linearLayoutPost.addView(creatingPost(tempView, post, position), 0);
        }
    }


    public void CreateAndAppendBottomPost() {
        LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int position = 0; position < bottomPost.size(); position++) {
            View tempView = li.inflate(R.layout.linearlayout_post, null);

            linearLayoutPost.addView(creatingPost(tempView, bottomPost, position), linearLayoutPost.getChildCount());
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

                    post.remove(position2);
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
        if ("".equals(res.toString()))
            return "0 second ago";
        else
            return res.toString();
    }


    @Override
    public void onResume() {
        super.onResume();
        //   new RefreshFacings().execute(userid, offset, bucket);
        if (MainActivity.isInternetPresent) {
            Login.arrayListAsyncs.add(new RefreshFacings());
            Login.arrayListAsyncs.get(Login.arrayListAsyncs.size() - 1).execute(userid, offset, bucket);
        }
    }
}
