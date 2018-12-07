package com.kedzie.vbox.machine.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IHost;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.jaxb.ChipsetType;
import com.kedzie.vbox.api.jaxb.FirmwareType;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.SliderView;
import com.kedzie.vbox.app.SliderView.OnSliderViewChangeListener;
import com.kedzie.vbox.task.DialogTask;

/**
 * @apiviz.stereotype fragment
 */
public class SystemMotherboardFragment extends Fragment {

	class LoadInfoTask extends DialogTask<IMachine, IHost> {
		public LoadInfoTask() { super((AppCompatActivity)getActivity(), _machine.getAPI(), R.string.progress_loading_data_generic); }

		@Override 
		protected IHost work(IMachine... m) throws Exception {
			m[0].getMemorySize();
			m[0].getRTCUseUTC();
			m[0].getBIOSSettings().getIOAPICEnabled();
			m[0].getChipsetType();
			m[0].getFirmwareType();
			IHost host = _vmgr.getVBox().getHost();
			host.getMemoryAvailable();
			host.getMemorySize();
			return host;
		}
		@Override
		protected void onSuccess(IHost result) {
			super.onSuccess(result);
		    _host = result;
		    populateViews(_machine, _host);
		}
	}
	
	private IMachine _machine;
	private IHost _host;
	private View _view;
	private TextView _errorText;
	private SliderView _baseMemoryBar;
	private CheckBox _ioApicCheckbox;
	private CheckBox _efiCheckbox;
	private CheckBox _utcCheckbox;
	private Spinner _chipsetSpinner;
	private ArrayAdapter<ChipsetType> _chipsetAdapter;
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
		_view = inflater.inflate(R.layout.settings_system_motherboard, null);
		_baseMemoryBar = (SliderView)_view.findViewById(R.id.baseMemory);
		_ioApicCheckbox = (CheckBox) _view.findViewById(R.id.io_apic);
		_efiCheckbox = (CheckBox) _view.findViewById(R.id.efi);
		_utcCheckbox = (CheckBox) _view.findViewById(R.id.utc);
		_chipsetSpinner = (Spinner) _view.findViewById(R.id.chipset);
		_chipsetAdapter = new ArrayAdapter<ChipsetType>(getActivity(), android.R.layout.simple_spinner_item, new ChipsetType [] { ChipsetType.PIIX_3, ChipsetType.ICH_9 } );
		_chipsetSpinner.setAdapter(_chipsetAdapter);
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
		_utcCheckbox.setChecked(_machine.getRTCUseUTC());
		_utcCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				_machine.setRTCUseUTC(isChecked);
			}
		});
		FirmwareType type = _machine.getFirmwareType();
		_efiCheckbox.setChecked(type.equals(FirmwareType.EFI) || 
				type.equals(FirmwareType.EFI_32) ||
				type.equals(FirmwareType.EFI_64) ||
				type.equals(FirmwareType.EFIDUAL));
		_efiCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					_machine.setFirmwareType(isChecked ? FirmwareType.EFI : FirmwareType.BIOS);
			}
		});
		_ioApicCheckbox.setChecked(_machine.getBIOSSettings().getIOAPICEnabled());
		_ioApicCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				_machine.getBIOSSettings().setIOAPICEnabled(isChecked);
			}
		});
		_chipsetSpinner.setSelection(_machine.getChipsetType().equals(ChipsetType.PIIX_3) ? 0 : 1);
		_chipsetSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				_machine.setChipsetType(_chipsetAdapter.getItem(position));
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		_baseMemoryBar.setMinValue(1);
		_baseMemoryBar.setMinValidValue(1);
		_baseMemoryBar.setMaxValidValue((int)(host.getMemorySize()*.8f));
		_baseMemoryBar.setMaxValue(host.getMemorySize());
		_baseMemoryBar.setValue(m.getMemorySize());
	    _baseMemoryBar.setOnSliderViewChangeListener(new OnSliderViewChangeListener() {
			@Override
			public void onSliderValidValueChanged(int newValue) {
				_machine.setMemorySize(newValue*1024);
				_errorHandler.showError("Base Memory", "");
			}
			@Override
			public void onSliderInvalidValueChanged(int newValue) {
				_errorHandler.showError("Base Memory", "Not enough memory for Operating System");
			}
		});
	}
}
