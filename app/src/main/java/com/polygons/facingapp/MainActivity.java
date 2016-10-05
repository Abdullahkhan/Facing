package com.polygons.facingapp;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.polygons.facingapp.tools.Constant;
import com.polygons.facingapp.tools.ViewPagerAdapter;


public class MainActivity extends AppCompatActivity {
    Context context = this;
    String userid;
    Toolbar toolbar;
    TabLayout tabLayout;
    public static ViewPager viewPager;
    public static ViewPagerAdapter viewPagerAdapter;
    Button buttonFace;
    static boolean isInternetPresent = true;
    LinearLayout linearLayoutInternetCheckMainActivity;
    ValueAnimator internetCheckAnimator;

    public static Fragment videoCapture = new VideoCapture();

    SharedPreferences sp;
    int[] tabIcons = {R.drawable.home, R.drawable.notification, R.drawable.centericon, R.drawable.search, R.drawable.profile};
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sp = getSharedPreferences(Constant.TAG_USER, Activity.MODE_PRIVATE);
        userid = sp.getString(Constant.TAG_USERID, "0");

        toolbar = (Toolbar) findViewById(R.id.toolBar);
        buttonFace = (Button) findViewById(R.id.buttonFace);

        linearLayoutInternetCheckMainActivity = (LinearLayout) findViewById(R.id.linearLayoutInternetCheckMainActivity);

        setSupportActionBar(toolbar);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.adFragments(new NewsFeed(), "NewsFeed");
        viewPagerAdapter.adFragments(new Notifications(), "Notifications");
//      viewPagerAdapter.adFragments(new Notifications(), "Notifications");
        viewPagerAdapter.adFragments(videoCapture, "Camera");
        viewPagerAdapter.adFragments(new SearchUser(), "Search");

        Bundle bundle = new Bundle();
        bundle.putString(Constant.TAG_USERID, userid);
        Profile profile = new Profile();
        profile.setArguments(bundle);

        viewPagerAdapter.adFragments(profile, "Profile");
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(1);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();


        buttonFace.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //   startActivity(new Intent(context, PostText.class));
                startActivity(new Intent(context, VideoCapture.class));
            }
        });

        for (int i = 0; i < tabLayout.getChildCount(); i++) {
            if (i == 2) {
                // tabLayout.getChildAt(i).setPadding(5, 5, 5, 5);
            } else {
                tabLayout.getChildAt(i).setPadding(15, 15, 15, 15);
            }
        }


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        tabLayout.getTabAt(4).setIcon(tabIcons[4]);
    }

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            String status = com.polygons.facingapp.tools.CheckNetwork.getConnectivityStatusString(context);
            if (status.equals("WIFI") || status.equals("MOBILE")) {
                isInternetPresent = true;
            } else if (status.equals("No Connection")) {
                isInternetPresent = false;
            }

            showNetworkState();
        }
    };

    public void showNetworkState() {
        if (isInternetPresent) {
            internetCheckCollapse();
        } else {
            internetCheckExpand();

            cancelAllAsync();
        }
    }

    public void cancelAllAsync()

    {

        NewsFeed.swipeLayout.setRefreshing(false);

        for (int i = 0; i < Login.arrayListAsyncs.size(); i++) {
            try {
                if (Login.arrayListAsyncs.get(i) != null) {
                    Login.arrayListAsyncs.get(i).cancel(true);
                }
            } catch (Exception e) {
            }

        }
    }

    private void internetCheckCollapse() {

        int finalHeight = linearLayoutInternetCheckMainActivity.getHeight();

        ValueAnimator mAnimator = internetCheckSlideAnimator(finalHeight, 0);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                linearLayoutInternetCheckMainActivity.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        mAnimator.start();
    }

    private void internetCheckExpand() {
        //set Visible
        linearLayoutInternetCheckMainActivity.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        linearLayoutInternetCheckMainActivity.measure(widthSpec, heightSpec);

        internetCheckAnimator = internetCheckSlideAnimator(0, linearLayoutInternetCheckMainActivity.getMeasuredHeight());
        internetCheckAnimator.start();


    }


    private ValueAnimator internetCheckSlideAnimator(int start, int end) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = linearLayoutInternetCheckMainActivity.getLayoutParams();
                layoutParams.height = value;
                linearLayoutInternetCheckMainActivity.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.polygons.facingapp/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.polygons.facingapp/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
