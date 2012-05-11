package com.kedzie.vbox.api;

import java.io.IOException;

import com.kedzie.vbox.soap.Cacheable;
import com.kedzie.vbox.soap.KSOAP;

public interface IProgress extends IManagedObjectRef {

	public void waitForCompletion(@KSOAP(type="int", value="timeout") int millseconds) throws IOException;
	public void waitForCompletion(@KSOAP(type="unsignedInt", value="operation") int operation, @KSOAP(type="int", value="timeout") int millseconds) throws IOException;
	public void waitForAsyncProgressCompletion(@KSOAP("pProgressAsync") String pProgressAsync); 
	
	@Cacheable public Integer getTimeout() ;
	@Cacheable public Integer getResultCode() ;
	@Cacheable public IVirtualBoxErrorInfo getErrorInfo() ;
	
	@Cacheable public String getDescription() ;
	@Cacheable public Integer getPercent() ;
	@Cacheable public Integer getTimeRemaining() ;
	@Cacheable public String getOperation() ;
	@Cacheable public Integer getOperationCount() ;
	@Cacheable public String getOperationDescription() ;
	@Cacheable public Integer getOperationPercent() ;
	@Cacheable public Integer getOperationWeight() ;
	@Cacheable public String getInitiator() ;
	@Cacheable public Boolean getCancelled() ;
	@Cacheable public Boolean getCancelable() ;
	@Cacheable public Boolean getCompleted() ;
	
	public void cancel() throws IOException;
	public void setTimeout(@KSOAP(type="unsignedInt", value="timeout") int timeout) throws IOException;
	public void setCurrentOperationProgress(@KSOAP(type="unsignedInt", value="percent") int percent) throws IOException;
	public void setNextOperation(@KSOAP("nextOperationDescription") String nextOperationDescription, @KSOAP(type="unsignedInt", value="nextOperationsWeight") int nextOperationsWeight) throws IOException;
}
