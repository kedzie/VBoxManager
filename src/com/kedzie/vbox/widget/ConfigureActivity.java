package com.kedzie.vbox.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.server.Server;

public class ConfigureActivity extends Activity {
    static final String TAG = "ConfigureActivity";

    private static final String PREFS_NAME = "com.kedzie.vbox.widget";
    
    public static final String KEY_IDREF = "_idRef";
    public static final String KEY_NAME = "_name";
    public static final String KEY_SERVER = "_server";
    
    private int mAppWidgetId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        setContentView(R.layout.appwidget_configure);
        
        mAppWidgetId = getIntent().getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        if(mAppWidgetId==AppWidgetManager.INVALID_APPWIDGET_ID)
        	finish();
        
        findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                savePref(ConfigureActivity.this, mAppWidgetId, "init);
//                Provider.updateAppWidget(ConfigureActivity.this, AppWidgetManager.getInstance(ConfigureActivity.this), mAppWidgetId, titlePrefix);
                setResult(RESULT_OK, new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId));
                finish();
            }
        });
    }
    
    static SharedPreferences getPrefs(Context context) {
    	return context.getSharedPreferences(PREFS_NAME, 0);
    }

    static void savePref(Context context, int appWidgetId, String key, String value) {
        SharedPreferences.Editor prefs = getPrefs(context).edit();
        prefs.putString(appWidgetId+key, value);
        prefs.commit();
    }

    static String loadPref(Context context, int appWidgetId, String key) {
        return getPrefs(context).getString(appWidgetId + key, "");
    }

    static void deletePrefs(Context context, int appWidgetId) {
    	SharedPreferences.Editor prefs = getPrefs(context).edit();
    	prefs.remove(appWidgetId+KEY_NAME);
    	prefs.remove(appWidgetId+KEY_SERVER);
    	prefs.remove(appWidgetId+KEY_IDREF);
    	prefs.commit();
    }
    
    static void savePrefs(Context context, IMachine machine, Server server, int appWidgetId) {
        savePref(context, appWidgetId, KEY_IDREF, machine.getIdRef());
        savePref(context, appWidgetId, KEY_SERVER, server.getId().toString());
        savePref(context, appWidgetId, KEY_NAME, machine.getName());
        Provider.updateAppWidget(context, AppWidgetManager.getInstance(context), appWidgetId, machine);
    }
}



