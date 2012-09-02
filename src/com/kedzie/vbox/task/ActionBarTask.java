package com.kedzie.vbox.task;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.soap.VBoxSvc;

public abstract class ActionBarTask<Input, Output> extends BaseTask<Input, Output> {

	protected SherlockFragmentActivity _sherlockActivity;
	
	/**
	 * @param TAG LogCat tag
	 * @param ctx Android <code>Context</code>
	 * @param vmgr VirtualBox API service
	 * @param msg  operation description
	 */
	public ActionBarTask(String TAG, SherlockFragmentActivity ctx, VBoxSvc vmgr) {
		super(TAG, vmgr);
		_sherlockActivity = ctx;
	}
	
	@Override
	protected void onPreExecute()		{
		_sherlockActivity.setSupportProgressBarIndeterminateVisibility(true);
	}
	
	@Override
	protected void onPostExecute(Output result)	{
		_sherlockActivity.setSupportProgressBarIndeterminateVisibility(false);
		_sherlockActivity.setSupportProgressBarVisibility(false);
	}

	@Override
	protected void onProgressUpdate(IProgress... p) {
		if(_indeterminate) {
			_indeterminate=false;
			_sherlockActivity.setSupportProgressBarIndeterminateVisibility(false);
			_sherlockActivity.setSupportProgressBarIndeterminate(false);
			_sherlockActivity.setSupportProgressBarVisibility(true);
		}
		_sherlockActivity.setSupportProgress(p[0].getPercent());
		_sherlockActivity.setSupportSecondaryProgress(p[0].getOperationPercent());
	}
}
