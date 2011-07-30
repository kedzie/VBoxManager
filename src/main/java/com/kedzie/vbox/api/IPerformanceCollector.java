package com.kedzie.vbox.api;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.ksoap2.serialization.SoapSerializationEnvelope;

public interface IPerformanceCollector extends IRemoteObject {

	public List<IPerformanceMetric> getMetrics(@KSOAP("metrics")String []metrics, @KSOAP("objects")String []objects) throws IOException;
	public List<IPerformanceMetric> setupMetrics(@KSOAP("metrics")String []metrics, @KSOAP("objects")String []objects,@KSOAP(namespace=SoapSerializationEnvelope.XSD, type="unsignedInt", value="period") int period, @KSOAP(namespace=SoapSerializationEnvelope.XSD, type="unsignedInt", value="count")int count) throws IOException;
	public List<IPerformanceMetric> enableMetrics(@KSOAP("metrics")String []metrics, @KSOAP("objects")String []objects) throws IOException;
	public List<IPerformanceMetric> disableMetrics(@KSOAP("metrics")String []metrics, @KSOAP("objects")String []objects) throws IOException;
	public Map<String, Map<String,Object>> queryMetrics(@KSOAP("metrics")String []metrics, @KSOAP("objects")String []objects,@KSOAP(namespace=SoapSerializationEnvelope.XSD, type="unsignedInt", value="period") int period, @KSOAP(namespace=SoapSerializationEnvelope.XSD, type="unsignedInt", value="count")int count) throws IOException;
}
