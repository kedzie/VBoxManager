package com.kedzie.vbox.task;

import com.kedzie.vbox.soap.VBoxSvc;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Shows progress on the {@link ActionBar}
 * @param <Input> task input
 * @param <Output> task output
 * @author Marek Kędzierski
 */
public abstract class ActionBarTask<Input, Output> extends BaseTask<Input, Output> {

	
	/**
	 * @param context calling Activity
	 * @param vmgr VirtualBox API service
	 */
	public ActionBarTask(AppCompatActivity context, VBoxSvc vmgr) {
		super(context, vmgr);
	}
}
