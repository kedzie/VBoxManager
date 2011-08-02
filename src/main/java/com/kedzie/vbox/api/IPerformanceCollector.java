package com.kedzie.vbox.api;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IPerformanceCollector extends IRemoteObject {

	public List<IPerformanceMetric> getMetrics(@KSOAP("metrics")String []metrics, @KSOAP("objects")String []objects) throws IOException;
	public List<IPerformanceMetric> setupMetrics(@KSOAP("metrics")String []metrics, @KSOAP("objects")String []objects,@KSOAP(type="unsignedInt", value="period") int period, @KSOAP(type="unsignedInt", value="count")int count) throws IOException;
	public List<IPerformanceMetric> enableMetrics(@KSOAP("metrics")String []metrics, @KSOAP("objects")String []objects) throws IOException;
	public List<IPerformanceMetric> disableMetrics(@KSOAP("metrics")String []metrics, @KSOAP("objects")String []objects) throws IOException;
	public Map<String,List<String>> queryMetricsData(@KSOAP("metrics")String []metrics, @KSOAP("objects")String []objects) throws IOException;
}
