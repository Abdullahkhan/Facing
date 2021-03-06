package com.polygons.facingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.polygons.facingapp.tools.Constant;
import com.polygons.facingapp.tools.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Suggestions extends Activity {
    Context context = this;
    ListView listViewSuggestions;
    Button buttonSkipSuggestions;
    ArrayList<String> arrayListUsernameSuggestions;
    ArrayList<String> arrayListUserIdSuggestions;
    String userid;
    JSONParser jsonParser = new JSONParser();
    String suggestionsURL = Login.myURL + "suggestions";
    String followTheUserURL = Login.myURL + "follow_friend";
    private static final String TAG_STATUS = "status";


    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suggestions);
        setAllXMLReferences();
        setAllClickListner();
        sp = getSharedPreferences(Constant.TAG_USER, Activity.MODE_PRIVATE);
        userid = sp.getString(Constant.TAG_USERID, "0");
        if (MainActivity.isInternetPresent) {

            Login.arrayListAsyncs.add(new GetSuggestions());
            Login.arrayListAsyncs.get(Login.arrayListAsyncs.size() - 1).execute(userid);
        }
    }

    void setAllXMLReferences() {
        listViewSuggestions = (ListView) findViewById(R.id.listViewSuggestions);
        buttonSkipSuggestions = (Button) findViewById(R.id.buttonSkipSuggestions);
    }

    void setAllClickListner() {
        listViewSuggestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(context, Profile.class);
                intent.putExtra(Constant.TAG_USERID, arrayListUserIdSuggestions.get(i));
                startActivity(intent);
            }
        });


        buttonSkipSuggestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MainActivity.class));
            }
        });
    }

    class GetSuggestions extends AsyncTask<Object, Object, Boolean> {

        @Override
        protected Boolean doInBackground(Object... args) {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Constant.TAG_USERID, (String) args[0]);

            JSONObject json = jsonParser.makeHttpRequest(suggestionsURL, Constant.TAG_POST_METHOD, params);

            try {
                boolean status = json.getBoolean(TAG_STATUS);
                if (status) {

                    JSONArray jsonArray = json.getJSONArray(Constant.TAG_RESULT);

                    arrayListUsernameSuggestions = new ArrayList<String>();
                    arrayListUserIdSuggestions = new ArrayList<String>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        arrayListUsernameSuggestions.add(jsonObject.getString("Name"));
                        arrayListUserIdSuggestions.add(jsonObject.getString("_id"));
                    }
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);

            if (s) {
                listViewSuggestions.setAdapter(adapter);
            }
        }
    }

    class FollowTheUser extends AsyncTask<Object, Object, Boolean> {
        @Override
        protected Boolean doInBackground(Object... args) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Constant.TAG_USERID, (String) args[0]);
            params.put("friend_id", (String) args[1]);

            JSONObject json = jsonParser.makeHttpRequest(followTheUserURL, Constant.TAG_POST_METHOD, params);

            try {
                boolean status = json.getBoolean(TAG_STATUS);
                if (status) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                Toast.makeText(context, "User Followed ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private BaseAdapter adapter = new BaseAdapter() {
        TextView username;
        LinearLayout linearLayoutItemListViewSuggestions;
        Button buttonItemListViewFollowSuggestion;


        @Override
        public int getCount() {
            return arrayListUsernameSuggestions.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayListUsernameSuggestions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View retval = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_listview_suggestions, null);


            username = (TextView) retval.findViewById(R.id.textViewItemListViewNameSuggestion);
            buttonItemListViewFollowSuggestion = (Button) retval.findViewById(R.id.buttonItemListViewFollowSuggestion);
            linearLayoutItemListViewSuggestions = (LinearLayout) retval.findViewById(R.id.linearLayoutItemListViewSuggestions);
            username.setText(arrayListUsernameSuggestions.get(position));

            linearLayoutItemListViewSuggestions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, Profile.class);
                    intent.putExtra(Constant.TAG_USERID, arrayListUserIdSuggestions.get(position));
                    startActivity(intent);
                }
            });

            buttonItemListViewFollowSuggestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (MainActivity.isInternetPresent) {

                        Login.arrayListAsyncs.add(new FollowTheUser());
                        Login.arrayListAsyncs.get(Login.arrayListAsyncs.size() - 1).execute(userid, arrayListUserIdSuggestions.get(position));
                    }
                }
            });

            return retval;
        }
    };
}
