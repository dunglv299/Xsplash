package com.teusoft.facesplash.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.teusoft.facesplash.R;
import com.teusoft.facesplash.service.BackgroundSoundService;
import com.teusoft.facesplash.service.BackgroundSoundService.LocalBinder;
import com.teusoft.facesplash.util.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private static final int ACTION_TAKE_PHOTO = 1;
	private static final int REQ_CODE_PICK_IMAGE = 2;
	private long currentTime;
	private String mCurrentPhotoPath;
	public static final String JPEG_FILE_PREFIX = "IMG_";
	public static final String JPEG_FILE_SUFFIX = ".jpg";
	int width;
	BackgroundSoundService mService;
	boolean mbound = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Display display = getWindowManager().getDefaultDisplay();
		width = display.getWidth();
		setFont();
		startMusicService();

	}

	public void setFont() {
		Typeface tf = Utils.getTypeface(this);
		// free version
		((TextView) findViewById(R.id.main_takephoto)).setTypeface(tf,
				Typeface.NORMAL);
	}

	public void onClickInfo(View v) {
		Intent i = new Intent(this, InfoActivity.class);
		startActivity(i);
	}

	public void takePicture(View v) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File f = null;
		currentTime = Calendar.getInstance().getTimeInMillis();
		try {
			f = setUpPhotoFile();
			mCurrentPhotoPath = f.getAbsolutePath();
			takePictureIntent
					.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		} catch (IOException e) {
			e.printStackTrace();
			f = null;
			mCurrentPhotoPath = null;
		}
		startActivityForResult(takePictureIntent, ACTION_TAKE_PHOTO);
	}

	/**
	 * Select image from gallery
	 */
	public void pickPicture(View v) {
		Intent i = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		File f = null;
		currentTime = Calendar.getInstance().getTimeInMillis();
		try {
			f = setUpPhotoFile();
			mCurrentPhotoPath = f.getAbsolutePath();
			i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		} catch (IOException e) {
			e.printStackTrace();
			f = null;
			mCurrentPhotoPath = null;
		}
		startActivityForResult(i, REQ_CODE_PICK_IMAGE);
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String imageFileName = JPEG_FILE_PREFIX + currentTime
				+ JPEG_FILE_SUFFIX;
		File albumF = Utils.getAlbumDir(this);
		File imageF = new File(albumF, imageFileName);
		return imageF;
	}

	private File setUpPhotoFile() throws IOException {
		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();
		return f;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ACTION_TAKE_PHOTO: {
			// Show status bar
			if (resultCode == Activity.RESULT_OK) {
				Log.e("mCurrentPhotoPath", mCurrentPhotoPath + "s");
				// Send path to other Activity
				if (mCurrentPhotoPath != null) {
					Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
					try {
						resizeBitmap(
								rotate(bitmap, Utils
										.getImageOrientation(mCurrentPhotoPath)),
								width);
					} catch (IOException e) {
						e.printStackTrace();
					}

					Intent mIntent = new Intent(this, ControlActivity.class);
					mIntent.putExtra("img_path", mCurrentPhotoPath);
					mIntent.putExtra("img_name", currentTime);
					mCurrentPhotoPath = null;
					startActivity(mIntent);
					// mService.playSound(2);
				}
			} else {
				File file = new File(mCurrentPhotoPath);
				if (file.exists()) {
					file.delete();
				}
				currentTime = 0;
			}
			break;
		}
		case REQ_CODE_PICK_IMAGE:
			if (resultCode == RESULT_OK) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String filePath = cursor.getString(columnIndex);
				cursor.close();
				Bitmap selectedBitmap = BitmapFactory.decodeFile(filePath);
				if (selectedBitmap == null) {
					Log.e("dunglv", "Exception dunglv");
					Toast.makeText(this,
							"Please select image from camera album",
							Toast.LENGTH_LONG).show();
					return;
				}
				try {
					resizeBitmap(
							rotate(selectedBitmap, Utils
									.getImageOrientation(mCurrentPhotoPath)),
							width);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent mIntent = new Intent(this, ControlActivity.class);
				mIntent.putExtra("img_path", mCurrentPhotoPath);
				mIntent.putExtra("img_name", currentTime);
				mCurrentPhotoPath = null;
				startActivity(mIntent);
				// mService.playSound(2);
			}
			break;
		}
	}

	/**
	 * Resize image after take camera
	 * 
	 * @throws IOException
	 */
	public void resizeBitmap(Bitmap bitmap, int newWidth) throws IOException {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		double scale = (double) newWidth / (double) width;
		bitmap = Bitmap.createScaledBitmap(bitmap, newWidth,
				(int) (scale * height), false);
		Utils.exportBitmapToFile(bitmap, createImageFile().getAbsolutePath());
	}

	public Bitmap rotate(Bitmap bitmap, int degree) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		Matrix mtx = new Matrix();
		mtx.postRotate(degree);

		return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopMusicService();
	}

	public void startMusicService() {
		Intent svc = new Intent(this, BackgroundSoundService.class);
		svc.putExtra(MainActivity.class.getSimpleName(), "MainActivity");
		bindService(svc, mConnection, Context.BIND_AUTO_CREATE);
	}

	public void stopMusicService() {
		// this.stopService(new Intent(this, BackgroundSoundService.class));
		if (mbound) {
			unbindService(mConnection);
			mbound = false;
		}
		this.stopService(new Intent(this, BackgroundSoundService.class));
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mbound = true;
			Log.e(TAG, "onServiceConnected");
			mService.playSound(1);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mbound = false;
			Log.e(TAG, "onServiceDisconnected");
		}
	};
}
