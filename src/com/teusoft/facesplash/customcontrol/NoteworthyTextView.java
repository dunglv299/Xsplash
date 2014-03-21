package com.teusoft.facesplash.customcontrol;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class NoteworthyTextView extends TextView {
	public static final String fontName = "Noteworthy.ttc";

	public NoteworthyTextView(Context context) {
		super(context);
		Typeface face = Typeface.createFromAsset(context.getAssets(), fontName);
		this.setTypeface(face);
	}

	public NoteworthyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Typeface face = Typeface.createFromAsset(context.getAssets(), fontName);
		this.setTypeface(face);
	}

	public NoteworthyTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Typeface face = Typeface.createFromAsset(context.getAssets(), fontName);
		this.setTypeface(face);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

	}

}
