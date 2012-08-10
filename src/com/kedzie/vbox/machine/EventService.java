package com.kedzie.vbox.machine;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.kedzie.vbox.BundleBuilder;
import com.kedzie.vbox.PreferencesActivity;
import com.kedzie.vbox.R;
import com.kedzie.vbox.Utils;
import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IMachineStateChangedEvent;
import com.kedzie.vbox.soap.VBoxSvc;

/**
 * Listen for VirtualBox events and publish notifications
 */
public class EventService extends Service {
	protected static final String TAG = EventService.class.getSimpleName();
	private static final int NOTIFICATION_ID=1;
	
	private VBoxSvc _vmgr;
	protected LocalBroadcastManager lbm;
	private BroadcastReceiver _receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(EventIntentService.com_virtualbox_EVENT)) {
				Log.i(TAG, "Recieved Broadcast");
				if(!Utils.getBooleanPreference(getApplicationContext(), PreferencesActivity.NOTIFICATIONS)) 
					return;
				new HandleEventTask().execute(intent);
			}
		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		_vmgr = intent.getParcelableExtra(VBoxSvc.BUNDLE);
		lbm = LocalBroadcastManager.getInstance(getApplicationContext());
		lbm.registerReceiver(_receiver, new IntentFilter(EventIntentService.com_virtualbox_EVENT));
		Utils.toast(this, "Starting Event Notifier Service");
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		lbm.unregisterReceiver(_receiver);
		Utils.toast(this, "Shutting down VirtualBox Event Notifier Service");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	/**
	 * Handle MachineStateChanged event
	 */
	class HandleEventTask extends AsyncTask<Intent, Void, Notification> {
		
		@Override
		protected Notification doInBackground(Intent... params) {
			IEvent event = BundleBuilder.getProxy(params[0], EventIntentService.BUNDLE_EVENT, IEvent.class);
			if(event instanceof IMachineStateChangedEvent) {
				IMachine eventMachine = BundleBuilder.getProxy(params[0], IMachine.BUNDLE, IMachine.class);
				Intent i = new Intent(EventService.this, MachineFragmentActivity.class).putExtra("vmgr", _vmgr);
				BundleBuilder.addProxy(i, IMachine.BUNDLE, eventMachine);
				return new Notification.Builder(EventService.this)
					.setContentTitle("VirtualBox event: " + event.getType())
					.setContentText(eventMachine.getName() + " changed State:  "  + eventMachine.getState())
					.setWhen(System.currentTimeMillis())
					.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_list_vbox))
					.setContentIntent(PendingIntent.getActivity(EventService.this, 0, i, 0))
					.getNotification();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Notification result)	{
			super.onPostExecute(result);
			if(result!=null)	{
				((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, result);
			}
		}
	}
}
