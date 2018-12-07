package com.kedzie.vbox.host;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TabHost;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IDHCPServer;
import com.kedzie.vbox.api.IHostNetworkInterface;
import com.kedzie.vbox.app.Tuple;
import com.kedzie.vbox.task.ActionBarTask;

public class HostNetworkDialog extends DialogFragment {

	/**
	 * Load Data
	 */
	private class LoadDataTask extends ActionBarTask<IHostNetworkInterface , Tuple<IHostNetworkInterface, IDHCPServer>> {

		public LoadDataTask() {
			super((AppCompatActivity)getActivity(), _interface.getAPI());
		}

		@Override
		protected Tuple<IHostNetworkInterface, IDHCPServer> work(IHostNetworkInterface... params) throws Exception {
			params[0].getIPAddress(); params[0].getNetworkMask();
			params[0].getIPV6Address(); params[0].getIPV6NetworkMaskPrefixLength();
			IDHCPServer dhcp = _vmgr.findDHCPServerByNetworkName(params[0].getNetworkName());
			if(dhcp!=null) {
				dhcp.getIPAddress(); dhcp.getLowerIP(); dhcp.getUpperIP(); dhcp.getNetworkMask();
			}
			return new Tuple<IHostNetworkInterface, IDHCPServer>(params[0], dhcp);
		}

		@Override
		protected void onSuccess(Tuple<IHostNetworkInterface, IDHCPServer> result) {
			super.onSuccess(result);
			_interface = result.first;
			_dhcp = result.second;
			populate();
		}
	}


	private class SaveTask extends ActionBarTask<Void, Void> {

		public SaveTask() {
			super((AppCompatActivity)getActivity(), null);
		}

		@Override
		protected Void work(Void... params) throws Exception {
			_interface.enableStaticIPConfig(_ipv4IpText.getText().toString(), _ipv4MaskText.getText().toString());
			_interface.enableStaticIPConfigV6(_ipv6IpText.getText().toString(), Integer.valueOf(_ipv6MaskText.getText().toString()));
			if(_enabledCheckBox.isChecked()) {
				if(_dhcp==null) //enabled DHCP for the first time
					_dhcp = _interface.getAPI().getVBox().createDHCPServer(_interface.getNetworkName());
				_dhcp.setEnabled(true);
				_dhcp.setConfiguration(_addressText.getText().toString(), _maskText.getText().toString(), _lowerBoundText.getText().toString(), _upperBoundText.getText().toString());
			} else {
				_dhcp.setEnabled(false);
			}
			return null;
		}

		@Override
		protected void onSuccess(Void result) {
			super.onSuccess(result);
			new LoadDataTask().execute(_interface);
		}
	}

	public static HostNetworkDialog getInstance(Bundle arguments) {
		HostNetworkDialog fragment = new HostNetworkDialog();
		fragment.setArguments(arguments);
		return fragment;
	}

	private TabHost mTabHost;

	private EditText _ipv4IpText;
	private EditText _ipv4MaskText;
	private EditText _ipv6IpText;
	private EditText _ipv6MaskText;
	private CheckBox _enabledCheckBox;
	private EditText _addressText;
	private EditText _maskText;
	private EditText _lowerBoundText;
	private EditText _upperBoundText;

	private Button _okButton;
	private Button _cancelButton;

	private IHostNetworkInterface _interface;
	private IDHCPServer _dhcp;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog =  super.onCreateDialog(savedInstanceState);
		dialog.setTitle("Host Network Interface");
		return dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_interface = (IHostNetworkInterface)getArguments().getParcelable(IHostNetworkInterface.BUNDLE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.host_settings_network, container, false);
		_ipv4IpText =(EditText)view.findViewById(R.id.hostnet_ipv4_ip);
		_ipv4MaskText =(EditText)view.findViewById(R.id.hostnet_ipv4_mask);
		_ipv6IpText =(EditText)view.findViewById(R.id.hostnet_ipv6_ip);
		_ipv6MaskText =(EditText)view.findViewById(R.id.hostnet_ipv6_mask);
		_enabledCheckBox = (CheckBox)view.findViewById(R.id.dhcp_enabled);
		_enabledCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				_addressText.setEnabled(isChecked);
				_maskText.setEnabled(isChecked);
				_lowerBoundText.setEnabled(isChecked);
				_upperBoundText.setEnabled(isChecked);
			}
		});
		_addressText =(EditText)view.findViewById(R.id.dhcp_address);
		_addressText.setEnabled(false);
		_maskText =(EditText)view.findViewById(R.id.dhcp_mask);
		_maskText.setEnabled(false);
		_lowerBoundText =(EditText)view.findViewById(R.id.dhcp_lowerbound);
		_lowerBoundText.setEnabled(false);
		_upperBoundText =(EditText)view.findViewById(R.id.dhcp_upperbound);
		_upperBoundText.setEnabled(false);
		_okButton = (Button)view.findViewById(R.id.ok_button);
		_okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new SaveTask().execute();
				getDialog().dismiss();
			}
		});
		_cancelButton = (Button)view.findViewById(R.id.cancel_button);
		_cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getDialog().dismiss();
			}
		});
		mTabHost = (TabHost)view.findViewById(android.R.id.tabhost);
		mTabHost.setup();
		mTabHost.addTab(mTabHost.newTabSpec("static").setIndicator("Static").setContent(R.id.staticTab));
		mTabHost.addTab(mTabHost.newTabSpec("dhcp").setIndicator("DHCP").setContent(R.id.dhcpTab));
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		new LoadDataTask().execute(_interface);
	}

	void populate() {
		_ipv4IpText.setText(_interface.getIPAddress());
		_ipv4MaskText.setText(_interface.getNetworkMask());
		_ipv6IpText.setText(_interface.getIPV6Address());
		_ipv6MaskText.setText(_interface.getIPV6NetworkMaskPrefixLength()+"");
		if(_dhcp==null)
			return;
		_enabledCheckBox.setChecked(_dhcp.getEnabled());
		_addressText.setText(_dhcp.getIPAddress());
		_maskText.setText(_dhcp.getNetworkMask());
		_lowerBoundText.setText(_dhcp.getLowerIP());
		_upperBoundText.setText(_dhcp.getUpperIP());
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mTabHost = null;
	}
}

