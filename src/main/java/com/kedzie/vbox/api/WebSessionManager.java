package com.kedzie.vbox.api;

import java.io.IOException;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.xmlpull.v1.XmlPullParserException;

import com.kedzie.vbox.machine.MachineListActivity;

public class WebSessionManager {

	private String url;
	private IVirtualBox vbox;
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
		SoapObject request = new SoapObject(MachineListActivity.NAMESPACE, "IWebsessionManager_logon");
		request.addProperty("username", username);
		request.addProperty("password", password);
		vbox = transport.getProxy(IVirtualBox.class, transport.callString(request));
	}
	
	public ISession getSession() throws IOException, XmlPullParserException {
		SoapObject request = new SoapObject(MachineListActivity.NAMESPACE, "IWebsessionManager_getSessionObject");
		request.addProperty("refIVirtualBox", vbox.getId());
		return transport.getProxy(ISession.class, transport.callString(request));
	}
	
	public IProgress launchVMInstance(IMachine m, ISession session, String mode) throws IOException, XmlPullParserException {
		SoapObject request = new SoapObject(MachineListActivity.NAMESPACE, "IMachine_launchVMProcess");
		request.addProperty("_this", m.getId());
		request.addProperty("session", session.getId());
		request.addProperty("type", mode);
		return transport.getProxy(IProgress.class, transport.callString(request));
	}
	
	public void lockMachine(IMachine m, ISession s, String mode) throws IOException, XmlPullParserException {
		SoapObject r = new SoapObject(MachineListActivity.NAMESPACE, "IMachine_lockMachine");
		r.addProperty("_this", m.getId());
		r.addProperty("session", s.getId());
		SoapPrimitive sp = new SoapPrimitive(MachineListActivity.NAMESPACE, "LockType", mode);
		r.addProperty("lockType",sp);
		transport.call(r);
	}
	
	public void logoff() throws IOException, XmlPullParserException {
		if(transport==null) return;
		SoapObject request = new SoapObject(MachineListActivity.NAMESPACE, "IWebsessionManager_logoff");
		request.addProperty("refIVirtualBox", vbox.getId());
		transport.call(request);
		transport = null;
	}
	
	public String getOSType(IMachine m) throws IOException, XmlPullParserException {
		SoapObject obj = new SoapObject(MachineListActivity.NAMESPACE, "IVirtualBox_getGuestOSType");
		obj.addProperty("_this", vbox.getId ());
		obj.addProperty("id", m.getOSTypeId());
		SoapObject OSType = transport.callObject(obj);
		return (String)OSType.getProperty("id");
	}
	
//	public Map<String, String> getOSTypes(IMachine m) throws IOException, XmlPullParserException {
//		SoapObject obj = new SoapObject(MachineListActivity.NAMESPACE, "IVirtualBox_getGuestOSTypes");
//		obj.addProperty("_this", vbox.getId ());
//		Vector<SoapObject> types = (Vector<SoapObject>)transport.call(obj);
//		
//	}
	
	public IVirtualBox getVBox() {
		return this.vbox;
	}
	public KSOAPTransport getTransport() {
		return this.transport;
	}
	public String getURL() { return this.url; }
}
