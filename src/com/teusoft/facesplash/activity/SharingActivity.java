package com.teusoft.facesplash.activity;

import java.io.File;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.teusoft.facesplash.R;
import com.teusoft.facesplash.util.Utils;

public class SharingActivity extends Activity implements OnClickListener {
	private static final String EMAIL_BODY = null;
	ImageButton cancelBtn;
	ImageButton mailBtn;
	ImageButton twritterBtn;
	ImageButton facebookBtn;
	ImageButton galleryBtn;
	String mCurrentPhotoPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sharing);
		mCurrentPhotoPath = getIntent().getStringExtra("img_path");
		initView();
	}

	public void initView() {
		cancelBtn = (ImageButton) findViewById(R.id.cancel_shareBtn);
		mailBtn = (ImageButton) findViewById(R.id.mail_btn);
		twritterBtn = (ImageButton) findViewById(R.id.twitter_btn);
		facebookBtn = (ImageButton) findViewById(R.id.facebook_btn);
		galleryBtn = (ImageButton) findViewById(R.id.gallery_btn);
		cancelBtn.setOnClickListener(this);
		mailBtn.setOnClickListener(this);
		twritterBtn.setOnClickListener(this);
		facebookBtn.setOnClickListener(this);
		galleryBtn.setOnClickListener(this);
	}

	public void goBack() {
		finish();
		overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
		deleteImageFile();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		goBack();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancel_shareBtn:
			goBack();
			break;
		case R.id.mail_btn:
			shareMail();
			break;
		case R.id.twitter_btn:
			Utils.shareSocialIntent(this, "Twitter", mCurrentPhotoPath);
			break;
		case R.id.facebook_btn:
			Utils.shareSocialIntent(this, "Facebook", mCurrentPhotoPath);
			break;
		case R.id.gallery_btn:
			shareGallery();
			break;
		default:
			break;
		}

	}

	private void shareMail() {
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("message/rfc822");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
				new String[] { "" });
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, EMAIL_BODY);
		emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(mCurrentPhotoPath));
		startActivity(Intent.createChooser(emailIntent, "Sharing Options"));
	}

	private void shareGallery() {
		try {
			MediaStore.Images.Media.insertImage(getContentResolver(),
					mCurrentPhotoPath, mCurrentPhotoPath, "Facewank Image");
			Toast.makeText(this, "Save to gallery successfully",
					Toast.LENGTH_SHORT).show();
			deleteImageFile();
			finish();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public void deleteImageFile() {
		File file;
		file = new File(mCurrentPhotoPath);
		if (file.exists()) {
			file.delete();
		}
	}

}
