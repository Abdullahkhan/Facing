package com.polygons.facingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.polygons.facingapp.tools.Constant;

import java.io.IOException;


public class VideoCapture extends android.support.v4.app.Fragment implements SurfaceHolder.Callback {
    MediaRecorder recorder;
    SurfaceHolder holder;
    boolean recording = false;
    Button start;
    ImageView play;
    LinearLayout linearLayoutVideoCapture;
    VideoView facingvideo;
    Button buttonPostVideo;
    Button buttonCancelVideo;
    SurfaceView cameraView;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_video_capture, container, false);
        return view;
    }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


        start=(Button)view.findViewById(R.id.start);

        linearLayoutVideoCapture =(LinearLayout)view.findViewById(R.id.linearLayoutVideoCapture);
       cameraView = (SurfaceView) view.findViewById(R.id.CameraView);
        play=(ImageView) view.findViewById(R.id.play);
        facingvideo=(VideoView)view.findViewById(R.id.facingvideo);
        buttonCancelVideo=(Button)view.findViewById(R.id.buttonCancelVideo);
        buttonPostVideo=(Button)view.findViewById(R.id.buttonPostVideo);

        buttonPostVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        buttonCancelVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction ft=getFragmentManager().beginTransaction();
                ft.detach(MainActivity.videoCapture).attach(MainActivity.videoCapture).commit();

                //                initRecorder();
//                prepareRecorder();


            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //facingvideo.refreshDrawableState();
                facingvideo.setBackgroundDrawable(null);
                facingvideo.setVideoPath(Constant.TAG_VIDEO_PATH);
                facingvideo.start();
            }
        });

        recorder = new MediaRecorder();
        initRecorder();



        holder = cameraView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // cameraView.setClickable(true);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recording) {
                    recorder.stop();

                    recording = false;

                    cameraView.setVisibility(View.GONE);
                    linearLayoutVideoCapture.setVisibility(View.VISIBLE);


                    // facingvideo.setVideoPath("/sdcard/videocapture_example.mp4");
                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(Constant.TAG_VIDEO_PATH,
                            MediaStore.Images.Thumbnails.MINI_KIND);
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(thumb);


                    facingvideo.setBackgroundDrawable(bitmapDrawable);
                   /* ;*/

                 //   Intent returnIntent = new Intent();
                   // returnIntent.putExtra("Path","/sdcard/videocapture_example.mp4");
                 //   setResult(Activity.RESULT_OK,returnIntent);
                 //   finish();

                } else {
                    recording = true;
                    recorder.start();
                }
            }
        });

    }

    private void initRecorder() {
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        recorder.setOrientationHint(90);
        CamcorderProfile cpHigh = CamcorderProfile
                .get(CamcorderProfile.QUALITY_HIGH);
        recorder.setProfile(cpHigh);
        recorder.setOutputFile(Constant.TAG_VIDEO_PATH);
        recorder.setMaxDuration(50000); // 50 seconds
        recorder.setMaxFileSize(5000000); // Approximately 5 megabytes
    }

    private void prepareRecorder() {
        recorder.setPreviewDisplay(holder.getSurface());

        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    /* public void onClick(View v) {
         if (recording) {
             recorder.stop();
             recording = false;

             // Let's initRecorder so we can record again
             initRecorder();
             prepareRecorder();
         } else {
             recording = true;
             recorder.start();
         }
     }
 */
    public void surfaceCreated(SurfaceHolder holder) {
        prepareRecorder();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (recording) {
            recorder.stop();
            recording = false;
        }
        recorder.release();

    }
}