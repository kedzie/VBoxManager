package com.kedzie.vbox;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Base64;

import com.kedzie.vbox.api.IManagedObjectRef;

/**
 * Handles unmarshalling of SOAP response based on <code>KSOAP</code> annotation metadata
 */
public class VBoxSerializationEnvelope extends SoapSerializationEnvelope {

	public VBoxSerializationEnvelope() {
		super(SoapEnvelope.VER11);
	}
	
	/**
	 * Unmarshall SoapEnvelope to correct type based on Remote method
	 * @param vmgr	VirtualBox JAXWS API
	 * @param returnType   remote method return type
	 * @param genericType  remote method return type (parameterized)
	 * @return  unmarshalled return value
	 * @throws <code>SoapFault</code>
	 */
	public Object getResponse(VBoxSvc vmgr, Class<?> returnType, Type genericType) throws SoapFault {
		if (bodyIn instanceof SoapFault) throw (SoapFault) bodyIn;
		boolean isCollection = Collection.class.isAssignableFrom(returnType);
		boolean isMap = Map.class.isAssignableFrom(returnType);
		KvmSerializable ks = (KvmSerializable) bodyIn;
		if ( (ks.getPropertyCount() == 0 && !isCollection && !isMap) || (ks.getPropertyCount() == 1 && ks.getProperty(0).toString().equals("anyType{}"))) return null;
		
		if(isMap) {
			Map<String, List<String>> map = new HashMap<String, List<String>>();
			PropertyInfo info = new PropertyInfo();
			for (int i = 0; i < ks.getPropertyCount(); i++) {
				ks.getPropertyInfo(i, null, info);
				if (!map.containsKey(info.getName())) 
					map.put(info.getName(), new ArrayList<String>());
				map.get(info.getName()).add(   ks.getProperty(i).toString() );
			}
			return map;
		} 
		if(isCollection) {
			Class<?> pClazz = (Class<?>)((ParameterizedType)genericType).getActualTypeArguments()[0];
			Collection<Object> list = new ArrayList<Object>(ks.getPropertyCount());
			for (int i = 0; i < ks.getPropertyCount(); i++)
				list.add( unmarshal(vmgr, pClazz, genericType, ks.getProperty(i)) );
			return list;
		}
		return unmarshal(vmgr, returnType, genericType, ks.getProperty(0));
	}
	
	/**
	 * @param vmgr	VirtualBox JAXWS API
	 * @param returnType remote method return type
	 * @param genericType remote method return type (parameterized)
	 * @param ret  marshalled value
	 * @return unmarshalled return value
	 */
	private  Object unmarshal(VBoxSvc vmgr, Class<?> returnType, Type genericType, Object ret) {
		if(ret==null) return null;
		if(returnType.isArray() && returnType.getComponentType().equals(byte.class))
			return Base64.decode(ret.toString().getBytes(), Base64.DEFAULT);
		if(returnType.equals(Boolean.class)) 	
			return Boolean.valueOf(ret.toString());
		else if(returnType.equals(Integer.class)) 	
			return Integer.valueOf(ret.toString());
		else if(returnType.equals(Long.class)) 	
			return Long.valueOf(ret.toString());
		else if(returnType.equals(String.class))	
			return ret.toString();
		else if(IManagedObjectRef.class.isAssignableFrom(returnType))	
			return vmgr.getProxy(returnType, ret.toString());
		else if(returnType.isEnum()) {
			for( Object element : returnType.getEnumConstants()) 
				if( element.toString().equals( ret.toString() ) ) 
					return element;
		}
		return ret;
	}
}
