package com.teusoft.facesplash;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import com.teusoft.facesplash.activity.ControlActivity;
import com.teusoft.facesplash.shakelistener.AccelerometerManager;
import com.teusoft.facesplash.util.Utils;

public class SimulationView extends View implements SensorEventListener {
	private static final String TAG = "SimulationView";
	public int wankSplash = 16;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private Display mDisplay;

	private Bitmap mBitmap;
	private int BALL_SIZE;

	private float mXOrigin;
	private float mYOrigin;

	private float mHorizontalBound;
	private float mVerticalBound;

	private float mSensorX;
	private float mSensorY;
	private float mSensorZ;
	private long mSensorTimeStamp;
	private float mLastZ;
	private float deltaZ;
	private boolean isInitZ;

	private Particle mBall = new Particle();
	private Context context;
	float mTouchX;
	float mTouchY;
	private CustomViewObserver mCustomViewObserver;
	private boolean isUp;
	private int wankCount;
	private int wankNumber;
	private static int[] SOUND1 = { R.raw.flap1_1, R.raw.flap1_2,
			R.raw.flap1_3, R.raw.flap1_4, R.raw.flap1_5, R.raw.flap1_6,
			R.raw.flap1_7, R.raw.flap1_8, R.raw.flap1_9 };
	private static int[] SOUND2 = { R.raw.flap2_1, R.raw.flap2_2,
			R.raw.flap2_3, R.raw.flap2_4, R.raw.flap2_5, R.raw.flap2_6,
			R.raw.flap2_7, R.raw.flap2_8, R.raw.flap2_9 };
	private int screenWidth;
	private int screenHeight;
	private MediaPlayer mp;

	public enum ACTION_TYPE {
		UP, DOWN
	};

	private ChangeAimListener aimListener;
	private boolean isStopWank;

	public interface ChangeAimListener {
		// you can define any parameter as per your requirement
		public void onChangeAim(float delta);
	}

	public SimulationView(Context context) {
		super(context);
	}

