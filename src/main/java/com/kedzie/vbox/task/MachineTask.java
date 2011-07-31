package com.kedzie.vbox.task;

import android.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.kedzie.vbox.BaseListActivity;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.WebSessionManager;

public abstract class MachineTask extends AsyncTask<IMachine, Void, IMachine> {
		protected BaseListActivity activity;
		protected WebSessionManager _vmgr;
		protected ProgressDialog pDialog;
		protected Handler _handler;

		public MachineTask(final BaseListActivity activity, WebSessionManager vmgr, String msg) {
			this.activity = activity;
			_vmgr = vmgr;
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage(msg);
			pDialog.setIndeterminate(false);
			pDialog.setProgressStyle(R.style.Widget_ProgressBar_Horizontal);
			_handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					new AlertDialog.Builder(activity)
						.setMessage(msg.getData().getString("msg"))
						.setPositiveButton("OK", new OnClickListener() { @Override public void onClick(DialogInterface dialog, int which) { 	finish();}})
						.show();
				}
			};
		}
		
		@Override
		protected void onPreExecute()		{
			pDialog.show();
		}
		
		@Override
		protected IMachine doInBackground(IMachine... params)	{
			try	{
				return work(params[0], _vmgr);
			} catch(Exception e)	{
				Message m = _handler.obtainMessage();
				m.setData(VBoxApplication.createBundle("msg", e.getMessage()));
				m.sendToTarget();
			}
			return params[0];
		}
		
		protected abstract IMachine work(IMachine m, WebSessionManager vmgr) throws Exception;
		
		@Override
		protected void onPostExecute(IMachine result)	{
			pDialog.dismiss();
		}
}
