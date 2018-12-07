package com.kedzie.vbox.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.kedzie.vbox.VBoxApplication;

/**
 * Base Activity for all application activities.  Enables indeterminate progress bar and disables it.
 * @author Marek Kędzierski
 */
public class BaseActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		super.onCreate(savedInstanceState);
		setProgressBarIndeterminateVisibility(false);
		setProgressBarVisibility(false);
	}


	@Override
	protected void onStart() {
		super.onStart();
		setProgressBarIndeterminateVisibility(false);
	}

	public VBoxApplication getApp() {
	    return (VBoxApplication)getApplication();
	}
}
