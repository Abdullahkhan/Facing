package com.polygons.facingapp;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.polygons.facingapp.AndroidMultiPartEntity.ProgressListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

	void setAllXMLReferences()
	{
		buttonOKReply = (Button) findViewById(R.id.buttonOKReply);
		videoViewPreviewReply = (VideoView) findViewById(R.id.videoViewPreviewReply);
		progressBarReplyFacing = (ProgressBar) findViewById(R.id.progressBarReplyFacing);
		textViewPercentageReplyFacing = (TextView) findViewById(R.id.textViewPercentageReplyFacing);
	}

	void  setAllClickListner()
	{
		buttonOKReply.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				new UploadFacingToServer().execute(username, replyUsername,
						facingID);
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
				new UploadFacingToServer().execute(username, replyUsername,
						facingID);


				// new UploadVideoToServer().execute();
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
			AsyncTask<String, Integer, String> {
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
			progressBarReplyFacing.setProgress(0);
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			// Making progress bar visible
			progressBarReplyFacing.setVisibility(View.VISIBLE);

			// updating progress bar value
			progressBarReplyFacing.setProgress(progress[0]);

			// updating percentage value
			textViewPercentageReplyFacing.setText(String.valueOf(progress[0])
					+ "%");
		}

		@Override
		protected String doInBackground(String... params) {
			return uploadFile(params[0], params[1], params[2]);
		}

		@SuppressWarnings("deprecation")
		private String uploadFile(String username, String replyUsername,
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
				} else {
					responseString = "Error occurred! Http Status Code: "
							+ statusCode;
				}

			} catch (ClientProtocolException e) {
				responseString = e.toString();
			} catch (IOException e) {
				responseString = e.toString();
			}

			return responseString;

		}

		@Override
		protected void onPostExecute(String result) {
			// pDialog.dismiss();
			startActivity(new Intent(context,NewsFeed.class));
		//	showAlert(result);
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
