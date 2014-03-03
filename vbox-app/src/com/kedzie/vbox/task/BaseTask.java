package com.kedzie.vbox.task;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.ksoap2.SoapFault;
import org.kxml2.kdom.Node;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
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
		
	protected WeakReference<SherlockFragmentActivity> _context;
	protected String TAG;
	/** VirtualBox web service API */
	 protected VBoxSvc _vmgr;
	 protected boolean _indeterminate=true;
	 /** <code>true</code> if user pressed back button while task is executing */
	protected boolean _cancelled=false;
	protected boolean _failed;
	private LinkedList<Future<?>> _futures = new LinkedList<Future<?>>();
	private ExecutorService _executor;
		
	/** 
	 * Show an Alert Dialog 
	 */
	protected Handler _alertHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
				new AlertDialog.Builder(_context.get())
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(msg.getData().getString("title"))
					.setMessage(msg.getData().getString("msg"))
					.setPositiveButton("OK", new OnClickListener() { @Override public void onClick(DialogInterface dialog, int which) { 	dialog.dismiss();}})
					.show();
		}
	};
	
	/**
	 * @param vmgr VirtualBox API service
	 */
	protected BaseTask(SherlockFragmentActivity context, VBoxSvc vmgr) {
        this("", context, vmgr);
		TAG = getClass().getSimpleName();
	}
	
	/**
	 * @param TAG LogCat tag
	 * @param vmgr VirtualBox API service
	 */
	protected BaseTask(String TAG, SherlockFragmentActivity context, VBoxSvc vmgr) {
		this.TAG = TAG;
		_vmgr=vmgr;
		_context=new WeakReference<SherlockFragmentActivity>(context);
        _executor = _vmgr!=null ? _vmgr.getExecutor() : Executors.newCachedThreadPool();
    }

    protected ExecutorService getExecutor() {
	    return _executor;
	}
	
	/**
	 * Fork off parallel execution
	 * @param task     the {@link Runnable} containing the task
	 */
	protected void fork(Runnable task) {
	    _futures.add(_executor.submit(task));
	}
	
	/**
	 * Wait for completion of all parallel executions
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	protected void join() throws InterruptedException, ExecutionException {
	    for(Future<?> future : _futures)
	        future.get();
	}
	
	protected SherlockFragmentActivity getContext() {
		return _context.get();
	}

	@Override
	protected Output doInBackground(Input... params)	{
		try	{
			Log.d(TAG, "Performing work...");
			return work(params);
		} catch(SoapFault e) {
			if(!_cancelled)
				showAlert(e);
		} catch(Exception e) {
		    if(!_cancelled) 
		        showAlert(e);
		}
		_failed=true;
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
		if(!_failed && getContext()!=null && !_cancelled)
			onSuccess(result);
		else if(_failed)
			onFailure();
	}
	
	/**
	 * Handle not-<code>null</code> result
	 * @param result the result
	 */
	protected void onSuccess(Output result) {}
	
	/**
	 * If work function threw exception
	 */
	protected void onFailure() {}
	
	@Override
    protected void onCancelled() {
	    Log.w(TAG, "Task Cancelled");
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
		Log.e(TAG, "SoapFault", e);
		Node detail = e.detail;
		while(detail.getChildCount()>0) {
		    Object child = detail.getChild(0);
		    if(child instanceof Node)
		        detail = (Node) detail.getChild(0);
		    else {
		        Log.i(TAG, "String detail: " + detail);
		        break;
		    }
		}
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
				.putString("title", getContext().getString(R.string.virtualbox_error_dialog_title))
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
			Utils.sleep(PROGRESS_INTERVAL);
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
