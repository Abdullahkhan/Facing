package com.polygons.facingapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.polygons.facingapp.tools.Constant;

public class ProfileMain extends FragmentActivity {
    SharedPreferences sp;
    String userid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_main);

        sp = getSharedPreferences(Constant.TAG_USER, Activity.MODE_PRIVATE);
        userid = sp.getString(Constant.TAG_USERID, "0");

        FragmentTransaction ft = getSupportFragmentManager()
                .beginTransaction();
        Profile profile = new Profile();
        Bundle bundle = new Bundle();
        bundle.putString(Constant.TAG_USERID, getIntent().getStringExtra(Constant.TAG_POST_USERID));
        profile.setArguments(bundle);

        ft.replace(R.id.linearLayoutProfileContainer, profile,
                Profile.TAG);
        ft.addToBackStack(null);

        ft.commit();

    }
}