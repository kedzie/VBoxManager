package com.kedzie.vbox.widget;

import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Update widgets
 * @author Marek Kędzierski
 */
public class UpdateWidgetService extends IntentService {
	private static final String TAG = "UpdateWidgetService";
	public final static String INTENT_WIDGET_IDS = "widgetIds";
	
	public UpdateWidgetService() {
		super("Update Widget Service");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		List<Integer> widgetIds = intent.getIntegerArrayListExtra(INTENT_WIDGET_IDS);
		for(int widgetId : widgetIds) {
			Log.i(TAG, "Updating App Widget: " + widgetId);
			//if logged on
			
		}
	}

}
