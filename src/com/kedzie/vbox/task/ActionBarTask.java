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
	
	/** # of active tasks, use to enable/disable indeterminate progress  */
	private static int _numActiveTasks;
	
	/**
	 * @param TAG LogCat tag
	 * @param ctx calling Activity
	 * @param vmgr VirtualBox API service
	 */
	public ActionBarTask(SherlockFragmentActivity context, VBoxSvc vmgr) {
		super(context, vmgr);
	}
	
	@Override
	protected void onPreExecute()		{
		_numActiveTasks++;
		getContext().setSupportProgressBarIndeterminateVisibility(true);
	}
	
	@Override
	protected final void onPostExecute(Output result)	{
		if(--_numActiveTasks==0)
			getContext().setSupportProgressBarIndeterminateVisibility(false);
		getContext().setSupportProgressBarVisibility(false);
		super.onPostExecute(result);
	}

	@Override
	protected void onProgressUpdate(IProgress... p) {
		if(_indeterminate) {
			_indeterminate=false;
			getContext().setSupportProgressBarIndeterminateVisibility(false);
			getContext().setSupportProgressBarIndeterminate(false);
			getContext().setSupportProgressBarVisibility(true);
		}
		getContext().setSupportProgress(p[0].getPercent());
		getContext().setSupportSecondaryProgress(p[0].getOperationPercent());
	}
}
