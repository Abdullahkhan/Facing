package com.polygons.facingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageCaptureCamera extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mCameraPreview;
    Context context = this;
    String imageUploadURL = Login.myURL + "set_profilepicture";
    ProgressBar progressBar;
    private long totalSize = 0;
    String userid;
    SharedPreferences sp;
    LinearLayout linearLayoutBottoHider;
    // ImageView clickedimage;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_capture_camera);
        mCamera = getCameraInstance();
        mCameraPreview = new CameraPreview(this, mCamera);
        mCameraPreview.setMinimumHeight(mCameraPreview.getWidth());
        //   clickedimage=(ImageView)findViewById(R.id.imgview);

        sp = getSharedPreferences(Constant.TAG_USER, Activity.MODE_PRIVATE);
        userid = sp.getString(Constant.TAG_USERID, "0");

        final FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        linearLayoutBottoHider = (LinearLayout) findViewById(R.id.linearLayoutBottoHider);


        final ViewGroup.LayoutParams params = (android.view.ViewGroup.LayoutParams) linearLayoutBottoHider
                .getLayoutParams();

        final View view = (View) findViewById(R.id.camera_preview);
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                preview.getViewTreeObserver();
                // int width = view.getMeasuredWidth();
                int height = view.getMeasuredHeight();
                params.height = view.getMeasuredWidth();

            }
        });
        preview.addView(mCameraPreview);

        //  Button captureButton = (Button) findViewById(R.id.button_capture);
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(null, null, mPicture);
            }
        });
    }

    /**
     * Helper method to access the camera returns null if it cannot get the
     * camera or does not exist
     *
     * @return
     */
    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            // cannot get camera or does not exist
        }
        return camera;
    }

    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {


                return;
            }
            try {

                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Log.i("Image", bitmap.getHeight() + " " + bitmap.getWidth());
                Bitmap profile = resizePicture(squareCropPicture(rotateBitmap(bitmap, 90)));

                FileOutputStream fos = new FileOutputStream(pictureFile);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                profile.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                fos.write(stream.toByteArray());
                fos.close();

                if (MainActivity.isInternetPresent) {
                    Login.arrayListAsyncs.add((AsyncTask) new UploadFacingToServer());
                    Login.arrayListAsyncs.get(Login.arrayListAsyncs.size() - 1).execute(pictureFile, userid);
                }
            //    new UploadFacingToServer().execute(pictureFile, userid);

                //            Intent in1 = new Intent(ImageCaptureCamera.this, MainActivity.class);
                //          in1.putExtra("image",data);
                //        startActivity(in1);
                //  ImageCaptureCamera.this.finish();
                // clickedimage.setImageBitmap(bitmap);

                mCamera.startPreview();
                //  i.putExtra("BitmapImage", bitmap);
                // startActivity(i);
                // setResult(Activity.RESULT_OK,i);
                //finish();

            } catch (FileNotFoundException e) {

            } catch (IOException e) {
            }
        }
    };

    public static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
      //  mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + "profilepic" + ".jpg");

        return mediaFile;
    }

    private Bitmap squareCropPicture(Bitmap bitmap) {

        Bitmap bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getWidth());

        return bitmap2;
    }

    public static Bitmap resizePicture(Bitmap bitmap) {
        return Bitmap.createScaledBitmap(bitmap, 300, 300, false);
    }

    private Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private class UploadFacingToServer extends AsyncTask<Object, Object, Boolean> {
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
            HttpPost httppost = new HttpPost(imageUploadURL);

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
                entity.addPart(Constant.TAG_PICTURE, new FileBody((File) arg[0]));

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

//               showAlert("Successfullly Uploaded Picture");
                finish();
            } else {
                showAlert("Failed");
            }
            // pDialog.dismiss();

        }
    }

    private void showAlert(final String message) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(message)
                        .setTitle("Response from Servers")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        // do nothing
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
}
