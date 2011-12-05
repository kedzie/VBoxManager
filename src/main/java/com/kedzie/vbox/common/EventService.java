package com.kedzie.vbox.common;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

import com.kedzie.vbox.VBoxSvc;

/**
 * Listen for VirtualBox events and publish notifications
 */
public class EventService extends Service {
	protected static final String TAG = EventService.class.getSimpleName();
	
	private EventThread _eventThread;
	
	@Override
	public IBinder onBind(Intent intent) {
		_eventThread = new EventThread((VBoxSvc)intent.getParcelableExtra("vmgr"));
		_eventThread.addListener((Messenger)intent.getParcelableExtra("listener"));
		_eventThread.start();
		return new LocalBinder();
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.i(TAG, "Destroying Event Service");
		boolean done = false;
        _eventThread._running= false;
        while (!done) {
            try {
                _eventThread.join();
                done = true;
            } catch (InterruptedException e) { }
        }
		return super.onUnbind(intent);
	}
	
	public  void setMessenger(Messenger m) {
		Log.i(TAG, "Setting event listener: " + m);
		_eventThread.addListener(m);
	}
	
	public  class LocalBinder extends Binder {
		public EventService getLocalBinder() { return EventService.this; }
	}
}
