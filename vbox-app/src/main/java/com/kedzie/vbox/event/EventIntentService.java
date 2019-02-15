package com.kedzie.vbox.event;

import android.app.*;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcelable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.api.IEventListener;
import com.kedzie.vbox.api.IEventSource;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IMachineEvent;
import com.kedzie.vbox.api.jaxb.VBoxEventType;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.LoopingThread;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.machine.MachineListActivity;
import com.kedzie.vbox.soap.VBoxSvc;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import timber.log.Timber;

/**
 * Polls VirtualBox for events and publishes them in local broadcasts.
 */
public class EventIntentService extends Service {
	private static final String TAG = EventIntentService.class.getSimpleName();

	public static final String NOTIFICATION_CHANNEL = "vbox";

	private static final int NOTIFICATION_ID = 749;
	private static final int DEFAULT_INTERVAL = 500;

	public static final String BUNDLE_EVENT = "evt";
	public static final String INTENT_INTERVAL="interval";

	VBoxSvc _vmgr;
	int _interval;
	@Inject
	LocalBroadcastManager _lbm;

    private EventThread eventThread;

    @Inject
    NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidInjection.inject(this);
        if(Utils.isVersion(Build.VERSION_CODES.O)) {
            if(mNotificationManager.getNotificationChannel(NOTIFICATION_CHANNEL)==null) {
                mNotificationManager.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL, "VboxManager", NotificationManager.IMPORTANCE_DEFAULT));
            }
        }
    }

    NotificationCompat.Builder getNotifactionBuilder() {
        return Utils.isVersion(Build.VERSION_CODES.O) ?
                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL) :
                new NotificationCompat.Builder(this);
    }
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            _vmgr = BundleBuilder.getVBoxSvc(intent);
            _interval = intent.getIntExtra(INTENT_INTERVAL, DEFAULT_INTERVAL);
            String title = getResources().getString(R.string.event_handler_notification_title);
            String content = getResources().getString(R.string.event_handler_notification_content, _vmgr.getServer().toString());

            Timber.i("Starting foreground event service");
            startForeground(NOTIFICATION_ID, getNotifactionBuilder().setContentTitle(title)
                    .setContentText(content)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_notif_vbox)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                    .setTicker(title)
                    .setAutoCancel(false).build());
            eventThread = new EventThread();
            eventThread.start();
        }
        return START_STICKY;
    }
	
	@Override
	public void onDestroy() {
        Timber.d("Event Service being destroyed");
        eventThread.quit();
		stopForeground(true);
		super.onDestroy();
	}

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class EventThread extends LoopingThread {

        private IEvent mEvent;
        private IEventSource mSource;
        private IEventListener mListener;

        public EventThread() {
            super("VirtualBox Event Handler");
        }

        @Override
        public void preExecute() {
            mSource =  _vmgr.getVBox().getEventSource();
            mListener = mSource.createListener();
            mSource.registerListener(mListener, new VBoxEventType[]{VBoxEventType.ANY}, false);
        }

        @Override
        public void loop() {
            try {
                if((mEvent = mSource.getEvent(mListener, 0))!=null) {
                    BundleBuilder bundle = new BundleBuilder().putProxy(BUNDLE_EVENT, mEvent);
                    if(mEvent instanceof IMachineEvent)
                        bundle.putProxy(IMachine.BUNDLE,  _vmgr.getVBox().findMachine(((IMachineEvent) mEvent).getMachineId()));
                    _lbm.sendBroadcast(new Intent(mEvent.getType().name()).putExtras(bundle.create()));
                    mSource.eventProcessed(mListener, mEvent);
                } else if(_running)
                    Thread.sleep(_interval);
            } catch (Throwable e) {
                Log.e(TAG, "Error", e);
                _running = false;
                stopSelf();
            }
        }

        @Override
        public void postExecute() {
            try {
                if(mSource !=null)
                    mSource.unregisterListener(mListener);
            } catch (Exception e) {
                Log.e(TAG, "Error unregistering mListener", e);
            }
        }
    }
}
