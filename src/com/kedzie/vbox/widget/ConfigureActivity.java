package com.kedzie.vbox.widget;

import java.util.List;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.kedzie.vbox.R;


public class ConfigureActivity extends Activity {
    static final String TAG = "ConfigureActivity";

    private static final String PREFS_NAME = "com.kedzie.vbox.widget.AppWidgetProvider";
    private static final String PREF_PREFIX_KEY = "prefix_";

    int mAppWidgetId;
    private EditText mAppWidgetPrefix;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setResult(RESULT_CANCELED);
        setContentView(R.layout.appwidget_configure);
        mAppWidgetPrefix = (EditText)findViewById(R.id.appwidget_prefix);
        
        mAppWidgetId = getIntent().getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        
        if(mAppWidgetId==AppWidgetManager.INVALID_APPWIDGET_ID)
        	finish();
        
        mAppWidgetPrefix.setText(loadTitlePref(ConfigureActivity.this, mAppWidgetId));
        
        findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String titlePrefix = mAppWidgetPrefix.getText().toString();
                saveTitlePref(ConfigureActivity.this, mAppWidgetId, titlePrefix);

                Provider.updateAppWidget(ConfigureActivity.this, AppWidgetManager.getInstance(ConfigureActivity.this), mAppWidgetId, titlePrefix);

                setResult(RESULT_OK, new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId));
                finish();
            }
        });
    }

    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.commit();
    }

    static String loadTitlePref(Context context, int appWidgetId) {
        String prefix = context.getSharedPreferences(PREFS_NAME, 0).getString(PREF_PREFIX_KEY + appWidgetId, null);
        return prefix!=null ? prefix : "prefix";
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
    }

    static void loadAllTitlePrefs(Context context, List<Integer> appWidgetIds, List<String> texts) {
    }
}



