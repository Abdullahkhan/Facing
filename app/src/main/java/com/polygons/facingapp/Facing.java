package com.polygons.facingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.polygons.facingapp.AndroidMultiPartEntity.ProgressListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Facing extends Activity {
    public static final int MEDIA_TYPE_VIDEO = 2;
    private Uri fileUri; // file url to store video
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private String filePath = null;
    public Context context = this;
    private String selectedPath;
    private static final String TAG = Facing.class.getSimpleName();
    VideoView videoViewPreview;
    Button buttonOK;
    ProgressBar progressBar;
    TextView txtPercentage;
    private long totalSize = 0;
    String videoUploadURL = Login.myURL + "/facing/uploadit.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facing);
        setAllXMLReferences();
        setAllClickListner();
        recordVideo();


    }


    void setAllClickListner() {
        buttonOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Login.arrayListAsyncs.add(new UploadFacingToServer());
                Login.arrayListAsyncs.get(Login.arrayListAsyncs.size() - 1).execute();

                //  new UploadFacingToServer().execute();
            }
        });
    }

    void setAllXMLReferences() {

        videoViewPreview = (VideoView) findViewById(R.id.videoViewPreview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txtPercentage = (TextView) findViewById(R.id.textViewPercentage);
        buttonOK = (Button) findViewById(R.id.buttonOK);


    }


    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                filePath = fileUri.getPath();
                // Uri selectedImageUri = data.getData();
                selectedPath = filePath;
                // textView.setText(selectedPath);
//                 buttonOK.setVisibility(View.VISIBLE);
                previewMedia();

                if (MainActivity.isInternetPresent) {
                    Login.arrayListAsyncs.add((AsyncTask) new UploadFacingToServer());
                    Login.arrayListAsyncs.get(Login.arrayListAsyncs.size() - 1).execute();
                }
                //  new UploadFacingToServer().execute();

                // launchUploadActivity();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Failed to record video", Toast.LENGTH_SHORT).show();

        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void previewMedia() {

        videoViewPreview.setVisibility(View.VISIBLE);
        videoViewPreview.setVideoPath(filePath);
        // start playing
        videoViewPreview.start();
        // new UploadFacingToServer().execute();
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Facing");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create " + "Facing" + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ",
                new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor
                .getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
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
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Object... progress) {
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private Boolean uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(videoUploadURL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(filePath);

                // Adding file data to http body
                entity.addPart("uploadvideo", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                entity.addPart("username",
                        new StringBody(Login.user.getUsername()));
                // entity.addPart("email", new StringBody("abc@gmail.com"));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                    return true;
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
                Log.i("Success", "Success");
            } else {
                Log.i("failed", "failed");
            }
        }
    }


}
