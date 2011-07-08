package com.kedzie.vbox.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

public class KSOAPTransport {
	private static final String NAMESPACE = "http://www.virtualbox.org/";
	
	private HttpTransportSE transport;
	private String url;
	
	public KSOAPTransport(String url) {
		this.url=url;
		this.transport = new HttpTransportSE(url);
	}
	
	public List<String> callStringArray(SoapObject object) throws IOException, XmlPullParserException {
		Vector<SoapPrimitive> vec = (Vector<SoapPrimitive>)call(object);
		List<String> s = new ArrayList<String>(vec.size());
		for(SoapPrimitive p : vec) {
			s.add(p.toString());
		}
		return s;
	}
	public String callString(SoapObject object) throws IOException, XmlPullParserException {
		return ((SoapPrimitive)call(object)).toString();
	}
	public Object call(SoapObject object) throws IOException, XmlPullParserException {
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(object);
		transport.call(NAMESPACE+object.getName(), envelope);
		return envelope.getResponse();
	}
}
