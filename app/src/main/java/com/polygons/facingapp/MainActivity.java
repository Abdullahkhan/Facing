package com.polygons.facingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.polygons.facingapp.tools.Constant;
import com.polygons.facingapp.tools.ViewPagerAdapter;


public class MainActivity extends AppCompatActivity {
    Context context=this;
    String userid;
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    Button buttonFace;

    public static Fragment videoCapture=new VideoCapture();

    SharedPreferences sp;
    int[] tabIcons = {R.drawable.ic_home_white_48dp, R.drawable.ic_notifications_white_48dp, R.drawable.ic_search_white_48dp, R.drawable.ic_account_circle_white_48dp,R.drawable.ic_home_white_48dp};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        sp = getSharedPreferences(Constant.TAG_USER, Activity.MODE_PRIVATE);
        userid = sp.getString(Constant.TAG_USERID, "0");

        toolbar = (Toolbar) findViewById(R.id.toolBar);
        buttonFace = (Button) findViewById(R.id.buttonFace);

        setSupportActionBar(toolbar);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.adFragments(new NewsFeed(), "NewsFeed");
        viewPagerAdapter.adFragments(new Notifications(), "Notifications");
        viewPagerAdapter.adFragments(videoCapture, "Camera");
        viewPagerAdapter.adFragments(new SearchUser(), "Search");

        Bundle bundle = new Bundle();
        bundle.putString(Constant.TAG_USERID, userid);
        Profile profile = new Profile();
        profile.setArguments(bundle);

        viewPagerAdapter.adFragments(profile, "Profile");
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(4);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();


        buttonFace.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
             //   startActivity(new Intent(context, PostText.class));
                startActivity(new Intent(context, VideoCapture.class));
            }
        });


    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        tabLayout.getTabAt(4).setIcon(tabIcons[4]);
    }


}
