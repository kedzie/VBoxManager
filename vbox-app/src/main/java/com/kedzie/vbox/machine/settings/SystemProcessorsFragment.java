package com.kedzie.vbox.machine.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IHost;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.jaxb.CPUPropertyType;
import com.kedzie.vbox.api.jaxb.ProcessorFeature;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.SliderView;
import com.kedzie.vbox.app.SliderView.OnSliderViewChangeListener;
import com.kedzie.vbox.task.DialogTask;

/**
 * @apiviz.stereotype fragment
 */
public class SystemProcessorsFragment extends Fragment {

	class LoadInfoTask extends DialogTask<IMachine, IHost> {
		public LoadInfoTask() { super((AppCompatActivity)getActivity(), _machine.getAPI(), R.string.progress_loading_data_generic); }

		@Override 
		protected IHost work(IMachine... m) throws Exception {
			m[0].getCPUCount(); 
			m[0].getCPUProperty(CPUPropertyType.PAE);
			IHost host = _vmgr.getVBox().getHost();
			host.getProcessorFeature(ProcessorFeature.PAE);
			host.getProcessorCount();
			host.getProcessorOnlineCount();
			return host;
		}
		@Override
		protected void onSuccess(IHost result) {
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
	private CheckBox _paeCheckBox;
	private ErrorSupport _errorHandler = new ErrorSupport();
	
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
		super.onSaveInstanceState(outState);
        outState.putParcelable(IMachine.BUNDLE, _machine);
        outState.putParcelable("host", _host);
        outState.putParcelable("errors", _errorHandler);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		_view = inflater.inflate(R.layout.settings_system_processors, null);
		_processorsBar = (SliderView)_view.findViewById(R.id.processors);
		_executionCapBar = (SliderView)_view.findViewById(R.id.execution_cap);
		_paeCheckBox = (CheckBox) _view.findViewById(R.id.pae_nx);
		_errorText = (TextView)_view.findViewById(R.id.error_message);
		_errorHandler.setTextView(_errorText);
		_errorHandler.showErrors();
		return _view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(_host!=null) 
			populateViews(_machine, _host);
		else 
			new LoadInfoTask().execute(_machine);
	}

	private void populateViews(IMachine m, IHost host) {
		_paeCheckBox.setEnabled(_host.getProcessorFeature(ProcessorFeature.PAE));
		_paeCheckBox.setChecked(m.getCPUProperty(CPUPropertyType.PAE));
		_paeCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				_machine.setCPUProperty(CPUPropertyType.PAE, isChecked);
			}
		});
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
				_errorHandler.showError("Execution Cap", "");
			}
			@Override
			public void onSliderInvalidValueChanged(int newValue) {
				_errorHandler.showError("Execution Cap", "Invalid Execution Cap");
			}
		});
	}
}
