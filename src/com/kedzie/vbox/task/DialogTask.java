package com.kedzie.vbox.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Message;

import com.kedzie.vbox.BundleBuilder;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.soap.VBoxSvc;

/**
 * Shows progress in a modal dialog
 * @param <Input>
 * @param <Output>
 * @author Marek Kędzierski
 */
public abstract class DialogTask<Input, Output> extends BaseTask<Input, Output> {

	protected ProgressDialog pDialog;
	
	/**
	 * @param TAG LogCat tag
	 * @param ctx Android <code>Context</code>
	 * @param vmgr VirtualBox API service
	 * @param msg  operation description
	 */
	public DialogTask(String TAG, Context ctx, VBoxSvc vmgr, String msg) {
		super(TAG, ctx, vmgr);
		pDialog = new ProgressDialog(_context);
		pDialog.setMessage(msg);
		pDialog.setIndeterminate(true);
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	}
	
	@Override
	protected void onPreExecute()		{
			pDialog.show();
	}
	
	@Override
	protected final void onPostExecute(Output result)	{
			pDialog.dismiss();
			super.onPostExecute(result);
	}

	@Override
	protected void onProgressUpdate(IProgress... p) {
		if(pDialog.isIndeterminate()) {	//Dismiss Indeterminate progress dialog and display the determinate one.
			pDialog.dismiss();
			pDialog = new ProgressDialog(this._context);
			pDialog.setTitle(p[0].getDescription());
			pDialog.setIndeterminate(false);
			pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pDialog.setCancelable(p[0].getCancelable()); 
			if(p[0].getCancelable()) {
				Message cancelMessage = _handler.obtainMessage(WHAT_CANCEL);
				cancelMessage.setData(new BundleBuilder().putProxy("progress", p[0]).create());
				pDialog.setCancelMessage(cancelMessage);
			}
			pDialog.show();
		}
		pDialog.setMessage("Operation " + p[0].getOperation() + "/" + p[0].getOperationCount() + " - " + p[0].getOperationDescription() + " - " + p[0].getOperationPercent() + "%  remaining " + p[0].getTimeRemaining() + "secs");
		pDialog.setProgress(p[0].getPercent());
		pDialog.setSecondaryProgress(p[0].getOperationPercent());
	}
}
