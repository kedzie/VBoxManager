package com.kedzie.vbox.task;


/**
 * Looping-thread with safe shutdown (via quit())
 */
public abstract class BaseThread extends Thread {
	/** thread is running */
	protected boolean _running=false;
	
	public BaseThread(String name) {
		super(name);
	}
	
	@Override
	public void run() {
		_running=true;
		preExecute();
		while(_running) {
			loop();
		}
		postExecute();
	}
	
	/**
	 * One time initialization
	 */
	public void preExecute() {}
	
	/**
	 * cleaup
	 */
	public void postExecute() {}

	/**
	 * Execute a loop iteration.
	 */
	public abstract void loop();

	/**
	 * Nicely shuts down the thread
	 */
	public void quit() {
		boolean done = false;
        _running=false;
        while (!done) {
            try {
                join();
                done = true;
            } catch (InterruptedException e) { }
        }
	}
}
