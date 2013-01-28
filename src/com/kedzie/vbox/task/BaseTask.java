package com.kedzie.vbox.task;

import java.io.IOException;

import org.ksoap2.SoapFault;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.IVirtualBoxErrorInfo;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.soap.VBoxSvc;

/**
 * VirtualBox® API Asynchronous task with progress & error handling.  
 * Don't subclass this directly, instead use {@link ActionBarTask} or {@link DialogTask}
 * @param <Input>  Operation input argument 
 * @param <Output> Operation output
 */
abstract class BaseTask<Input, Output> extends AsyncTask<Input, IProgress, Output> {
	/** interval used to update progress bar for longing-running operation*/
	protected final static int PROGRESS_INTERVAL = 200;
		
	protected Context _context;
	protected final String TAG;
	/** VirtualBox web service API */
	 protected VBoxSvc _vmgr;
	 protected boolean _indeterminate=true;
	 /** <code>true</code> if user pressed back button while task is executing */
	protected boolean _cancelled=false;
		
	/** 
	 * Show an Alert Dialog 
	 */
	protected Handler _alertHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
				new AlertDialog.Builder(_context)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(msg.getData().getString("title"))
					.setMessage(msg.getData().getString("msg"))
					.setPositiveButton("OK", new OnClickListener() { @Override public void onClick(DialogInterface dialog, int which) { 	dialog.dismiss();}})
					.show();
		}
	};
	
	/** 
     * Cancel the current operation
     */
	protected Handler _cancelHandler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	        Log.i(TAG, "Cancel Handler received Cancel Message");
	        Utils.toastLong(_context, _context.getString(R.string.cancelling_operation_toast));
	        IProgress progress = BundleBuilder.getProxy(msg.getData(), IProgress.BUNDLE, IProgress.class);
	        try {
	            progress.cancel();
	            BaseTask.this.cancel(true);
	        } catch (IOException e) {
	            Log.e(TAG, "Error cancelling operation", e);
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
			Log.d(TAG, "Performing work...");
			return work(params);
		} catch(Exception e) {
		    if(!_cancelled)
		        showAlert(e);
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
	
	@Override
	protected void onPostExecute(Output result) {
		super.onPostExecute(result);
		if(result!=null && _context!=null)
			onResult(result);
	}
	
	/**
	 * Handle not-null result
	 * @param result the result
	 */
	protected void onResult(Output result) {}
	
	@Override
    protected void onCancelled() {
	    Log.i(TAG, "Task Cancelled");
        _cancelled=true;
        super.onCancelled();
    }
	
	/**
	 * Show an Alert dialog 
	 * @param e <code>Throwable</code> which caused the error
	 */
	protected void showAlert(Throwable e) {
		Log.e(TAG, "caught throwable", e);
		while(Utils.isEmpty(e.getMessage()) && e.getCause()!=null)
			e = e.getCause();
		new BundleBuilder().putString("title", e.getClass().getSimpleName()).putString("msg", e.getMessage()).sendMessage(_alertHandler, 0);
	}
	
	/**
	 * Show an Alert dialog 
	 * @param e <code>Throwable</code> which caused the error
	 */
	protected void showAlert(SoapFault e) {
		Log.e(TAG, "caught SoapFault", e);
		new BundleBuilder().putString("title", "Soap Fault")
				.putString("msg", String.format("Code: %1$s\nActor: %2$s\nString: %3$s", e.faultcode, e.faultactor, e.faultstring))
				.sendMessage(_alertHandler, 0);
	}
	
	/**
	 * Show {@link IVirtualBoxErrorInfo}
	 * @param code result code
	 * @param msg error text
	 */
	protected void showAlert(int code, String msg) {
		Log.e(TAG, "Alert error: " + msg);
		new BundleBuilder()
				.putString("title", "VirtualBox error")
				.putString("msg", "Result Code: " + code + " - " + msg)
				.sendMessage(_alertHandler, 0);
	}

	/**
	 * Handle VirtualBox API progress functionality
	 * @param p  <code>IProgress</code> of the ongoing task
	 * @return <code>IProgress</code> of the finished task
	 * @throws IOException
	 */
	protected void handleProgress(IProgress p)  throws IOException {
		Log.d(TAG, "Handling progress");
		while(!p.getCompleted()) {
			cacheProgress(p);
			publishProgress(p);
			try { Thread.sleep(PROGRESS_INTERVAL);	} catch (InterruptedException e) { Log.e(TAG, "Interrupted", e); 	}
		}
		Log.d(TAG, "Operation Completed. result code: " + p.getResultCode());
		if(p.getResultCode()!=0) {
			IVirtualBoxErrorInfo info = p.getErrorInfo();
			showAlert(p.getResultCode(), info != null ? info.getText() : "No Message");
			return;
		}
		return;
	}
	
	private void cacheProgress(IProgress p ) throws IOException { 
		p.clearCacheNamed("getDescription", "getOperation", "getOperationDescription", "getOperationWeight", "getOperationPercent", "getTimeRemaining", "getPercent", "getCompleted");
		p.getDescription(); p.getOperation(); p.getOperationCount(); p.getOperationDescription(); 
		p.getPercent(); p.getOperationPercent(); p.getOperationWeight(); p.getTimeRemaining();
		p.getCompleted(); p.getCancelable();
	}
}
