package com.polygons.facingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.polygons.facingapp.tools.Constant;
import com.polygons.facingapp.tools.InteractiveScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class NewsFeed extends android.support.v4.app.Fragment {
    Context context = getContext();
    Activity activity = getActivity();
    View view;
    //    Button buttonFace;
    Button buttonLogOut;
    Button buttonRefreshFacings;
    Button buttonSearchUser;
    Button buttonShowFollowers;
    Button buttonShowFollowing;
    InteractiveScrollView scrollViewNewsFeed;
    ListView listViewNewsFeed;
    LinearLayout linearLayoutPost;
    SwipeRefreshLayout swipeLayout;

    String userid;
    String offset = "0";
    String bucket = "5";
    Boolean isBottomReached = false;
    ProgressDialog pDialog;
    static ArrayList<ArrayList<String>> facingsUrlArrayList;
    ArrayList<String> singleFacingURLArraylist;
    public static int loader = R.drawable.ic_launcher;

    JSONArray allFacingsURL = null;
    JSONParser jsonparser = new JSONParser();
    JSONObject json;
    String refreshFacings = Login.myURL + "newsfeed";
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
                Toast.makeText(getActivity(), "refreshing ", Toast.LENGTH_SHORT).show();

                new RefreshFacings().execute(userid, offset, bucket);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAllXMLReferences();
        setAllButtonOnClickListeners();
        sp = this.getActivity().getSharedPreferences("user", Activity.MODE_PRIVATE);
        userid = sp.getString(Constant.TAG_USERID, "0");
        Log.i(Constant.TAG_USERID, userid);

        scrollViewNewsFeed.setOnBottomReachedListener(new InteractiveScrollView.OnBottomReachedListener() {
            @Override
            public void onBottomReached() {

                if (!isBottomReached) {
                    new RefreshBottomFacings().execute(userid, linearLayoutPost.getChildCount() + "", bucket);
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

//        buttonFace = (Button) view.findViewById(R.id.buttonFace);
//      buttonSearchUser = (Button) findViewById(R.id.buttonSearchUser);
        listViewNewsFeed = (ListView) view.findViewById(R.id.listViewNewsFeed);
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

    class RefreshFacings extends AsyncTask<String, String, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Refreshing your Facings");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            // pDialog.show();

        }

        @Override
        protected Boolean doInBackground(String... args) {
            post = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Constant.TAG_USERID, args[0]);
            params.put(Constant.TAG_OFFSET, args[1]);
            params.put(Constant.TAG_BUCKET, args[2]);
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
                        eachPost.put(Constant.TAG_POST_ID, postObject.getString(Constant.TAG_POST_ID));
                        eachPost.put(Constant.TAG_POST_USERID, postObject.getString(Constant.TAG_POST_USERID));
                        eachPost.put(Constant.TAG_USER_FIRST_NAME, postObject.getString(Constant.TAG_USER_FIRST_NAME));
                        eachPost.put(Constant.TAG_USER_LAST_NAME, postObject.getString(Constant.TAG_USER_LAST_NAME));
                        eachPost.put(Constant.TAG_POST, postObject.getString(Constant.TAG_POST));
                        eachPost.put(Constant.TAG_TIME, postObject.getString(Constant.TAG_TIME));
                        post.add(eachPost);

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
                        // Toast.makeText(getActivity(), "Newsfeed updated ", Toast.LENGTH_SHORT).show();

                    }
                });
            }

        }
    }

    class RefreshBottomFacings extends AsyncTask<String, String, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Refreshing your Facings");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            // pDialog.show();

        }

        @Override
        protected Boolean doInBackground(String... args) {
            bottomPost = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Constant.TAG_USERID, args[0]);
            params.put(Constant.TAG_OFFSET, args[1]);
            params.put(Constant.TAG_BUCKET, args[2]);
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
                        eachPost.put(Constant.TAG_POST_ID, postObject.getString(Constant.TAG_POST_ID));
                        eachPost.put(Constant.TAG_POST_USERID, postObject.getString(Constant.TAG_POST_USERID));
                        eachPost.put(Constant.TAG_POST, postObject.getString(Constant.TAG_POST));
                        eachPost.put(Constant.TAG_TIME, postObject.getString(Constant.TAG_TIME));
                        bottomPost.add(eachPost);

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
                        CreateAndAppendBottomPost();
                        Toast.makeText(getActivity(), "Bottom Newsfeed updated ", Toast.LENGTH_SHORT).show();
                        isBottomReached = false;

                    }
                });
            }

        }
    }

    public void CreateAndAppendPost() {
        LayoutInflater li = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int position = post.size() - 1; position >= 0; position--) {
            View tempView = li.inflate(R.layout.linearlayout_post, null);


            TextView textViewItemListViewPostIDPostNewsFeed = (TextView) tempView.findViewById(R.id.textViewItemListViewPostIDPostNewsFeed);
            TextView textViewItemListViewNamePostNewsFeed = (TextView) tempView.findViewById(R.id.textViewItemListViewFullNamePostNewsFeed);
            TextView textViewItemListViewPostNewsFeed = (TextView) tempView.findViewById(R.id.textViewItemListViewPostNewsFeed);
            TextView textViewItemListViewTimePostNewsFeed = (TextView) tempView.findViewById(R.id.textViewItemListViewTimePostNewsFeed);


            textViewItemListViewPostIDPostNewsFeed.setText(post.get(position).get(Constant.TAG_POST_ID));
            textViewItemListViewNamePostNewsFeed.setText(post.get(position).get(Constant.TAG_USER_FIRST_NAME) + " " + post.get(position).get(Constant.TAG_USER_LAST_NAME));//// TODO: 9/4/2016
            textViewItemListViewPostNewsFeed.setText(post.get(position).get(Constant.TAG_POST));
            textViewItemListViewTimePostNewsFeed.setText(toDuration(System.currentTimeMillis() - Long.parseLong(post.get(position).get(Constant.TAG_TIME))));
            linearLayoutPost.addView(tempView, 0);
        }
    }

    public void CreateAndAppendBottomPost() {
        LayoutInflater li = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int position = 0; position < bottomPost.size(); position++) {
            View tempView = li.inflate(R.layout.linearlayout_post, null);


            TextView textViewItemListViewPostIDPostNewsFeed = (TextView) tempView.findViewById(R.id.textViewItemListViewPostIDPostNewsFeed);
            TextView textViewItemListViewNamePostNewsFeed = (TextView) tempView.findViewById(R.id.textViewItemListViewFullNamePostNewsFeed);
            TextView textViewItemListViewPostNewsFeed = (TextView) tempView.findViewById(R.id.textViewItemListViewPostNewsFeed);
            TextView textViewItemListViewTimePostNewsFeed = (TextView) tempView.findViewById(R.id.textViewItemListViewTimePostNewsFeed);


            textViewItemListViewPostIDPostNewsFeed.setText(bottomPost.get(position).get(Constant.TAG_POST_ID));
            textViewItemListViewNamePostNewsFeed.setText(bottomPost.get(position).get(Constant.TAG_USER_FIRST_NAME + " " + Constant.TAG_USER_LAST_NAME));// TODO: 9/4/2016
            textViewItemListViewPostNewsFeed.setText(bottomPost.get(position).get(Constant.TAG_POST));
            textViewItemListViewTimePostNewsFeed.setText(toDuration(System.currentTimeMillis() - Long.parseLong(bottomPost.get(position).get(Constant.TAG_TIME))));
            linearLayoutPost.addView(tempView, linearLayoutPost.getChildCount());
        }
    }

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

    private void showAlert(final String message) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(message).setTitle("Response from Servers")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // do nothing
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

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
        new RefreshFacings().execute(userid, offset, bucket);

    }
}
