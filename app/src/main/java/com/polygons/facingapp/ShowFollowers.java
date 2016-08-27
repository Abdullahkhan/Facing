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

public class ShowFollowers extends Activity {
	Button buttonRefreshShowFollowers;
	ListView listViewShowFollowers;
	Context context = this;
	JSONParser jsonparser = new JSONParser();
	JSONArray findFollowers = null;

	public static ArrayList<HashMap<String, String>> userArrayList;

	String showFollowersURL = Login.myURL + "/facing/show_followers.php";

	String TAG_USERNAME = "username";
	String TAG_SUCCESS = "success";
	String TAG_MESSAGE = "message";
	ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_followers);
		setAllXMLReferences();
		setAllClickListner();
	}

	void setAllXMLReferences()
	{
		buttonRefreshShowFollowers = (Button) findViewById(R.id.buttonRefreshShowFollowers);
		listViewShowFollowers = (ListView) findViewById(R.id.listViewShowFollowers);
	}

	void setAllClickListner()
	{
		buttonRefreshShowFollowers
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						new AllFollowers().execute(Login.user.getUsername());
					}
				});
	}
	class AllFollowers extends AsyncTask<String, String, String> {
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
		protected String doInBackground(String... args) {

			HashMap<String, String> params = new HashMap<String, String>();
			params.put(TAG_USERNAME, args[0]);
			JSONObject json = jsonparser.makeHttpRequest(showFollowersURL,
					"POST", params);

			try {
				userArrayList = new ArrayList<HashMap<String, String>>();

				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					findFollowers = json.getJSONArray("followers");
					for (int i = 0; i < findFollowers.length(); i++) {
						JSONObject c = findFollowers.getJSONObject(i);

						// String FirstName = c.getString("FirstName");
						String follower = c.getString("followers");

						HashMap<String, String> map = new HashMap<String, String>();
						// Log.d("Noti", username);
						map.put("FirstName", "FirstName");
						map.put("follower", follower);

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
//							new String[] { "FirstName", "follower" },
//							new int[] { R.id.pid, R.id.name });
//
//					listViewShowFollowers.setAdapter(adapter);
//
//				}
//			});

		}
	}

}
