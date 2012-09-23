package com.kedzie.vbox.widget;

import java.util.ArrayList;
import java.util.List;

import com.kedzie.vbox.api.jaxb.VBoxEventType;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WidgetBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("BroadcastReceiver", "intent=" + intent);

        if (intent.getAction().equals(VBoxEventType.ON_MACHINE_STATE_CHANGED.name())) {
            List<Integer> appWidgetIds = new ArrayList<Integer>();
            List<String> texts = new ArrayList<String>();
            ConfigureActivity.loadAllTitlePrefs(context, appWidgetIds, texts);

            AppWidgetManager gm = AppWidgetManager.getInstance(context);
            final int N = appWidgetIds.size();
            for (int i=0; i<N; i++)
                Provider.updateAppWidget(context, gm, appWidgetIds.get(i), texts.get(i)+"statechange");
        }
    }

}
