package com.polygons.facingapp;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
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
    Button buttonFace;
    Button buttonLogOut;
    Button buttonRefreshFacings;
    Button buttonSearchUser;
    Button buttonShowFollowers;
    Button buttonShowFollowing;
    ListView listViewNewsFeed;
    ProgressDialog pDialog;
    Context context = this;
    static ArrayList<ArrayList<String>> facingsUrlArrayList;
    ArrayList<String> singleFacingURLArraylist;
    public static int loader = R.drawable.ic_launcher;

    JSONArray allFacingsURL = null;
    JSONParser jsonparser = new JSONParser();
    JSONObject json;
    String TAG_SUCCESS = "success";
    String TAG_MESSAGE = "message";
    public static String TAG_POSITION = "position";
    String refreshFacings = Login.myURL + "/facing/news_feed.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newsfeed);
        setAllXMLReferences();
        setAllButtonOnClickListeners();
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
        buttonLogOut = (Button) findViewById(R.id.buttonLogOut);
        buttonRefreshFacings = (Button) findViewById(R.id.buttonRefreshFacings);
        buttonSearchUser = (Button) findViewById(R.id.buttonSearchUser);
        buttonShowFollowers = (Button) findViewById(R.id.buttonShowFollowers);
        buttonShowFollowing = (Button) findViewById(R.id.buttonShowFollowing);
        listViewNewsFeed = (ListView) findViewById(R.id.listViewNewsFeed);
    }

    private void setAllButtonOnClickListeners() {
        buttonFace.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Facing.class));
            }
        });
        buttonRefreshFacings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new RefreshFacings().execute(Login.user.getUsername());

            }
        });
        buttonSearchUser.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SearchUser.class));
            }
        });
        buttonShowFollowers.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ShowFollowers.class));
            }
        });
        buttonShowFollowing.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ShowFollowing.class));
            }
        });

    }

    class RefreshFacings extends AsyncTask<String, String, String> {
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
        protected String doInBackground(String... args) {

            // List<NameValuePair> params = new ArrayList<NameValuePair>();
            // params.add(new BasicNameValuePair("username", Username));
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("username", args[0]);
            json = jsonparser.makeHttpRequest(refreshFacings, "POST", params);
            facingsUrlArrayList = new ArrayList<ArrayList<String>>();

            try {
                int success = json.getInt(TAG_SUCCESS);

                switch (success) {
                    case 0:

                        showAlert(json.getString(TAG_MESSAGE));
                        break;
                    case 1:
                        allFacingsURL = json.getJSONArray("all_news_feed");
                        // showAlert("HELLO");

                        for (int i = 0; i < allFacingsURL.length(); i++) {
                            JSONObject c = allFacingsURL.getJSONObject(i);
                            JSONArray allFacingURLJSONArray = c
                                    .getJSONArray("entry");

                            for (int j = 0; j < allFacingURLJSONArray.length(); j++) {
                                JSONArray b = allFacingURLJSONArray.getJSONArray(j);
                                singleFacingURLArraylist = new ArrayList<String>();
                                for (int k = 0; k < b.length(); k++) {

                                    JSONObject singleFacingURL = b.getJSONObject(k);
                                    String facing = singleFacingURL
                                            .getString("facing");
                                    singleFacingURLArraylist.add(facing);
                                    System.out.println(facing);
                                }
                                facingsUrlArrayList.add(singleFacingURLArraylist);
                            }
                        }

                        break;
                    default:
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            pDialog.dismiss();
            // startTheVideo();
            // sample.setText(facingsUrlArrayList.toString());
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    listViewNewsFeed.setAdapter(adapter);
                    // showAlert(allFacingsURL.toString());
                    // Adapter adapter = new Adapter(context,
                    // R.layout.abc_list_menu_item_layout, facingsUrlArrayList);
                    // // adapter.notifyDataSetChanged();
                    // setListAdapter(adapter);
                }
            });

            //
            // ArrayAdapter<ArrayList<String>> listAdapter = new
            // ArrayAdapter<ArrayList<String>>(
            // Profile.this, R.id.videoview_list,
            // facingsUrlArrayList);
            // lv.setOnItemClickListener(new OnItemClickListener() {
            // @Override
            // public void onItemClick(AdapterView<?> arg0, View arg1,
            // int arg2, long arg3) {
            //
            // }
            // });
            // runOnUiThread(new Runnable() {
            // public void run() {
            // ListAdapter adapter = new SimpleAdapter(Profile.this,
            // Followers.userArrayList, R.layout.usernewsfeed,
            // new String[] { "FirstName", "follower" },
            // new int[] { R.id.facingsVideoView });
            //
            // setListAdapter(adapter);
            //
            // }
            // });

        }
    }

    private BaseAdapter adapter = new BaseAdapter() {
        ImageView imageView;
        TextView username;
        Bitmap bmImg;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View retval = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_listview_newsfeed, null);


            imageView = (ImageView) retval.findViewById(R.id.imageButtonItemListViewNewsFeed);
            username = (TextView) retval.findViewById(R.id.textViewItemListViewNewsFeedUsername);

            ImageLoader imgLoader = new ImageLoader(getApplicationContext());
            int loader = R.drawable.ic_launcher;
            // whenever you want to load an image from url
            // call DisplayImage function
            // url - image url to load
            // loader - loader image, will be displayed before getting image
            // image - ImageView
            imgLoader.DisplayImage(facingsUrlArrayList.get(position).get(0), loader, imageView);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PlayFacing.class);
                    intent.putExtra(TAG_POSITION, position);
                    startActivity(intent);

                }
            });
            username.setText(facingsUrlArrayList.get(position).get(0).split("_")[1].substring(0,facingsUrlArrayList.get(position).get(0).split("_")[1].length()-4));
//			TextView title=(TextView) retval.findViewById(R.id.title);
//			title.setText(facingsUrlArrayList.get(position).get(0));
//			VideoView videoView=(VideoView) retval.findViewById(R.id.videoViewItemListViewNewsFeed);
//			videoView.setVideoPath(facingsUrlArrayList.get(position).get(0));
//			//videoView.start();
//			 videoView.seekTo(1000);
//			videoView.setOnTouchListener(new View.OnTouchListener() {
//				@Override
//				public boolean onTouch(View v, MotionEvent event) {
//					Intent intent = new Intent(context, PlayFacing.class);
//					intent.putExtra(TAG_POSITION, position);
//					startActivity(intent);
//					return false;
//				}
//			});
//			videoView.start();
//			final ImageButton imageButton= (ImageButton) retval.findViewById(R.id.imageButtonItemListViewNewsFeed);
//			Bitmap thumbnail= ThumbnailUtils.createVideoThumbnail(facingsUrlArrayList.get(position).get(0).toString(), MediaStore.Images.Thumbnails.MINI_KIND);

//			imageButton.setImageBitmap(thumbnail);

//			BitmapDrawable bitmapDrawable =new BitmapDrawable(thumbnail);
//			videoView.setBackgroundDrawable(bitmapDrawable);
//			imageButton.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					imageButton.setVisibility(View.GONE);
//				}
//			});

            return retval;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return facingsUrlArrayList.get(position).get(0);
        }

        @Override
        public int getCount() {
            return facingsUrlArrayList.size();
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

    @Override
    protected void onResume() {
        super.onResume();
//        new RefreshFacings().execute(Login.user.getUsername());

    }
}
