import java.util.List;

import org.virtualbox_4_0.IMachineStateChangedEvent;
import org.virtualbox_4_0.LockType;
import org.virtualbox_4_0.SessionState;
import org.virtualbox_4_0.VBoxEventType;

import android.util.Log;
import android.widget.Toast;

import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IEventListener;
import com.kedzie.vbox.api.IEventSource;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.ISession;
import com.kedzie.vbox.api.IVirtualBox;
import com.kedzie.vbox.api.WebSessionManager;
import com.kedzie.vbox.event.IEvent;



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
		vmgr.logoff();
	}
	
}
