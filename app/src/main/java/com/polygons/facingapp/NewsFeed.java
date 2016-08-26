package com.polygons.facingapp;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.polygons.facingapp.tools.ImageLoader;

public class NewsFeed extends Activity {
    Context context = this;
    Button buttonFace;
    Button buttonLogOut;
    Button buttonRefreshFacings;
    Button buttonSearchUser;
    Button buttonShowFollowers;
    Button buttonShowFollowing;
    ListView listViewNewsFeed;
    String userid;
    ProgressDialog pDialog;
    static ArrayList<ArrayList<String>> facingsUrlArrayList;
    ArrayList<String> singleFacingURLArraylist;
    public static int loader = R.drawable.ic_launcher;

    JSONArray allFacingsURL = null;
    JSONParser jsonparser = new JSONParser();
    JSONObject json;
    String TAG_SUCCESS = "status";
    String TAG_MESSAGE = "message";
    public static String TAG_POSITION = "position";
    String refreshFacings = Login.myURL + "newsfeed";
    SharedPreferences sp;

    ArrayList<ArrayList<String>> post;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newsfeed);
        setAllXMLReferences();
        setAllButtonOnClickListeners();
        sp = getSharedPreferences("user", Activity.MODE_PRIVATE);
        userid = sp.getString("userid", "0");
        Log.i("userid", userid);
        //   new RefreshFacings().execute(userid, "0", "20");


        listViewNewsFeed.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(context, PlayFacing.class);
                intent.putExtra(TAG_POSITION, position);
                startActivity(intent);
            }
        });

    }

    private void setAllXMLReferences() {

        buttonFace = (Button) findViewById(R.id.buttonFace);
//      buttonSearchUser = (Button) findViewById(R.id.buttonSearchUser);
        listViewNewsFeed = (ListView) findViewById(R.id.listViewNewsFeed);
    }

    private void setAllButtonOnClickListeners() {
        buttonFace.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, PostText.class));
            }
        });
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
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Refreshing your Facings");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected Boolean doInBackground(String... args) {
            post = new ArrayList<ArrayList<String>>();
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid", args[0]);
            params.put("offset", args[1]);
            params.put("bucket", args[2]);
            json = jsonparser.makeHttpRequest(refreshFacings, "POST", params);
            facingsUrlArrayList = new ArrayList<ArrayList<String>>();
            JSONArray jsonArray;
            try {
                Boolean success = json.getBoolean(TAG_SUCCESS);
                Log.i("newsfeed", json.toString());
                if (success) {
                    jsonArray = json.getJSONArray("message");
                    Log.i("newsfeed", jsonArray.toString());

                    for (int i = 0; i < jsonArray.length(); i++) {
                        ArrayList<String> eachPost = new ArrayList<String>();
                        JSONObject postObject = jsonArray.getJSONObject(i);
                        eachPost.add(postObject.getString("_id"));
                        eachPost.add(postObject.getString("user_id"));
                        eachPost.add(postObject.getString("post"));
                        eachPost.add(postObject.getString("time"));
                        post.add(eachPost);
                        Log.i("newsfeed", post.toString());

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
            pDialog.dismiss();
            if (result) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        listViewNewsFeed.setAdapter(adapter);
                    }
                });
            }

        }
    }

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
            textViewItemListViewNamePostNewsFeed = (TextView) retval.findViewById(R.id.textViewItemListViewNamePostNewsFeed);
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
        runOnUiThread(new Runnable() {

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
    public static final List<String> timesString = Arrays.asList("year", "month", "day", "hour", "minute", "second");

    public static String toDuration(long duration) {

        StringBuffer res = new StringBuffer();
        for (int i = 0; i < NewsFeed.times.size(); i++) {
            Long current = NewsFeed.times.get(i);
            long temp = duration / current;
            if (temp > 0) {
                res.append(temp).append(" ").append(NewsFeed.timesString.get(i)).append(temp > 1 ? "s" : "").append(" ago");
                break;
            }
        }
        if ("".equals(res.toString()))
            return "0 second ago";
        else
            return res.toString();
    }


    @Override
    protected void onResume() {
        super.onResume();
        new RefreshFacings().execute(userid, "0", "20");

    }
}
