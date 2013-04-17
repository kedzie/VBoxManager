package com.kedzie.vbox.task;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.app.VBoxProgressDialog;
import com.kedzie.vbox.soap.VBoxSvc;

/**
 * Shows progress in a modal dialog
 * 
 * @param <Input>			type of task input argument(s)
 * @param <Output>		type of task output argument
 */
public abstract class DialogTask<Input, Output> extends BaseTask<Input, Output> {

	private ProgressDialog iDialog;
	private VBoxProgressDialog pDialog;
	
	/**
	 * Constructor in <em>Indeterminate</em> operation
	 * @param TAG LogCat tag
	 * @param context Android <code>Context</code>
	 * @param vmgr VirtualBox API service
	 * @param msg  operation description string resource
	 */
	public DialogTask(SherlockFragmentActivity context, VBoxSvc vmgr, int msg) {
		this(context, vmgr, context.getResources().getString(msg));
	}
	
	/**
	 * Constructor in <em>Determinate</em> operation
	 * @param TAG LogCat tag
	 * @param ctx Android <code>Context</code>
	 * @param vmgr VirtualBox API service
	 * @param msg  operation description
	 */
	public DialogTask(SherlockFragmentActivity context, VBoxSvc vmgr, String msg) {
		this(context, vmgr, msg, false);
	}
	
	/**
	 * Constructor in <em>Determinate</em> operation
	 * @param TAG 				LogCat tag
	 * @param context 			Android <code>Context</code>
	 * @param vmgr 				VirtualBox API service
	 * @param msg  				operation description string resource
	 * @param cancelable		whether the dialog is cancelable with the <em>Back</em> button
	 */
	public DialogTask(SherlockFragmentActivity context, VBoxSvc vmgr, int msg, boolean cancelable) {
		this(context, vmgr, context.getResources().getString(msg), cancelable);
	}
	
	/**
	 * Constructor in <em>Determinate</em> operation
	 * @param TAG 				LogCat tag
	 * @param context 			Android <code>Context</code>
	 * @param vmgr 				VirtualBox API service
	 * @param msg  				operation description
	 * @param cancelable		whether the dialog is cancelable with the <em>Back</em> button
	 */
	public DialogTask(SherlockFragmentActivity context, VBoxSvc vmgr, String msg, boolean cancelable) {
		super(context, vmgr);
		iDialog = new ProgressDialog(context);
		iDialog.setMessage(msg);
		iDialog.setCancelable(cancelable);
		if(cancelable)
		iDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                DialogTask.this.cancel(true);
            }
        });
		iDialog.setIndeterminate(true);
	}
	
	@Override
	protected void onPreExecute()		{
		iDialog.show();
	}
	
	@Override
	protected void onPostExecute(Output result)	{
			if(iDialog!=null)
				iDialog.dismiss(); 
			if(pDialog!=null)
				pDialog.dismiss();
			super.onPostExecute(result);
	}

	@Override
    protected Output work(Input... params) throws Exception {
        return null;
    }

    @Override
	protected void onProgressUpdate(IProgress... p) {
		if(iDialog!=null) {	//Dismiss Indeterminate progress dialog and display the determinate one.
			iDialog.dismiss();
			iDialog=null;
			pDialog = new VBoxProgressDialog();
			pDialog.setCancelable(p[0].getCancelable());
			if(p[0].getCancelable()) {
				pDialog.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						DialogTask.this.cancel(true);
					}
				});
			}
			Utils.showDialog(getContext().getSupportFragmentManager(), "progress", pDialog);
		}
		pDialog.update(p[0]);
	}
}
