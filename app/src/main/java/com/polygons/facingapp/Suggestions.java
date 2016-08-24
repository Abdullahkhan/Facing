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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.polygons.facingapp.tools.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Suggestions extends Activity {
    Context context = this;
    ListView listViewSuggestions;
    ArrayList<String> arrayListUsernameSuggestions;
    ArrayList<String> arrayListUserIdSuggestions;
    String userid;
    JSONParser jsonParser = new JSONParser();
    String suggestionsURL = Login.myURL + "default_follows";


    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suggestions);
        listViewSuggestions = (ListView) findViewById(R.id.listViewSuggestions);

        sp = getSharedPreferences("user", Activity.MODE_PRIVATE);
        userid = sp.getString("userid", "0");

        new GetSuggestions().execute(userid);


    }

    class GetSuggestions extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid", args[0]);

//            JSONObject json = jsonParser.makeHttpRequest(suggestionsURL, "POST", params);
            try {

                // Log.i("Suggest", jsonParser.makeHttpRequest(suggestionsURL, "POST", params).toString());
                //   JSONArray jsonArray = new JSONArray(jsonParser.makeHttpRequest(suggestionsURL, "POST", params).toString());
                JSONArray jsonArray = new JSONArray("[\n" +
                        "  {\n" +
                        "    \"_id\": \"57bbfd7ca6d0cf030033d3ac\",\n" +
                        "    \"Name\": \"facing\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"_id\": \"57bbfdd6a6d0cf030033d3ae\",\n" +
                        "    \"Name\": \"abd7\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"_id\": \"57bc0152a6d0cf030033d3af\",\n" +
                        "    \"Name\": \"abd7\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"_id\": \"57bc0155a6d0cf030033d3b0\",\n" +
                        "    \"Name\": \"abd8\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"_id\": \"57bc2a297c0813030091be6c\",\n" +
                        "    \"Name\": \"zeeshan\"\n" +
                        "  }\n" +
                        "]");
                arrayListUsernameSuggestions = new ArrayList<String>();
                arrayListUserIdSuggestions = new ArrayList<String>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    arrayListUsernameSuggestions.add(jsonObject.getString("Name"));
                    arrayListUserIdSuggestions.add(jsonObject.getString("_id"));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            listViewSuggestions.setAdapter(adapter);

        }
    }

    ;

    private BaseAdapter adapter = new BaseAdapter() {
        TextView username;

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
        public View getView(int position, View convertView, ViewGroup parent) {

            View retval = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_listview_suggestions, null);


            username = (TextView) retval.findViewById(R.id.textViewItemListViewNameSuggestion);
            username.setText(arrayListUsernameSuggestions.get(position));

            username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, Profile.class));
                }
            });

            return retval;
        }
    };
}
