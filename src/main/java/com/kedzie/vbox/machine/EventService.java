package com.kedzie.vbox.machine;

import org.virtualbox_4_1.VBoxEventType;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import com.kedzie.vbox.VBoxApplication.BundleBuilder;
import com.kedzie.vbox.VBoxSvc;
import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.api.IEventListener;
import com.kedzie.vbox.api.IEventSource;
import com.kedzie.vbox.api.IMachineEvent;

public class EventService extends Service{
	protected static final String TAG = "vbox."+ EventService.class.getSimpleName();

	private Handler _handler;
	private HandlerThread _ht= new HandlerThread("Event Handler", HandlerThread.MIN_PRIORITY);
	private boolean _running=true;
	IEventSource evSource = null;
	IEventListener listener = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate");
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "onBind");
		_ht.start();
		Handler h = new Handler(_ht.getLooper()) {
			@Override public void handleMessage(Message msg) {
				VBoxSvc _vmgr = msg.getData().getParcelable("vmgr");
				Messenger messenger = msg.getData().getParcelable("listener"); 
				evSource = _vmgr.getVBox().getEventSource();
				listener = evSource.createListener();
				IEvent event = null;
				try {
					evSource.registerListener(listener, new VBoxEventType [] { VBoxEventType.MachineEvent }, false);
					while(_running) {
							if((event=_vmgr.getEventProxy(evSource.getEvent(listener, 0)))!=null) {
								BundleBuilder b = new BundleBuilder().putString("evt", event.getIdRef());
								if(event instanceof IMachineEvent) b.putString("machine",  _vmgr.getVBox().findMachine(((IMachineEvent)event).getMachineId()).getIdRef());
								Message m = obtainMessage(EventThread.WHAT_EVENT);
								m.what = EventThread.WHAT_EVENT;
								m.setData(b.create());
								synchronized(EventService.class) {
									if(messenger!=null && _running) messenger.send(m);
								}
								evSource.eventProcessed(listener, event); 
							} else
								Thread.sleep(500);
					}
					if(listener!=null && evSource!=null)  
						evSource.unregisterListener(listener);
				} catch (Throwable e) {
				} 
			}
		};
		new BundleBuilder().putAll(intent.getExtras()).sendMessage(h, 0);
		return new LocalBinder();
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.i(TAG, "onUnbind");
		_running=false;
		return super.onUnbind(intent);
	}
	
//	public void setHandler(Handler h) {
//		synchronized (EventService.class) {
//			this._handler = h;
//		}
//	}
	
	public  class LocalBinder extends Binder {
		public EventService getLocalBinder() { return EventService.this; }
	}
}
