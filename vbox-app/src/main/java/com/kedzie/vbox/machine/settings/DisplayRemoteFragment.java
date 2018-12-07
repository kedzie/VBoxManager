package com.kedzie.vbox.machine.settings;

import android.os.Bundle;
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
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IVRDEServer;
import com.kedzie.vbox.api.jaxb.AuthType;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.task.ActionBarTask;

import java.util.Arrays;

/**
 * Edit remote desktop server
 * @apiviz.stereotype fragment
 */
public class DisplayRemoteFragment extends Fragment {
	
	/**
	 * load Remote Desktop Server info
	 */
	class LoadDataTask extends ActionBarTask<IMachine, IVRDEServer> {
		
		public LoadDataTask() { 
			super((AppCompatActivity)getActivity(), _machine.getAPI());
		}
		
		@Override 
		protected IVRDEServer work(IMachine... m) throws Exception {
			IVRDEServer server = m[0].getVRDEServer();
			Log.i(TAG, "VRDE Properties: " + Arrays.toString(server.getVRDEProperties()));
			Log.i(TAG, "TCP/Ports: " + server.getVRDEProperty(IVRDEServer.PROPERTY_PORT));
			server.getEnabled();
			server.getAuthTimeout();
			server.getAuthType();
			server.getAllowMultiConnection();
			return server;
		}
		@Override
		protected void onSuccess(IVRDEServer result) {
		        _server = result;
				populate();
		}
	}
	
	private View _view;
	private EditText _portText;
	private Spinner _authMethodSpinner;
	private ArrayAdapter<AuthType>_authMethodAdapter; 
	private EditText _authTimeoutText;
	private CheckBox _enabledCheckBox;
	
	private CheckBox _multipleConnectionsCheckBox;
	
	private IMachine _machine;
	private IVRDEServer _server;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_machine = (IMachine)getArguments().getParcelable(IMachine.BUNDLE);
		if(savedInstanceState!=null) {
			_server = (IVRDEServer)savedInstanceState.getParcelable(IVRDEServer.BUNDLE);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		_view = inflater.inflate(R.layout.settings_display_remote, null);
		_portText = (EditText)_view.findViewById(R.id.server_port);
		_authTimeoutText = (EditText)_view.findViewById(R.id.auth_timeout);
		_authMethodSpinner = (Spinner)_view.findViewById(R.id.auth_method);
		_authMethodAdapter = new ArrayAdapter<AuthType>(getActivity(), android.R.layout.simple_spinner_item, AuthType.values());
		_authMethodSpinner.setAdapter(_authMethodAdapter);
		_enabledCheckBox = (CheckBox)_view.findViewById(R.id.enabled);
		_multipleConnectionsCheckBox = (CheckBox)_view.findViewById(R.id.multiple_connections);
		return _view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(_server!=null) 
			populate();
		else 
			new LoadDataTask().execute(_machine);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		BundleBuilder.putProxy(outState, IVRDEServer.BUNDLE, _server);
	}

	private void populate() {
		_enabledCheckBox.setChecked(_server.getEnabled());
		_enabledCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				_server.setEnabled(isChecked);
			}
		});
		_portText.setText(_server.getVRDEProperty("TCP/Ports"));
		_portText.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				_server.setVRDEProperty("TCP/Ports", s.toString());
			}
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
		});
		_authMethodSpinner.setSelection(Utils.indexOf(AuthType.values(), _server.getAuthType()));
		_authMethodSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				_server.setAuthType(_authMethodAdapter.getItem(position));
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
			});
		_authTimeoutText.setText(_server.getAuthTimeout()+"");
		_authTimeoutText.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				_server.setAuthTimeout(Integer.valueOf(_authTimeoutText.getText().toString()));
			}
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
		});
		_multipleConnectionsCheckBox.setChecked(_server.getAllowMultiConnection());
		_multipleConnectionsCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				_server.setAllowMultiConnection(isChecked);
			}
		});
	}
}