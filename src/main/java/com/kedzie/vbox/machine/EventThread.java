package com.kedzie.vbox.machine;

import org.virtualbox_4_1.VBoxEventType;

import android.os.Handler;
import android.util.Log;

import com.kedzie.vbox.VBoxApplication.BundleBuilder;
import com.kedzie.vbox.WebSessionManager;
import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.api.IEventListener;
import com.kedzie.vbox.api.IEventSource;

public class EventThread extends Thread {
	private static final String TAG = "vbox.EventThread";
	public static final int WHAT_EVENT = 1;
	public static final int WHAT_ERROR = 2;
	private boolean _running=false;
	private WebSessionManager _vmgr;
	private Handler _h;
	
	public EventThread(Handler h, WebSessionManager vmgr) {
		_h=h;
		_vmgr=vmgr;
	}

	@Override
	public void run() {
		_running=true;
		try {
			IEventSource evSource = _vmgr.getVBox().getEventSource();
			IEventListener listener = evSource.createListener();
			evSource.registerListener(listener, new VBoxEventType [] { VBoxEventType.Any, VBoxEventType.MachineEvent }, false);
			while(_running) {
					IEvent event = evSource.getEvent(listener, 1000);
					if(event==null) continue;
					new BundleBuilder().putString("evt", event.getId()).sendMessage(_h, WHAT_EVENT);
					evSource.eventProcessed(listener, event);
			}
			evSource.unregisterListener(listener);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			new BundleBuilder().putString("exception", e.getMessage()).sendMessage(_h, WHAT_ERROR);
		}
	}
	
	public void postStop() {
		_running=false;
	}
}
