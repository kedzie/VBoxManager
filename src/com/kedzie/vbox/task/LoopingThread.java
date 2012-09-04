package com.kedzie.vbox.task;


/**
 * Invoke an operation repeatedly with hooks for initialization/teardown.
 * @author Marek Kędzierski
 */
public abstract class LoopingThread extends Thread {
	
	/** main loop is running */
	protected  boolean _running=false; 
	
	public LoopingThread(String name) {
		super(name);
	}
	
	@Override
	public final void run() {
		_running=true;
		preExecute();
		while(_running)
				loop();
		postExecute();
	}
	
	/**
	 * One time initialization before main loop
	 */
	public void preExecute() {}
	
	/**
	 * Performed after loop termination
	 */
	public void postExecute() {}

	/**
	 * Execute a loop iteration.
	 */
	public abstract void loop();

	/**
	 * Nicely shuts down the thread
	 * <ol>
	 * <li>Set {@link LoopingThread#_running} to false to exit the main loop.
	 * <li>If Thread is waiting between iterations interupt it
	 * <li>Repeatedly call {@link Thread#join} until successful
	 * </ol>
	 */
	public final void quit() {
		boolean done = false;
        _running=false;
        while (!done) {  
            try {
            	if(getState().equals(State.WAITING) || getState().equals(State.TIMED_WAITING)) 
            		interrupt();
                join(); //if join is interrupted try again
                done = true;
            } catch (InterruptedException e) { } 
        }
	}
}
