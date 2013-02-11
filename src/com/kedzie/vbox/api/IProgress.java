package com.kedzie.vbox.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;

public interface IProgress extends IManagedObjectRef, Parcelable {
    public static final String BUNDLE = "progress";
    
    static final ClassLoader LOADER = IProgress.class.getClassLoader();
    
    public static final Parcelable.Creator<IProgress> CREATOR = new Parcelable.Creator<IProgress>() {
		public IProgress createFromParcel(Parcel in) {
			VBoxSvc vmgr =  in.readParcelable(LOADER);
			String id = in.readString();
			Map<String, Object> cache = new HashMap<String, Object>();
			in.readMap(cache, LOADER);
			return (IProgress) vmgr.getProxy(IProgress.class, id, cache); 
		}
		public IProgress[] newArray(int size) {  
			return new IProgress[size]; 
		}
	};

	public void waitForCompletion(@KSOAP(type="int", value="timeout") int millseconds) throws IOException;
	public void waitForCompletion(@KSOAP(type="unsignedInt", value="operation") int operation, @KSOAP(type="int", value="timeout") int millseconds) throws IOException;
	public void waitForAsyncProgressCompletion(@KSOAP("pProgressAsync") String pProgressAsync); 
	
	@KSOAP(cacheable=true) public Integer getTimeout() ;
	@KSOAP(cacheable=true) public Integer getResultCode() ;
	@KSOAP(cacheable=true) public IVirtualBoxErrorInfo getErrorInfo() ;
	
	@KSOAP(cacheable=true) public String getDescription() ;
	@KSOAP(cacheable=true) public Integer getPercent() ;
	@KSOAP(cacheable=true) public Integer getTimeRemaining() ;
	@KSOAP(cacheable=true) public Integer getOperation() ;
	@KSOAP(cacheable=true) public Integer getOperationCount() ;
	@KSOAP(cacheable=true) public String getOperationDescription() ;
	@KSOAP(cacheable=true) public Integer getOperationPercent() ;
	@KSOAP(cacheable=true) public Integer getOperationWeight() ;
	@KSOAP(cacheable=true) public String getInitiator() ;
	@KSOAP(cacheable=true) public Boolean getCancelled() ;
	@KSOAP(cacheable=true) public Boolean getCancelable() ;
	@KSOAP(cacheable=true) public Boolean getCompleted() ;
	
	public void cancel() throws IOException;
	public void setTimeout(@KSOAP(type="unsignedInt", value="timeout") int timeout) throws IOException;
	public void setCurrentOperationProgress(@KSOAP(type="unsignedInt", value="percent") int percent) throws IOException;
	public void setNextOperation(@KSOAP("nextOperationDescription") String nextOperationDescription, @KSOAP(type="unsignedInt", value="nextOperationsWeight") int nextOperationsWeight) throws IOException;
}
