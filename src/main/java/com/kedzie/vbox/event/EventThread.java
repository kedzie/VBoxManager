package com.kedzie.vbox.event;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class EventThread extends Thread {
	private static final String TAG = "vbox.EventThread";
	private boolean _running=false;
	private Context _context;
	private Handler _h;
	
	public EventThread(Context ctx, Handler h) {
		_context=ctx;
		_h=h;
	}

	@Override
	public void run() {
		_running=true;
		while(_running) {
			try {
				Thread.sleep(1000);
				_h.sendEmptyMessage(1);
			} catch (InterruptedException e) {
				Log.e(TAG, "Event Thread interrupted", e);
			}
		}
	}
	
	public void postStop() {
		_running=false;
	}
}
