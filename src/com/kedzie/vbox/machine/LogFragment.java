package com.kedzie.vbox.machine;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.task.ActionBarTask;

/**
 * 
 * @author Marek Kędzierski
 * @apiviz.stereotype fragment
 */
public class LogFragment extends SherlockFragment {
	private static final int MAX_LOG_SIZE=409600; //400 Kbps
	
	class LoadLogTask extends ActionBarTask<IMachine, String> {
		public LoadLogTask() {
			super("LoadLogTask", getSherlockActivity(), null);
		}

		@Override 
		protected String work(IMachine... m) throws Exception {
			return new String(m[0].readLog(0, 0, MAX_LOG_SIZE));
		}

		@Override
		protected void onResult(String result) {
			if(result.length()==MAX_LOG_SIZE)	
			    Log.w(TAG,"Log size: " + result.length());
			_logText.setText(_log=result);
		}
	}

	private TextView _logText;
	private IMachine _machine;
	private String _log;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_machine = BundleBuilder.getProxy(getArguments(), IMachine.BUNDLE, IMachine.class);
		if(savedInstanceState!=null)
			_log = savedInstanceState.getString("log");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.machine_log, null);
		_logText = (TextView)view.findViewById(R.id.logText);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(_log!=null) 
			_logText.setText(_log);
		else
			new LoadLogTask().execute(_machine);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString("log", _log);
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		switch(item.getItemId()) {
		case R.id.option_menu_refresh:
			new LoadLogTask().execute(_machine);
			return true;
		}
		return false;
	}
}
