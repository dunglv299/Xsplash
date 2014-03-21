package com.teusoft.facesplash.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;
import com.teusoft.facesplash.R;

public class Utils {
	/**
	 * Export bitmap with transperant to white
	 * 
	 * @param view
	 * @return
	 */
	public static Bitmap getBitmapFromViewWithColorAndWaterMark(
			Context context, View view, int color) {
		if (view.getWidth() > 0) {
			// Define a bitmap with the same size as the view
			Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(),
					view.getHeight(), Bitmap.Config.ARGB_8888);
			Bitmap waterMarkBitmap = BitmapFactory.decodeResource(
					context.getResources(), R.drawable.water_mark);
			// Bind a canvas to it
			Canvas canvas = new Canvas(returnedBitmap);
			// Get the view's background
			Drawable bgDrawable = view.getBackground();
			if (bgDrawable != null) {
				// has background drawable, then draw it on the canvas
				bgDrawable.draw(canvas);
			} else {
				// does not have background drawable, then draw white background
				// on
				// the canvas
				canvas.drawColor(color);
			}
			// draw the view on the canvas
			view.draw(canvas);
			returnedBitmap = overlay(returnedBitmap, waterMarkBitmap);
			// return the bitmap
			return returnedBitmap;
		}
		return null;
	}

	public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
		Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(),
				bmp1.getHeight(), bmp1.getConfig());
		// createa matrix for the manipulation
		Matrix matrix = new Matrix();
		matrix.setTranslate(0, bmp1.getHeight() - bmp2.getHeight());
		Canvas canvas = new Canvas(bmOverlay);
		canvas.drawBitmap(bmp1, new Matrix(), null);
		canvas.drawBitmap(bmp2, matrix, null);
		return bmOverlay;
	}

	/**
	 * If you want to use layout with transparent
	 * 
	 * @param v
	 * @return
	 */
	public static Bitmap getBitmapFromView(View v) {
		Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		v.layout(0, 0, v.getWidth(), v.getHeight());
		v.draw(c);
		return b;
	}

	public static File getAlbumDir(Context context) {
		File storageDir = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			// storageDir = context
			// .getExternalFilesDir(Environment.DIRECTORY_PICTURES);
			storageDir = context.getExternalFilesDir("FaceWank");
			if (storageDir != null) {
				if (!storageDir.mkdirs()) {
					if (!storageDir.exists()) {
						Log.e("Pictures", "failed to create directory");
						return null;
					}
				}
			}

		} else {
			Log.v(context.getString(R.string.app_name),
					"External storage is not mounted READ/WRITE.");
		}

		return storageDir;
	}

	/**
	 * Export bitmap to file in project
	 */
	public static void exportBitmapToFile(Bitmap bitmap, String imageFileName) {
		try {
			FileOutputStream out = new FileOutputStream(imageFileName);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Export bitmap to file in project
	 */
	public static void exportBitmapToNewFile(Bitmap bitmap, String imageFileName) {
		try {
			FileOutputStream out = new FileOutputStream(imageFileName);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Export bitmap to file in project
	 */
	public static void exportBitmapToFilePNG(Bitmap bitmap, String imageFileName) {
		try {
			FileOutputStream out = new FileOutputStream(imageFileName);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getImageOrientation(String imagePath) {
		int rotate = 0;
		try {

			File imageFile = new File(imagePath);
			ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.e("rotate", rotate + "");
		return rotate;
	}

	public static Bitmap getBitmapFromPath(String mCurrentPhotoPath) {
		/*
		 * There isn't enough memory to open up more than a couple camera photos
		 */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		// int targetW = mImageView.getWidth();
		// int targetH = mImageView.getHeight();
		int targetW = 1200;
		int targetH = 1600;

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW / targetW, photoH / targetH);
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		return bitmap;
	}

	public static Typeface getTypeface(Context context) {
		Typeface tf = Typeface.createFromAsset(context.getAssets(),
				"Noteworthy.ttc");
		return tf;
	}

	public static void shareSocialIntent(Context context, String nameApp,
			String imagePath) {
		// List<Intent> targetedShareIntents = new ArrayList<Intent>();
		Intent share = new Intent(android.content.Intent.ACTION_SEND);
		share.setType("image/jpeg");
		List<ResolveInfo> resInfo = context.getPackageManager()
				.queryIntentActivities(share, 0);
		if (!resInfo.isEmpty()) {
			boolean isHasApplication = false;
			for (ResolveInfo info : resInfo) {
				Intent targetedShare = new Intent(
						android.content.Intent.ACTION_SEND);
				targetedShare.setType("image/jpeg"); // put here your mime type

				if (info.activityInfo.packageName.toLowerCase(
						Locale.getDefault()).contains(
						nameApp.toLowerCase(Locale.getDefault()))
						|| info.activityInfo.name.toLowerCase(
								Locale.getDefault()).contains(
								nameApp.toLowerCase(Locale.getDefault()))) {
					isHasApplication = true;
					targetedShare.putExtra(Intent.EXTRA_TEXT, "");
					targetedShare.putExtra(Intent.EXTRA_STREAM,
							Uri.fromFile(new File(imagePath)));
					targetedShare.setPackage(info.activityInfo.packageName);
					// targetedShareIntents.add(targetedShare);
					context.startActivity(targetedShare);
					break;
				}
			}
			if (!isHasApplication) {
				Toast.makeText(context,
						"Please install " + nameApp + " application",
						Toast.LENGTH_LONG).show();
			}

			// Intent chooserIntent = Intent.createChooser(
			// targetedShareIntents.remove(0), "Select app to share");
			// chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
			// targetedShareIntents.toArray(new Parcelable[] {}));
			// startActivity(chooserIntent);
		}
	}

	public static float getRandom(float minX, float maxX) {
		Random rand = new Random();
		float finalX = rand.nextFloat() * (maxX - minX) + minX;
		return finalX;
	}

	public static int getRandom(int from, int to) {
		if (from < to)
			return from + new Random().nextInt(Math.abs(to - from));
		return from - new Random().nextInt(Math.abs(to - from));
	}

	@SuppressLint("NewApi")
	public static void removeOnGlobalLayoutListener(View v,
			ViewTreeObserver.OnGlobalLayoutListener listener) {
		if (Build.VERSION.SDK_INT < 16) {
			v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
		} else {
			v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
		}
	}
}
