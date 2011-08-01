package com.kedzie.vbox.task;

import java.io.IOException;

import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.SessionState;

import android.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.kedzie.vbox.BaseListActivity;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.WebSessionManager;

public abstract class MachineTask extends AsyncTask<IMachine, IProgress, IMachine> {
		private static final String TAG = "vbox."+MachineTask.class.getSimpleName();
		protected BaseListActivity activity;
		protected WebSessionManager _vmgr;
		protected ProgressDialog pDialog;
		protected Handler _handler;

		public MachineTask(final BaseListActivity activity, WebSessionManager vmgr, String msg, boolean indeterminate) {
			this.activity = activity;
			_vmgr = vmgr;
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage(msg);
			pDialog.setIndeterminate(indeterminate);
			pDialog.setProgressStyle(R.style.Widget_ProgressBar_Horizontal);
			
		}
		
		@Override
		protected void onPreExecute()		{
			_handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					new AlertDialog.Builder(activity)
						.setMessage(msg.getData().getString("msg"))
						.setPositiveButton("OK", new OnClickListener() { @Override public void onClick(DialogInterface dialog, int which) { 	dialog.dismiss();}})
						.show();
				}
			};
			pDialog.show();
		}
		
		@Override
		protected void onProgressUpdate(IProgress... values) {
			IProgress p = values[0];
			try {
				pDialog.setMessage(p.getOperationDescription());
				pDialog.setSecondaryProgress(p.getOperationPercent());
				pDialog.setProgress(p.getPercent());
				pDialog.setTitle(p.getDescription());
			} catch (IOException e) {
				Log.e(TAG, "Progress", e);
			}
		}

		@Override
		protected IMachine doInBackground(IMachine... params)	{
			try	{
				if( _vmgr.getSession().getState().equals(SessionState.Unlocked))
					params[0].lockMachine(_vmgr.getSession(), LockType.Shared);
				IProgress p = work(params[0], _vmgr, _vmgr.getSession().getConsole());
				if(p==null) return null;
				while(p.getCompleted()) {
					publishProgress(p);
					Thread.sleep(500);
				}
				return params[0];
			} catch(Exception e)	{
				Message m = _handler.obtainMessage();
				m.setData(VBoxApplication.createBundle("msg", e.getMessage()));
				m.sendToTarget();
			}
			return params[0];
		}
		
		protected abstract IProgress work(IMachine m, WebSessionManager vmgr, IConsole console) throws Exception;
		
		@Override
		protected void onPostExecute(IMachine result)	{
			pDialog.dismiss();
		}
}
