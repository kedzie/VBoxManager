package com.kedzie.vbox.widget;

import java.util.ArrayList;
import java.util.List;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WidgetBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BroadcastReceiver", "intent=" + intent);

        if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED) || intent.getAction().equals(Intent.ACTION_TIME_CHANGED)) {
            List<Integer> appWidgetIds = new ArrayList<Integer>();
            List<String> texts = new ArrayList<String>();
            ConfigureActivity.loadAllTitlePrefs(context, appWidgetIds, texts);

            AppWidgetManager gm = AppWidgetManager.getInstance(context);
            final int N = appWidgetIds.size();
            for (int i=0; i<N; i++)
                Provider.updateAppWidget(context, gm, appWidgetIds.get(i), texts.get(i));
        }
    }

}
