package com.kedzie.vbox.api;

import java.io.IOException;

import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

public class WebSessionManager {
	private static final String TAG = "vbox."+WebSessionManager.class.getSimpleName();
	private String url;
	private IVirtualBox vbox;
	private ISession _session;
	private KSOAPTransport transport;
	
	public WebSessionManager() {}
	
	public WebSessionManager(String url, String id) {
		this.url=url;
		this.transport = new KSOAPTransport(url);
		this.vbox = transport.getProxy(IVirtualBox.class, id);
	}
	
	public void logon(String url,  String username, String password) throws IOException, XmlPullParserException {
		this.url=url;
		this.transport=new KSOAPTransport(url);
		SoapObject request = new SoapObject(KSOAPTransport.NAMESPACE, "IWebsessionManager_logon");
		request.addProperty("username", username);
		request.addProperty("password", password);
		vbox = transport.getProxy(IVirtualBox.class, transport.call(request).toString());
	}
	
	public void logoff() throws IOException, XmlPullParserException {
		if(transport==null || vbox==null) return;
		SoapObject request = new SoapObject(KSOAPTransport.NAMESPACE, "IWebsessionManager_logoff");
		request.addProperty("refIVirtualBox", vbox.getId());
		transport.call(request);
		transport = null;
		_session=null;
	}
	
	public ISession getSession() throws IOException, XmlPullParserException {
		if(transport==null || vbox==null) throw new IllegalArgumentException("Not Logged On");
		if(_session==null) {
			SoapObject request = new SoapObject(KSOAPTransport.NAMESPACE, "IWebsessionManager_getSessionObject");
			request.addProperty("refIVirtualBox", vbox.getId());
			_session = transport.getProxy(ISession.class, transport.call(request).toString());
		}
		return _session;
	}
	
	public IVirtualBox getVBox() { return this.vbox;	}
	public KSOAPTransport getTransport() { return this.transport; }
	public String getURL() { return this.url; }
}
