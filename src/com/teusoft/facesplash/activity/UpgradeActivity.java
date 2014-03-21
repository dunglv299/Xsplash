package com.teusoft.facesplash.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.teusoft.billing.util.IabHelper;
import com.teusoft.billing.util.IabResult;
import com.teusoft.billing.util.Inventory;
import com.teusoft.billing.util.Purchase;
import com.teusoft.facesplash.R;
import com.teusoft.facesplash.util.ManageSharedPreferences;
import com.teusoft.facesplash.util.Utils;

public class UpgradeActivity extends Activity implements OnClickListener {
	ImageView upgrade1Btn;
	ImageView upgrade2Btn;
	public static final String TAG = "UpgradeActivity";
	public static final String THREE_WANK = "three_splash";
	public static final String INSTA_WANK = "insta_splash";
	static final int RC_REQUEST = 10001;
	// The helper object
	IabHelper mHelper;
	boolean isThreeWankUpgrade = false;
	boolean isInstaWankUpgrade = false;
	ManageSharedPreferences manageSharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upgrade);
		manageSharedPreferences = new ManageSharedPreferences(this);
		Typeface tf = Utils.getTypeface(this);
		((TextView) findViewById(R.id.upgrade_tv)).setTypeface(tf,
				Typeface.BOLD);
		upgrade1Btn = (ImageView) findViewById(R.id.upgrade1_btn);
		upgrade2Btn = (ImageView) findViewById(R.id.upgrade2_btn);
		upgrade1Btn.setOnClickListener(this);
		upgrade2Btn.setOnClickListener(this);
		init();
	}

	public void init() {
		String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuYGCrYIRVYYP5z5QFk48ySLdYbXKBwUt4BNxcAuyszHDT6ENN7LfrOtExlSruEnxqOduwhzKldg8dWe/wnWgkfKcg7YG/qspMASU1xB95J+QWPwqWwCkd6ixhrmzX5txoQ3Du5sjCt5Nx5YFbU5nCUTKZCUOyGgTGB9HzXFyfh4BYFZaPXoXsnVsklYqAsyZ+nAThgt95HV3niGSTgaC/LgjK4mYs6REtJ/xSdqhIaajG9ylkzwcpcV8J5voP1mPRYEAkFwjqem0uEEw18Zp3LAbYiF1EIEiKVH1JeSPKVNlogbRZCzfBz7NWKMCk58IkvN9qTF0L1ApqXAKlVbuzQIDAQAB";

		// Some sanity checks to see if the developer (that's you!) really
		// followed the
		// instructions to run this sample (don't put these checks on your app!)
		if (base64EncodedPublicKey.contains("CONSTRUCT_YOUR")) {
			throw new RuntimeException(
					"Please put your app's public key in MainActivity.java. See README.");
		}
		if (getPackageName().startsWith("com.example")) {
			throw new RuntimeException(
					"Please change the sample's package name! See README.");
		}

		// Create the helper, passing it our context and the public key to
		// verify signatures with
		Log.d(TAG, "Creating IAB helper.");
		mHelper = new IabHelper(this, base64EncodedPublicKey);

		// enable debug logging (for a production application, you should set
		// this to false).
		mHelper.enableDebugLogging(true);

		// Start setup. This is asynchronous and the specified listener
		// will be called once setup completes.
		Log.d(TAG, "Starting setup.");
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				Log.d(TAG, "Setup finished.");

				if (!result.isSuccess()) {
					// Oh noes, there was a problem.
					complain("Problem setting up in-app billing: " + result);
					return;
				}

				// Have we been disposed of in the meantime? If so, quit.
				if (mHelper == null)
					return;

				// IAB is fully set up. Now, let's get an inventory of stuff we
				// own.
				Log.d(TAG, "Setup successful. Querying inventory.");
				mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});
	}

	public void onBack(View v) {
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.upgrade1_btn:
			upgradeThreeSplash();
			break;
		case R.id.upgrade2_btn:
			upgradeInstaWank();
			break;

		default:
			break;
		}

	}

	public void upgradeThreeSplash() {
		Log.d(TAG,
				"Upgrade button clicked; launching purchase flow for upgrade.");
		if (isThreeWankUpgrade) {
			alert("This item has been purchased already!");
			Log.e(TAG, "This item has been purchased already!");
		}

		/*
		 * TODO: for security, generate your payload here for verification. See
		 * the comments on verifyDeveloperPayload() for more info. Since this is
		 * a SAMPLE, we just use an empty string, but on a production app you
		 * should carefully generate this.
		 */
		String payload = "";

		mHelper.launchPurchaseFlow(this, THREE_WANK, RC_REQUEST,
				mPurchaseFinishedListener, payload);
	}

	public void upgradeInstaWank() {
		Log.d(TAG,
				"Upgrade button clicked; launching purchase flow for upgrade.");
		if (isInstaWankUpgrade) {
			alert("This item has been purchased already!");
			Log.e(TAG, "This item has been purchased already!");
		}
		String payload = "";
		mHelper.launchPurchaseFlow(this, INSTA_WANK, RC_REQUEST,
				mPurchaseFinishedListener, payload);
	}

	// Callback for when a purchase is finished
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			Log.d(TAG, "Purchase finished: " + result + ", purchase: "
					+ purchase);

			// if we were disposed of in the meantime, quit.
			if (mHelper == null)
				return;

			if (result.isFailure()) {
				Log.e(TAG, "Error purchasing: " + result);
				return;
			}
			if (!verifyDeveloperPayload(purchase)) {
				complain("Error purchasing. Authenticity verification failed.");
				return;
			}

			Log.d(TAG, "Purchase successful.");

			if (purchase.getSku().equals(THREE_WANK)) {
				// bought the three wank upgrade!
				Log.d(TAG,
						"Purchase is three wank upgrade. Congratulating user.");
				alert("Thank you for upgrading to three splashes!");
				isThreeWankUpgrade = true;
			} else if (purchase.getSku().equals(INSTA_WANK)) {
				// bought the insta wank upgrade!
				Log.d(TAG,
						"Purchase is instawank upgrade. Congratulating user.");
				alert("Thank you for upgrading to splash instantly!");
				isInstaWankUpgrade = true;
			}
			manageSharedPreferences.putBoolean(THREE_WANK, isThreeWankUpgrade);
			manageSharedPreferences.putBoolean(INSTA_WANK, isInstaWankUpgrade);
		}
	};

	void complain(String message) {
		Log.e(TAG, "**** FaceWank Error: " + message);
		alert("Error: " + message);
	}

	void alert(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(this);
		bld.setMessage(message);
		bld.setNeutralButton("OK", null);
		Log.d(TAG, "Showing alert dialog: " + message);
		bld.create().show();
	}

	boolean verifyDeveloperPayload(Purchase p) {
		String payload = p.getDeveloperPayload();
		return true;
	}

	// Listener that's called when we finish querying the items and
	// subscriptions we own
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result,
				Inventory inventory) {
			Log.d(TAG, "Query inventory finished.");

			// Have we been disposed of in the meantime? If so, quit.
			if (mHelper == null)
				return;

			// Is it a failure?
			if (result.isFailure()) {
				complain("Failed to query inventory: " + result);
				return;
			}

			Log.d(TAG, "Query inventory was successful.");

			/*
			 * Check for items we own. Notice that for each purchase, we check
			 * the developer payload to see if it's correct! See
			 * verifyDeveloperPayload().
			 */

			// Do we have the premium upgrade?
			Purchase threeWankPurchase = inventory.getPurchase(THREE_WANK);
			isThreeWankUpgrade = (threeWankPurchase != null && verifyDeveloperPayload(threeWankPurchase));
			Log.d(TAG, "User is "
					+ (isThreeWankUpgrade ? "THREE WANK" : "NOT THREE WANK"));

			Purchase instaWankPurchase = inventory.getPurchase(INSTA_WANK);
			isInstaWankUpgrade = (instaWankPurchase != null && verifyDeveloperPayload(instaWankPurchase));
			Log.d(TAG, "User is "
					+ (isThreeWankUpgrade ? "INSTA WANK" : "NOT INSTA WANK"));
			manageSharedPreferences.putBoolean(INSTA_WANK, isInstaWankUpgrade);
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + ","
				+ data);
		if (mHelper == null)
			return;

		// Pass on the activity result to the helper for handling
		try {
			if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
				// not handled, so handle it ourselves (here's where you'd
				// perform any handling of activity results not related to
				// in-app
				// billing...
				super.onActivityResult(requestCode, resultCode, data);
			} else {
				Log.d(TAG, "onActivityResult handled by IABUtil.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
