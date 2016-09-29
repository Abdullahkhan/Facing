package com.polygons.facingapp;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ShowFollowing extends Activity {
	Button buttonRefreshShowFollowing;
	ListView listViewShowFollowing;
	Context context = this;
	JSONParser jsonparser = new JSONParser();
	JSONArray findFollowing = null;

	public static ArrayList<HashMap<String, String>> userArrayList;

	String showFollowingURL = Login.myURL + "/facing/show_following.php";

	String TAG_USERNAME = "username";
	String TAG_SUCCESS = "success";
	String TAG_MESSAGE = "message";
	ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_following);
		setAllXMLReferences();
		setAllClickListner();
	}

	void setAllXMLReferences()
	{
		buttonRefreshShowFollowing = (Button) findViewById(R.id.buttonRefreshShowFollowing);
		listViewShowFollowing = (ListView) findViewById(R.id.listViewShowFollowing);
	}

	void setAllClickListner()
	{
		buttonRefreshShowFollowing
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						new AllFollowers().execute(Login.user.getUsername());
					}
				});
	}

	class AllFollowers extends AsyncTask<Object, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(context);
			pDialog.setMessage("Refreshing your notifications");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();

		}

		@Override
		protected String doInBackground(Object... args) {

			HashMap<String, String> params = new HashMap<String, String>();
			params.put(TAG_USERNAME, (String) args[0]);
			JSONObject json = jsonparser.makeHttpRequest(showFollowingURL,
					"POST", params);

			try {
				userArrayList = new ArrayList<HashMap<String, String>>();

				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					findFollowing = json.getJSONArray("following");
					for (int i = 0; i < findFollowing.length(); i++) {
						JSONObject c = findFollowing.getJSONObject(i);

						// String FirstName = c.getString("FirstName");
						String follower = c.getString("following");

						HashMap<String, String> map = new HashMap<String, String>();
						// Log.d("Noti", username);
						map.put("FirstName", "FirstName");
						map.put("following", follower);

						userArrayList.add(map);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();

//			runOnUiThread(new Runnable() {
//				public void run() {
//					ListAdapter adapter = new SimpleAdapter(
//							context,
//							userArrayList,
//							R.layout.item_listview_searchuser,// temporary item
//																// layout
//							new String[] { "FirstName", "following" },
//							new int[] { R.id.pid, R.id.name });
//
//					listViewShowFollowing.setAdapter(adapter);
//
//				}
//			});

		}
	}

}
