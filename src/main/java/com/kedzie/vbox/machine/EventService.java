package com.kedzie.vbox.machine;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import com.kedzie.vbox.VBoxApplication.BundleBuilder;
import com.kedzie.vbox.VBoxSvc;
import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.api.IEventListener;
import com.kedzie.vbox.api.IEventSource;
import com.kedzie.vbox.api.IMachineEvent;
import com.kedzie.vbox.api.jaxb.VBoxEventType;

public class EventService extends Service{
	protected static final String TAG = "vbox."+ EventService.class.getSimpleName();
	public static final int WHAT_EVENT = 1;
	private static final int INTERVAL = 500;
	private HandlerThread _ht= new HandlerThread(TAG, HandlerThread.MIN_PRIORITY);
	private boolean _running=true;
	private Messenger _messenger;
	private VBoxSvc _vmgr;
	
	@Override
	public IBinder onBind(Intent intent) {
		_messenger = intent.getParcelableExtra("listener"); 
		_vmgr = intent.getParcelableExtra("vmgr");
		_ht.start();
		Handler h = new Handler(_ht.getLooper()) {
			@Override 
			public void handleMessage(Message msg) {
				IEventSource evSource = _vmgr.getVBox().getEventSource();
				IEventListener listener = evSource.createListener();
				IEvent event = null;
				try {
					evSource.registerListener(listener, new VBoxEventType [] { VBoxEventType.MACHINE_EVENT }, false);
					while(_running) {
							if((event=evSource.getEvent(listener, 0))!=null) {
								BundleBuilder b = new BundleBuilder().putString("evt", event.getIdRef());
								if(event instanceof IMachineEvent) b.putString("machine",  _vmgr.getVBox().findMachine(((IMachineEvent)event).getMachineId()).getIdRef());
								synchronized(EventService.class) {
									if(_messenger!=null && _running)  
										b.sendMessage(_messenger, WHAT_EVENT);
								}
								evSource.eventProcessed(listener, event); 
							} else
								Thread.sleep(INTERVAL);
					}
				} catch (Throwable e) {} 
				finally {
					try {
						if(listener!=null && evSource!=null)  evSource.unregisterListener(listener);
					} catch (Throwable e) {}
				}
			}
		};
		h.sendEmptyMessage(0);
		return new LocalBinder();
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		_running=false;
		_ht.quit();
		return super.onUnbind(intent);
	}
	
	public synchronized void setMessenger(Messenger m) {
		_messenger = m;
	}
	
	public  class LocalBinder extends Binder {
		public EventService getLocalBinder() { return EventService.this; }
	}
}
