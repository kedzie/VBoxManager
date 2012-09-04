package com.kedzie.vbox.task;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.soap.VBoxSvc;

/**
 * Shows progress on the {@link ActionBar}
 * @param <Input> task input
 * @param <Output> task output
 * @author Marek Kędzierski
 */
public abstract class ActionBarTask<Input, Output> extends BaseTask<Input, Output> {

	protected SherlockFragmentActivity _sherlockActivity;
	
	/**
	 * @param TAG LogCat tag
	 * @param ctx calling Activity
	 * @param vmgr VirtualBox API service
	 */
	public ActionBarTask(String TAG, SherlockFragmentActivity ctx, VBoxSvc vmgr) {
		super(TAG, ctx, vmgr);
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
