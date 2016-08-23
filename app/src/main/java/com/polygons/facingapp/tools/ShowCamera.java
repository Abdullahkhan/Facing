package com.polygons.facingapp.tools;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@SuppressWarnings("deprecation")
public class ShowCamera extends SurfaceView implements SurfaceHolder.Callback {
	private Camera camera;

	public ShowCamera(Context context) {
		super(context);

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			camera.setPreviewDisplay(holder);
			camera.startPreview();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

}
