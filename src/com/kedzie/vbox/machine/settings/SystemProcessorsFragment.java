package com.kedzie.vbox.machine.settings;

import android.os.Bundle;
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
 * 
 * @author Marek Kędzierski
 * @apiviz.stereotype fragment
 */
public class SystemProcessorsFragment extends SherlockFragment {

	class LoadInfoTask extends ActionBarTask<IMachine, IHost> {
		public LoadInfoTask() { super("LoadInfoTask", getSherlockActivity(), _machine.getVBoxAPI()); }

		@Override 
		protected IHost work(IMachine... m) throws Exception {
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
	private Bundle errorMessages = new Bundle();
	
	private void showErrors() {
		String msg = "";
		for (String key : errorMessages.keySet()) 
			msg+=errorMessages.getString(key)+"\n";
		_errorText.setText(msg);
	}
	
	private void showError(String field,String msg) {
		if(msg==null)
			errorMessages.remove(field);
		else
			errorMessages.putString(field, msg);
		showErrors();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_machine = BundleBuilder.getProxy(savedInstanceState!=null ? savedInstanceState : getArguments(), IMachine.BUNDLE, IMachine.class);
		if(savedInstanceState!=null) {
		    _host = savedInstanceState.getParcelable("host");
		    errorMessages = savedInstanceState.getBundle("errors");
		}
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(IMachine.BUNDLE, _machine);
        outState.putParcelable("host", _host);
        outState.putBundle("errors", errorMessages);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		_view = inflater.inflate(R.layout.settings_system_processors, null);
		_processorsBar = (SliderView)_view.findViewById(R.id.processors);
		_executionCapBar = (SliderView)_view.findViewById(R.id.execution_cap);
		_errorText = (TextView)_view.findViewById(R.id.error_message);
		showErrors();
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
