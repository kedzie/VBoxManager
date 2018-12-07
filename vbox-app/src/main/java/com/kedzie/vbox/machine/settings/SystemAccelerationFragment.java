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
import com.kedzie.vbox.api.jaxb.HWVirtExPropertyType;
import com.kedzie.vbox.api.jaxb.ProcessorFeature;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.task.DialogTask;

/**
 * @apiviz.stereotype fragment
 */
public class SystemAccelerationFragment extends Fragment {

	class LoadInfoTask extends DialogTask<IMachine, IMachine> {
		public LoadInfoTask() { super((AppCompatActivity)getActivity(), _machine.getAPI(), R.string.progress_loading_data_generic); }

		@Override 
		protected IMachine work(IMachine... m) throws Exception {
			_host = _vmgr.getVBox().getHost();
			_host.getProcessorFeature(ProcessorFeature.HW_VIRT_EX);
			_host.getProcessorFeature(ProcessorFeature.NESTED_PAGING);
			_machine.getHWVirtExProperty(HWVirtExPropertyType.ENABLED);
			_machine.getHWVirtExProperty(HWVirtExPropertyType.NESTED_PAGING);
			return _machine;
		}
		@Override
		protected void onSuccess(IMachine result) {
			super.onSuccess(result);
			_machine=result;
		    populateViews(_machine);
		}
	}
	
	private IMachine _machine;
	private IHost _host;
	private View _view;
	private TextView _errorText;
	private CheckBox _vtxCheckbox;
	private CheckBox _nestedPagingCheckbox;
	private ErrorSupport _errorHandler = new ErrorSupport();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_machine = BundleBuilder.getProxy(savedInstanceState!=null ? savedInstanceState : getArguments(), IMachine.BUNDLE, IMachine.class);
		if(savedInstanceState!=null) {
		    _errorHandler = savedInstanceState.getParcelable("errors");
		}
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
        outState.putParcelable(IMachine.BUNDLE, _machine);
        
        outState.putParcelable("errors", _errorHandler);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		_view = inflater.inflate(R.layout.settings_system_acceleration, null);
		_vtxCheckbox = (CheckBox) _view.findViewById(R.id.vtx_amdv);
		_nestedPagingCheckbox = (CheckBox) _view.findViewById(R.id.nested_paging);
		_errorText = (TextView)_view.findViewById(R.id.error_message);
		_errorHandler.setTextView(_errorText);
		_errorHandler.showErrors();
		return _view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		new LoadInfoTask().execute(_machine);
	}

	private void populateViews(IMachine m) {
		_vtxCheckbox.setEnabled(_host.getProcessorFeature(ProcessorFeature.HW_VIRT_EX));
		_vtxCheckbox.setChecked(_machine.getHWVirtExProperty(HWVirtExPropertyType.ENABLED));
		_vtxCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				_machine.setHWVirtExProperty(HWVirtExPropertyType.ENABLED, isChecked);
			}
		});
		_nestedPagingCheckbox.setEnabled(_host.getProcessorFeature(ProcessorFeature.NESTED_PAGING));
		_nestedPagingCheckbox.setChecked(_machine.getHWVirtExProperty(HWVirtExPropertyType.NESTED_PAGING));
		_nestedPagingCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				_machine.setHWVirtExProperty(HWVirtExPropertyType.NESTED_PAGING, isChecked);
			}
		});
	}
}
