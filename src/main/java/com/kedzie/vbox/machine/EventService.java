package com.kedzie.vbox.machine;

import java.util.ArrayList;
import java.util.List;
import com.kedzie.vbox.VBoxApplication.BundleBuilder;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class EventService extends Service{
	protected static final String TAG = "vbox."+ EventService.class.getSimpleName();

	private List<Messenger> listeners = new ArrayList<Messenger>();
	
	Messenger m = new Messenger(new Handler() {
		public void handleMessage(Message msg) {
			Log.i(TAG, "Got Message");
			listeners.add(msg.replyTo);
			Message m = new Message();
			msg.what = EventThread.WHAT_ERROR;
			msg.setData(new BundleBuilder().putString("exception", "Back from service").create());
			try {
				msg.replyTo.send(m);
			} catch (RemoteException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
	});
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
//		HandlerThread thread = new HandlerThread("vbox", HandlerThread.NORM_PRIORITY);
//		thread.start();
		
//		Handler h = new Handler(thread.getLooper()) {
//			@Override
//			public void handleMessage(Message msg) {
//				try {
//					VBoxSvc _vmgr = msg.getData().getParcelable("vmgr");
//					IMachine _machine = _vmgr.getProxy(IMachine.class, msg.getData().getString("machine"));
//					if(_vmgr.getVBox().getSessionObject().getState().equals(SessionState.Unlocked))
//						_machine.lockMachine(_vmgr.getVBox().getSessionObject(),LockType.Shared);
//					IConsole console = _vmgr.getVBox().getSessionObject().getConsole();
//					IEventSource evSource = console.getEventSource();
//					IEventListener listener = evSource.createListener();
//					evSource.registerListener(listener, new VBoxEventType [] { VBoxEventType.Any }, true);
//					for(int i=0; i<10; i++) {
//						IEvent event = evSource.getEvent(listener, 2000);
//						Log.i(TAG, "EVent: " + event);
//						Toast.makeText(getApplicationContext(), "Event: "+event, Toast.LENGTH_LONG).show();
//						Thread.sleep(2000);
//					}
//					evSource.unregisterListener(listener);
//				} catch (Exception e) {
//					Log.e(TAG, "", e);
//				}
//				Toast.makeText(getApplicationContext(), "Service is finished", Toast.LENGTH_LONG).show();
//				stopSelf();
//			}
//		};
//		Message msg = h.obtainMessage();
//		msg.setData(intent.getExtras());
//		h.sendMessage(msg);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return this.m.getBinder();
	}

}
