package com.fatowl.videorecorder;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class PlaybackActivity extends Activity implements OnClickListener,
		SurfaceHolder.Callback {
	MediaPlayer player;

	SurfaceHolder holder;
	SurfaceView surfaceView;

	Button play;

	boolean playing = false;
	boolean canPlay = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		setContentView(R.layout.activity_playback);

		play = (Button) findViewById(R.id.buttonPlay);
		play.setOnClickListener(this);

		surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		holder = surfaceView.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		player = new MediaPlayer();

		try {
			player.setDataSource("mnt/sdcard/videocapture_example.mp4");
		} catch (Exception e) {
			e.printStackTrace();
		}

		player.prepareAsync();

		player.setOnPreparedListener(new OnPreparedListener() {

			public void onPrepared(MediaPlayer mp) {
				canPlay = true;
			}
		});

		player.setOnCompletionListener(new OnCompletionListener() {

			public void onCompletion(MediaPlayer mp) {
				play.setText("Play");
				playing = false;
			}
		});
	}

	public void onClick(View v) {
		if (v.getId() == R.id.buttonPlay) {
			if (canPlay) {
				if (playing) {
					player.pause();
					play.setText("Play");
					playing = false;
				} else {
					player.setDisplay(holder);
					player.start();
					play.setText("Pause");
					playing = true;
				}
			}
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		try {
			if (playing) {
				player.stop();
				playing = false;
			}
			player.release();
			finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		if (playing) {
			player.stop();
			playing = false;
		}
		player.release();
	}
}
