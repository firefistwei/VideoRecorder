package com.fatowl.videorecorder;

import java.io.IOException;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

/*
* Author: Le Ba Vui
*/
public class MainActivity extends Activity implements OnClickListener,
		SurfaceHolder.Callback {
	MediaRecorder recorder;

	SurfaceHolder holder;
	SurfaceView surfaceView;

	Button record, preview;
	ProgressBar progressBar;

	Camera camera;

	boolean recording = false;
	boolean previewing = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		recorder = new MediaRecorder();
		// initRecorder();
		setContentView(R.layout.activity_main);

		surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		holder = surfaceView.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		record = (Button) findViewById(R.id.buttonRecord);
		record.setOnClickListener(this);
		preview = (Button) findViewById(R.id.buttonPreview);
		preview.setOnClickListener(this);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressBar.setVisibility(ProgressBar.INVISIBLE);

		// surfaceView.setClickable(true);
		// surfaceView.setOnClickListener(this);

	}

	private void initRecorder() {
		recorder = new MediaRecorder();
		
		recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

		CamcorderProfile cpHigh = CamcorderProfile
				.get(CamcorderProfile.QUALITY_HIGH);
		recorder.setProfile(cpHigh);

		recorder.setOutputFile("/sdcard/videocapture_example.mp4");
		recorder.setMaxDuration(60000); // 60 seconds
		recorder.setMaxFileSize(100000000); // Approximately 100 megabytes
	}

	private void prepareRecorder() {
		recorder.setPreviewDisplay(holder.getSurface());

		try {
			recorder.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			finish();
		} catch (IOException e) {
			e.printStackTrace();
			finish();
		}
	}

	public void onClick(View v) {
		if (v.getId() == R.id.buttonRecord) {
			try {
				if (recording) {
					recorder.stop();
					recorder.release();
					recording = false;
					progressBar.setVisibility(ProgressBar.INVISIBLE);
					record.setText("Record");
				} else {
					recording = true;
					camera.stopPreview();
					camera.release();
					previewing = false;
					initRecorder();
					prepareRecorder();
					recorder.start();
					progressBar.setVisibility(ProgressBar.VISIBLE);
					record.setText("Stop");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (v.getId() == R.id.buttonPreview) {
			if (previewing) {
				camera.stopPreview();
				camera.release();
			}
			Intent intent = new Intent(MainActivity.this,
					PlaybackActivity.class);
			startActivity(intent);
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Camera.Parameters p = camera.getParameters();

		p.setPreviewSize(width, height);
		camera.setParameters(p);
		try {
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		camera.startPreview();
		previewing = true;
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		try {
			if (recording) {
				recorder.stop();
				recording = false;
			}
			recorder.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
