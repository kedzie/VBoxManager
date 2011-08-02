import org.virtualbox_4_1.VBoxEventType;

import com.kedzie.vbox.api.IVirtualBox;
import com.kedzie.vbox.api.WebSessionManager;
import com.kedzie.vbox.event.IEvent;
import com.kedzie.vbox.event.IEventListener;
import com.kedzie.vbox.event.IEventSource;



public class TestKSoap {

	public static void main(String[] args) throws Exception {
		WebSessionManager vmgr = new WebSessionManager();
		vmgr.logon("http://localhost:18083", "test", "test");
		IVirtualBox vbox = vmgr.getVBox();
//		List<IMachine> machines =  vbox.getMachines();
//		IMachine m = machines.get(2);
//		ISession session = vmgr.getSession();
//		m.lockMachine(session,LockType.Shared);
//		IConsole console = session.getConsole();
//		IEventSource evSource = console.getEventSource();
		IEventSource evSource = vbox.getEventSource();
		IEventListener listener = evSource.createListener();
		evSource.registerListener(listener, new VBoxEventType [] {VBoxEventType.OnMachineStateChanged }, false);
		for(int i=0; i<10; i++) {
			IEvent event = evSource.getEvent(listener, 2000);
			if(event!=null) {
				if(event.getType().equals(VBoxEventType.OnMachineStateChanged)) {
					
				}
				System.out.println("Event: "+event.getType());
				evSource.eventProcessed(listener, event);
			}
		}
		evSource.unregisterListener(listener);
//		session.unlockMachine();
		vmgr.getVBox().logoff();
	}
	
}
