package com.teusoft.facesplash.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.teusoft.facesplash.CustomViewObserver;
import com.teusoft.facesplash.R;
import com.teusoft.facesplash.SimulationView;
import com.teusoft.facesplash.SimulationView.ChangeAimListener;
import com.teusoft.facesplash.gesturedetectors.MoveGestureDetector;
import com.teusoft.facesplash.gesturedetectors.RotateGestureDetector;
import com.teusoft.facesplash.gesturedetectors.ShoveGestureDetector;
import com.teusoft.facesplash.service.BackgroundSoundService;
import com.teusoft.facesplash.service.BackgroundSoundService.LocalBinder;
import com.teusoft.facesplash.util.ManageSharedPreferences;
import com.teusoft.facesplash.util.Utils;

public class ControlActivity extends Activity implements CustomViewObserver,
		OnTouchListener, ChangeAimListener, OnClickListener,
		OnCheckedChangeListener {

	private static final String TAG = "ControlActivity";
	public static int maxWank = 1;
	public static final float volumeSound = 0.3f;
	private SimulationView mSimulationView;
	RelativeLayout mainContent;
	ImageView wankImageView;
	ImageView mAimImageView;
	ImageView captureImageView;
	RadioButton wankToolBtn;
	RadioButton instaWankBtn;
	RadioButton resizeBtn;
	RadioButton eraseBtn;
	TextView wankNumberTv;
	ImageButton homeBtn;
	ImageButton shareBtn;
	ImageView upgradeBtn;

	private Bitmap wankBitmap;
	private Bitmap captureBitmap;
	private String mCurrentPhotoPath;
	float mTouchX;
	float mTouchY;
	int screenHeight;
	int screenWidth;
	private int mAimWidth;
	private int mAimHeight;
	private Bitmap mTapBitmap;

	private boolean isPlayTap;
	private boolean isSelectInstaWank;
	private boolean isSelectRotate;
	private List<ImageView> listImageWank;
	private boolean isStopTouch;
	private Bitmap mBitmap;
	MediaPlayer mp;
	TextView tutorialTextView;
	private ManageSharedPreferences manageSharedPreferences;
	private boolean isShowAim;
	BackgroundSoundService mService;
	boolean mbound = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		manageSharedPreferences = new ManageSharedPreferences(this);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		screenHeight = displaymetrics.heightPixels;
		screenWidth = displaymetrics.widthPixels;
		setContentView(R.layout.activity_control);
		initView();

		mCurrentPhotoPath = getIntent().getStringExtra("img_path");
		captureBitmap = Utils.getBitmapFromPath(mCurrentPhotoPath);
		if (captureBitmap == null) {
			finish();
			Toast.makeText(this, "Fail to load image", Toast.LENGTH_LONG)
					.show();
			return;
		}
		initGesture();

		// Set first touch
		mSimulationView.setmTouchX(screenWidth / 2);
		mSimulationView.setmTouchY(screenHeight / 2);
		initButtonTapToAim();
		listImageWank = new ArrayList<ImageView>();
		wankToolBtn.setChecked(true);

		// Start service background
		Intent svc = new Intent(this, BackgroundSoundService.class);
		svc.putExtra(ControlActivity.class.getSimpleName(), "ControlActivity");
		startService(svc);
	}

	private void initView() {
		captureImageView = (ImageView) findViewById(R.id.capture_imageview);
		mainContent = (RelativeLayout) findViewById(R.id.mainContent);
		wankToolBtn = (RadioButton) findViewById(R.id.wank_tool_btn);
		instaWankBtn = (RadioButton) findViewById(R.id.insta_wank_btn);
		resizeBtn = (RadioButton) findViewById(R.id.resize_btn);
		eraseBtn = (RadioButton) findViewById(R.id.erase_btn);
		upgradeBtn = (ImageView) findViewById(R.id.upgrade_btn);
		wankToolBtn.setOnCheckedChangeListener(this);
		instaWankBtn.setOnCheckedChangeListener(this);
		resizeBtn.setOnCheckedChangeListener(this);
		upgradeBtn.setOnClickListener(this);
		eraseBtn.setOnClickListener(this);
		wankNumberTv = (TextView) findViewById(R.id.wank_number_tv);
		wankNumberTv.setText(maxWank + "");
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		mSimulationView = new SimulationView(this, this);
		mainContent.addView(mSimulationView, params);
		homeBtn = (ImageButton) findViewById(R.id.home_btn);
		shareBtn = (ImageButton) findViewById(R.id.share_btn);
		homeBtn.setOnClickListener(this);
		shareBtn.setOnClickListener(this);
		tutorialTextView = (TextView) findViewById(R.id.tutorial_tv);
		startAnimationsText(getString(R.string.touch_screen));
	}

	private void startAnimationsText(String text) {
		tutorialTextView.setText(text);
		Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_up);
		scaleAnimation.setFillAfter(true);
		tutorialTextView.startAnimation(scaleAnimation);

	}

	@Override
	protected void onResume() {
		super.onResume();
		// With free version
		setViewForPurchase();
		mSimulationView.startSimulation(this);
		mSimulationView.setObserver(this);
		if (isShowAim) {
			showAim();
		} else {
			hideAim();
		}

	}

	private void setViewForPurchase() {
		boolean isThreeWank = manageSharedPreferences
				.getBoolean(UpgradeActivity.THREE_WANK);
		boolean isInstaWank = manageSharedPreferences
				.getBoolean(UpgradeActivity.INSTA_WANK);
		// isThreeWank = true; // test
		// isInstaWank = true;
		Log.e(TAG, "isThreeWank: " + isThreeWank + " ,isInstaWank: "
				+ isInstaWank);
		if (isThreeWank) {
			maxWank = 3;
			wankNumberTv.setText(maxWank + "");
		}
		if (isInstaWank) {
			findViewById(R.id.insta_layout).setVisibility(View.VISIBLE);
		}
		if (isThreeWank && isInstaWank) {
			upgradeBtn.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSimulationView.stopSimulation();
		if (mp != null) {
			if (mp.isPlaying()) {
				mp.stop();
			}
			mp.release();
			mp = null;
		}
	}

	public BitmapFactory.Options options() {
		BitmapFactory.Options o = new Options();
		o.inScaled = false;
		o.inPreferredConfig = Bitmap.Config.RGB_565;
		return o;
	}

	private void initButtonTapToAim() {
		mTapBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.tap_to_aim, options());
		mAimImageView = new ImageView(this);
		LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mAimImageView.setLayoutParams(vp);
		mAimImageView.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@SuppressLint("NewApi")
					@Override
					public void onGlobalLayout() {
						// Ensure you call it only once :
						// mAimImageView.getViewTreeObserver()
						// .removeOnGlobalLayoutListener(this);
						Utils.removeOnGlobalLayoutListener(mAimImageView, this);
						mAimWidth = mAimImageView.getWidth();
						mAimHeight = mAimImageView.getHeight();
						mAimImageView
								.setTranslationX((screenWidth - mAimWidth) / 2);
						mAimImageView
								.setTranslationY((screenHeight - mAimHeight) / 2);
					}
				});
		mAimImageView.setImageBitmap(mTapBitmap);
		moveButtonAim(screenWidth / 2, screenHeight / 2);
		mainContent.addView(mAimImageView);

	}

	private void moveButtonAim(float x, float y) {
		mAimImageView.setTranslationX(x - (mAimWidth / 2));
		mAimImageView.setTranslationY(y - (mAimHeight / 2));
		if (isPlayTap) {
			playSoundTap();
		}
		isPlayTap = true;
	}

	public void showAim() {
		mAimImageView.setVisibility(View.VISIBLE);
		isPlayTap = true;
		isShowAim = true;
	}

	public void hideAim() {
		mAimImageView.setVisibility(View.INVISIBLE);
	}

	public void showTapToWank() {
		mTapBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.tap_to_wank, options());
		mAimImageView.setImageBitmap(mTapBitmap);
	}

	public void changeTapToAim() {
		mTapBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.tap_to_aim, options());
		mAimImageView.setImageBitmap(mTapBitmap);
	}

	@Override
	public void showImageWank(final float x, final float y, int wankNumber) {
		BitmapFactory.Options o = new Options();
		o.inPreferredConfig = Bitmap.Config.RGB_565;

		TypedArray images = getResources().obtainTypedArray(R.array.list_wank);
		int choice = (int) (Math.random() * images.length());
		wankBitmap = BitmapFactory.decodeResource(getResources(),
				images.getResourceId(choice, R.drawable.wank1), o);
		float randomSize = Utils.getRandom(0.5f, 1.0f);
		wankBitmap = Bitmap.createScaledBitmap(wankBitmap,
				(int) (wankBitmap.getWidth() * randomSize),
				(int) (wankBitmap.getHeight() * randomSize), true);
		images.recycle();

		wankImageView = new ImageView(this);
		LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		wankImageView.setLayoutParams(vp);
		wankImageView.setRotation((float) Math.random() * 360);
		wankImageView.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@SuppressLint("NewApi")
					@Override
					public void onGlobalLayout() {
						// Ensure you call it only once :
						Utils.removeOnGlobalLayoutListener(wankImageView, this);
						// Here you can get the size :)
						wankImageView.setTranslationX(x
								- (wankImageView.getWidth() / 2));
						wankImageView.setTranslationY(y
								- (wankImageView.getHeight() / 2));
					}
				});
		wankImageView.setImageBitmap(wankBitmap);
		Animation animationScale = AnimationUtils.loadAnimation(this,
				R.anim.scale_wank);
		wankImageView.startAnimation(animationScale);
		mainContent.addView(wankImageView);
		hideAim();
		if (isSelectInstaWank) {
			wankNumber = 0;
			mSimulationView.setWankNumber(wankNumber);
			// mSimulationView.setStopWank(true);
		}
		wankNumberTv.setText(String.valueOf(maxWank - wankNumber));
		listImageWank.add(wankImageView);
		isStopTouch = true;
	}

	@Override
	public void hideImageWank() {
		for (ImageView imageView : listImageWank) {
			// imageView.setVisibility(View.GONE);
			mainContent.removeView(imageView);
		}
		listImageWank.clear();
	}

	public void initGesture() {
		// Determine the center of the screen to center 'earth'
		mFocusX = screenWidth / 2f;
		mFocusY = screenHeight / 2f;
		// Set this class as touchListener to the ImageView
		captureImageView.setOnTouchListener(this);

		// Determine dimensions of 'earth' image
		mImageWidth = captureBitmap.getWidth();
		mImageHeight = captureBitmap.getHeight();

		// View is scaled and translated by matrix, so scale and translate
		// initially
		float scaledImageCenterX = (mImageWidth * mScaleFactor) / 2;
		float scaledImageCenterY = (mImageHeight * mScaleFactor) / 2;

		mMatrix.postScale(mScaleFactor, mScaleFactor);
		mMatrix.postTranslate(mFocusX - scaledImageCenterX, mFocusY
				- scaledImageCenterY);
		captureImageView.setImageMatrix(mMatrix);

		// Setup Gesture Detectors
		mScaleDetector = new ScaleGestureDetector(getApplicationContext(),
				new ScaleListener());
		mRotateDetector = new RotateGestureDetector(getApplicationContext(),
				new RotateListener());
		mMoveDetector = new MoveGestureDetector(getApplicationContext(),
				new MoveListener());
		mShoveDetector = new ShoveGestureDetector(getApplicationContext(),
				new ShoveListener());
		captureImageView.setImageBitmap(captureBitmap);
	}

	private Matrix mMatrix = new Matrix();
	private float mScaleFactor = 1.f;
	private float mRotationDegrees = 0.f;
	private float mFocusX = 0.f;
	private float mFocusY = 0.f;
	private int mAlpha = 255;
	private int mImageHeight, mImageWidth;

	private ScaleGestureDetector mScaleDetector;
	private RotateGestureDetector mRotateDetector;
	private MoveGestureDetector mMoveDetector;
	private ShoveGestureDetector mShoveDetector;

	public boolean onTouch(View v, MotionEvent event) {
		// Get Touch possion for shake
		mTouchX = event.getX();
		mTouchY = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (isPlayTap && !isStopTouch) {
				startAnimationsText(getString(R.string.shake_to_quirt));
				moveButtonAim(mTouchX, mTouchY);
				mSimulationView.setmTouchX(mTouchX);
				mSimulationView.setmTouchY(mTouchY);
			}
			if (isSelectInstaWank) {
				isPlayTap = false;
				hideImageWank();
				showImageWank(mTouchX, mTouchY, 0);
				mSimulationView.playSoundWank();
				// if (!mp.isPlaying()) {
				// mp.setOnPreparedListener(new OnPreparedListener() {
				// @Override
				// public void onPrepared(MediaPlayer mp) {
				// mp.start();
				// }
				// });
				//
				// }
			}
			break;
		}
		if (isSelectRotate) {
			mScaleDetector.onTouchEvent(event);
			mRotateDetector.onTouchEvent(event);
			mMoveDetector.onTouchEvent(event);
			mShoveDetector.onTouchEvent(event);

			float scaledImageCenterX = (mImageWidth * mScaleFactor) / 2;
			float scaledImageCenterY = (mImageHeight * mScaleFactor) / 2;

			mMatrix.reset();
			mMatrix.postScale(mScaleFactor, mScaleFactor);
			mMatrix.postRotate(mRotationDegrees, scaledImageCenterX,
					scaledImageCenterY);
			mMatrix.postTranslate(mFocusX - scaledImageCenterX, mFocusY
					- scaledImageCenterY);

			ImageView view = (ImageView) v;
			view.setImageMatrix(mMatrix);
			view.setAlpha(mAlpha);
		}
		return true; // indicate event was handled
	}

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mScaleFactor *= detector.getScaleFactor(); // scale change since
														// previous event

			// Don't let the object get too small or too large.
			mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));

			return true;
		}
	}

	private class RotateListener extends
			RotateGestureDetector.SimpleOnRotateGestureListener {
		@Override
		public boolean onRotate(RotateGestureDetector detector) {
			mRotationDegrees -= detector.getRotationDegreesDelta();
			return true;
		}
	}

	private class MoveListener extends
			MoveGestureDetector.SimpleOnMoveGestureListener {
		@Override
		public boolean onMove(MoveGestureDetector detector) {
			PointF d = detector.getFocusDelta();
			mFocusX += d.x;
			mFocusY += d.y;

			// mFocusX = detector.getFocusX();
			// mFocusY = detector.getFocusY();
			return true;
		}
	}

	private class ShoveListener extends
			ShoveGestureDetector.SimpleOnShoveGestureListener {
		@Override
		public boolean onShove(ShoveGestureDetector detector) {
			mAlpha += detector.getShovePixelsDelta();
			if (mAlpha > 255)
				mAlpha = 255;
			else if (mAlpha < 0)
				mAlpha = 0;

			return true;
		}
	}

	// out of memory
	@Override
	public void onChangeAim(float delta) {
		// TODO Auto-generated method stub
		float x = 1f + delta;
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				(int) (mAimWidth * x), (int) (mAimHeight * x));
		layoutParams.leftMargin = 0 - (int) (delta * mAimWidth) / 2;
		layoutParams.topMargin = 0 - (int) (delta * mAimHeight) / 2;
		mAimImageView.setLayoutParams(layoutParams);
	}

	public void playSoundTap() {
		MediaPlayer mp = MediaPlayer.create(this, R.raw.tap_sound);
		mp.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
			}
		});
		mp.setVolume(volumeSound, volumeSound);
		mp.start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_btn:
			finish();
			break;
		case R.id.share_btn:
			shareImage();
			break;
		case R.id.cancel_shareBtn:
			break;
		case R.id.erase_btn:
			selectEraseWank();
			eraseBtn.setChecked(false);
			startAnimationsText(getString(R.string.clear_now));
			break;
		case R.id.upgrade_btn:
			Intent i = new Intent(this, UpgradeActivity.class);
			startActivity(i);
			break;
		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			unSelectedButton((RadioButton) buttonView);
		}
		switch (buttonView.getId()) {
		case R.id.wank_tool_btn:
			if (isChecked) {
				startAnimationsText(getString(R.string.wank_now));
				selectFaceWankTool();
				isShowAim = true;
			}

			break;
		case R.id.insta_wank_btn:
			if (isChecked) {
				startAnimationsText(getString(R.string.insta_wank_now));
				selectInstaWank();
				isShowAim = true;
			}
			break;
		case R.id.resize_btn:
			if (isChecked) {
				startAnimationsText(getString(R.string.pan_now));
				selectRotateImage();
				isShowAim = false;
			}

			break;

		default:
			break;
		}
	}

	public void unSelectedButton(RadioButton toggleButton) {
		RadioButton[] buttons = { wankToolBtn, instaWankBtn, resizeBtn };
		for (RadioButton button : buttons) {
			if (button.getId() != toggleButton.getId()) {
				button.setChecked(false);
			}
		}

	}

	public void resetRadioButton() {
		isPlayTap = false;
		isSelectRotate = false;
		isSelectInstaWank = false;
		mSimulationView.setStopWank(true);
		isStopTouch = false;
	}

	/**
	 * Button menu listener
	 * 
	 * @param isChecked
	 */
	private void selectFaceWankTool() {
		resetRadioButton();
		hideImageWank();
		showAim();
		changeTapToAim();
		mSimulationView.setStopWank(false);
	}

	private void selectInstaWank() {
		resetRadioButton();
		wankNumberTv.setText(maxWank + "");
		hideImageWank();
		isSelectInstaWank = true;
		showAim();
		showTapToWank();
	}

	private void selectRotateImage() {
		hideAim();
		resetRadioButton();
		isSelectRotate = true;
	}

	private void selectEraseWank() {
		// resetRadioButton();
		hideImageWank();
		if (wankToolBtn.isChecked()) {
			wankNumberTv.setText(maxWank + "");
			mSimulationView.setWankNumber(0);
			selectFaceWankTool();
		}
		if (instaWankBtn.isChecked()) {
			showAim();
		}
	}

	/**
	 * Share image to social network
	 */
	private void shareImage() {
		hideAim();
		mBitmap = Utils.getBitmapFromViewWithColorAndWaterMark(this,
				mainContent, Color.BLACK);
		new SaveImageAsync().execute();
		Intent mIntent = new Intent(ControlActivity.this, SharingActivity.class);
		mIntent.putExtra("img_path", getNewImageFileName());
		startActivity(mIntent);
		overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
	}

	private String getNewImageFileName() {
		return mCurrentPhotoPath.replace(".jpg", "_n.jpg");
	}

	public class SaveImageAsync extends AsyncTask<Void, String, Void> {
		@Override
		protected Void doInBackground(Void... filePath) {
			Utils.exportBitmapToNewFile(mBitmap, getNewImageFileName());
			return null;
		}

		@Override
		protected void onPostExecute(Void filename) {
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mService.playSound(1);
		if (mbound) {
			unbindService(mConnection);
			mbound = false;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		startMusicService();
	}

	public void startMusicService() {
		Intent svc = new Intent(this, BackgroundSoundService.class);
		svc.putExtra(MainActivity.class.getSimpleName(), "MainActivity");
		bindService(svc, mConnection, Context.BIND_AUTO_CREATE);
	}

	// Connection between activity and service
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mbound = true;
			Log.e(TAG, "onServiceConnected");
			mService.playSound(2);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mbound = false;
			Log.e(TAG, "onServiceDisconnected");
			mService.playSound(1);
		}
	};

}
