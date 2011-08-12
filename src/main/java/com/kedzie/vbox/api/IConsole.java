package com.kedzie.vbox.api;

import java.io.IOException;

import com.kedzie.vbox.KSOAP;


public interface IConsole extends IRemoteObject {

	public IProgress powerUp() throws IOException;
	public IProgress powerUpPaused() throws IOException;
	public IProgress powerDown() throws IOException;
	public void sleepButton() throws IOException;
	public void powerButton() throws IOException;
	public void reset() throws IOException;
	public void pause() throws IOException;
	public void resume() throws IOException;
	public IProgress saveState() throws IOException;
	public void adoptSavedState(@KSOAP("saveStateFile") String saveStateFile) throws IOException;
	public void discardSavedState( @KSOAP("fRemoveFile") boolean removeFile) throws IOException;
	
	public IEventSource getEventSource() throws IOException;
	public IDisplay getDisplay() throws IOException;
	
	public IProgress takeSnapshot(@KSOAP("name") String name, @KSOAP("description") String description) throws IOException;
	public IProgress deleteSnapshot(@KSOAP("snapshot") ISnapshot s) throws IOException;
	public IProgress restoreSnapshot(@KSOAP("snapshot") ISnapshot s) throws IOException;
}
