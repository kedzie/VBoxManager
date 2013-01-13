package com.kedzie.vbox.machine.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IHost;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.SliderView;
import com.kedzie.vbox.app.SliderView.OnSliderViewChangeListener;
import com.kedzie.vbox.task.ActionBarTask;

/**
 * @apiviz.stereotype fragment
 */
public class SystemProcessorsFragment extends SherlockFragment {
	private static final String TAG = "SystemProcessorsFragment";

	class LoadInfoTask extends ActionBarTask<IMachine, IHost> {
		public LoadInfoTask() { super("LoadInfoTask", getSherlockActivity(), _machine.getVBoxAPI()); }

		@Override 
		protected IHost work(IMachine... m) throws Exception {
			Log.d(TAG, "Loading data");
			m[0].getCPUCount(); 
			IHost host = _vmgr.getVBox().getHost();
			host.getProcessorCount();
			host.getProcessorOnlineCount();
			return host;
		}
		@Override
		protected void onResult(IHost result) {
		    _host = result;
		    populateViews(_machine, _host);
		}
	}
	
	private IMachine _machine;
	private IHost _host;
	private View _view;
	private TextView _errorText;
	private SliderView _processorsBar;
	private SliderView _executionCapBar;
	private ErrorCapability _errorHandler = new ErrorCapability();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_machine = BundleBuilder.getProxy(savedInstanceState!=null ? savedInstanceState : getArguments(), IMachine.BUNDLE, IMachine.class);
		if(savedInstanceState!=null) {
		    _host = savedInstanceState.getParcelable("host");
		    _errorHandler = savedInstanceState.getParcelable("errors");
		} 
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(IMachine.BUNDLE, _machine);
        outState.putParcelable("host", _host);
        outState.putParcelable("errors", _errorHandler);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		_view = inflater.inflate(R.layout.settings_system_processors, null);
		_processorsBar = (SliderView)_view.findViewById(R.id.processors);
		_executionCapBar = (SliderView)_view.findViewById(R.id.execution_cap);
		_errorText = (TextView)_view.findViewById(R.id.error_message);
		_errorHandler.setTextView(_errorText);
		_errorHandler.showErrors();
		return _view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(savedInstanceState!=null) 
			populateViews(_machine, _host);
		else 
			new LoadInfoTask().execute(_machine);
	}

	private void populateViews(IMachine m, IHost host) {
		Log.d(TAG, "Populating data");
	    _processorsBar.setMinValue(1);
	    _processorsBar.setMinValidValue(1);
	    _processorsBar.setMaxValidValue(host.getProcessorOnlineCount());
	    _processorsBar.setMaxValue(host.getProcessorOnlineCount());
		_processorsBar.setValue(m.getCPUCount());
		_processorsBar.setOnSliderViewChangeListener(new OnSliderViewChangeListener() {
			@Override
			public void onSliderValidValueChanged(int newValue) {
				_machine.setCPUCount(newValue);
			}
			@Override
			public void onSliderInvalidValueChanged(int newValue) {
			}
		});
		_executionCapBar.setOnSliderViewChangeListener(new OnSliderViewChangeListener() {
			@Override
			public void onSliderValidValueChanged(int newValue) {
				_machine.setCPUExecutionCap(newValue);
			}
			@Override
			public void onSliderInvalidValueChanged(int newValue) {
				_executionCapBar.setValue(_executionCapBar.getMinValidValue());
			}
		});
	}
}
