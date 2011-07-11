package com.kedzie.vbox.api;

public interface ISession extends IRemoteObject {

	public void unlockMachine();
	public IConsole getConsole();
	public String getType();
}
