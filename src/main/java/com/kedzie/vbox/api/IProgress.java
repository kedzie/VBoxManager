package com.kedzie.vbox.api;

import java.io.IOException;

import com.kedzie.vbox.soap.Cacheable;
import com.kedzie.vbox.soap.KSOAP;

public interface IProgress extends IManagedObjectRef {

	public void waitForCompletion(@KSOAP(type="int", value="timeout") int millseconds) throws IOException;
	public void waitForCompletion(@KSOAP(type="unsignedInt", value="operation") int operation, @KSOAP(type="int", value="timeout") int millseconds) throws IOException;
	public void waitForAsyncProgressCompletion(@KSOAP("pProgressAsync") String pProgressAsync); 
	
	public Integer getTimeout() throws IOException;
	public Integer getResultCode() throws IOException;
	public String getErrorInfo() throws IOException;
	
	@Cacheable public String getDescription() throws IOException;
	public Integer getPercent() throws IOException;
	public Integer getTimeRemaining() throws IOException;
	public String getOperation() throws IOException;
	@Cacheable public Integer getOperationCount() throws IOException;
	public String getOperationDescription() throws IOException;
	public Integer getOperationPercent() throws IOException;
	public Integer getOperationWeight() throws IOException;
	@Cacheable public String getInitiator() throws IOException;
	public Boolean getCancelled() throws IOException;
	@Cacheable public Boolean getCancelable() throws IOException;
	public Boolean getCompleted() throws IOException;
	
	public void cancel() throws IOException;
	public void setTimeout(@KSOAP(type="unsignedInt", value="timeout") int timeout) throws IOException;
	public void setCurrentOperationProgress(@KSOAP(type="unsignedInt", value="percent") int percent) throws IOException;
	public void setNextOperation(@KSOAP("nextOperationDescription") String nextOperationDescription, @KSOAP(type="unsignedInt", value="nextOperationsWeight") int nextOperationsWeight) throws IOException;
}
