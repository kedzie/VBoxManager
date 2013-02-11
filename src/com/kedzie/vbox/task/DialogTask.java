package com.kedzie.vbox.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Message;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.soap.VBoxSvc;

/**
 * Shows progress in a modal dialog
 * @param <Input>			type of task input argument(s)
 * @param <Output>		type of task output argument
 */
public abstract class DialogTask<Input, Output> extends BaseTask<Input, Output> {

	protected ProgressDialog pDialog;
	
	/**
	 * Constructor in <em>Indeterminate</em> operation
	 * @param TAG LogCat tag
	 * @param context Android <code>Context</code>
	 * @param vmgr VirtualBox API service
	 * @param msg  operation description string resource
	 */
	public DialogTask(String TAG, Context context, VBoxSvc vmgr, int msg) {
		this(TAG, context, vmgr, context.getResources().getString(msg));
	}
	
	/**
	 * Constructor in <em>Determinate</em> operation
	 * @param TAG LogCat tag
	 * @param ctx Android <code>Context</code>
	 * @param vmgr VirtualBox API service
	 * @param msg  operation description
	 */
	public DialogTask(String TAG, Context context, VBoxSvc vmgr, String msg) {
		this(TAG, context, vmgr, msg, false);
	}
	
	/**
	 * Constructor in <em>Determinate</em> operation
	 * @param TAG 				LogCat tag
	 * @param context 			Android <code>Context</code>
	 * @param vmgr 				VirtualBox API service
	 * @param msg  				operation description
	 * @param cancelable		whether the dialog is cancelable with the <em>Back</em> button
	 */
	public DialogTask(String TAG, Context context, VBoxSvc vmgr, String msg, boolean cancelable) {
		super(TAG, context, vmgr);
		pDialog = new ProgressDialog(_context.get());
		pDialog.setMessage(msg);
		pDialog.setIndeterminate(true);
		pDialog.setCancelable(cancelable);
		if(cancelable)
			pDialog.setCancelMessage(_cancelHandler.obtainMessage(0));
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	}
	
	@Override
	protected void onPreExecute()		{
			pDialog.show();
	}
	
	@Override
	protected void onPostExecute(Output result)	{
			pDialog.dismiss();
			super.onPostExecute(result);
	}

	@Override
    protected Output work(Input... params) throws Exception {
        return null;
    }

    @Override
	protected void onProgressUpdate(IProgress... p) {
		if(pDialog.isIndeterminate()) {	//Dismiss Indeterminate progress dialog and display the determinate one.
			pDialog.dismiss();
			pDialog = new ProgressDialog(_context.get());
			pDialog.setTitle(p[0].getDescription());
			pDialog.setIndeterminate(false);
			pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pDialog.setCancelable(p[0].getCancelable()); 
			if(p[0].getCancelable()) {
				Message cancelMessage = _cancelHandler.obtainMessage(0);
				cancelMessage.setData(new BundleBuilder().putProxy("progress", p[0]).create());
				pDialog.setCancelMessage(cancelMessage);
			}
			pDialog.show();
		}
		pDialog.setMessage(_context.get().getResources().getString(R.string.progress_message, p[0].getOperation(), p[0].getOperationCount(), p[0].getOperationDescription(), p[0].getOperationPercent(),p[0].getTimeRemaining() ));
		pDialog.setProgress(p[0].getPercent());
		pDialog.setSecondaryProgress(p[0].getOperationPercent());
	}
}
