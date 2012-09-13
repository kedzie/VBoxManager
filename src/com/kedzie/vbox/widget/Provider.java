package com.kedzie.vbox.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

import com.kedzie.vbox.R;

public class Provider extends AppWidgetProvider {
    private static final String TAG = "ExampleAppWidgetProvider";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate");
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            String titlePrefix = ConfigureActivity.loadTitlePref(context, appWidgetIds[i]);
            updateAppWidget(context, appWidgetManager, appWidgetIds[i], titlePrefix);
        }
    }
    
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.d(TAG, "onDeleted");
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) 
            ConfigureActivity.deleteTitlePref(context, appWidgetIds[i]);
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled");
        context.getPackageManager().setComponentEnabledSetting(
                new ComponentName("com.example.android.apis", ".widget.Receiver"),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(TAG, "onDisabled");
        context.getPackageManager().setComponentEnabledSetting(
                new ComponentName("com.example.android.apis", ".widget.Receiver"),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, String titlePrefix) {
        Log.d(TAG, "updateAppWidget appWidgetId=" + appWidgetId + " titlePrefix=" + titlePrefix);
        CharSequence text = context.getString(R.string.appwidget_text_format,
        		ConfigureActivity.loadTitlePref(context, appWidgetId),
                "0x" + Long.toHexString(SystemClock.elapsedRealtime()));

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_provider);
        views.setTextViewText(R.id.appwidget_text, text);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}


