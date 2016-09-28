package com.polygons.facingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.polygons.facingapp.tools.Constant;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;


public class VideoCapture extends android.support.v4.app.Fragment implements SurfaceHolder.Callback {
    MediaRecorder recorder;
    SurfaceHolder holder;
    boolean recording = false;
    Button start;
    ImageView play;
    FrameLayout frameLayoutVideoPreview;
    VideoView facingvideo;
    Button buttonPostVideo;
    Button buttonCancelVideo;
    SurfaceView cameraView;
    LinearLayout linearLayoutBottoHider;
    LinearLayout linearLayoutVideoBottoHider;
    View view;
    String userid;
    SharedPreferences sp;
    private long totalSize = 0;
    String postVideoURL = Login.myURL + "post";
    FrameLayout frameLayoutCameraPreview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_video_capture, container, false);
        return view;
    }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            sp = this.getActivity().getSharedPreferences(Constant.TAG_USER, Activity.MODE_PRIVATE);
            userid = sp.getString(Constant.TAG_USERID, "0");
        start=(Button)view.findViewById(R.id.start);

            frameLayoutVideoPreview =(FrameLayout) view.findViewById(R.id.frameLayoutVideoPreview);
       cameraView = (SurfaceView) view.findViewById(R.id.CameraView);
//        play=(ImageView) view.findViewById(R.id.play);
        facingvideo=(VideoView)view.findViewById(R.id.facingvideo);
        buttonCancelVideo=(Button)view.findViewById(R.id.buttonCancelVideo);
        buttonPostVideo=(Button)view.findViewById(R.id.buttonPostVideo);
        linearLayoutBottoHider=(LinearLayout)view.findViewById(R.id.linearLayoutBottoHider);
        linearLayoutVideoBottoHider=(LinearLayout)view.findViewById(R.id.linearLayoutVideoBottoHider);
            frameLayoutCameraPreview=(FrameLayout)view.findViewById(R.id.frameLayoutCameraPreview);

            final ViewGroup.LayoutParams params = (android.view.ViewGroup.LayoutParams) linearLayoutBottoHider
                    .getLayoutParams();

            ViewTreeObserver vto = view.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    cameraView.getViewTreeObserver();
                    // int width = view.getMeasuredWidth();
                    //  int height = view.getMeasuredHeight();
                    params.height = cameraView.getMeasuredWidth();

                }
            });
            linearLayoutBottoHider.setLayoutParams(params);

            final ViewGroup.LayoutParams paramsVideo = (android.view.ViewGroup.LayoutParams) linearLayoutVideoBottoHider
                    .getLayoutParams();

            ViewTreeObserver vto2 = view.getViewTreeObserver();
            vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    cameraView.getViewTreeObserver();
                    // int width = view.getMeasuredWidth();
                    //  int height = view.getMeasuredHeight();
                    paramsVideo.height = cameraView.getMeasuredWidth();

                }
            });
            linearLayoutVideoBottoHider.setLayoutParams(paramsVideo);

        buttonPostVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new PostThisVideo().execute(new File(Constant.TAG_VIDEO_PATH),userid);
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
            facingvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //facingvideo.refreshDrawableState();
                facingvideo.setBackgroundDrawable(null);
                facingvideo.setVideoPath(Constant.TAG_VIDEO_PATH);
                facingvideo.start();
            }
        });

        recorder = new MediaRecorder();

        holder = cameraView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

            initRecorder();
        // cameraView.setClickable(true);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recording) {
                    recorder.stop();

                    recording = false;

                    frameLayoutCameraPreview.setVisibility(View.GONE);
                    frameLayoutVideoPreview.setVisibility(View.VISIBLE);


                    // facingvideo.setVideoPath("/sdcard/videocapture_example.mp4");
                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(Constant.TAG_VIDEO_PATH,
                            MediaStore.Images.Thumbnails.MINI_KIND);
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(thumb);


                    facingvideo.setBackgroundDrawable(bitmapDrawable);
                    start.setBackground(getResources().getDrawable(R.drawable.round_button));

                    /* ;*/

                 //   Intent returnIntent = new Intent();
                   // returnIntent.putExtra("Path","/sdcard/videocapture_example.mp4");
                 //   setResult(Activity.RESULT_OK,returnIntent);
                 //   finish();

                } else {
                    recording = true;
                    recorder.start();
                    start.setBackground(getResources().getDrawable(R.drawable.square_button));
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
       // prepareRecorder();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (recording) {
            recorder.stop();
            recording = false;
        }
        recorder.release();

    }

    private class PostThisVideo extends AsyncTask<Object, Integer, Boolean> {
        // @Override
        // protected void onPreExecute() {
        // super.onPreExecute();
        // pDialog = new ProgressDialog(context);
        // pDialog.setMessage("Please wait while we upload your video...");
        // pDialog.setIndeterminate(false);
        // pDialog.setCancelable(true);
        // pDialog.show();
        // }
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            //      progressBar.setProgress(0);
            super.onPreExecute();
        }


        @Override
        protected Boolean doInBackground(Object... arg) {


            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(postVideoURL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });


                //    File sourceFile = new File(filePath);

                // Adding file data to http body
                entity.addPart(Constant.TAG_POST, new FileBody((File) arg[0]));

                // Extra parameters if you want to pass to server
                entity.addPart(Constant.TAG_USERID,
                        new StringBody(userid));
                // entity.addPart("email", new StringBody("abc@gmail.com"));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {

                    try {
                        JSONObject result = new JSONObject(EntityUtils.toString(r_entity));
                        if (result.getBoolean(Constant.TAG_STATUS)) {
                            return true;
                        }
                    } catch (Exception e) {

                    }
                    return false;
                    // Server response
                    //   responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return false;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {

                Toast.makeText(getActivity(),"Post Successfully",Toast.LENGTH_SHORT).show();
                FragmentTransaction ft=getFragmentManager().beginTransaction();
                ft.detach(MainActivity.videoCapture).attach(MainActivity.videoCapture).commit();

                MainActivity.viewPager.setCurrentItem(0);
            } else {
                Toast.makeText(getActivity(),"Failed",Toast.LENGTH_SHORT).show();

            }
            // pDialog.dismiss();

        }
    }

}