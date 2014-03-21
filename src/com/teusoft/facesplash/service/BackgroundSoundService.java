package com.teusoft.facesplash.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.teusoft.facesplash.R;

public class BackgroundSoundService extends Service {
	MediaPlayer player;
	private final IBinder mBinder = new LocalBinder();

	public IBinder onBind(Intent arg0) {

		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.e("BackgroundSoundService", "Oncreate");
	}

	public class LocalBinder extends Binder {
		public BackgroundSoundService getService() {
			return BackgroundSoundService.this;
		}
	}

	public void playSound(int soundNumber) {
		if (player != null && player.isPlaying()) {
			player.stop();
			player.release();
		}
		if (soundNumber == 1) {
			player = MediaPlayer.create(this, R.raw.sound1);
		} else {
			player = MediaPlayer.create(this, R.raw.sound2);
			player.setVolume(0.25f, 0.25f);
		}
		player.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
			}

		});
		player.setLooping(true);
		player.start();
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		// if (intent != null) {
		// if (intent.hasExtra(MainActivity.class.getSimpleName())) {
		// playSound(1);
		// } else if (intent.hasExtra(ControlActivity.class.getSimpleName())) {
		// playSound(2);
		// }
		//
		// }
		return 1;
	}

	public void onStart(Intent intent, int startId) {
		// TO DO
	}

	public IBinder onUnBind(Intent arg0) {
		// TO DO Auto-generated method
		return null;
	}

	public void onStop() {

	}

	public void onPause() {

	}

	@Override
	public void onDestroy() {
		if (player != null && player.isPlaying()) {
			player.stop();
			player.release();
		}
	}

	@Override
	public void onLowMemory() {

	}

	public int getRandomNumber() {
		return 100;
	}
}