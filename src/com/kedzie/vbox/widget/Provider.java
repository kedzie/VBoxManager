package com.kedzie.vbox.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;

public class Provider extends AppWidgetProvider {
    private static final String TAG = "ExampleAppWidgetProvider";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, IMachine vm) {
        Log.i(TAG, "updateAppWidget appWidgetId=" + appWidgetId + " vm=" + vm.getName());
        
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_provider);
        views.setTextViewText(R.id.appwidget_text, vm.getName());

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i(TAG, "onUpdate");
        context.startService(new Intent(context, UpdateWidgetService.class).putExtra(UpdateWidgetService.INTENT_WIDGET_IDS, appWidgetIds));
    }
    
    @Override
    public void onEnabled(Context context) {
        Log.i(TAG, "onEnabled");
    }

    @Override
    public void onDisabled(Context context) {
        Log.i(TAG, "onDisabled");
    }
    
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.i(TAG, "onDeleted");
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) 
            ConfigureActivity.deletePrefs(context, appWidgetIds[i]);
    }
}


