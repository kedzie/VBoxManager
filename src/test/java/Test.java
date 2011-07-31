import java.util.ArrayList;
import java.util.List;

import org.virtualbox_4_1.IConsole;
import org.virtualbox_4_1.IEvent;
import org.virtualbox_4_1.IEventListener;
import org.virtualbox_4_1.IEventSource;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.VBoxEventType;
import org.virtualbox_4_1.VirtualBoxManager;


public class Test {

	public static void main(String[] args) {
		VirtualBoxManager mgr = VirtualBoxManager.createInstance(null);
		mgr.connect("http://localhost:18083", "test", "test");
		List<IMachine> machines = mgr.getVBox().getMachines();
		IMachine m = machines.get(0);
		ISession s = mgr.getSessionObject();
		m.lockMachine(s, LockType.Shared);
		IConsole c = s.getConsole();
		IEventSource ev = c.getEventSource();
		IEventListener el = ev.createListener();
		List<VBoxEventType> eventTypes = new ArrayList<VBoxEventType>();
		eventTypes.add( VBoxEventType.Any );
		eventTypes.add(VBoxEventType.MachineEvent);
		ev.registerListener(el, eventTypes, false);
		IEvent e = ev.getEvent(el, 10000);
		if(e!=null) {
			System.out.println("Event: " + e);
			ev.eventProcessed(el, e);
		}
		ev.unregisterListener(el);
		mgr.disconnect();
	}
	
}
