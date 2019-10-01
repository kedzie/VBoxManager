package com.kedzie.vbox.app;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by kedzie on 2/16/14.
 */
public class LoggingFrameLayout extends FrameLayout {
	private static final String TAG = "LoggingFrameLayout";

	public LoggingFrameLayout(Context context) {
		super(context);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		Log.v(TAG, "Touch event: " + ev.toString());
		return super.dispatchTouchEvent(ev);
	}
}
