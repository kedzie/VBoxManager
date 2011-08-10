package com.kedzie.vbox.task;

import java.io.IOException;

import android.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.kedzie.vbox.BaseListActivity;
import com.kedzie.vbox.VBoxApplication.BundleBuilder;
import com.kedzie.vbox.VBoxSvc;
import com.kedzie.vbox.api.IProgress;

/**
 * VirtualBox API Asynchronous task with progress & error handling
 * @param <Input>  Operation input argument 
 * @param <Output> Operation output
 * @author Marek Kedzierski
 * @Aug 8, 2011
 */
public abstract class BaseTask<Input, Output> extends AsyncTask<Input, IProgress, Output> {
		private static final String TAG = "vbox."+BaseTask.class.getSimpleName();
		protected final static int PROGRESS_INTERVAL = 500;
		
		protected Context context;
		protected ProgressDialog pDialog;
		protected VBoxSvc _vmgr;
		protected Handler _handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				new AlertDialog.Builder(context)
					.setMessage(msg.getData().getString("msg"))
					.setPositiveButton("OK", new OnClickListener() { @Override public void onClick(DialogInterface dialog, int which) { 	dialog.dismiss();}})
					.show();
			}
		};

		/**
		 * @param ctx Android <code>Context</code>
		 * @param vmgr VirtualBox API service
		 * @param msg <code>ProgressDialog</code> operation description
		 * @param indeterminate <code>ProgressDialog.setIndeterminate()</code> true if indeterminate progress, false if progress is determinate
		 */
		public BaseTask(Context ctx, VBoxSvc vmgr, String msg, boolean indeterminate) {
			this.context= ctx;
			_vmgr=vmgr;
			pDialog = new ProgressDialog(ctx);
			pDialog.setMessage(msg);
			pDialog.setIndeterminate(indeterminate);
			pDialog.setProgressStyle(indeterminate ? ProgressDialog.STYLE_SPINNER : ProgressDialog.STYLE_HORIZONTAL);
		}
		
		/**
		 * @param ctx Android <code>Context</code>
		 * @param vmgr VirtualBox API service
		 * @param msg <code>ProgressDialog</code> operation description
		 * @param indeterminate <code>ProgressDialog.setIndeterminate()</code> true if indeterminate progress, false if progress is determinate
		 * @param handler		<code>android.os.handler</code> for error handling.  <code>msg</code> parameter contains error message
		 */
		public BaseTask(final Context ctx, VBoxSvc vmgr, String msg, boolean indeterminate, Handler h) {
			this(ctx, vmgr, msg, indeterminate);
			this._handler=h;
		}

		@Override
		protected void onProgressUpdate(IProgress... p) {
			try {
				pDialog.setTitle(p[0].getDescription());
				pDialog.setMessage(p[0].getOperation() + "/" + p[0].getOperationCount() + " - " + p[0].getOperationDescription());
				pDialog.setProgress(p[0].getPercent());
				pDialog.setSecondaryProgress(p[0].getOperationPercent());
			} catch (IOException e) {
				showAlert(e);
			}
		}
		
		protected void showAlert(Throwable e) {
			Log.e(TAG, e.getMessage(), e);
			while(e.getCause()!=null) e = e.getCause();
			new BundleBuilder().putString("msg", e.getMessage()).sendMessage(_handler, BaseListActivity.WHAT_ERROR);
		}
		
		protected IProgress handleProgress(IProgress p)  throws IOException {
			if(p==null) return null;
			while(!p.getCompleted()) {
				publishProgress(p);
				try { Thread.sleep(PROGRESS_INTERVAL);	} catch (InterruptedException e) { Log.e(TAG, "Interrupted", e); 	}
			}
			return p;
		}
		
		@Override
		protected void onPreExecute()		{
			pDialog.show();
		}
		
		@Override
		protected Output doInBackground(Input... params)	{
			try	{
				return work(params);
			} catch(Exception e)	{
				showAlert(e);
			}
			return null;
		}
		
		protected abstract Output work(Input...params) throws Exception;
		
		@Override
		protected void onPostExecute(Output result)	{
			pDialog.dismiss();
		}
}
