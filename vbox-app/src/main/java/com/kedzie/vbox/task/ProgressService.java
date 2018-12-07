package com.kedzie.vbox.task;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.IVirtualBoxErrorInfo;
import com.kedzie.vbox.app.Utils;

import java.io.IOException;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * Created by kedzie on 3/1/14.
 */
public class ProgressService extends IntentService {
    private static final String TAG = "ProgressService";

    /** interval used to update progress bar for longing-running operation*/
    protected final static int PROGRESS_INTERVAL = 500;

    private static final String NOTIFICATION_CHANNEL = "vbox";

    public static final String INTENT_ICON = "icon";

    private IProgress mProgress;
    private int id;
    private int icon;
    @Inject
    NotificationManager mNotificationManager;

    public ProgressService() {
        super("VirtualBox Progress Monitor");
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidInjection.inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mProgress = intent.getParcelableExtra(IProgress.BUNDLE);
        icon = intent.getIntExtra(INTENT_ICON, icon);
        ++id;
        try {
            Log.d(TAG, "Handling progress");
            while(!mProgress.getCompleted()) {
                cacheProgress(mProgress);
                mNotificationManager.notify(id, new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
                        .setContentTitle(mProgress.getDescription())
                        .setContentText(getString(R.string.progress_notification_text, mProgress.getOperation(), mProgress.getOperationCount(), mProgress.getOperationDescription()))
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(icon)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), icon))
                        .setTicker(mProgress.getDescription())
                        .setProgress(100, mProgress.getPercent(), false)
                        .setAutoCancel(true)
                        .build());
                Utils.sleep(PROGRESS_INTERVAL);
            }
            Log.d(TAG, "Operation Completed. result code: " + mProgress.getResultCode());
            int result = mProgress.getResultCode();
            if(result==0) {
                mNotificationManager.notify(id, new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
                        .setContentTitle(mProgress.getDescription() + " Success")
                        .setContentText(getString(R.string.progress_notification_success, mProgress.getDescription()))
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(icon)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), icon))
                        .setTicker(mProgress.getDescription())
                        .setProgress(100, 100, false)
                        .setAutoCancel(true)
                        .build());
            } else {
                IVirtualBoxErrorInfo errorInfo = mProgress.getErrorInfo();
                mNotificationManager.notify(id, new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
                        .setContentTitle(mProgress.getDescription() + " Failed")
                        .setContentText(getString(R.string.progress_notification_failure, mProgress.getDescription(), errorInfo.getText()))
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(icon)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), icon))
                        .setTicker(mProgress.getDescription())
                        .setProgress(100, 100, false)
                        .setAutoCancel(true)
                        .build());
            }
        } catch (IOException e) {
            mNotificationManager.notify(id, new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
                    .setContentTitle(mProgress.getDescription() + " Failed")
                    .setContentText(getString(R.string.progress_notification_failure, mProgress.getDescription(), e.getMessage()))
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(icon)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), icon))
                    .setTicker(mProgress.getDescription())
                    .setProgress(100, 100, false)
                    .setAutoCancel(true)
                    .build());
        }
    }

    private void cacheProgress(IProgress p ) throws IOException {
        p.clearCacheNamed("getDescription", "getOperation", "getOperationDescription", "getOperationWeight", "getOperationPercent", "getTimeRemaining", "getPercent", "getCompleted");
        p.getDescription(); p.getOperation(); p.getOperationCount(); p.getOperationDescription();
        p.getPercent(); p.getOperationPercent(); p.getOperationWeight(); p.getTimeRemaining();
        p.getCompleted(); p.getCancelable();
    }
}
