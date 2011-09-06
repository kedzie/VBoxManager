package com.kedzie.vbox;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.VBoxApplication.BundleBuilder;


public class BaseListActivity extends ListActivity {
	protected static final String TAG = BaseListActivity.class.getSimpleName();
	public static final int WHAT_ERROR = 6, WHAT_ERROR_FINISH=8;
	
	protected Handler _alertHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case WHAT_ERROR:
				new AlertDialog.Builder(BaseListActivity.this).setTitle("Error").setMessage(msg.getData().getString("msg"))
					.setPositiveButton("OK", new OnClickListener() { @Override public void onClick(DialogInterface dialog, int which) { 	dialog.dismiss();}}).show();
				break;
			case WHAT_ERROR_FINISH:
				new AlertDialog.Builder(BaseListActivity.this).setTitle("Error").setMessage(msg.getData().getString("msg"))
					.setPositiveButton("OK", new OnClickListener() { @Override public void onClick(DialogInterface dialog, int which) { 	finish();}}).show();
				break;
			}
		}
   	};
	
	public VBoxApplication getApp() { 
		return (VBoxApplication)getApplication(); 
	}
	
	public void showAlert(String msg) {
		showAlert(msg, WHAT_ERROR);
	}
	
	public void showAlert(String msg, int what) {
		new BundleBuilder().putString("msg", msg).sendMessage(_alertHandler, what);
	}
	
	public void showAlert(Throwable e) {
		showAlert(e, WHAT_ERROR);
	}
	
	public void showAlert(Throwable e, int what) {
		Log.e(TAG, "Error", e);
		while(e.getCause()!=null) e = e.getCause();
		showAlert( (e.getMessage()==null || "".equals(e.getMessage())) ? e.toString() : e.getMessage(), what);
	}
}
