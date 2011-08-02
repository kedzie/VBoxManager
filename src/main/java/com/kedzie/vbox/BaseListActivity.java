package com.kedzie.vbox;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;


public class BaseListActivity<T> extends ListActivity {
	protected static final String TAG = BaseListActivity.class.getSimpleName();
	protected static final int DIALOG_PROGRESS = 1496;
	protected static final int DIALOG_ALERT = 1497;
	
	protected Handler _h;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_h = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				new AlertDialog.Builder(BaseListActivity.this)
				.setMessage(msg.getData().getString("msg"))
				.setPositiveButton("OK", new OnClickListener() { @Override public void onClick(DialogInterface dialog, int which) { 	finish();}})
				.show();
			}
       	};
	}
	
	@SuppressWarnings("unchecked")
	protected ArrayAdapter<T> getAdapter() {
		return (ArrayAdapter<T>)getListAdapter();
	}
	
	public void showAlert(String msg) {
		Message m = _h.obtainMessage();
		m.setData(createBundle("msg", msg));
		m.sendToTarget();
	}
	
	public void showAlert(Exception e) {
		Log.e(TAG, "Error", e);
		if(e.getMessage()==null || "".equals(e.getMessage())) {
			if(e.getCause() !=null && e.getCause().getMessage() !=null && !e.getCause().getMessage().equals(""))
				showAlert(e.getCause().getMessage());
			else
				showAlert(e.toString());
		} else {
			showAlert(e.getMessage());
		}
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle b) {
		Dialog d=null;
		switch(id) {
		case DIALOG_PROGRESS:
			d = new ProgressDialog(this);
			((ProgressDialog)d).setIndeterminate(true);
			return d;
		case DIALOG_ALERT:
			d= new AlertDialog.Builder(this)
			.setPositiveButton("OK", new OnClickListener() {@Override public void onClick(DialogInterface dialog, int which) { finish();	}})
			.create();
		}
		return d;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle b) {
		switch(id) {
		case DIALOG_PROGRESS:
			ProgressDialog p = (ProgressDialog)dialog;
			p.setMessage(b.getString("msg"));
			return;
		case DIALOG_ALERT:
			AlertDialog a =(AlertDialog)dialog; 
			a.setMessage(b.getString("msg"));
			return;
		}
	}

	protected Bundle createBundle(String key, String value) {
		Bundle b = new Bundle();
		b.putString(key, value);
		return b;
	}
}
