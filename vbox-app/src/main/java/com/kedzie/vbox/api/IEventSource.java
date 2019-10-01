package com.kedzie.vbox.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.api.jaxb.VBoxEventType;
import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;

@KSOAP
public interface IEventSource extends IManagedObjectRef, Parcelable {
    
    static final ClassLoader LOADER = IEventSource.class.getClassLoader();

    public static Parcelable.Creator<IEventSource> CREATOR = new Parcelable.Creator<IEventSource>() {
        @Override
        public IEventSource createFromParcel(Parcel in) {
            VBoxSvc vmgr =  in.readParcelable(LOADER);
            String id = in.readString();
            Map<String, Object> cache = new HashMap<String, Object>();
            in.readMap(cache, LOADER);
            return (IEventSource) vmgr.getProxy(IEventSource.class, id, cache); 
        }

        @Override
        public IEventSource[] newArray(int size) {
            return new IEventSource[size];
        }
    };
    
	public IEventListener createListener() ;
	public void registerListener(@KSOAP("listener")IEventListener l, @KSOAP("interesting") VBoxEventType []events, @KSOAP("active") boolean active);
	public void unregisterListener(@KSOAP("listener") IEventListener l) throws IOException;
	public void eventProcessed(@KSOAP("listener")IEventListener l, @KSOAP("event")IEvent event) throws IOException;
	public IEvent getEvent(@KSOAP("listener")IEventListener l, @KSOAP(type="int", value="timeout") int timeout) throws IOException;
	public IEventSource createAggregator(@KSOAP("subordinates") IEventSource...subordinates) throws IOException;
	public void handleEvent(@KSOAP("event")IEvent event) throws IOException;
	public boolean fireEvent(@KSOAP("event")IEvent event, @KSOAP(type="unsignedInt", value="timeout") int timeout) throws IOException;
}
