package com.kedzie.vbox.task;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.app.BundleBuilder;
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

	private VBoxProgressDialog pDialog;
	
	/**
	 * Constructor in <em>Indeterminate</em> operation
	 * @param context Android <code>Context</code>
	 * @param vmgr VirtualBox API service
	 * @param msg  operation description string resource
	 */
	public DialogTask(SherlockFragmentActivity context, VBoxSvc vmgr, int msg) {
		this(context, vmgr, context.getResources().getString(msg));
	}
	
	/**
	 * Constructor in <em>Determinate</em> operation
	 * @param context Android <code>Context</code>
	 * @param vmgr VirtualBox API service
	 * @param msg  operation description
	 */
	public DialogTask(SherlockFragmentActivity context, VBoxSvc vmgr, String msg) {
		this(context, vmgr, msg, false);
	}
	
	/**
	 * Constructor in <em>Determinate</em> operation
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
	 * @param context 			Android <code>Context</code>
	 * @param vmgr 				VirtualBox API service
	 * @param msg  				operation description
	 * @param cancelable		whether the dialog is cancelable with the <em>Back</em> button
	 */
	public DialogTask(SherlockFragmentActivity context, VBoxSvc vmgr, String msg, boolean cancelable) {
		super(context, vmgr);
        pDialog = new VBoxProgressDialog();
        pDialog.setArguments(new BundleBuilder().putString("msg", msg).putBoolean("cancelable", cancelable).create());
        pDialog.setTask(this);
	}
	
	@Override
	protected void onPreExecute()		{
        Utils.showDialog(getContext().getSupportFragmentManager(), "progress", pDialog);
	}
	
	@Override
	protected void onPostExecute(Output result)	{
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
		pDialog.update(p[0]);
	}
}
