package com.kedzie.vbox.api;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.soap.VBoxSvc;

import org.ksoap2.serialization.SoapSerializationEnvelope;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author kedzie
 */
public abstract class BaseProxy implements IManagedObjectRef, Parcelable {
    public static final String NAMESPACE = "http://www.virtualbox.org/";

    private final String TAG;

    /** Unique identifier (UIUD) of {@link IManagedObjectRef} */
    protected String _uiud;
    /** Type of {@link IManagedObjectRef} */
    protected Class<?> _type;
    /** cached property values */
    protected Map<String, Object> _cache;
    /** service */
    protected VBoxSvc _vmgr;

    public BaseProxy(VBoxSvc vmgr, String id, Class<?> type, Map<String,Object> cache) {
        _vmgr = vmgr;
        _uiud=id;
        _type=type;
        _cache = cache!=null ? cache : new HashMap<String, Object>();
        TAG = _type.getSimpleName();
    }

    @Override
    public String getIdRef() {
        return _uiud;
    }

    @Override
    public void clearCache() {
        _cache.clear();
    }

    @Override
    public void clearCacheNamed(String... names) {
        for(String arg : names)
            _cache.remove(arg);
    }

    @Override
    public Map<String, Object> getCache() {
        return _cache;
    }

    @Override
    public VBoxSvc getAPI() {
        return _vmgr;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(_uiud);
    }

    @Override
    public boolean equals(Object o) {
        if(o==null) return false;
        if(!(o instanceof IManagedObjectRef) || !_type.isAssignableFrom(o.getClass()))
            return false;
        return Objects.equals(_uiud, ((IManagedObjectRef) o).getIdRef());
    }

    @Override
    public String toString() {
        return _type.getSimpleName() + " #" + _uiud + "\n" + Utils.toString("Cache", _cache);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(_vmgr, 0);
        out.writeString(_uiud);
        out.writeMap(_cache);
    }

    /**
     * Thread for making SOAP invocations
     */
    public class AsynchronousThread implements Runnable {
        private String request;
        private SoapSerializationEnvelope envelope;

        public AsynchronousThread(String request, SoapSerializationEnvelope envelope) {
            this.request=request;
            this.envelope = envelope;
        }

        @Override
        public void run() {
            try {
                _vmgr.httpCall(request, envelope);
            } catch (IOException e) {
                Log.e(TAG, "Error", e);
            }
        }
    }
}
