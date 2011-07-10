package com.kedzie.vbox.api;

import java.io.IOException;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.xmlpull.v1.XmlPullParserException;

import com.kedzie.vbox.VBoxMonitorActivity;

public class WebSessionManager {

	private IVirtualBox vbox;
	private KSOAPTransport transport;
	
	public void logon(KSOAPTransport transport,  String username, String password) throws IOException, XmlPullParserException {
		this.transport=transport;
		SoapObject request = new SoapObject(VBoxMonitorActivity.NAMESPACE, "IWebsessionManager_logon");
		request.addProperty("username", username);
		request.addProperty("password", password);
		vbox = transport.getProxy(IVirtualBox.class, transport.callString(request));
	}
	
	public ISession getSession() throws IOException, XmlPullParserException {
		SoapObject request = new SoapObject(VBoxMonitorActivity.NAMESPACE, "IWebsessionManager_getSessionObject");
		request.addProperty("refIVirtualBox", vbox.getId());
		return transport.getProxy(ISession.class, transport.callString(request));
	}
	
	public IProgress launchVMInstance(IMachine m, ISession session, String mode) throws IOException, XmlPullParserException {
		SoapObject request = new SoapObject(VBoxMonitorActivity.NAMESPACE, "IMachine_launchVMProcess");
		request.addProperty("_this", m.getId());
		request.addProperty("session", session.getId());
		request.addProperty("type", mode);
		return transport.getProxy(IProgress.class, transport.callString(request));
	}
	
	public void lockMachine(IMachine m, ISession s, String mode) throws IOException, XmlPullParserException {
		SoapObject r = new SoapObject(VBoxMonitorActivity.NAMESPACE, "IMachine_lockMachine");
		r.addProperty("_this", m.getId());
		r.addProperty("session", s.getId());
		SoapPrimitive sp = new SoapPrimitive(VBoxMonitorActivity.NAMESPACE, "LockType", mode);
		r.addProperty("lockType",sp);
		transport.call(r);
	}
	
	public void logoff() throws IOException, XmlPullParserException {
		if(transport==null) return;
		SoapObject request = new SoapObject(VBoxMonitorActivity.NAMESPACE, "IWebsessionManager_logoff");
		request.addProperty("refIVirtualBox", vbox.getId());
		transport.call(request);
		transport = null;
	}
	
	public IVirtualBox getVBox() {
		return this.vbox;
	}
}
