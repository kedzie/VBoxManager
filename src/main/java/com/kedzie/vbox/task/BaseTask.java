package com.kedzie.vbox.task;

import java.io.IOException;

import android.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.kedzie.vbox.BaseListActivity;
import com.kedzie.vbox.VBoxApplication.BundleBuilder;
import com.kedzie.vbox.WebSessionManager;
import com.kedzie.vbox.api.IProgress;

public abstract class BaseTask<Input, Output> extends AsyncTask<Input, IProgress, Output> {
		private static final String TAG = "vbox."+BaseTask.class.getSimpleName();
		protected Context context;
		protected ProgressDialog pDialog;
		protected WebSessionManager _vmgr;
		protected Handler _handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				new AlertDialog.Builder(context)
					.setMessage(msg.getData().getString("msg"))
					.setPositiveButton("OK", new OnClickListener() { @Override public void onClick(DialogInterface dialog, int which) { 	dialog.dismiss();}})
					.show();
			}
		};
		
		public BaseTask(final Context ctx, WebSessionManager vmgr, String msg, boolean indeterminate, Handler h) {
			this(ctx, vmgr, msg, indeterminate);
			this._handler=h;
		}

		public BaseTask(final Context ctx, WebSessionManager vmgr, String msg, boolean indeterminate) {
			this.context= ctx;
			_vmgr=vmgr;
			pDialog = new ProgressDialog(ctx);
			pDialog.setMessage(msg);
			pDialog.setIndeterminate(indeterminate);
			if(!indeterminate) pDialog.setProgressStyle(R.style.Widget_ProgressBar_Horizontal);
		}
		
		@Override
		protected void onProgressUpdate(IProgress... p) {
			try {
				pDialog.setMessage(p[0].getOperationDescription());
				pDialog.setSecondaryProgress(p[0].getOperationPercent());
				pDialog.setProgress(p[0].getPercent());
				pDialog.setTitle(p[0].getDescription());
			} catch (IOException e) {
				showAlert(e);
			}
		}
		
		protected void showAlert(Throwable e) {
			Log.e(TAG, e.getMessage(), e);
			while(e.getCause()!=null) e = e.getCause();
			new BundleBuilder().putString("msg", e.getMessage()).sendMessage(_handler, BaseListActivity.WHAT_ERROR);
		}
		
		@Override
		protected void onPreExecute()		{
			pDialog.show();
		}
		
		@Override
		protected Output doInBackground(Input... params)	{
			try	{
				return work(params);
			} catch(Exception e)	{
				showAlert(e);
			}
			return null;
		}
		
		protected abstract Output work(Input...params) throws Exception;
		
		@Override
		protected void onPostExecute(Output result)	{
			pDialog.dismiss();
		}
}
