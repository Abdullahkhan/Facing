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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SearchUser extends Activity {
	private EditText editTextSearchField;
	private Button buttonSearch;
	private ListView listViewSearchedUsers;
	ProgressDialog pDialog;
	Context context = this;
	JSONParser jsonparser = new JSONParser();
	JSONArray searchUserJSONArray = null;

	ArrayList<HashMap<String, String>> userArrayList;
	String TAG_SUCCESS = "success";
	String TAG_MESSAGE = "message";
	String TAG_USERNAME = "username";
	String TAG_FIRSTNAME = "firstName";
	String TAG_USERNAME_TO_BE_FOLLOWED = "targetUsername";

	String searchUserURL = Login.myURL + "/facing/find_user.php";
	String FollowTheUserURL = Login.myURL + "/facing/friend_the_user.php";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchuser);
		editTextSearchField = (EditText) findViewById(R.id.editTextSearchField);
		buttonSearch = (Button) findViewById(R.id.buttonSearch);
		listViewSearchedUsers = (ListView) findViewById(R.id.listViewSearchedUsers);
		buttonSearch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new SearchUserPlease().execute(editTextSearchField.getText()
						.toString());
				listViewSearchedUsers
						.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								TextView name = (TextView) view
										.findViewById(R.id.name);
								String toBeFollowedUsername = name.getText()
										.toString();
								new FollowTheUser().execute(
										Login.user.getUsername(),
										toBeFollowedUsername

								);
							}
						});
			}
		});

	}

	class SearchUserPlease extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(context);
			pDialog.setMessage("Please wait while find your friend");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();

		}

		@Override
		protected String doInBackground(String... args) {
			// String name = inputFindNameOfFriendEditText.getText().toString();
			//
			// List<NameValuePair> params = new ArrayList<NameValuePair>();
			// params.add(new BasicNameValuePair("name", name));
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("name", args[0]);
			JSONObject json = jsonparser.makeHttpRequest(searchUserURL, "POST",
					params);

			try {
				int success = json.getInt(TAG_SUCCESS);
				userArrayList = new ArrayList<HashMap<String, String>>();
				if (success == 1) {
					searchUserJSONArray = json.getJSONArray("user");

					for (int i = 0; i < searchUserJSONArray.length(); i++) {
						JSONObject c = searchUserJSONArray.getJSONObject(i);

						String FirstName = c.getString(TAG_FIRSTNAME);
						String username = c.getString(TAG_USERNAME);

						HashMap<String, String> map = new HashMap<String, String>();

						map.put(TAG_FIRSTNAME, FirstName);
						map.put(TAG_USERNAME, username);

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

			runOnUiThread(new Runnable() {
				public void run() {
					ListAdapter adapter = new SimpleAdapter(context,
							userArrayList, R.layout.item_listview_searchuser,
							new String[] { "firstName", "username" },
							new int[] { R.id.pid, R.id.name });

					listViewSearchedUsers.setAdapter(adapter);

				}
			});

		}

	}

	class FollowTheUser extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(context);
			pDialog.setMessage("Please wait while we send friend request");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();

		}

		@Override
		protected String doInBackground(String... args) {

			// List<NameValuePair> params = new ArrayList<NameValuePair>();
			// params.add(new BasicNameValuePair("currentUsername", Login.user
			// .getUsername()));
			// params.add(new BasicNameValuePair("usernameToBeFollowed",
			// usernameToBeFollowed));
			HashMap<String, String> params = new HashMap<String, String>();
			params.put(TAG_USERNAME, args[0]);
			params.put(TAG_USERNAME_TO_BE_FOLLOWED, args[1]);
			JSONObject json = jsonparser.makeHttpRequest(FollowTheUserURL,
					"POST", params);
			try {
				Log.i("friend", json.toString());
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					((Activity) context).runOnUiThread(new Runnable() {
						  public void run() {
							  showAlert("User has been followed");						  }
						});
					
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();

		}

	}

	private void showAlert(String message) {
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
}
