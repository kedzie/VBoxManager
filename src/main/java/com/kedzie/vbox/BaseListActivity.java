package com.kedzie.vbox;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;


public class BaseListActivity extends ListActivity {
	protected static final String TAG = BaseListActivity.class.getSimpleName();
	protected static final int DIALOG_PROGRESS = 1496;

	@Override
	protected Dialog onCreateDialog(int id, Bundle b) {
		Dialog dialog = null;
		switch(id) {
		case DIALOG_PROGRESS:
			dialog = new ProgressDialog(this);
			((ProgressDialog)dialog).setIndeterminate(true);
		}
		return dialog;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle b) {
		((ProgressDialog)dialog).setMessage(b.getString("msg"));
	}

	public void showProgress(String msg) {
		Bundle b = new Bundle();
		b.putString("msg", msg);
		showDialog(DIALOG_PROGRESS, b);
	}
	
	public void dismissProgress() {
		dismissDialog(DIALOG_PROGRESS);
	}
	
	public VBoxApplication getVBoxApplication() {
		return (VBoxApplication)getApplication();
	}
}
