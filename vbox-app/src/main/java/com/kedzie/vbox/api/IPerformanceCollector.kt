package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.metrics.MetricQuery
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy
import java.io.IOException
import java.util.HashMap

@KsoapProxy
@Ksoap
interface IPerformanceCollector : IManagedObjectRef {
    companion object {
        const val CPU_LOAD_USER = "CPU/Load/User"
        const val CPU_LOAD_KERNEL = "CPU/Load/Kernel"
        const val RAM_USAGE_USED = "RAM/Usage/Used"
        const val GUEST_CPU_LOAD_USER = "Guest/CPU/Load/User"
        const val GUEST_CPU_LOAD_KERNEL = "Guest/CPU/Load/Kernel"
    }

    /**
     * Returns parameters of specified metrics for a set of objects.
     * <dl><dt>Notes</dt><dd>Null metrics array means all metrics. Null object array means all existing objects.</dd></dl>
     * @param metrics    Metric name filter. Currently, only a comma-separated list of metrics is supported.
     * @param objects        Set of objects to return metric parameters for.
     * @return    Array of returned metric parameters.
     * @throws IOException
     */
    suspend fun getMetrics( metricNames: Array<String>,
            vararg objects: IManagedObjectRef): List<IPerformanceMetric>

    /**
     *
     * Sets parameters of specified base metrics for a set of objects.
     *
     * Returns an array of IPerformanceMetric describing the metrics have been affected.
     * <dl><dt>Notes</dt><dd>Null or empty metric name array means all metrics. Null or empty object array means all existing objects. If metric name array contains a single element and object array contains many, the single metric name array element is applied to each object array element to form metric/object pairs.</dd></dl>
     * @param metrics    Metric name filter. Comma-separated list of metrics with wildcard support.
     * @param period        Time interval in seconds between two consecutive samples of performance data.
     * @param count        Number of samples to retain in performance data history. Older samples get discarded.
     * @param objects        Set of objects to setup metric parameters for.
     * @return                Array of metrics that have been modified by the call to this method
     * @throws IOException
     */
    suspend fun setupMetrics(metricNames: Array<String>,
            @Ksoap(type = "unsignedInt") period: Int,
            @Ksoap(type = "unsignedInt") count: Int,
            vararg objects: IManagedObjectRef): List<IPerformanceMetric>

    /**
     *
     * Turns on collecting specified base metrics.
     *
     * Returns an array of [IPerformanceMetric] describing the metrics have been affected.
     * <dl><dt>Note:</dt><dd>Null or empty metric name array means all metrics. Null or empty object array means all existing objects. If metric name array contains a single element and object array contains many, the single metric name array element is applied to each object array element to form metric/object pairs.</dd></dl>
     * @param metrics    Metric name filter. Comma-separated list of metrics with wildcard support.
     * @param objects        Set of objects to enable metrics for.
     * @return        Array of metrics that have been modified by the call to this method.
     * @throws IOException
     */
    suspend fun enableMetrics(metricNames: Array<String>,
            vararg objects: IManagedObjectRef): List<IPerformanceMetric>

    /**
     *
     * Turns off collecting specified base metrics.
     *
     * Returns an array of [IPerformanceMetric] describing the metrics have been affected.
     * <dl><dt>Note:</dt><dd>Null or empty metric name array means all metrics. Null or empty object array means all existing objects. If metric name array contains a single element and object array contains many, the single metric name array element is applied to each object array element to form metric/object pairs.</dd></dl>
     * @param metrics    Metric name filter. Comma-separated list of metrics with wildcard support.
     * @param objects    Set of objects to disable metrics for.
     * @return  Array of metrics that have been modified by the call to this method.
     */
    suspend fun disableMetrics(
            metricNames: Array<String>,
            vararg objects: IManagedObjectRef): List<IPerformanceMetric>

    /**
     *
     * Queries collected metrics data for a set of objects.
     *
     * The data itself and related metric information are returned in seven parallel and one flattened array of arrays. Elements of returnMetricNames, returnObjects, returnUnits, returnScales, returnSequenceNumbers, returnDataIndices and returnDataLengths with the same index describe one set of values corresponding to a single metric.
     *
     * The returnData parameter is a flattened array of arrays. Each start and length of a sub-array is indicated by returnDataIndices and returnDataLengths. The first value for metric metricNames[i] is at returnData[returnIndices[i]].
     *
     * Data collection continues behind the scenes after call to queryMetricsData. The return data can be seen as the snapshot of the current state at the time of queryMetricsData call. The internally kept metric values are not cleared by the call. This makes possible querying different subsets of metrics or aggregates with subsequent calls. If periodic querying is needed it is highly suggested to query the values with interval*count period to avoid confusion. This way a completely new set of data values will be provided by each query.
     * <dl><dt>Note:</dt><dd>Null or empty metric name array means all metrics. Null or empty object array means all existing objects. If metric name array contains a single element and object array contains many, the single metric name array element is applied to each object array element to form metric/object pairs.</dd></dl>
     * @param metrics    Metric name filter. Comma-separated list of metrics with wildcard support.
     * @param objects    Set of objects to query metrics for.
     * @return  * returnMetricNames 	Names of metrics returned in returnData.
     *  * returnObjects 	Objects associated with metrics returned in returnData.
     *  * returnUnits 	Units of measurement for each returned metric.
     *  * returnScales 	Divisor that should be applied to return values in order to get floating point values. For example: (double)returnData[returnDataIndices[0]+i] / returnScales[0] will retrieve the floating point value of i-th sample of the first metric.
     *  * returnSequenceNumbers 	Sequence numbers of the first elements of value sequences of particular metrics returned in returnData. For aggregate metrics it is the sequence number of the sample the aggregate started calculation from.
     *  * returnDataIndices 	Indices of the first elements of value sequences of particular metrics returned in returnData.
     *  * returnDataLengths 	Lengths of value sequences of particular metrics.
     *  * returnData 	Flattened array of all metric data containing sequences of values for each metric.
     *
     */
    suspend fun queryMetricsData(
            metricNames: Array<out String>,
            objects: Array<out String>)
            : Map<String, List<String>>
}

/**
 * Query metric data for specified [IManagedObjectRef]
 * @param obj object to get metrics for
 * @param metrics specify which metrics/accumulations to query. * for all
 * @return  [Map] from metric name to [MetricQuery]
 */
suspend fun IPerformanceCollector.queryMetrics(obj: String, vararg metrics: String): Map<String, MetricQuery> {
    val data = queryMetricsData(metrics, arrayOf(obj))

    val ret = HashMap<String, MetricQuery>()
    for (i in 0 until data.get("returnMetricNames")!!.size) {
        val q = MetricQuery()
        q.name = data.get("returnMetricNames")!![i]
        q.`object` = data.get("returnObjects")!![i]
        q.scale = data.get("returnScales")!![i].toInt()
        q.unit = data.get("returnUnits")!![i]
        val start = data.get("returnDataIndices")!![i].toInt()
        val length = data.get("returnDataLengths")!![i].toInt()

        q.values = IntArray(length)
        var j = 0
        for (s in data.get("returnval")!!.subList(start, start + length))
            q.values[j++] = Integer.valueOf(s) / q.scale
        ret[q.name] = q
    }
    return ret
}

