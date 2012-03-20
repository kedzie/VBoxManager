package com.kedzie.vbox.api;

import java.io.IOException;

import com.kedzie.vbox.soap.Cacheable;
import com.kedzie.vbox.soap.KSOAP;

public interface IProgress extends IManagedObjectRef {

	public void waitForCompletion(@KSOAP(type="int", value="timeout") int millseconds) throws IOException;
	public void waitForCompletion(@KSOAP(type="unsignedInt", value="operation") int operation, @KSOAP(type="int", value="timeout") int millseconds) throws IOException;
	public void waitForAsyncProgressCompletion(@KSOAP("pProgressAsync") String pProgressAsync); 
	
	@Cacheable public Integer getTimeout() throws IOException;
	@Cacheable public Integer getResultCode() throws IOException;
	@Cacheable public IVirtualBoxErrorInfo getErrorInfo() throws IOException;
	
	@Cacheable public String getDescription() throws IOException;
	@Cacheable public Integer getPercent() throws IOException;
	@Cacheable public Integer getTimeRemaining() throws IOException;
	@Cacheable public String getOperation() throws IOException;
	@Cacheable public Integer getOperationCount() throws IOException;
	@Cacheable public String getOperationDescription() throws IOException;
	@Cacheable public Integer getOperationPercent() throws IOException;
	@Cacheable public Integer getOperationWeight() throws IOException;
	@Cacheable public String getInitiator() throws IOException;
	@Cacheable public Boolean getCancelled() throws IOException;
	@Cacheable public Boolean getCancelable() throws IOException;
	public Boolean getCompleted() throws IOException;
	
	public void cancel() throws IOException;
	public void setTimeout(@KSOAP(type="unsignedInt", value="timeout") int timeout) throws IOException;
	public void setCurrentOperationProgress(@KSOAP(type="unsignedInt", value="percent") int percent) throws IOException;
	public void setNextOperation(@KSOAP("nextOperationDescription") String nextOperationDescription, @KSOAP(type="unsignedInt", value="nextOperationsWeight") int nextOperationsWeight) throws IOException;
}
