package com.kedzie.vbox;

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

import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.WebSessionManager;

public abstract class BaseTask<Input, Output> extends AsyncTask<Input, IProgress, Output> {
		private static final String TAG = "vbox."+BaseTask.class.getSimpleName();
		protected Context context;
		protected ProgressDialog pDialog;
		protected Handler _handler;
		protected WebSessionManager _vmgr;

		public BaseTask(final Context ctx, WebSessionManager vmgr, String msg, boolean indeterminate) {
			this.context= ctx;
			_vmgr=vmgr;
			pDialog = new ProgressDialog(ctx);
			pDialog.setMessage(msg);
			pDialog.setIndeterminate(indeterminate);
			if(!indeterminate) pDialog.setProgressStyle(R.style.Widget_ProgressBar_Horizontal);
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
				showAlert(e);
			}
		}
		
		protected void showAlert(Exception e) {
			Log.e(TAG, e.getMessage(), e);
			Message m = _handler.obtainMessage();
			m.setData(VBoxApplication.createBundle("msg", e.getMessage()));
			m.sendToTarget();
		}
		
		@Override
		protected void onPreExecute()		{
			_handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					new AlertDialog.Builder(context)
						.setMessage(msg.getData().getString("msg"))
						.setPositiveButton("OK", new OnClickListener() { @Override public void onClick(DialogInterface dialog, int which) { 	dialog.dismiss();}})
						.show();
				}
			};
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
