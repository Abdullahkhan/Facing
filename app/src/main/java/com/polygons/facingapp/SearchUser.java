package com.polygons.facingapp;

import java.util.ArrayList;
import java.util.HashMap;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.polygons.facingapp.tools.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchUser extends Fragment {
    Context context = getContext();
    View view;
    String userid;

    private EditText editTextSearchField;
    private Button buttonSearch;
    private ListView listViewSearchedUsers;
    ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    ArrayList<ArrayList<String>> arrayListUser;
    String TAG_FIRSTNAME = "firstName";
    String TAG_USERNAME_TO_BE_FOLLOWED = "targetUsername";

    String searchUserURL = Login.myURL + "search";
    String followTheUserURL = Login.myURL + "follow_friend";
    String unFollowTheUserURL = Login.myURL + "unfollow";
    String isFollowTheUserURL = Login.myURL + "isfollow";

    SharedPreferences sp;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.searchuser, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAllXMLReferences();
        setAllClickListner();
        sp = this.getActivity().getSharedPreferences("user", Activity.MODE_PRIVATE);
        userid = sp.getString("userid", "0");
    }

    void setAllXMLReferences() {
        editTextSearchField = (EditText) view.findViewById(R.id.editTextSearchField);
        buttonSearch = (Button) view.findViewById(R.id.buttonSearch);
        listViewSearchedUsers = (ListView) view.findViewById(R.id.listViewSearchedUsers);
    }

    void setAllClickListner() {
        buttonSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new SearchUserPlease().execute(editTextSearchField.getText()
                        .toString(), userid);

//                listViewSearchedUsers
//                        .setOnItemClickListener(new OnItemClickListener() {
//
//                            @Override
//                            public void onItemClick(AdapterView<?> parent,
//                                                    View view, int position, long id) {
//                                TextView name = (TextView) view
//                                        .findViewById(R.id.name);
//                                String toBeFollowedUsername = name.getText()
//                                        .toString();
//                                new FollowTheUser().execute(
//                                        Login.user.getUsername(),
//                                        toBeFollowedUsername
//
//                                );
//                            }
//                        });
            }
        });
    }

    class SearchUserPlease extends AsyncTask<String, String, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait while find your friend");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected Boolean doInBackground(String... args) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Constant.TAG_SEARCH, args[0]);
            params.put(Constant.TAG_USERID, args[1]);
            JSONObject json = jsonParser.makeHttpRequest(searchUserURL, Constant.TAG_POST_METHOD,
                    params);
            try {
                Boolean success = json.getBoolean(Constant.TAG_STATUS);
                arrayListUser = new ArrayList<ArrayList<String>>();
                if (success) {
                    JSONArray firstNameJSONArray = json.getJSONArray("result'");
                    for (int i = 0; i < firstNameJSONArray.length(); i++) {
                        JSONObject c = firstNameJSONArray.getJSONObject(i);

                        ArrayList<String> eachUser = new ArrayList<String>();
                        eachUser.add(c.getString("_id"));
                        eachUser.add(c.getString("Name"));
                        eachUser.add(c.getString("Lname"));
                        arrayListUser.add(eachUser);


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


            listViewSearchedUsers.setAdapter(adapter);

        }

    }


    class FollowTheUser extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Constant.TAG_USERID, args[0]);
            params.put("friend_id", args[1]);

            JSONObject json = jsonParser.makeHttpRequest(followTheUserURL, Constant.TAG_POST_METHOD, params);

            try {
                boolean status = json.getBoolean(Constant.TAG_STATUS);
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
                Toast.makeText(getActivity(), "User Followed ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class UnFollowTheUser extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid", args[0]);
            params.put("friend_id", args[1]);

            JSONObject json = jsonParser.makeHttpRequest(unFollowTheUserURL, "POST", params);

            try {
                boolean status = json.getBoolean(Constant.TAG_STATUS);
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
                Toast.makeText(getActivity(), "User unfollowed ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class IsFollowed extends AsyncTask<String, String, Boolean> {
        Boolean isFollowed = false;

        @Override
        protected Boolean doInBackground(String... args) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Constant.TAG_USERID, args[0]);
            params.put("friend_id", args[1]);

            JSONObject json = jsonParser.makeHttpRequest(isFollowTheUserURL, Constant.TAG_POST_METHOD, params);
            try {
                boolean status = json.getBoolean(Constant.TAG_STATUS);
                if (status) {
                    Log.i("isFollow", json.toString());
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                isFollowed = true;
            }
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

    private BaseAdapter adapter = new BaseAdapter() {

        LinearLayout linearLayoutItemListViewSearch;
        TextView textViewItemListViewFirstNameSearch;
        TextView textViewItemListViewLastNameSearch;
        Button buttonItemListViewFollowSearch;
        Button buttonItemListViewUnFollowSearch;

        @Override
        public int getCount() {
            return arrayListUser.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayListUser.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View retval = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_listview_searchuser, null);
            linearLayoutItemListViewSearch = (LinearLayout) retval.findViewById(R.id.linearLayoutItemListViewSearch);
            textViewItemListViewFirstNameSearch = (TextView) retval.findViewById(R.id.textViewItemListViewFirstNameSearch);
            textViewItemListViewLastNameSearch = (TextView) retval.findViewById(R.id.textViewItemListViewLastNameSearch);
            buttonItemListViewFollowSearch = (Button) retval.findViewById(R.id.buttonItemListViewFollowSearch);
            buttonItemListViewUnFollowSearch = (Button) retval.findViewById(R.id.buttonItemListViewUnFollowSearch);


            textViewItemListViewFirstNameSearch.setText(arrayListUser.get(position).get(1));
            textViewItemListViewLastNameSearch.setText(arrayListUser.get(position).get(2));

            linearLayoutItemListViewSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), ProfileMain.class);
                    intent.putExtra(Constant.TAG_USERID, arrayListUser.get(position).get(0));
                    startActivity(intent);
                }
            });


            buttonItemListViewFollowSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new FollowTheUser().execute(userid, arrayListUser.get(position).get(0));

                }
            });
            buttonItemListViewUnFollowSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new UnFollowTheUser().execute(userid, arrayListUser.get(position).get(0));
                }
            });
//            IsFollowed isFollowed = new IsFollowed();
//
//            try {
//
//                isFollowed.execute(userid, arrayListUser.get(position).get(0));
//            } catch (Exception e) {
//            }
//            Log.i("isFollowoo", isFollowed.isFollowed + "");
//
//            if (isFollowed.isFollowed) {
//                buttonItemListViewUnFollowSearch.setVisibility(View.VISIBLE);
//                buttonItemListViewFollowSearch.setVisibility(View.GONE);
//
//
//            } else {
//                buttonItemListViewFollowSearch.setVisibility(View.VISIBLE);
//                buttonItemListViewUnFollowSearch.setVisibility(View.GONE);
//            }

            return retval;
        }
    };

}
