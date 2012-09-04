package com.kedzie.vbox.task;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.ksoap2.SoapFault;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.kedzie.vbox.BundleBuilder;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.IVirtualBoxErrorInfo;
import com.kedzie.vbox.soap.VBoxSvc;

/**
 * VirtualBox API Asynchronous task with progress & error handling.  
 * Don't subclass this directly, instead use {@link ActionBarTask} or {@link DialogTask}
 * 
 * @param <Input>  Operation input argument 
 * @param <Output> Operation output
 * @author Marek Kedzierski
 */
abstract class BaseTask<Input, Output> extends AsyncTask<Input, IProgress, Output> {
	private final String TAG;
	/** interval used to update progress bar for longing-running operation*/
	protected final static int PROGRESS_INTERVAL = 200;
	protected final static int WHAT_ERROR=6, WHAT_CANCEL=7;
		
	protected Context _context;
	/** VirtualBox web service API */
	 protected VBoxSvc _vmgr;
	 protected boolean _indeterminate=true;
		
	/** Show an Alert Dialog */
	protected Handler _handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case WHAT_ERROR:
				new AlertDialog.Builder(_context)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(msg.getData().getString("title"))
					.setMessage(msg.getData().getString("msg"))
					.setPositiveButton("OK", new OnClickListener() { @Override public void onClick(DialogInterface dialog, int which) { 	dialog.dismiss();}})
					.show();
				break;
			case WHAT_CANCEL:
				Toast.makeText(_context, "Cancelling Operation...", Toast.LENGTH_LONG).show();
				break;
			}
		}
	};
	
	/**
	 * @param TAG LogCat tag
	 * @param vmgr VirtualBox API service
	 */
	protected BaseTask(String TAG, Context ctx, VBoxSvc vmgr) {
		this.TAG = TAG;
		_vmgr=vmgr;
		_context=ctx;
	}

	@Override
	protected Output doInBackground(Input... params)	{
		try	{
			return work(params);
		} catch(SoapFault e) {
			showAlert(TAG, e);
		} catch(Throwable e)	{
			showAlert(TAG, e);
		}
		return null;
	}
	
	/**
	 * Implement task with default exception handling
	 * @param params task input parameters
	 * @return task return value
	 * @throws <code>Exception</code> any exception thrown will be displayed to user in an Alert dialog
	 */
	protected abstract Output work(Input...params) throws Exception;
	
	/**
	 * Show an Alert dialog 
	 * @param e <code>Throwable</code> which caused the error
	 */
	protected void showAlert(String tag, Throwable e) {
		Log.e(tag, "caught throwable", e);
		while(e.getCause()!=null)
			e = e.getCause();
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		new BundleBuilder()
				.putString("title", e.getClass().getSimpleName())
				.putString("msg", e.getMessage())
				.putString("stacktrace", sw.toString())
				.sendMessage(_handler, WHAT_ERROR);
	}
	
	protected void showAlert(String tag, int code, String msg) {
		Log.e(tag, "Alert error: " + msg);
		new BundleBuilder()
				.putString("title", "VirtualBox error")
				.putString("msg", "Result Code: " + code + " - " + msg)
				.sendMessage(_handler, WHAT_ERROR);
	}

	/**
	 * Handle VirtualBox API progress functionality
	 * @param p  <code>IProgress</code> of the ongoing task
	 * @return <code>IProgress</code> of the finished task
	 * @throws IOException
	 */
	protected void handleProgress(IProgress p)  throws IOException {
		if(p==null) { //TODO remove this null check
			Log.w(TAG, "Received NULL progress");
			return;
		}
		while(!p.getCompleted()) {
			cacheProgress(p);
			publishProgress(p);
			try { Thread.sleep(PROGRESS_INTERVAL);	} catch (InterruptedException e) { Log.e(TAG, "Interrupted", e); 	}
		}
		Log.i(TAG, "Operation Completed. result code: " + p.getResultCode());
		if(p.getResultCode()!=0) {
			IVirtualBoxErrorInfo info = p.getErrorInfo();
			showAlert(TAG, p.getResultCode(), info != null ? info.getText() : "No Message");
			return;
		}
		cacheProgress(p);
		publishProgress(p);
		return;
	}
	
	private void cacheProgress(IProgress p ) throws IOException { 
		p.clearCache();
		p.getDescription(); p.getOperation(); p.getOperationCount(); p.getOperationDescription(); 
		p.getPercent(); p.getOperationPercent(); p.getOperationWeight(); p.getTimeRemaining();
		p.getCompleted(); p.getResultCode(); p.getErrorInfo();
	}
}
