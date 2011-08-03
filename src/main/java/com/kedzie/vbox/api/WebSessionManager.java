package com.kedzie.vbox.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class WebSessionManager implements Parcelable {
	private String _url;
	private IVirtualBox _vbox;
	private KSOAPTransport _transport;
	
	public WebSessionManager() {}
	
	public WebSessionManager(Parcel p) {
		_url = p.readString();
		_transport = new KSOAPTransport(_url);
		_vbox = _transport.getProxy(IVirtualBox.class, p.readString());
	 }
	
	@Override
	public void writeToParcel(Parcel dest, int flags) { dest.writeString(_url); dest.writeString(_vbox.getId()); }
	@Override
	public int describeContents() { return 0; }
	 public static final Parcelable.Creator<WebSessionManager> CREATOR = new Parcelable.Creator<WebSessionManager>() {
		 public WebSessionManager createFromParcel(Parcel in) {  return new WebSessionManager(in); }
		 public WebSessionManager[] newArray(int size) {  return new WebSessionManager[size]; }
	 };
	
	public void logon(String url,  String username, String password) throws IOException, XmlPullParserException {
		_url=url;
		_transport=new KSOAPTransport(url);
		SoapObject request = new SoapObject(KSOAPTransport.NAMESPACE, "IWebsessionManager_logon");
		request.addProperty("username", username);
		request.addProperty("password", password);
		_vbox = _transport.getProxy(IVirtualBox.class, _transport.call(request).toString());
	}
	
	public List<IPerformanceMetric> setupHostMetrics( Context context, String...objects) throws IOException {
		return setupMetrics( new String [] { "*:" },  context.getSharedPreferences(context.getPackageName(), 0).getInt("period", 1),context.getSharedPreferences(context.getPackageName(), 0).getInt("count", 25), objects);
	}

	public List<IPerformanceMetric> setupMachineMetrics( Context context, String...objects) throws IOException {
		return setupMetrics(  new String [] { "Guest/*:" }, context.getSharedPreferences(context.getPackageName(), 0).getInt("period", 1),context.getSharedPreferences(context.getPackageName(), 0).getInt("count", 25), objects);
	}
	
	public List<IPerformanceMetric> setupMetrics( int period, int count, String... objects) throws IOException {
		return setupMetrics( new String [] { "*:" }, period, count, objects);
	}
	
	public List<IPerformanceMetric> setupMetrics( String[] metrics, int period, int count, String... objects) throws IOException {
		IPerformanceCollector pc = _vbox.getPerformanceCollector();
		pc.setupMetrics(metrics, objects, period, count);
		return pc.enableMetrics(metrics, objects);
	}
	
	public void disableMetrics( String... objects) throws IOException {
		String []baseMetrics = new String [] { "*:" };
		_vbox.getPerformanceCollector().disableMetrics(baseMetrics, objects);
	}
	
	public Map<String, Map<String,Object>> queryMetricsData(String []metrics, int count, int period, String...obj) throws IOException {
		IPerformanceCollector pc = _vbox.getPerformanceCollector();
		Map<String, List<String>> data= pc.queryMetricsData(metrics, obj);
		
		List<Integer> vals = new ArrayList<Integer>();
		for(String s : (List<String>)data.get("returnval")) vals.add(Integer.valueOf(s));
		
		Map<String, Map<String,Object>> ret = new HashMap<String, Map<String, Object>>();
		for(int i=0; i<((List<String>)data.get("returnMetricNames")).size(); i++) {
			Map<String, Object> metric = new HashMap<String, Object>();
			for(Map.Entry<String, List<String>> entry : data.entrySet())
				metric.put(entry.getKey().substring(6), ((List<String>)entry.getValue()).get(i) );
			
			int start = Integer.valueOf((String)metric.remove("DataIndices"));
			int length = Integer.valueOf((String)metric.remove("DataLengths"));
			List<Integer> metricValues = new ArrayList<Integer>(count);
			for(int t=0; t<count-length; t++)	metricValues.add(0);
			metricValues.addAll( vals.subList(start, start+length) );
			metric.put("val", metricValues);
			ret.put(  metric.get("MetricNames").toString(), metric );
		}
		return ret;
	}
	public <T> T getProxy(Class<T> clazz, String id) { return _transport.getProxy(clazz, id); }
	public IVirtualBox getVBox() { return _vbox;	}
}
