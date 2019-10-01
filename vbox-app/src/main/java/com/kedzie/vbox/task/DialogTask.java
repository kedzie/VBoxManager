package com.kedzie.vbox.task;

import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.VBoxProgressDialog;
import com.kedzie.vbox.soap.VBoxSvc;

import java.lang.ref.WeakReference;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Shows progress in a modal dialog
 * 
 * @param <Input>			type of task input argument(s)
 * @param <Output>		type of task output argument
 */
public abstract class DialogTask<Input, Output> extends BaseTask<Input, Output> {

	private VBoxProgressDialog pDialog;
    private boolean cancelable;
	
	/**
	 * Constructor in <em>Indeterminate</em> operation
	 * @param context Android <code>Context</code>
	 * @param vmgr VirtualBox API service
	 */
	public DialogTask(AppCompatActivity context, VBoxSvc vmgr) {
		this(context, vmgr, false);
	}
	
	/**
	 * Constructor in <em>Determinate</em> operation
	 * @param context 			Android <code>Context</code>
	 * @param vmgr 				VirtualBox API service
	 * @param cancelable		whether the dialog is cancelable with the <em>Back</em> button
	 */
	public DialogTask(AppCompatActivity context, VBoxSvc vmgr, boolean cancelable) {
		super(context, vmgr);
        this.cancelable=cancelable;
	}
	
	@Override
	protected void onPreExecute() {
        pDialog = new VBoxProgressDialog();
        pDialog.setArguments(new BundleBuilder().putBoolean("cancelable", cancelable).create());
        pDialog.setTask(this);
        pDialog.showAllowingStateLoss(getContext().getSupportFragmentManager().beginTransaction(), "progress");
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
        if(pDialog!=null)
		    pDialog.update(p[0]);
	}

    public void setProgressDialog(VBoxProgressDialog dialog) {
        pDialog = dialog;
    }

    public VBoxProgressDialog getProgressDialog() {
        return pDialog;
    }

    public void setActivity(AppCompatActivity activity) {
        this._context = new WeakReference<AppCompatActivity>(activity);
    }
}
