package com.kedzie.vbox.api;

import java.io.IOException;

import org.ksoap2.serialization.SoapSerializationEnvelope;

public interface IProgress extends IRemoteObject {

	public void waitForCompletion(@KSOAP(namespace=SoapSerializationEnvelope.XSD, type="unsignedInt", value="timeout") int millseconds) throws IOException;
	public String getDescription() throws IOException;
	public Boolean getCancelable() throws IOException;
	public Integer getPercent() throws IOException;
	public Boolean getCompleted() throws IOException;
	public Integer getTimeRemaining() throws IOException;
	public Integer getResultCode() throws IOException;
	
	public Integer getOperationCount() throws IOException;
	public String getErrorInfo() throws IOException;
	public String getOperation() throws IOException;
	public String getOperationDescription() throws IOException;
	public Integer getOperationPercent() throws IOException;
	public Integer getOperationWeight() throws IOException;
}
