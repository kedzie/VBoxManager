import java.util.List;
import java.util.Map;

import com.kedzie.vbox.VBoxSvc;
import com.kedzie.vbox.api.IHost;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IPerformanceCollector;
import com.kedzie.vbox.api.IPerformanceMetric;
import com.kedzie.vbox.api.IVirtualBox;



public class TestKSoap {

	public static void main(String[] args) throws Exception {
		VBoxSvc vmgr = new VBoxSvc("http://localhost:18083");
		vmgr.logon( "test", "test");
		IVirtualBox vbox = vmgr.getVBox();
		List<IMachine> machines =  vbox.getMachines();
		IMachine m = machines.get(0);

		IHost host = vbox.getHost();
		IPerformanceCollector pc = vbox.getPerformanceCollector();
		
		System.out.println("Setup Metrics\n---------------------");
		for(IPerformanceMetric metric : pc.setupMetrics(new String [] { "*:" }, new String [] { m.getIdRef() }, 1, 50 )) 
			System.out.println(metric.getMetricName() + " " + metric.getUnit());
		
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
