package com.kedzie.vbox.task;


/**
 * Looping-thread with safe shutdown (via quit())
 */
public abstract class LoopingThread extends Thread {
	/** thread is running */
	protected boolean _running=false;
	protected boolean _paused=false;
	
	public LoopingThread(String name) {
		super(name);
	}
	
	@Override
	public void run() {
		_running=true;
		preExecute();
		while(_running) {
			if(!_paused)
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
	 * Pause execution
	 */
	public void pause() {
		_paused=true;
	}
	
	/**
	 * Resume execution
	 */
	public void unpause() {
		_paused=false;
	}
	
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
