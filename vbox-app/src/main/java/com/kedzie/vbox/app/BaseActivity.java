package com.kedzie.vbox.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.kedzie.vbox.VBoxApplication;
import roboguice.RoboGuice;
import roboguice.activity.event.*;
import roboguice.context.event.OnConfigurationChangedEvent;
import roboguice.context.event.OnCreateEvent;
import roboguice.context.event.OnDestroyEvent;
import roboguice.context.event.OnStartEvent;
import roboguice.event.EventManager;
import roboguice.inject.ContentViewListener;
import roboguice.inject.RoboInjector;
import roboguice.util.RoboContext;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Base Activity for all application activities.  Enables indeterminate progress bar and disables it.
 * @author Marek Kędzierski
 */
public class BaseActivity extends AppCompatActivity implements RoboContext {
	protected EventManager eventManager;
	protected HashMap<Key<?>,Object> scopedObjects = new HashMap<Key<?>, Object>();

	@Inject
	ContentViewListener ignored; // BUG find a better place to put this

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		final RoboInjector injector = RoboGuice.getInjector(this);
		eventManager = injector.getInstance(EventManager.class);
		injector.injectMembersWithoutViews(this);
		super.onCreate(savedInstanceState);
		eventManager.fire(new OnCreateEvent<Activity>(this,savedInstanceState));
		setProgressBarIndeterminateVisibility(false);
		setProgressBarVisibility(false);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		eventManager.fire(new OnSaveInstanceStateEvent(this, outState));
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		eventManager.fire(new OnRestartEvent(this));
	}

	@Override
	protected void onStart() {
		super.onStart();
		eventManager.fire(new OnStartEvent<Activity>(this));
		setProgressBarIndeterminateVisibility(false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		eventManager.fire(new OnResumeEvent(this));
	}

	@Override
	protected void onPause() {
		super.onPause();
		eventManager.fire(new OnPauseEvent(this));
	}

	@Override
	protected void onNewIntent( Intent intent ) {
		super.onNewIntent(intent);
		eventManager.fire(new OnNewIntentEvent(this));
	}

	@Override
	protected void onStop() {
		try {
			eventManager.fire(new OnStopEvent(this));
		} finally {
			super.onStop();
		}
	}

	@Override
	protected void onDestroy() {
		try {
			eventManager.fire(new OnDestroyEvent<Activity>(this));
		} finally {
			try {
				RoboGuice.destroyInjector(this);
			} finally {
				super.onDestroy();
			}
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		final Configuration currentConfig = getResources().getConfiguration();
		super.onConfigurationChanged(newConfig);
		eventManager.fire(new OnConfigurationChangedEvent<Activity>(this,currentConfig, newConfig));
	}

	@Override
	public void onSupportContentChanged() {
		super.onSupportContentChanged();
		RoboGuice.getInjector(this).injectViewMembers(this);
		eventManager.fire(new OnContentChangedEvent(this));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		eventManager.fire(new OnActivityResultEvent(this, requestCode, resultCode, data));
	}

	@Override
	public Map<Key<?>, Object> getScopedObjectMap() {
		return scopedObjects;
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		if (shouldInjectOnCreateView(name))
			return injectOnCreateView(name, context, attrs);

		return super.onCreateView(name, context, attrs);
	}

	@Override
	public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
		if (shouldInjectOnCreateView(name))
			return injectOnCreateView(name, context, attrs);

		return super.onCreateView(parent, name, context, attrs);
	}

	protected static boolean shouldInjectOnCreateView(String name) {
		return false; // && Character.isLowerCase(name.charAt(0)) && !name.startsWith("com.android") && !name.equals("fragment");
	}

	protected static View injectOnCreateView(String name, Context context, AttributeSet attrs) {
		try {
			final Constructor<?> constructor = Class.forName(name).getConstructor(Context.class, AttributeSet.class);
			final View view = (View) constructor.newInstance(context, attrs);
			RoboGuice.getInjector(context).injectMembers(view);
			RoboGuice.getInjector(context).injectViewMembers(view);
			return view;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	public VBoxApplication getApp() {
	    return (VBoxApplication)getApplication();
	}
}
