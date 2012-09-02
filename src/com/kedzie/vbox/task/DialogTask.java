package com.kedzie.vbox.task;

import android.app.ProgressDialog;
import android.content.Context;

import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.soap.VBoxSvc;

public abstract class DialogTask<Input, Output> extends BaseTask<Input, Output> {

	protected ProgressDialog pDialog;
	protected String description;
	
	/**
	 * @param TAG LogCat tag
	 * @param ctx Android <code>Context</code>
	 * @param vmgr VirtualBox API service
	 * @param msg  operation description
	 */
	public DialogTask(String TAG, Context ctx, VBoxSvc vmgr, String msg) {
		super(TAG, vmgr);
		this.context=ctx;
		description=msg;
		pDialog = new ProgressDialog(context);
		pDialog.setMessage(description);
		pDialog.setIndeterminate(true);
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	}
	
	@Override
	protected void onPreExecute()		{
			pDialog.show();
	}
	
	@Override
	protected void onPostExecute(Output result)	{
			pDialog.dismiss();
	}

	@Override
	protected void onProgressUpdate(IProgress... p) {
		if(pDialog.isIndeterminate()) {	//Dismiss Indeterminate progress dialog and display the determinate one.
			pDialog.dismiss();
			pDialog = new ProgressDialog(this.context);
			pDialog.setTitle(p[0].getDescription());
			pDialog.setIndeterminate(false);
			pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pDialog.setCancelable(p[0].getCancelable()); 
			pDialog.setCancelMessage(_handler.obtainMessage(WHAT_CANCEL));
			pDialog.show();
		}
		pDialog.setMessage("Operation " + p[0].getOperation() + "/" + p[0].getOperationCount() + " - " + p[0].getOperationDescription() + " - " + p[0].getOperationPercent() + "%  remaining " + p[0].getTimeRemaining() + "secs");
		pDialog.setProgress(p[0].getPercent());
		pDialog.setSecondaryProgress(p[0].getOperationPercent());
	}
}
