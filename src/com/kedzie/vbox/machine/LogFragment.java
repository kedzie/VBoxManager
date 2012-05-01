package com.kedzie.vbox.machine;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.kedzie.vbox.BundleBuilder;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.task.BaseTask;

public class LogFragment extends SherlockFragment {
	private static final String TAG = LogFragment.class.getSimpleName();
	private static final int MAX_LOG_SIZE=1024;

	private View _view;
	private TextView _logText;
	private IMachine _machine;
	private String _log;

	public static LogFragment getInstance(Bundle args) {
		LogFragment f = new LogFragment();
		f.setArguments(args);
		return f;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(savedInstanceState!=null)
			_logText.setText(_log);
		else
			new LoadLogTask().execute(_machine);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_machine = BundleBuilder.getProxy(savedInstanceState!=null ? savedInstanceState : getArguments(), "machine", IMachine.class);
		if(savedInstanceState!=null)
			_log = savedInstanceState.getString("log");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		_view = inflater.inflate(R.layout.machine_log, null);
		_logText = (TextView)_view.findViewById(R.id.logText);
		return _view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		BundleBuilder.putProxy(outState, "machine", _machine);
		outState.putString("log", _log);
	}

	class LoadLogTask extends BaseTask<IMachine, String> {

		public LoadLogTask() {
			super(LoadLogTask.class.getSimpleName(), getSherlockActivity(), null, "Reading Log");
		}

		@Override 
		protected String work(IMachine... m) throws Exception {
			return new String(m[0].readLog(0, 0, MAX_LOG_SIZE));
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(result!=null) {
				Log.i(TAG,"Log size: " + result.length());
				_logText.setText(result);
			}
		}
	}
}
