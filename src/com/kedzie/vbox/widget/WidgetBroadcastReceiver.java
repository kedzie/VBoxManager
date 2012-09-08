package com.kedzie.vbox.widget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

/**
 * A BroadcastReceiver that listens for updates for the ExampleAppWidgetProvider.  This
 * BroadcastReceiver starts off disabled, and we only enable it when there is a widget
 * instance created, in order to only receive notifications when we need them.
 */
public class WidgetBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BroadcastReceiver", "intent=" + intent);

        String action = intent.getAction();
        if (action.equals(Intent.ACTION_TIMEZONE_CHANGED) || action.equals(Intent.ACTION_TIME_CHANGED)) {
            AppWidgetManager gm = AppWidgetManager.getInstance(context);
            ArrayList<Integer> appWidgetIds = new ArrayList<Integer>();
            ArrayList<String> texts = new ArrayList<String>();
            ConfigureActivity.loadAllTitlePrefs(context, appWidgetIds, texts);

            final int N = appWidgetIds.size();
            for (int i=0; i<N; i++)
                Provider.updateAppWidget(context, gm, appWidgetIds.get(i), texts.get(i));
        }
    }

}
