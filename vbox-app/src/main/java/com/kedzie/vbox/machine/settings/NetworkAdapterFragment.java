package com.kedzie.vbox.machine.settings;

import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IHostNetworkInterface;
import com.kedzie.vbox.api.INetworkAdapter;
import com.kedzie.vbox.api.jaxb.HostNetworkInterfaceType;
import com.kedzie.vbox.api.jaxb.NetworkAdapterPromiscModePolicy;
import com.kedzie.vbox.api.jaxb.NetworkAdapterType;
import com.kedzie.vbox.api.jaxb.NetworkAttachmentType;
import com.kedzie.vbox.app.Tuple;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.task.DialogTask;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @apiviz.stereotype fragment
 */
public class NetworkAdapterFragment extends Fragment {
    private static final String TAG = "NetworkAdapterFragment";

	class LoadInfoTask extends DialogTask<INetworkAdapter, Tuple<INetworkAdapter, String[]>> {
		
		public LoadInfoTask() {
			super((AppCompatActivity)getActivity(), _adapter.getAPI(), R.string.progress_loading_data_generic);
		}

		@Override 
		protected Tuple<INetworkAdapter, String[]> work(INetworkAdapter...params) throws Exception {
			INetworkAdapter adapter = params[0];
			params[0].clearCache();
			adapter.getAdapterType(); adapter.getAttachmentType(); adapter.getCableConnected(); 
			adapter.getEnabled(); adapter.getPromiscModePolicy(); adapter.getMACAddress();
			String[] interfaces = null;
			if(adapter.getAttachmentType().equals(NetworkAttachmentType.BRIDGED)) {
				adapter.getBridgedInterface();
				List<IHostNetworkInterface> types = _vmgr.getVBox().getHost().findHostNetworkInterfacesOfType(HostNetworkInterfaceType.BRIDGED);
				interfaces = new String[types.size()+1];
				interfaces[0] = "Not Attached";
				for(int i=0; i<types.size(); i++)
					interfaces[i+1] = types.get(i).getName();
			} else if(adapter.getAttachmentType().equals(NetworkAttachmentType.HOST_ONLY)) {
				adapter.getHostOnlyInterface();
				List<IHostNetworkInterface> types = _vmgr.getVBox().getHost().findHostNetworkInterfacesOfType(HostNetworkInterfaceType.HOST_ONLY);
				interfaces = new String[types.size()+1];
				interfaces[0] = "Not Attached";
				for(int i=0; i<types.size(); i++)
					interfaces[i+1] = types.get(i).getName();
			} else if(adapter.getAttachmentType().equals(NetworkAttachmentType.GENERIC)) {
				adapter.getGenericDriver();
			} else if(adapter.getAttachmentType().equals(NetworkAttachmentType.INTERNAL)) {
				adapter.getInternalNetwork();
			} else if(adapter.getAttachmentType().equals(NetworkAttachmentType.NAT)) {
				interfaces = new String[1];
				interfaces[0] = "Not Attached";
			} 
			return new Tuple<INetworkAdapter, String[]>(adapter, interfaces);
		}
		@Override
		protected void onSuccess(Tuple<INetworkAdapter, String[]> result) {
			super.onSuccess(result);
			_adapter = result.first;
			Log.d(TAG, "Host Interfaces: " + Arrays.toString(result.second));
		    _hostInterfaces = result.second;
		    if(_hostInterfaces!=null) {
		    	_nameAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, _hostInterfaces);
		    	_nameSpinner.setAdapter(_nameAdapter);
		    }
		    populate();
		}
	}
	
	private INetworkAdapter _adapter;
	private String[] _hostInterfaces;
	
    @BindView(R.id.network_enabled)
	 CheckBox _enabledCheckBox;
    @BindView(R.id.network_name_text)
     EditText _nameText;
    @BindView(R.id.network_mac)
     EditText _macText;
    @BindView(R.id.network_cable_connected)
	 CheckBox _cableConnectedCheckBox;

	@BindView(R.id.network_attached)
	 Spinner _attachmentTypeSpinner;
	private ArrayAdapter<NetworkAttachmentType> _attachmentTypeAdapter;
	private NetworkAttachmentType[] _attachmentTypes = Utils.removeNull(NetworkAttachmentType.values());

	@BindView(R.id.network_name_spinner)
	 Spinner _nameSpinner;
	private ArrayAdapter<String> _nameAdapter;

    @BindView(R.id.network_adapter_type)
	 Spinner _adapterTypeSpinner;
	private ArrayAdapter<NetworkAdapterType> _adapterTypeAdapter;
	private NetworkAdapterType[] _adapterTypes = Utils.removeNull(NetworkAdapterType.values());

	@BindView(R.id.network_promiscuous)
	 Spinner _promiscuousModeSpinner;
	private ArrayAdapter<NetworkAdapterPromiscModePolicy> _promiscuousModeAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_adapter = (INetworkAdapter) (savedInstanceState==null ? getArguments().getParcelable(INetworkAdapter.BUNDLE) : savedInstanceState.getParcelable(INetworkAdapter.BUNDLE));
		if(savedInstanceState!=null) {
		    _hostInterfaces = savedInstanceState.getStringArray("interfaces");
		}
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
        outState.putStringArray("interfaces", _hostInterfaces);
        outState.putParcelable(INetworkAdapter.BUNDLE, _adapter);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.settings_network_adapter, null);
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        _adapterTypeAdapter = new ArrayAdapter<NetworkAdapterType>(getActivity(), android.R.layout.simple_spinner_item, _adapterTypes);
        _adapterTypeSpinner.setAdapter(_adapterTypeAdapter);
        _attachmentTypeAdapter = new ArrayAdapter<NetworkAttachmentType>(getActivity(), android.R.layout.simple_spinner_item, _attachmentTypes);
        _attachmentTypeSpinner.setAdapter(_attachmentTypeAdapter);
        _promiscuousModeAdapter = new ArrayAdapter<NetworkAdapterPromiscModePolicy>(getActivity(), android.R.layout.simple_spinner_item, NetworkAdapterPromiscModePolicy.values());
        _promiscuousModeSpinner.setAdapter(_promiscuousModeAdapter);
    }

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(_adapter.getCache().containsKey("getEnabled"))
			safePopulate();
		else
			new LoadInfoTask().execute(_adapter);
	}

    private void safePopulate() {
        try {
            populate();
        } catch(NetworkOnMainThreadException e) {
            Log.w(TAG, "Populate error. reloading data" + e);
            new LoadInfoTask().execute(_adapter);
        }
    }

	private void populate() {
		boolean enabled = _adapter.getEnabled();
		_enabledCheckBox.setChecked(enabled);
		_enabledCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				_adapter.setEnabled(isChecked);
				new LoadInfoTask().execute(_adapter);
			}
		});
		_cableConnectedCheckBox.setEnabled(enabled);
		_macText .setEnabled(enabled);
		_adapterTypeSpinner.setEnabled(enabled);
		_attachmentTypeSpinner.setEnabled(enabled);
		_nameSpinner.setEnabled(enabled);
		_nameText.setEnabled(enabled);
		_promiscuousModeSpinner.setEnabled(enabled);

		_attachmentTypeSpinner.setSelection(Utils.indexOf(_attachmentTypes, _adapter.getAttachmentType()));
		_attachmentTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				_adapter.setAttachmentType(_attachmentTypeAdapter.getItem(position));
				new LoadInfoTask().execute(_adapter);
			}
			@Override public void onNothingSelected(AdapterView<?> parent) {}
		});
		if(_adapter.getAttachmentType().equals(NetworkAttachmentType.BRIDGED)) {
			_nameSpinner.setVisibility(View.VISIBLE);
			_nameText.setVisibility(View.GONE);
			if(Utils.isEmpty(_adapter.getBridgedInterface()))
				_nameSpinner.setSelection(0);
			else
				_nameSpinner.setSelection(Utils.indexOf(_hostInterfaces, _adapter.getBridgedInterface()));
		} else if(_adapter.getAttachmentType().equals(NetworkAttachmentType.HOST_ONLY)) {
			_nameSpinner.setVisibility(View.VISIBLE);
			_nameText.setVisibility(View.GONE);
			if(Utils.isEmpty(_adapter.getHostOnlyInterface()))
				_nameSpinner.setSelection(0);
			else
				_nameSpinner.setSelection(Utils.indexOf(_hostInterfaces, _adapter.getHostOnlyInterface()));
		} else if(_adapter.getAttachmentType().equals(NetworkAttachmentType.GENERIC)) {
			_nameSpinner.setVisibility(View.GONE);
			_nameText.setVisibility(View.VISIBLE);
			_nameText.setText(_adapter.getGenericDriver());
		} else if(_adapter.getAttachmentType().equals(NetworkAttachmentType.INTERNAL)) {
			_nameSpinner.setVisibility(View.GONE);
			_nameText.setVisibility(View.VISIBLE);
			_nameText.setText(_adapter.getInternalNetwork());
		} else if(_adapter.getAttachmentType().equals(NetworkAttachmentType.NAT)) {
			_nameSpinner.setVisibility(View.VISIBLE);
			_nameText.setVisibility(View.GONE);
			_nameSpinner.setSelection(0);
			_nameSpinner.setEnabled(false);
		} 
		_nameSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(_adapter.getAttachmentType().equals(NetworkAttachmentType.BRIDGED))
					_adapter.setBridgedInterface(_nameAdapter.getItem(position));
				else if(_adapter.getAttachmentType().equals(NetworkAttachmentType.HOST_ONLY))
					_adapter.setHostOnlyInterface(_nameAdapter.getItem(position));
				else if(_adapter.getAttachmentType().equals(NetworkAttachmentType.GENERIC))
					_adapter.setGenericDriver(_nameAdapter.getItem(position));
				else if(_adapter.getAttachmentType().equals(NetworkAttachmentType.INTERNAL))
					_adapter.setInternalNetwork(_nameAdapter.getItem(position));
			}
			@Override public void onNothingSelected(AdapterView<?> parent) {}
		});
		_adapterTypeSpinner.setSelection(Utils.indexOf(_adapterTypes, _adapter.getAdapterType()));
		_adapterTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				_adapter.setAdapterType(_adapterTypeAdapter.getItem(position));
			}
			@Override public void onNothingSelected(AdapterView<?> parent) {}
		});
		_promiscuousModeSpinner.setSelection(Utils.indexOf(NetworkAdapterPromiscModePolicy.values(), _adapter.getPromiscModePolicy()));
		_promiscuousModeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				_adapter.setPromiscModePolicy(_promiscuousModeAdapter.getItem(position));
			}
			@Override public void onNothingSelected(AdapterView<?> parent) {}
		});
		_cableConnectedCheckBox.setChecked( _adapter.getCableConnected());
		_cableConnectedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				_adapter.setCableConnected(isChecked);
			}
		});
		_macText.setText(_adapter.getMACAddress());
		_macText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				_adapter.setMACAddress(_macText.getText().toString());
			}
			@Override	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override public void afterTextChanged(Editable s) {}
		});
	}
}
