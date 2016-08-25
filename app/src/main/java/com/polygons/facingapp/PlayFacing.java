package com.polygons.facingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.ArrayList;

public class PlayFacing extends Activity {
    VideoView videoViewFacing;
    Button buttonReply;
    int position;
    public int i = 0;
    Context context = this;
    public static String TAG_USERNAME = "username";
    public static String TAG_REPLY_USERNAME = "replyUsername";
    public static String TAG_FACING_ID = "facingID";
    private static ArrayList<ArrayList<String>> clonedfacingsUrlArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playfacing);
        setAllXMLReferences();
        setAllClickListner();
        clonedfacingsUrlArrayList = new ArrayList<ArrayList<String>>();
        for (int i = 0; i < NewsFeed.facingsUrlArrayList.size(); i++) {
            ArrayList<String> al = new ArrayList<String>();
            for (int j = 0; j < NewsFeed.facingsUrlArrayList.get(i).size(); j++) {
                al.add(NewsFeed.facingsUrlArrayList.get(i).get(j));


            }
            al.remove(0);
            clonedfacingsUrlArrayList.add(al);

        }
        position = getIntent().getExtras().getInt(NewsFeed.TAG_POSITION);
        startTheVideo();
    }

    void setAllXMLReferences()
    {
        videoViewFacing = (VideoView) findViewById(R.id.videoViewFacing);
        buttonReply = (Button) findViewById(R.id.buttonReply);
    }

    void  setAllClickListner()
    {
        buttonReply.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String replyUsernameTemp = clonedfacingsUrlArrayList.get(
                        position).get(0);
                String[] parts = replyUsernameTemp.split("_");
                String part1 = parts[0];
                String replyUsername = parts[1].substring(0,
                        parts[1].length() - 4);
                System.out.println(replyUsername);

                String[] facingIDtemp = part1.split("/");
                String facingID = facingIDtemp[facingIDtemp.length - 1];
                System.out.println(facingID);
                //
                Intent intent = new Intent(context, ReplyFacing.class);
                intent.putExtra(TAG_USERNAME, Login.user.getUsername());
                intent.putExtra(TAG_REPLY_USERNAME, replyUsername);
                intent.putExtra(TAG_FACING_ID, facingID);
                startActivity(intent);
            }
        });
    }
    public void startTheVideo() {
        Log.d("set vid", clonedfacingsUrlArrayList.get(position).toString());
        System.out.println(clonedfacingsUrlArrayList.get(position)
                .toString());
        videoViewFacing.setVideoPath(clonedfacingsUrlArrayList.get(position)
                .get(i));
        videoViewFacing.setMediaController(new MediaController(this));
        videoViewFacing.start();
        videoViewFacing
                .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.setDisplay(null);
                        mp.reset();
                        mp.setDisplay(videoViewFacing.getHolder());
                        i = (i + 1)
                                % clonedfacingsUrlArrayList.get(position)
                                .size();
                        videoViewFacing
                                .setVideoPath(clonedfacingsUrlArrayList.get(
                                        position).get(i));
                        videoViewFacing.start();
                    }
                });
    }


}
