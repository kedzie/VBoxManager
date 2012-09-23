package com.kedzie.vbox.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.jaxb.VBoxEventType;

public class Provider extends AppWidgetProvider {
    private static final String TAG = "ExampleAppWidgetProvider";
    static WidgetBroadcastReceiver _receiver;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, String titlePrefix) {
        Log.i(TAG, "updateAppWidget appWidgetId=" + appWidgetId + " titlePrefix=" + titlePrefix);
        CharSequence text = context.getString(R.string.appwidget_text_format,
        		titlePrefix,
                "0x" + Long.toHexString(SystemClock.elapsedRealtime()));

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_provider);
        views.setTextViewText(R.id.appwidget_text, text);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i(TAG, "onUpdate");
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            String titlePrefix = ConfigureActivity.loadTitlePref(context, appWidgetIds[i]);
            updateAppWidget(context, appWidgetManager, appWidgetIds[i], titlePrefix);
        }
    }
    
    @Override
    public void onEnabled(Context context) {
        Log.i(TAG, "onEnabled");
        _receiver = new WidgetBroadcastReceiver();
        LocalBroadcastManager.getInstance(context).registerReceiver(
        		_receiver, new IntentFilter(VBoxEventType.ON_MACHINE_STATE_CHANGED.name()));
//        context.getPackageManager().setComponentEnabledSetting(
//                new ComponentName("com.example.android.apis", ".widget.Receiver"),
//                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                PackageManager.DONT_KILL_APP);
    }

    @Override
    public void onDisabled(Context context) {
        Log.i(TAG, "onDisabled");
        LocalBroadcastManager.getInstance(context).unregisterReceiver(_receiver);
//        context.getPackageManager().setComponentEnabledSetting(
//                new ComponentName("com.example.android.apis", ".widget.Receiver"),
//                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                PackageManager.DONT_KILL_APP);
    }
    
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.i(TAG, "onDeleted");
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) 
            ConfigureActivity.deleteTitlePref(context, appWidgetIds[i]);
    }
}


