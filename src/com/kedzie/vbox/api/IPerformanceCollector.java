package com.kedzie.vbox.api;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.kedzie.vbox.soap.KSOAP;

/**
 *<p>The {@link IPerformanceCollector} interface represents a service that collects and stores performance metrics data.
 * Performance metrics are associated with objects of interfaces like {@link IHost} and {@link IMachine}. Each object has a distinct set of performance metrics. The set can be obtained with {@link IPerformanceCollector#getMetrics}.</p>
 * <p>Metric data is collected at the specified intervals and is retained internally. The interval and the number of retained samples can be set with {@link IPerformanceCollector#setupMetrics}. Both metric data and collection settings are not persistent, they are discarded as soon as VBoxSVC process terminates. Moreover, metric settings and data associated with a particular VM only exist while VM is running. They disappear as soon as VM shuts down. It is not possible to set up metrics for machines that are powered off. One needs to start VM first, then set up metric collection parameters.</p>
 * <p>Metrics are organized hierarchically, with each level separated by a slash (/) character. Generally, the scheme for metric names is like this:</p>
 * <p><code>Category/Metric[/SubMetric][:aggregation]</code></p>
 * <p>"<code>Category/Metric</code>" together form the base metric name. A base metric is the smallest unit for which a sampling interval and the number of retained samples can be set. Only base metrics can be enabled and disabled. All sub-metrics are collected when their base metric is collected. Collected values for any set of sub-metrics can be queried with {@link IPerformanceCollector#queryMetricsData}.</p>
 * <p>For example "<code>CPU/Load/User:avg</code>" metric name stands for the "CPU" category, "Load" metric, "User" submetric, "average" aggregate. An aggregate function is computed over all retained data. Valid aggregate functions are:</p>
 * <ul>
 * <li><code>avg</code> -- average</li>
 * <li><code>min</code> -- minimum</li>
 * <li><code>max</code> -- maximum</li>
 * </ul>
 * <p>When setting up metric parameters, querying metric data, enabling or disabling metrics wildcards can be used in metric names to specify a subset of metrics. For example, to select all CPU-related metrics use CPU/*, all averages can be queried using *:avg and so on. To query metric values without aggregates *: can be used.</p>
 * <p>The valid names for base metrics are:</p>
 * <ul>
 * <li><code>CPU/Load</code></li>
 * <li><code>CPU/MHz</code></li>
 * <li><code>RAM/Usage</code></li>
 * <li><code>RAM/VMM</code></li>
 * </ul>
 * <p>The general sequence for collecting and retrieving the metrics is:</p>
 * <ul>
 * <li>Obtain an instance of {@link IPerformanceCollector} with {@link IVirtualBox#getPerformanceCollector}</li>
 * <li>Allocate and populate an array with references to objects the metrics will be collected for. Use references to IHost and IMachine objects.</li>
 * <li>Allocate and populate an array with base metric names the data will be collected for.</li>
 * <li>Call  {@link IPerformanceCollector#setupMetrics}. From now on the metric data will be collected and stored.</li>
 * <li>Wait for the data to get collected.</li>
 * <li>Allocate and populate an array with references to objects the metric values will be queried for. You can re-use the object array used for setting base metrics.</li>
 * <li>Allocate and populate an array with metric names the data will be collected for. Note that metric names differ from base metric names.</li>
 * <li>Call {@link IPerformanceCollector#queryMetricsData}. The data that have been collected so far are returned. Note that the values are still retained internally and data collection continues.</li>
 * </ul>
 * <p>For an example of usage refer to the following files in VirtualBox SDK:</p>
 * <ul>
* <li>Java: <code>bindings/webservice/java/jax-ws/samples/metrictest.java</code>  </li>
* <li>Python: <code>bindings/xpcom/python/sample/shellcommon.py</code>  </li>
* </ul>
 * @see {@link ISession}
 */
public interface IPerformanceCollector extends IManagedObjectRef {
	public static final String CPU_LOAD_USER = "CPU/Load/User";
	public static final String CPU_LOAD_KERNEL = "CPU/Load/Kernel";
	public static final String RAM_USAGE_USED = "RAM/Usage/Used";
	public static final String GUEST_CPU_LOAD_USER = "Guest/CPU/Load/User";
	public static final String GUEST_CPU_LOAD_KERNEL = "Guest/CPU/Load/Kernel";

	/**
	 * Returns parameters of specified metrics for a set of objects.
	 * <dl><dt>Notes</dt><dd>Null metrics array means all metrics. Null object array means all existing objects.</dd></dl>
	 * @param metrics	Metric name filter. Currently, only a comma-separated list of metrics is supported.
	 * @param objects		Set of objects to return metric parameters for.
	 * @return	Array of returned metric parameters.
	 * @throws IOException
	 */
	public List<IPerformanceMetric> getMetrics(
			@KSOAP("metricNames")String []metrics, 
			@KSOAP("objects")IManagedObjectRef...objects) throws IOException;
	
	/**
	 * <p>Sets parameters of specified base metrics for a set of objects.</p>
	* <p>Returns an array of IPerformanceMetric describing the metrics have been affected.</p>
	 * @param metrics	Metric name filter. Comma-separated list of metrics with wildcard support.
	 * @param period		Time interval in seconds between two consecutive samples of performance data.
	 * @param count		Number of samples to retain in performance data history. Older samples get discarded.
	 * @param objects		Set of objects to setup metric parameters for.
	 * @return				Array of metrics that have been modified by the call to this method.
	 * @throws IOException
	 */
	public List<IPerformanceMetric> setupMetrics(
			@KSOAP("metricNames")String []metrics, 
			@KSOAP(type="unsignedInt", value="period") int period, 
			@KSOAP(type="unsignedInt", value="count")int count, 
			@KSOAP("objects")Collection<IManagedObjectRef> objects) throws IOException;

