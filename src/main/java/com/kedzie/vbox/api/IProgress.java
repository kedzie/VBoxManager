package com.kedzie.vbox.api;

import java.io.IOException;

import com.kedzie.vbox.KSOAP;

public interface IProgress extends IRemoteObject {

	public void waitForCompletion(@KSOAP(type="int", value="timeout") int millseconds) throws IOException;
	public void waitForCompletion(@KSOAP(type="unsignedInt", value="operation") int operation, @KSOAP(type="int", value="timeout") int millseconds) throws IOException;
	public void waitForAsyncProgressCompletion(@KSOAP("pProgressAsync") String pProgressAsync); 
	
	@KSOAP(cache=false) public Integer getTimeout() throws IOException;
	@KSOAP(cache=false) public Integer getResultCode() throws IOException;
	@KSOAP(cache=false) public String getErrorInfo() throws IOException;
	
	@KSOAP(cache=true) public String getDescription() throws IOException;
	@KSOAP(cache=true) public Integer getPercent() throws IOException;
	@KSOAP(cache=true) public Integer getTimeRemaining() throws IOException;
	@KSOAP(cache=true) public String getOperation() throws IOException;
	@KSOAP(cache=true) public Integer getOperationCount() throws IOException;
	@KSOAP(cache=true) public String getOperationDescription() throws IOException;
	@KSOAP(cache=true) public Integer getOperationPercent() throws IOException;
	@KSOAP(cache=true) public Integer getOperationWeight() throws IOException;
	
	@KSOAP(cache=true) public String getInitiator() throws IOException;
	
	@KSOAP(cache=false) public Boolean getCancelled() throws IOException;
	@KSOAP(cache=true) public Boolean getCancelable() throws IOException;
	@KSOAP(cache=false) public Boolean getCompleted() throws IOException;
	
	public void cancel() throws IOException;
	public void setTimeout(@KSOAP(type="unsignedInt", value="timeout") int timeout) throws IOException;
	public void setCurrentOperationProgress(@KSOAP(type="unsignedInt", value="percent") int percent) throws IOException;
	public void setNextOperation(@KSOAP("nextOperationDescription") String nextOperationDescription, @KSOAP(type="unsignedInt", value="nextOperationsWeight") int nextOperationsWeight) throws IOException;
}
