package com.kedzie.vbox.host;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Throwables;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IDHCPServer;
import com.kedzie.vbox.api.IHost;
import com.kedzie.vbox.api.IHostNetworkInterface;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.jaxb.HostNetworkInterfaceType;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.Tuple;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.ActionBarTask;
import com.kedzie.vbox.task.DialogTask;

import org.ksoap2.SoapFault;

import java.util.ArrayList;
import java.util.List;

public class HostNetworkListFragment extends Fragment {

	private View _view;
	private ListView _listView;
	private TextView _hostInterfaceAdapterText;
	private TextView _hostInterfaceDHCPEnabledText;
	private ArrayAdapter<IHostNetworkInterface> _listAdapter;
//	private boolean _dualPane;

	private VBoxSvc _vmgr;
	private IHost _host;
	private ArrayList<IHostNetworkInterface> _interfaces;
	private ArrayList<IDHCPServer> _dhcpServers;
	
	
	/**
	 * create a network interface
	 */
	private class AddInterfaceTask extends DialogTask<IHost, IHostNetworkInterface> {
		
		public AddInterfaceTask() {
			super((AppCompatActivity)getActivity(), _host.getAPI(), R.string.progress_creating_network_interface);
		}
		
		@Override
		protected IHostNetworkInterface work(IHost...params) throws Exception {
			Tuple<IHostNetworkInterface, IProgress> val = _vmgr.createHostOnlyNetworkInterface(params[0]);
			handleProgress(val.second);
			return val.first;
		};
		
		@Override
		protected void onSuccess(IHostNetworkInterface result) {
			super.onSuccess(result);
			new LoadDataTask().execute();
			showInterfaceDialog(result);
		}
	}
	
	/**
	 * Delete a network interface
	 */
	private class DeleteInterfaceTask extends ActionBarTask<IHostNetworkInterface, IHostNetworkInterface> {
		
		public DeleteInterfaceTask() {
			super((AppCompatActivity)getActivity(), _host.getAPI());
		}
		
		@Override
		protected IHostNetworkInterface work(IHostNetworkInterface...params) throws Exception {
			handleProgress(_host.removeHostOnlyNetworkInterface(params[0].getId()));
			return params[0];
		};
		
		@Override
		protected void onSuccess(IHostNetworkInterface result) {
			super.onSuccess(result);
			new LoadDataTask().execute();
		}
	}
	
	/**
	 * Load list of host network interfaces
	 */
	private class LoadDataTask extends ActionBarTask<Void, ArrayList<IHostNetworkInterface>> {
		
		public LoadDataTask() {
			super((AppCompatActivity)getActivity(), HostNetworkListFragment.this._vmgr);
		}
		
		@Override
		protected ArrayList<IHostNetworkInterface> work(Void... params) throws Exception {
			_host = _vmgr.getVBox().getHost();
			ArrayList<IHostNetworkInterface> data = _host.findHostNetworkInterfacesOfType(HostNetworkInterfaceType.HOST_ONLY);
			_dhcpServers = new ArrayList<IDHCPServer>(data.size());
			for(IHostNetworkInterface net : data) {
				net.getId();
				net.getName();
				net.getNetworkName();
				net.getDHCPEnabled();
				try {
				IDHCPServer dhcp = _vmgr.getVBox().findDHCPServerByNetworkName(net.getNetworkName());
				if(dhcp!=null) {
					dhcp.getEnabled();
					_dhcpServers.add(dhcp);
				}
				} catch(Throwable e) {
					Throwable cause = Throwables.getRootCause(e);
					if(cause instanceof SoapFault) {
						SoapFault sf = (SoapFault) cause;
						Log.e(TAG, "SoapFault finding DHCP Server " + sf.detail.getText(0), e);
					}
					_dhcpServers.add(null);
				}
			}
			return data;
		}
		
		@Override
		protected void onSuccess(ArrayList<IHostNetworkInterface> result) {
			super.onSuccess(result);
			_interfaces = result;
			Log.d(TAG, "# of interfaces: " + _interfaces.size());
			_listAdapter = new ItemAdapter(getActivity(), _interfaces);
			_listView.setAdapter(_listAdapter);
		}
	}
	
	/**
     * List adapter for Fragments
     */
    private class ItemAdapter extends ArrayAdapter<IHostNetworkInterface> {
        private LayoutInflater inflater;
        
        public ItemAdapter(Context context, List<IHostNetworkInterface> objects) {
            super(context, 0, objects);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	IHostNetworkInterface info = getItem(position);
            if(convertView==null) {
            	convertView = inflater.inflate(R.layout.simple_selectable_list_item, parent, false);
            	convertView.setTag((TextView)convertView.findViewById(android.R.id.text1));
            }
            TextView text1 = (TextView)convertView.getTag();
            text1.setText(info.getName());
            return convertView;
        }
    }
	
	void showInterfaceDialog(IHostNetworkInterface hostInterface) {
		Utils.showDialog(getFragmentManager(), "dialog", HostNetworkDialog.getInstance(new BundleBuilder().putParcelable(IHostNetworkInterface.BUNDLE, hostInterface).create()));
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList("interfaces", _interfaces);
		outState.putParcelable(IHost.BUNDLE, _host);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		_vmgr = BundleBuilder.getVBoxSvc(getArguments());
		if(savedInstanceState!=null) {
			_host = savedInstanceState.getParcelable(IHost.BUNDLE);
			_interfaces = savedInstanceState.getParcelableArrayList("interfaces");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		_dualPane = getActivity().findViewById(R.id.details)!=null;
		_view = inflater.inflate(R.layout.host_settings_network_list, null);
		_listView = (ListView)_view.findViewById(R.id.host_interface_list);
		_listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		_listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				_listView.setItemChecked(position, true);
				_hostInterfaceAdapterText.setText("Manually Configured");
				IDHCPServer dhcp = _dhcpServers.get(position);
				if(dhcp!=null && dhcp.getEnabled())
					_hostInterfaceDHCPEnabledText.setText("Enabled");
				else
					_hostInterfaceDHCPEnabledText.setText("Disabled");
			}
		});
		registerForContextMenu(_listView);
		_hostInterfaceAdapterText = (TextView)_view.findViewById(R.id.host_interface_adapter);
		_hostInterfaceDHCPEnabledText = (TextView)_view.findViewById(R.id.host_interface_dhcp_enabled);
		return _view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		new LoadDataTask().execute();
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.host_interface_actions, menu);
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.option_menu_add:
				new AddInterfaceTask().execute(_host);
				return true;
		}
		return false;
	}
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE, R.id.host_interface_context_menu_edit, Menu.NONE, R.string.edit);
        menu.add(Menu.NONE, R.id.host_interface_context_menu_delete, Menu.NONE, R.string.delete);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
    	int position = ((AdapterContextMenuInfo)item.getMenuInfo()).position;
    	final IHostNetworkInterface hostInterface = _listAdapter.getItem(position);
    	switch (item.getItemId()) {
    		case R.id.host_interface_context_menu_edit:
    			showInterfaceDialog(hostInterface);
    			return true;
    		case R.id.host_interface_context_menu_delete:
    			new DeleteInterfaceTask().execute(hostInterface);
    			return true;
    	}
    	return false;
    }
}