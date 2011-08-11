package com.kedzie.vbox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.virtualbox_4_1.VBoxEventType;
import org.xmlpull.v1.XmlPullParserException;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.api.IMachineStateChangedEvent;
import com.kedzie.vbox.api.IPerformanceMetric;
import com.kedzie.vbox.api.ISessionStateChangedEvent;
import com.kedzie.vbox.api.IVirtualBox;
import com.kedzie.vbox.machine.PreferencesActivity;

public class VBoxSvc implements Parcelable {
	private String _url;
	private IVirtualBox _vbox;
	private KSOAPTransport _transport;
	
	public VBoxSvc(String url) { 
		_url=url; 
		_transport = new KSOAPTransport(_url);
	}
	
	public VBoxSvc(String url, String vboxID) { 
		this(url);
		_vbox = _transport.getProxy(IVirtualBox.class, vboxID);
	}
	
	public VBoxSvc(VBoxSvc in) {
		this(in._url, in._vbox.getIdRef());
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) { 
		dest.writeString(_url); 
		dest.writeString(_vbox.getIdRef()); 
	}
	
	@Override
	public int describeContents() { 
		return 0; 
	}
	
	 public static final Parcelable.Creator<VBoxSvc> CREATOR = new Parcelable.Creator<VBoxSvc>() {
		 public VBoxSvc createFromParcel(Parcel in) { return new VBoxSvc(in.readString(), in.readString()); }
		 public VBoxSvc[] newArray(int size) {  return new VBoxSvc[size]; }
	 };
	
	public IVirtualBox logon(String username, String password) throws IOException, XmlPullParserException {
		_vbox = _transport.getProxy(IVirtualBox.class, null).logon(username, password);
//		SoapObject request = new SoapObject(KSOAPTransport.NAMESPACE, "IWebsessionManager_logon");
//		request.addProperty("username", username);
//		request.addProperty("password", password);
//		_vbox = _transport.getProxy(IVirtualBox.class, _transport.call(request).toString());
		return _vbox;
	}
	
	public List<IPerformanceMetric> setupMetrics( Context ctx, String object, String...metrics) throws IOException { return setupMetrics(ctx, new String [] {object}, metrics); }
	
	public List<IPerformanceMetric> setupMetrics( Context ctx, Collection<String> objects, String...metrics) throws IOException {	return setupMetrics(ctx, objects.toArray(new String[objects.size()]), metrics); }
	
	public List<IPerformanceMetric> setupMetrics( Context ctx, String[] objects, String...metrics) throws IOException {
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
	public String getURL() { return _url; }
	
	public IEvent getEventProxy(String id) {
		IEvent event = getProxy(IEvent.class, id);
		if(event.getType().equals(VBoxEventType.OnMachineStateChanged)) return getProxy( IMachineStateChangedEvent.class, id );
		else if(event.getType().equals(VBoxEventType.OnSessionStateChanged))	return getProxy( ISessionStateChangedEvent.class, id );
		return event;
	}
	
	public IEvent getEventProxy(IEvent event) {
		if(event==null) return null;
		return getEventProxy(event.getIdRef());
	}
}
