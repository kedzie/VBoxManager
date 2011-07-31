package com.kedzie.vbox.api;

import java.io.IOException;

import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import android.os.Parcel;
import android.os.Parcelable;

public class WebSessionManager implements Parcelable {
	private String _url;
	private IVirtualBox _vbox;
	private ISession _session;
	private KSOAPTransport _transport;
	
	public WebSessionManager() {}
	
	public WebSessionManager(Parcel p) {
		_url = p.readString();
		_transport = new KSOAPTransport(_url);
		_vbox = _transport.getProxy(IVirtualBox.class, p.readString());
	 }
	
	public void logon(String url,  String username, String password) throws IOException, XmlPullParserException {
		_url=url;
		_transport=new KSOAPTransport(url);
		SoapObject request = new SoapObject(KSOAPTransport.NAMESPACE, "IWebsessionManager_logon");
		request.addProperty("username", username);
		request.addProperty("password", password);
		_vbox = _transport.getProxy(IVirtualBox.class, _transport.call(request).toString());
	}
	
	public void logoff() throws IOException, XmlPullParserException {
		if(_transport==null || _vbox==null) return;
		SoapObject request = new SoapObject(KSOAPTransport.NAMESPACE, "IWebsessionManager_logoff");
		request.addProperty("refIVirtualBox", _vbox.getId());
		_transport.call(request);
		_transport = null;
		_session=null;
	}
	
	public ISession getSession() throws IOException, XmlPullParserException {
		if(_transport==null || _vbox==null) throw new IllegalArgumentException("Not Logged On");
		if(_session==null) {
			SoapObject request = new SoapObject(KSOAPTransport.NAMESPACE, "IWebsessionManager_getSessionObject");
			request.addProperty("refIVirtualBox", _vbox.getId());
			_session = _transport.getProxy(ISession.class, _transport.call(request).toString());
		}
		return _session;
	}
	
	public IVirtualBox getVBox() { return _vbox;	}
	public KSOAPTransport getTransport() { return _transport; }
	public String getURL() { return _url; }

	@Override
	public void writeToParcel(Parcel dest, int flags) { dest.writeString(_url); dest.writeString(_vbox.getId()); }
	@Override
	public int describeContents() { return 0; }
	 public static final Parcelable.Creator<WebSessionManager> CREATOR = new Parcelable.Creator<WebSessionManager>() {
		 public WebSessionManager createFromParcel(Parcel in) {  return new WebSessionManager(in); }
		 public WebSessionManager[] newArray(int size) {  return new WebSessionManager[size]; }
	 };
}
