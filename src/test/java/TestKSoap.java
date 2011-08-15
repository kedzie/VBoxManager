import java.util.List;
import com.kedzie.vbox.VBoxSvc;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IDisplay;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IVirtualBox;
import com.kedzie.vbox.api.jaxb.LockType;



public class TestKSoap {

	public static void main(String[] args) throws Exception {
		VBoxSvc vmgr = new VBoxSvc("http://localhost:18083");
		vmgr.logon( "test", "test");
		IVirtualBox vbox = vmgr.getVBox();
		List<IMachine> machines =  vbox.getMachines();
		IMachine m = machines.get(2);

		m.lockMachine(vbox.getSessionObject(), LockType.SHARED);
		System.out.println("log file name:"+m.queryLogFilename(0));
		System.out.println("saved Thumbnail size:"+m.querySavedThumbnailSize(0));
		System.out.println("saved screenshot png size:"+m.querySavedScreenshotPNGSize(0));
		IConsole console = vbox.getSessionObject().getConsole();
		IDisplay display = console.getDisplay();
		System.out.println("DIsplay: " + display);
		vbox.getSessionObject().unlockMachine();
		
//		IPerformanceCollector pc = vbox.getPerformanceCollector();
//		System.out.println("Setup Metrics\n---------------------");
//		for(IPerformanceMetric metric : pc.setupMetrics(new String [] { "*:" }, new String [] { m.getIdRef() }, 1, 50 )) 
//			System.out.println(metric.getMetricName() + " " + metric.getUnit());
		
//		for(Map.Entry<String, Map<String, Object>> entry : data.entrySet()) {
//			System.out.println("Metric: " + entry.getKey() + "\n----------------------------");
//			for(Map.Entry<String, Object> e : entry.getValue().entrySet()) {
//				System.out.println(e.getKey() + " --> " + e.getValue());
//			}
//			System.out.println("------------------------------------\n");
//		}
		vmgr.getVBox().logoff();
	}
	
}
