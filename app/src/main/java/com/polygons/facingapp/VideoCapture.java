package com.polygons.facingapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
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

//public class VideoCapture {
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
    Camera camera;


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
        start = (Button) view.findViewById(R.id.start);

        frameLayoutVideoPreview = (FrameLayout) view.findViewById(R.id.frameLayoutVideoPreview);
        cameraView = (SurfaceView) view.findViewById(R.id.CameraView);
//      play=(ImageView) view.findViewById(R.id.play);
        facingvideo = (VideoView) view.findViewById(R.id.facingvideo);
        buttonCancelVideo = (Button) view.findViewById(R.id.buttonCancelVideo);
        buttonPostVideo = (Button) view.findViewById(R.id.buttonPostVideo);
        linearLayoutBottoHider = (LinearLayout) view.findViewById(R.id.linearLayoutBottoHider);
        linearLayoutVideoBottoHider = (LinearLayout) view.findViewById(R.id.linearLayoutVideoBottoHider);
        frameLayoutCameraPreview = (FrameLayout) view.findViewById(R.id.frameLayoutCameraPreview);

        frameLayoutCameraPreview.setVisibility(View.VISIBLE);
        frameLayoutVideoPreview.setVisibility(View.GONE);


        cameraView.getViewTreeObserver();
        linearLayoutBottoHider.setLayoutParams(new LinearLayout.LayoutParams(getScreenWidth(), getScreenWidth()));
        linearLayoutVideoBottoHider.setLayoutParams(new LinearLayout.LayoutParams(getScreenWidth(), getScreenWidth()));

        buttonPostVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.isInternetPresent) {

                    Login.arrayListAsyncs.add(new PostThisVideo());
                    Login.arrayListAsyncs.get(Login.arrayListAsyncs.size() - 1).execute(new File(Constant.TAG_VIDEO_PATH), userid);
                }
            }
        });

        buttonCancelVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(MainActivity.videoCapture);
                // MainActivity.videoCapture= new VideoCapture();
                MainActivity.viewPagerAdapter.notifyDataSetChanged();
                ft.attach(MainActivity.videoCapture).commit();

                //                initRecorder();
//                prepareRecorder();
                frameLayoutCameraPreview.setVisibility(View.VISIBLE);
                frameLayoutVideoPreview.setVisibility(View.GONE);

            }
        });
        linearLayoutVideoBottoHider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facingvideo.refreshDrawableState();
                linearLayoutVideoBottoHider.setBackgroundDrawable(null);
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
                    camera.stopPreview();
                    camera.release();
                    camera = null;

                    recording = true;
                    recorder.start();
                    start.setBackground(getResources().getDrawable(R.drawable.square_button));
                }
            }
        });


    }

    public void refreshCamera() {

        if (holder.getSurface() == null) {

            // preview surface does not exist

            return;

        }


        // stop preview before making changes

        try {

            camera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (Exception e) {

        }
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    private void initRecorder() {
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        recorder.setOrientationHint(90);
        CamcorderProfile cpHigh = CamcorderProfile
                .get(CamcorderProfile.QUALITY_HIGH);
        cpHigh.videoFrameHeight = 400;
        cpHigh.videoFrameWidth = 400;
        recorder.setProfile(cpHigh);
        recorder.setOutputFile(Constant.TAG_VIDEO_PATH);
        recorder.setMaxDuration(50000); // 50 seconds
        recorder.setMaxFileSize(50000000); // Approximately 50 megabytes

    }

    private void prepareRecorder() {
        recorder.setPreviewDisplay(holder.getSurface());
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                recorder.setPreviewDisplay(holder.getSurface());
            }
        }, 1000);

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
        try {
            // open the camera
            camera = Camera.open();
        } catch (RuntimeException e) {
            // check for exceptions
            System.err.println(e);
            return;
        }
        Camera.Parameters param;
        param = camera.getParameters();

        // modify parameter
        param.setPreviewSize(352, 288);
        camera.setParameters(param);
        try {
            // The Surface has been created, now tell the camera where to draw
            // the preview.
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (Exception e) {
            // check for exceptions
            System.err.println(e);
            return;
        }

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        refreshCamera();

        // prepareRecorder();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (recording) {
            recorder.stop();
            recording = false;
        }
        recorder.release();

    }

    private class PostThisVideo extends AsyncTask<Object, Object, Boolean> {
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


                Toast.makeText(getActivity(), "Post Successfully", Toast.LENGTH_SHORT).show();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(MainActivity.videoCapture).attach(MainActivity.videoCapture).commit();

                MainActivity.viewPager.setCurrentItem(0);
                frameLayoutCameraPreview.setVisibility(View.VISIBLE);
                frameLayoutVideoPreview.setVisibility(View.GONE);
            } else {
                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();

            }

        }
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        if (visible) {
            //Do your stuff here
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(MainActivity.videoCapture).attach(MainActivity.videoCapture).commit();
        } else {
            if (camera != null) {
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        }

        super.setMenuVisibility(visible);
    }
//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//        if (hidden) {
//            //do when hidden
//
//        } else {
//            //do when show
//            try {
//                // open the camera
//                camera = Camera.open();
//            } catch (RuntimeException e) {
//                // check for exceptions
//                System.err.println(e);
//                return;
//            }
//            Camera.Parameters param;
//            param = camera.getParameters();
//
//            // modify parameter
//            param.setPreviewSize(352, 288);
//            camera.setParameters(param);
//            try {
//                // The Surface has been created, now tell the camera where to draw
//                // the preview.
//                camera.setPreviewDisplay(holder);
//                camera.startPreview();
//            } catch (Exception e) {
//                // check for exceptions
//                System.err.println(e);
//                return;
//            }
//
//
//            refreshCamera();
//
//        }
//    }
}