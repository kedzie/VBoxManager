package com.kedzie.vbox.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.jaxb.VBoxEventType;
import com.kedzie.vbox.server.Server;

public class Provider extends AppWidgetProvider {
    private static final String TAG = "ExampleAppWidgetProvider";
    private static final String PREFS_NAME = "com.kedzie.vbox.widget";
    public static final String KEY_IDREF = "_idRef";
    public static final String KEY_NAME = "_name";
    public static final String KEY_SERVER = "_server";
    
    static void savePref(Context context, int appWidgetId, String key, String value) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(appWidgetId+key, value);
        prefs.commit();
    }

    static void savePrefs(Context context, IMachine machine, Server server, int appWidgetId) {
        savePref(context, appWidgetId, KEY_IDREF, machine.getIdRef());
        savePref(context, appWidgetId, KEY_SERVER, server.getId().toString());
        savePref(context, appWidgetId, KEY_NAME, machine.getName());
        Provider.updateAppWidget(context, appWidgetId, machine);
        AlarmManager am =  (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC, 10000, 10000, getPendingIntent(context, appWidgetId));
       
    }
    
    private static PendingIntent getPendingIntent(Context context, int appWidgetId) {
       return PendingIntent.getService(context, 0, 
                new Intent(context, UpdateWidgetService.class) 
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] { appWidgetId } ), 
                0);
    }
    
    static String loadPref(Context context, int appWidgetId, String key) {
        return context.getSharedPreferences(PREFS_NAME, 0).getString(appWidgetId + key, "");
    }

    static void deletePrefs(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(appWidgetId+KEY_NAME);
        prefs.remove(appWidgetId+KEY_SERVER);
        prefs.remove(appWidgetId+KEY_IDREF);
        prefs.commit();
        AlarmManager am =  (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(getPendingIntent(context, appWidgetId));
    }
    
    static void updateAppWidget(Context context, int appWidgetId, IMachine vm) {
        Log.i(TAG, "updateAppWidget appWidgetId=" + appWidgetId + " vm=" + vm.getName());
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_provider);
        views.setTextViewText(R.id.appwidget_text, vm.getName()+" - " + vm.getState());
        views.setOnClickPendingIntent(R.id.appwidget_text, getPendingIntent(context, appWidgetId));
        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, views);
    }
    
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i(TAG, "onUpdate");
        context.startService(new Intent(context, UpdateWidgetService.class).putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds));
    }
    
    @Override
    public void onEnabled(Context context) {
        Log.i(TAG, "onEnabled");
        LocalBroadcastManager.getInstance(context).registerReceiver(this, 
                new IntentFilter(VBoxEventType.ON_MACHINE_STATE_CHANGED.name()));
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
            deletePrefs(context, appWidgetIds[i]);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(VBoxEventType.ON_MACHINE_STATE_CHANGED.name())) {
            Log.i(TAG, "Received update broadcast!");
        } 
        super.onReceive(context, intent);
    }
}


