package com.kedzie.vbox.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.jaxb.VBoxEventType;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.machine.MachineFragmentActivity;
import com.kedzie.vbox.machine.PreferencesActivity;
import com.kedzie.vbox.server.Server;
import com.kedzie.vbox.soap.VBoxSvc;

public class Provider extends AppWidgetProvider {
    /**  Widget update interval */
    private static final int UPDATE_INTERVAL = 10000;
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
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(appWidgetId+KEY_IDREF, machine.getIdRef());
        prefs.putString(appWidgetId+KEY_SERVER, server.getId().toString());
        prefs.putString(appWidgetId+KEY_NAME, machine.getName());
        prefs.commit();
        Provider.updateAppWidget(context, appWidgetId, machine);
    }
    
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(appWidgetIds[0]+KEY_NAME);
        prefs.remove(appWidgetIds[0]+KEY_SERVER);
        prefs.remove(appWidgetIds[0]+KEY_IDREF);
        prefs.commit();
    }
    
    static String loadPref(Context context, int appWidgetId, String key) {
        return context.getSharedPreferences(PREFS_NAME, 0).getString(appWidgetId + key, "");
    }

    static void updateAppWidget(Context context, int appWidgetId, IMachine vm) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_provider);
        
        views.setImageViewResource(R.id.machine_list_item_ostype, VBoxApplication.getInstance().getOSDrawable(vm.getOSTypeId()));
        views.setTextViewText(R.id.machine_list_item_name, vm.getName());
        views.setTextViewText(R.id.machine_list_item_state_text, vm.getState().toString());
        views.setImageViewResource(R.id.machine_list_item_state, VBoxApplication.getInstance().getDrawable(vm.getState()));
        String snapshotText = vm.getCurrentSnapshot()!=null ?
                            String.format("(%1$s)%2$s", vm.getCurrentSnapshot().getName(), vm.getCurrentStateModified() ? "*" : "") : "";
        views.setTextViewText(R.id.machine_list_item_snapshot, snapshotText);
                            
//        views.setOnClickPendingIntent(R.id.machine_view, PendingIntent.getService(context, 0, 
//                new Intent(context, UpdateWidgetService.class) .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] { appWidgetId } ), 0));
        views.setOnClickPendingIntent(R.id.machine_view, PendingIntent.getService(context, 0, 
                new Intent(context, MachineFragmentActivity.class) .putExtra(IMachine.BUNDLE, vm).putExtra(VBoxSvc.BUNDLE, vm.getVBoxAPI()), 0));
        
        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, views);
    }
    
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {}
    
    @Override
    public void onEnabled(Context context) {
        Log.i(TAG, "onEnabled");
        PreferenceManager.getDefaultSharedPreferences(context).registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.i(TAG, "Shared Preferences Changed");
            }
        });
        getAlarmManager(context).setRepeating(AlarmManager.RTC, UPDATE_INTERVAL, UPDATE_INTERVAL, 
                PendingIntent.getBroadcast(context, 0, new Intent(context, Provider.class), 0));
        LocalBroadcastManager.getInstance(context).registerReceiver(this, 
                new IntentFilter(VBoxEventType.ON_MACHINE_STATE_CHANGED.name()));
    }

    @Override
    public void onDisabled(Context context) {
        Log.i(TAG, "onDisabled");
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this); 
        getAlarmManager(context).cancel(PendingIntent.getBroadcast(context, 0, new Intent(context, Provider.class), 0));
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction()==null || intent.getAction().equals(VBoxEventType.ON_MACHINE_STATE_CHANGED)) {
            context.startService(new Intent(context, UpdateWidgetService.class)
                    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, getAppWidgetIds(context)));
            Log.i(TAG, "Shared Prefs: " + Utils.getBooleanPreference(context, PreferencesActivity.NOTIFICATIONS));
        } else
            super.onReceive(context, intent);
    }
    
    private static int[] getAppWidgetIds(Context context) {
        return AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, Provider.class));
    }
    
    private static AlarmManager getAlarmManager(Context context) {
        return (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    }
}