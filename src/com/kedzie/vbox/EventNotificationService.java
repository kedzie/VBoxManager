package com.kedzie.vbox;

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

import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.jaxb.VBoxEventType;
import com.kedzie.vbox.machine.MachineFragmentActivity;
import com.kedzie.vbox.soap.VBoxSvc;

/**
 * Listen for VirtualBox events and publish notifications
 */
public class EventNotificationService extends Service {
	protected static final String TAG = EventNotificationService.class.getSimpleName();
	private static final int NOTIFICATION_ID=1;
	
	private VBoxSvc _vmgr;
	protected LocalBroadcastManager lbm;
	private BroadcastReceiver _receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "Recieved Broadcast: " + intent.getAction());
			if(intent.getAction().equals(VBoxEventType.ON_MACHINE_STATE_CHANGED.name())) {
				if(!Utils.getBooleanPreference(getApplicationContext(), PreferencesActivity.NOTIFICATIONS)) 
					return;
				new HandleEventTask().execute(intent);
			}
		}
	};
	
	/**
	 * Handle MachineStateChanged event
	 */
	class HandleEventTask extends AsyncTask<Intent, Void, Notification> {
		@Override
		protected Notification doInBackground(Intent... params) {
			IMachine eventMachine = BundleBuilder.getProxy(params[0], IMachine.BUNDLE, IMachine.class);
			Intent i = new Intent(EventNotificationService.this, MachineFragmentActivity.class).putExtra(VBoxSvc.BUNDLE, _vmgr);
			BundleBuilder.addProxy(i, IMachine.BUNDLE, eventMachine);
			return new Notification.Builder(EventNotificationService.this)
					.setContentTitle(String.format("%s is %s", eventMachine.getName(), eventMachine.getState()))
					.setContentText(String.format("Virtual Machine %s changed state to [%s]", eventMachine.getName(), eventMachine.getState()))
					.setWhen(System.currentTimeMillis())
					.setSmallIcon(R.drawable.ic_notif_vbox)
					.setLargeIcon(BitmapFactory.decodeResource(getResources(), ((VBoxApplication)getApplication()).getDrawable(eventMachine.getState())))
					.setContentIntent(PendingIntent.getActivity(EventNotificationService.this, 0, i, 0))
					.getNotification();
		}
		
		@Override
		protected void onPostExecute(Notification result)	{
			super.onPostExecute(result);
			if(result!=null)	
				((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, result);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		_vmgr = intent.getParcelableExtra(VBoxSvc.BUNDLE);
		lbm = LocalBroadcastManager.getInstance(getApplicationContext());
		lbm.registerReceiver(_receiver, new IntentFilter(VBoxEventType.ON_MACHINE_STATE_CHANGED.name()));
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		lbm.unregisterReceiver(_receiver);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
