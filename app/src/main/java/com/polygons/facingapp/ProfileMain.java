package com.polygons.facingapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class ProfileMain extends FragmentActivity {
    SharedPreferences sp;
    String userid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_main);

        sp = getSharedPreferences("user", Activity.MODE_PRIVATE);
        userid = sp.getString("userid", "0");

        FragmentTransaction ft = getSupportFragmentManager()
                .beginTransaction();
        Profile profile = new Profile();
        Bundle bundle = new Bundle();
        bundle.putString("userid", getIntent().getStringExtra("userid"));
        profile.setArguments(bundle);

        ft.replace(R.id.linearLayoutProfileContainer, profile,
                Profile.TAG);
        ft.addToBackStack(null);

        ft.commit();

    }
}