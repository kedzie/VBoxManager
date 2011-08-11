package com.kedzie.vbox.machine;

import org.virtualbox_4_1.VBoxEventType;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import com.kedzie.vbox.VBoxApplication.BundleBuilder;
import com.kedzie.vbox.VBoxSvc;
import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.api.IEventListener;
import com.kedzie.vbox.api.IEventSource;
import com.kedzie.vbox.api.IMachineEvent;

public class EventThread extends Thread {
	public static final int WHAT_EVENT = 1, WHAT_ERROR = 2;
	private boolean _running=true, _stopped=false;
	private VBoxSvc _vmgr;
	private Handler _h;
	private Messenger _messenger;
	
	public EventThread(VBoxSvc vmgr) {
		super("VBox EventHandler");
		_vmgr=new VBoxSvc(vmgr);
	}
	
	public EventThread(Handler h, VBoxSvc vmgr) {
		this(vmgr);
		_h=h;
	}
	
	public EventThread(Messenger m, VBoxSvc vmgr) {
		this(vmgr);
		_messenger=m;
	}

	@Override
	public void run() {
		IEvent event = null;
		IEventSource evSource = _vmgr.getVBox().getEventSource();
		IEventListener listener = evSource.createListener();
		try {
			evSource.registerListener(listener, new VBoxEventType [] { VBoxEventType.MachineEvent }, false);
			while(_running) {
				if((event=evSource.getEvent(listener, 0))!=null) {
					event = _vmgr.getEventProxy(event.getIdRef());
					BundleBuilder b = new BundleBuilder().putString("evt", event.getIdRef());
					if(event instanceof IMachineEvent) b.putString("machine",  _vmgr.getVBox().findMachine(((IMachineEvent)event).getMachineId()).getIdRef());
					Message m = new Message();
					m.what = EventThread.WHAT_EVENT;
					m.setData(b.create());
					synchronized(EventService.class) {
						if(_messenger!=null) 
							_messenger.send(m);
						if(_h!=null)	{
							m.setTarget(_h);
							_h.sendMessage(m);
						}
					}
					evSource.eventProcessed(listener, event); 
				} else
					Thread.sleep(500);
			}
		} catch (Throwable e) {
			new BundleBuilder().putString("exception", e.getMessage()).sendMessage(_h, WHAT_ERROR);
		} finally {
			_stopped=true;
			if(listener!=null && evSource!=null) evSource.unregisterListener(listener);
		}
	}
	
	public void postStop() { _running=false;	}
	public boolean isStopped() { return _stopped; }
	public boolean isRunning() { return _running; }
}