	/**
	 * <p>Sets parameters of specified base metrics for a set of objects.</p>
	* <p>Returns an array of IPerformanceMetric describing the metrics have been affected.</p>
	* <dl><dt>Notes</dt><dd>Null or empty metric name array means all metrics. Null or empty object array means all existing objects. If metric name array contains a single element and object array contains many, the single metric name array element is applied to each object array element to form metric/object pairs.</dd></dl>
	 * @param metrics	Metric name filter. Comma-separated list of metrics with wildcard support.
	 * @param period		Time interval in seconds between two consecutive samples of performance data.
	 * @param count		Number of samples to retain in performance data history. Older samples get discarded.
	 * @param objects		Set of objects to setup metric parameters for.
	 * @return				Array of metrics that have been modified by the call to this method
	 * @throws IOException
	 */
	public List<IPerformanceMetric> setupMetrics(
			@KSOAP("metricNames")String []metrics, 
			@KSOAP(type="unsignedInt", value="period") int period, 
			@KSOAP(type="unsignedInt", value="count")int count, 
			@KSOAP("objects")IManagedObjectRef...objects) throws IOException;
	
	/**
	 * <p>Turns on collecting specified base metrics.</p>
	* <p>Returns an array of {@link IPerformanceMetric} describing the metrics have been affected.</p>
	* <dl><dt>Note:</dt><dd>Null or empty metric name array means all metrics. Null or empty object array means all existing objects. If metric name array contains a single element and object array contains many, the single metric name array element is applied to each object array element to form metric/object pairs.</dd></dl>
	 * @param metrics	Metric name filter. Comma-separated list of metrics with wildcard support.
	 * @param objects		Set of objects to enable metrics for.
	 * @return		Array of metrics that have been modified by the call to this method.
	 * @throws IOException
	 */
	public List<IPerformanceMetric> enableMetrics(
			@KSOAP("metricNames")String []metrics, 
			@KSOAP("objects")IManagedObjectRef...objects) throws IOException;

	/**
	 * <p>Turns off collecting specified base metrics.
	 * <p>Returns an array of {@link IPerformanceMetric} describing the metrics have been affected.
	 * <dl><dt>Note:</dt><dd>Null or empty metric name array means all metrics. Null or empty object array means all existing objects. If metric name array contains a single element and object array contains many, the single metric name array element is applied to each object array element to form metric/object pairs.</dd></dl>
	 * @param metrics	Metric name filter. Comma-separated list of metrics with wildcard support.
	 * @param objects	Set of objects to disable metrics for.
	 * @return  Array of metrics that have been modified by the call to this method.
	 * @throws IOException
	 */
	public List<IPerformanceMetric> disableMetrics(
			@KSOAP("metricNames")String []metrics, 
			@KSOAP("objects")IManagedObjectRef...objects) throws IOException;

	/**
	 * <p>Queries collected metrics data for a set of objects.</p>
	 * <p>The data itself and related metric information are returned in seven parallel and one flattened array of arrays. Elements of returnMetricNames, returnObjects, returnUnits, returnScales, returnSequenceNumbers, returnDataIndices and returnDataLengths with the same index describe one set of values corresponding to a single metric.</p>
	 * <p>The returnData parameter is a flattened array of arrays. Each start and length of a sub-array is indicated by returnDataIndices and returnDataLengths. The first value for metric metricNames[i] is at returnData[returnIndices[i]].</p>
	 * <p>Data collection continues behind the scenes after call to queryMetricsData. The return data can be seen as the snapshot of the current state at the time of queryMetricsData call. The internally kept metric values are not cleared by the call. This makes possible querying different subsets of metrics or aggregates with subsequent calls. If periodic querying is needed it is highly suggested to query the values with interval*count period to avoid confusion. This way a completely new set of data values will be provided by each query.</p>
	 * <dl><dt>Note:</dt><dd>Null or empty metric name array means all metrics. Null or empty object array means all existing objects. If metric name array contains a single element and object array contains many, the single metric name array element is applied to each object array element to form metric/object pairs.</dd></dl>
	 * @param metrics	Metric name filter. Comma-separated list of metrics with wildcard support.
	 * @param objects	Set of objects to query metrics for.
	 * @return <ul><li>returnMetricNames 	Names of metrics returned in returnData.</li>
	* <li>returnObjects 	Objects associated with metrics returned in returnData.</li>
	* <li>returnUnits 	Units of measurement for each returned metric.</li>
	* <li>returnScales 	Divisor that should be applied to return values in order to get floating point values. For example: (double)returnData[returnDataIndices[0]+i] / returnScales[0] will retrieve the floating point value of i-th sample of the first metric.</li>
	* <li>returnSequenceNumbers 	Sequence numbers of the first elements of value sequences of particular metrics returned in returnData. For aggregate metrics it is the sequence number of the sample the aggregate started calculation from.</li>
	* <li>returnDataIndices 	Indices of the first elements of value sequences of particular metrics returned in returnData.</li>
	* <li>returnDataLengths 	Lengths of value sequences of particular metrics.</li>
	* <li>returnData 	Flattened array of all metric data containing sequences of values for each metric.</li>
	* </ul>
	 * @throws IOException
	 */
	public Map<String,List<String>> queryMetricsData(
			@KSOAP("metricNames")String []metrics, 
			@KSOAP("objects")String...objects) throws IOException;

}
