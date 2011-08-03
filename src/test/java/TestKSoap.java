import java.util.List;
import java.util.Map;

import com.kedzie.vbox.api.IHost;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IPerformanceCollector;
import com.kedzie.vbox.api.IPerformanceMetric;
import com.kedzie.vbox.api.IVirtualBox;
import com.kedzie.vbox.api.WebSessionManager;



public class TestKSoap {

	public static void main(String[] args) throws Exception {
		WebSessionManager vmgr = new WebSessionManager();
		vmgr.logon("http://localhost:18083", "test", "test");
		IVirtualBox vbox = vmgr.getVBox();
		List<IMachine> machines =  vbox.getMachines();
		IMachine m = machines.get(0);

		IHost host = vbox.getHost();
		IPerformanceCollector pc = vbox.getPerformanceCollector();
		vmgr.disableMetrics(host.getId());
		
		System.out.println("Setup Metrics\n---------------------");
		for(IPerformanceMetric metric : vmgr.setupMetrics(1, 50, host.getId())) 
			System.out.println(metric.getMetricName() + " " + metric.getUnit());
		
		System.out.println("----------------------\nHost Metrics\n---------------------");
		for(IPerformanceMetric metric : pc.getMetrics( new String[] { "RAM/Usage/*:", "CPU/Load/*:" }, new String[] { host.getId() })) 
			System.out.println(metric.getMetricName() + " " + metric.getUnit() + " " + metric.getMaximumValue());
		
		System.out.println("----------------------\nMachine Metrics\n---------------------");
		for(IPerformanceMetric metric : pc.getMetrics( new String[] { "Guest/*:",  }, new String[] { m.getId() })) 
			System.out.println(metric.getMetricName() + " " + metric.getUnit() + " " + metric.getMaximumValue());
		
		Thread.sleep(5000);
		Map<String, Map<String, Object>> data = vmgr.queryMetricsData(new String[] { "RAM/Usage/*:", "CPU/Load/*:" }, 50, 1, host.getId());
		
		for(Map.Entry<String, Map<String, Object>> entry : data.entrySet()) {
			System.out.println("Metric: " + entry.getKey() + "\n----------------------------");
			for(Map.Entry<String, Object> e : entry.getValue().entrySet()) {
				System.out.println(e.getKey() + " --> " + e.getValue());
			}
			System.out.println("------------------------------------\n");
		}
		vmgr.getVBox().logoff();
	}
	
}
