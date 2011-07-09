package com.kedzie.vbox.api;

public interface IConsole extends IRemoteObject {

	public void powerButton();
	public IProgress powerDown();
	public void reset();
	public void pause();
	public void resume();
	public IProgress saveState();
	
}
