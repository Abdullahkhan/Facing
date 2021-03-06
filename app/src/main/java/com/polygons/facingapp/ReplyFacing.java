package com.polygons.facingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class ReplyFacing extends Activity {

    private String username;
    private String replyUsername;
    private String facingID;
    Context context = this;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private Uri fileUri; // file url to store video
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final String TAG = ReplyFacing.class.getSimpleName();
    private String filePath = null;
    private String selectedPath;
    long totalSize = 0;

    String videoReplyUploadURL = Login.myURL + "/facing/reply_uploadit.php";
    ProgressBar progressBarReplyFacing;
    Button buttonOKReply;
    VideoView videoViewPreviewReply;
    TextView textViewPercentageReplyFacing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reply_facing);
        setAllXMLReferences();
        setAllClickListner();
        username = getIntent().getExtras().getString(PlayFacing.TAG_USERNAME);
        replyUsername = getIntent().getExtras().getString(
                PlayFacing.TAG_REPLY_USERNAME);
        facingID = getIntent().getExtras().getString(PlayFacing.TAG_FACING_ID);
        recordVideo();
    }

    void setAllXMLReferences() {
        buttonOKReply = (Button) findViewById(R.id.buttonOKReply);
        videoViewPreviewReply = (VideoView) findViewById(R.id.videoViewPreviewReply);
        progressBarReplyFacing = (ProgressBar) findViewById(R.id.progressBarReplyFacing);
        textViewPercentageReplyFacing = (TextView) findViewById(R.id.textViewPercentageReplyFacing);
    }

    void setAllClickListner() {
        buttonOKReply.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (MainActivity.isInternetPresent) {

                    Login.arrayListAsyncs.add(new UploadFacingToServer());
                    Login.arrayListAsyncs.get(Login.arrayListAsyncs.size() - 1).execute(username, replyUsername, facingID);
                }
            }
        });
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
                // buttonOKReply.setVisibility(View.VISIBLE);
                previewMedia();
                if (MainActivity.isInternetPresent) {

                    Login.arrayListAsyncs.add(new UploadFacingToServer());
                    Login.arrayListAsyncs.get(Login.arrayListAsyncs.size() - 1).execute(username, replyUsername, facingID);
                }

                // new UploadVideoToServer().execute();
                // launchUploadActivity();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(context,
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            Toast.makeText(context,
                    "Sorry! Failed to record video", Toast.LENGTH_SHORT).show();

        }
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

    private void previewMedia() {

        videoViewPreviewReply.setVisibility(View.VISIBLE);
        videoViewPreviewReply.setVideoPath(filePath);
        // start playing
        videoViewPreviewReply.start();
        // new UploadFacingToServer().execute();
    }

    private class UploadFacingToServer extends
            AsyncTask<Object, Object, Boolean> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBarReplyFacing.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Object... progress) {
            // Making progress bar visible
            progressBarReplyFacing.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBarReplyFacing.setProgress((int) progress[0]);

            // updating percentage value
            textViewPercentageReplyFacing.setText(String.valueOf(progress[0])
                    + "%");
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            return uploadFile((String) params[0], (String) params[1], (String) params[2]);
        }

        @SuppressWarnings("deprecation")
        private Boolean uploadFile(String username, String replyUsername,
                                   String facingID) {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(videoReplyUploadURL);

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
                entity.addPart("username", new StringBody(username));
                entity.addPart("replyUsername", new StringBody(replyUsername));
                entity.addPart("facingID", new StringBody(facingID));
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
            // pDialog.dismiss();
            if (result) {
                startActivity(new Intent(context, NewsFeed.class));
            } else {
                Log.i("Result","failed");
            }

            //	showAlert(result);
        }
    }


}
