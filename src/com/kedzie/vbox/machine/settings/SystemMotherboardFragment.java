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
public class SystemMotherboardFragment extends SherlockFragment {
	private static final String TAG = "SystemMotherboardFragment";

	class LoadInfoTask extends ActionBarTask<IMachine, IHost> {
		public LoadInfoTask() { super("SystemMotherboardFragment", getSherlockActivity(), _machine.getVBoxAPI()); }

		@Override 
		protected IHost work(IMachine... m) throws Exception {
			Log.d(TAG, "Loading data");
			m[0].getMemorySize();
			IHost host = _vmgr.getVBox().getHost();
			host.getMemoryAvailable();
			host.getMemorySize();
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
	private SliderView _baseMemoryBar;
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
		_view = inflater.inflate(R.layout.settings_system_motherboard, null);
		_baseMemoryBar = (SliderView)_view.findViewById(R.id.baseMemory);
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
		_baseMemoryBar.setMinValue(1);
		_baseMemoryBar.setMinValidValue(1);
		_baseMemoryBar.setMaxValidValue((int)(host.getMemorySize()*.8f));
		_baseMemoryBar.setMaxValue(host.getMemorySize());
		_baseMemoryBar.setValue(m.getMemorySize());
	    _baseMemoryBar.setOnSliderViewChangeListener(new OnSliderViewChangeListener() {
			@Override
			public void onSliderValidValueChanged(int newValue) {
				_machine.setMemorySize(newValue);
				_errorHandler.showError("Base Memory", "");
			}
			@Override
			public void onSliderInvalidValueChanged(int newValue) {
				_errorHandler.showError("Base Memory", "Not enough memory for Operating System");
			}
		});
	}
}
