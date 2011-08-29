package com.kedzie.vbox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParserException;
import android.os.Parcel;
import android.os.Parcelable;
import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.api.IMachineStateChangedEvent;
import com.kedzie.vbox.api.ISessionStateChangedEvent;
import com.kedzie.vbox.api.IVirtualBox;
import com.kedzie.vbox.api.jaxb.VBoxEventType;

public class VBoxSvc implements Parcelable {
	public static final String[] METRICS_HOST =  { "*:" };
	public static final String[] METRICS_MACHINE =  { "*:" };
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
		return _vbox;
	}
	
	public Map<String, Map<String,Object>> queryMetricsData(String object, int count, int period, String...metrics) throws IOException {
		Map<String, List<String>> data= _vbox.getPerformanceCollector().queryMetricsData(metrics, new String[] { object });
		List<Integer> vals = new ArrayList<Integer>(data.get("returnval").size());
		for(String s : data.get("returnval")) 
			vals.add(Integer.valueOf(s));
		
		Map<String, Map<String,Object>> ret = new HashMap<String, Map<String, Object>>();
		for(int i=0; i<data.get("returnMetricNames").size(); i++) {
			Map<String, Object> metric = new HashMap<String, Object>();
			for(Map.Entry<String, List<String>> entry : data.entrySet())
				metric.put(entry.getKey().substring(6), entry.getValue().get(i) );
			int start = Integer.valueOf(metric.remove("DataIndices").toString());
			int length = Integer.valueOf(metric.remove("DataLengths").toString());
			List<Integer> metricValues = new ArrayList<Integer>(count);
			for(int t=0; t<count-length; t++)	
				metricValues.add(0);
			metricValues.addAll( vals.subList(start, start+length) );
			metric.put("val", metricValues);
			ret.put(  metric.get("MetricNames").toString(), metric );
		}
		return ret;
	}
	
	public <T> T getProxy(Class<T> clazz, String id) { return _transport.getProxy(clazz, id); }
	
	public IVirtualBox getVBox() { return _vbox;	}
	public String getURL() { return _url; }
}