	public SimulationView(Context context, ChangeAimListener aimListener) {
		super(context);
		this.aimListener = aimListener;
		WindowManager mWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		mDisplay = mWindowManager.getDefaultDisplay();

		mSensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		Bitmap ball = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_empty_transperant);
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics displaymetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(displaymetrics);
		screenWidth = displaymetrics.widthPixels;
		screenHeight = displaymetrics.heightPixels;
		BALL_SIZE = screenWidth;
		mBitmap = Bitmap.createScaledBitmap(ball, BALL_SIZE, BALL_SIZE, true);

	}

	// this is to set the observer
	public void setObserver(CustomViewObserver observer) {
		mCustomViewObserver = observer;
	}

	public void startSimulation(Context context) {
		this.context = context;
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_UI);
		// Check device supported Accelerometer senssor or not
		if (AccelerometerManager.isSupported(context)) {
			// Start Accelerometer Listening
			// AccelerometerManager.startListening(this);
		}
	}

	public void stopSimulation() {
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
			return;
		switch (mDisplay.getRotation()) {
		case Surface.ROTATION_0:
			mSensorX = event.values[0];
			mSensorY = event.values[1];
			break;
		case Surface.ROTATION_90:
			mSensorX = -event.values[1];
			mSensorY = event.values[0];
			break;
		case Surface.ROTATION_180:
			mSensorX = -event.values[0];
			mSensorY = -event.values[1];
			break;
		case Surface.ROTATION_270:
			mSensorX = event.values[1];
			mSensorY = -event.values[0];
			break;
		}
		mSensorZ = event.values[2];
		mSensorTimeStamp = event.timestamp;
		if (!isInitZ) {
			mLastZ = mSensorZ;
			isInitZ = true;
		}
		deltaZ = Math.abs(mLastZ - mSensorZ);

		if (deltaZ > 0.1f) {
			changeAimSize(deltaZ * 0.05f);
		}
		mLastZ = mSensorZ;

	}

	/**
	 * Change size of aim
	 */
	public void changeAimSize(float delta) {
		aimListener.onChangeAim(delta);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mXOrigin = w * 0.5f;
		mYOrigin = h * 0.5f;

		mHorizontalBound = (w - mBitmap.getWidth()) * 0.5f;
		mVerticalBound = (h - mBitmap.getHeight()) * 0.5f;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		float x = (mXOrigin - mBitmap.getWidth() / 2) + mBall.mPosX;
		float y = (mYOrigin - mBitmap.getHeight() / 2) - mBall.mPosY;
		if (!isStopWank) {
			if (y <= (screenHeight - screenWidth - 300) && !isUp) {
				playSound(ACTION_TYPE.UP);
				isUp = true;
				wankCount++;
				Log.e(TAG, "up");
			} else if (y >= (screenHeight - screenWidth - 100) && isUp) {
				playSound(ACTION_TYPE.DOWN);
				isUp = false;
				wankCount++;
				Log.e(TAG, "down");
			}
		}
		onShake();
		mBall.updatePosition(mSensorX, mSensorY, mSensorZ, mSensorTimeStamp);
		mBall.resolveCollisionWithBounds(mHorizontalBound, mVerticalBound);
		canvas.drawBitmap(mBitmap, x, y, null);
		invalidate();
	}

	public void onShake() {
		wankSplash = Utils.getRandom(3, 7);
		if (wankCount >= wankSplash * 2) {
			if (wankNumber <= ControlActivity.maxWank) {
				wankNumber++;
				mCustomViewObserver.showImageWank(mTouchX, mTouchY, wankNumber);
				playSoundWank();
				wankCount = 0;
			}

			// } else if (wankCount == 6 && isShowImageView) {
			// // mCustomViewObserver.hideImage();
			// isShowImageView = false;
			// wankCount = 0;
		}

		// Only shake 3 time
		if (wankNumber == ControlActivity.maxWank) {
			wankCount = 0;
			isStopWank = true;
		}
	}

	/**
	 * Play sound when current equal with target
	 */
	public void playSoundWank() {
		mp = MediaPlayer.create(context, R.raw.short_flatulence);
		mp.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
			}
		});
		mp.setVolume(ControlActivity.volumeSound, ControlActivity.volumeSound);
		mp.start();

	}

	/**
	 * Play sound when current equal with target
	 */
	public void playSound(ACTION_TYPE type) {
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(SOUND1.length);
		MediaPlayer mp = null;
		try {
			if (type == ACTION_TYPE.UP) {
				mp = MediaPlayer.create(context, SOUND1[1]);
			} else {
				mp = MediaPlayer.create(context, SOUND2[1]);
			}
			mp.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.release();
				}
			});
			mp.setVolume(1, 1);
			mp.start();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * @return the mTouchX
	 */
	public float getmTouchX() {
		return mTouchX;
	}

	/**
	 * @param mTouchX
	 *            the mTouchX to set
	 */
	public void setmTouchX(float mTouchX) {
		this.mTouchX = mTouchX;
	}

	/**
	 * @return the mTouchY
	 */
	public float getmTouchY() {
		return mTouchY;
	}

	/**
	 * @param mTouchY
	 *            the mTouchY to set
	 */
	public void setmTouchY(float mTouchY) {
		this.mTouchY = mTouchY;
	}

	/**
	 * @return the isStopWank
	 */
	public boolean isStopWank() {
		return isStopWank;
	}

	/**
	 * @param isStopWank
	 *            the isStopWank to set
	 */
	public void setStopWank(boolean isStopWank) {
		this.isStopWank = isStopWank;
	}

	/**
	 * @return the wankCount
	 */
	public int getWankCount() {
		return wankCount;
	}

	/**
	 * @param wankCount
	 *            the wankCount to set
	 */
	public void setWankCount(int wankCount) {
		this.wankCount = wankCount;
	}

	/**
	 * @return the wankNumber
	 */
	public int getWankNumber() {
		return wankNumber;
	}

	/**
	 * @param wankNumber
	 *            the wankNumber to set
	 */
	public void setWankNumber(int wankNumber) {
		this.wankNumber = wankNumber;
	}
}
