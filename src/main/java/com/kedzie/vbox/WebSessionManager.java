package com.kedzie.vbox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;
import org.virtualbox_4_1.VBoxEventType;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.api.IMachineStateChangedEvent;
import com.kedzie.vbox.api.IPerformanceMetric;
import com.kedzie.vbox.api.IVirtualBox;

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
	
	public IVirtualBox logon(String url,  String username, String password) throws IOException, XmlPullParserException {
		_url=url;
		_transport=new KSOAPTransport(url);
		SoapObject request = new SoapObject(KSOAPTransport.NAMESPACE, "IWebsessionManager_logon");
		request.addProperty("username", username);
		request.addProperty("password", password);
		_vbox = _transport.getProxy(IVirtualBox.class, _transport.call(request).toString());
		return _vbox;
	}
	
	public List<IPerformanceMetric> setupMetrics( Context ctx, String object, String...metrics) throws IOException {
		return setupMetrics(ctx, new String [] {object}, metrics);
	}
	
	public List<IPerformanceMetric> setupMetrics( Context ctx, String [] objects, String...metrics) throws IOException {
		return _vbox.getPerformanceCollector().setupMetrics(metrics, objects, ctx.getSharedPreferences(ctx.getPackageName(), 0).getInt(PreferencesActivity.PERIOD, 1),ctx.getSharedPreferences(ctx.getPackageName(), 0).getInt(PreferencesActivity.COUNT, 25));
	}
	
	public Map<String, Map<String,Object>> queryMetricsData(Context ctx, String object, String...metrics) throws IOException {
		return queryMetricsData(object, ctx.getSharedPreferences(ctx.getPackageName(), 0).getInt(PreferencesActivity.PERIOD, 1),ctx.getSharedPreferences(ctx.getPackageName(), 0).getInt(PreferencesActivity.COUNT, 25), metrics);
	}
	
	public Map<String, Map<String,Object>> queryMetricsData(String object, int count, int period, String...metrics) throws IOException {
		Map<String, List<String>> data= _vbox.getPerformanceCollector().queryMetricsData(metrics, new String[] { object });
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
	
	public IEvent getEventProxy(String id) {
		IEvent event = getProxy(IEvent.class, id);
		if(event.getType().equals(VBoxEventType.OnMachineStateChanged))
			return getProxy( IMachineStateChangedEvent.class, event.getId() );
		else if(event.getType().equals(VBoxEventType.OnSessionStateChanged))
			return getProxy( IEvent.class, event.getId() );
		return event;
	}
}
