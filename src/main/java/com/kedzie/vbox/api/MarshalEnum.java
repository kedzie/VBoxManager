package com.kedzie.vbox.api;

import java.io.IOException;

import org.ksoap2.serialization.Marshal;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.virtualbox_4_0.jaxws.VBoxEventType;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class MarshalEnum implements Marshal {
	private static final String NAMESPACE = "http://www.virtualbox.org";
	
	@Override
	public Object readInstance(XmlPullParser parser, String namespace, String name, PropertyInfo expected) throws IOException, XmlPullParserException {
		String stringValue = parser.nextText();
        Object result;
        if (name.equals("VBoxEventType")) {
            result = VBoxEventType.valueOf(stringValue);
        } else {
            throw new RuntimeException("float, double, or decimal expected");
        }
        return result;
	}

	@Override
	public void writeInstance(XmlSerializer writer, Object instance) throws IOException {
		writer.text(instance.toString());
	}

	@Override
	public void register(SoapSerializationEnvelope envelope) {
		cm.addMapping(cm.xsd, "float", Float.class, this);
        cm.addMapping(cm.xsd, "double", Double.class, this);
        cm.addMapping(cm.xsd, "decimal", java.math.BigDecimal.class, this);
	}

}
