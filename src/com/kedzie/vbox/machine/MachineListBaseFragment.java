package com.kedzie.vbox.machine;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IManagedObjectRef;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.metrics.MetricPreferencesActivity;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.DialogTask;

/**
 * 
 * @author Marek Kędzierski
 * @apiviz.stereotype fragment
 */
public class MachineListBaseFragment extends SherlockFragment {
	protected static final String TAG = MachineListBaseFragment.class.getSimpleName();
	
	protected VBoxSvc _vmgr;
	protected List<IMachine> _machines;
	protected ListView _listView;
	protected String _version;
	/** Selected item index. -1 means it was not set in a saved state. */
	protected int _curCheckPosition=-1;
	/** Handles selection of a VM in the list */
	protected OnSelectMachineListener _machineSelectedListener;
	
	/**
	 * Listener who is notified when a VirtualMachine is selected from the list
	 */
	public static interface OnSelectMachineListener {
		/**
		 * Virtual Machine has been selected from list
		 * @param machine The selected {@link IMachine}
		 */
		public void onMachineSelected(IMachine machine);
	}
	
	/** 
	 * Load the Machines 
	 */
	class LoadMachinesTask extends DialogTask<Void, List<IMachine>>	{
		public LoadMachinesTask(VBoxSvc vmgr) { 
			super( "LoadMachinesTask", getActivity(), vmgr, "Loading Machines");
		}

		@Override
		protected List<IMachine> work(Void... params) throws Exception {
			List<IMachine> machines =_vmgr.getVBox().getMachines(); 
			_vmgr.getVBox().getHost().getMemorySize();
			for(IMachine m :  machines)
				MachineView.cacheProperties(m);
			_vmgr.getVBox().getPerformanceCollector().setupMetrics(new String[] { "*:" }, 
					Utils.getIntPreference(getActivity(), MetricPreferencesActivity.PERIOD), 
					Utils.getIntPreference(getActivity(), MetricPreferencesActivity.COUNT), 
					(IManagedObjectRef)null);
			_vmgr.getVBox().getVersion();
			return machines;
		}

		@Override
		protected void onResult(List<IMachine> result)	{
			_machines = result;
			_version = _vmgr.getVBox().getVersion();
			getSherlockActivity().getSupportActionBar().setSubtitle(getResources().getString(R.string.vbox_version, _version));
			_listView.setAdapter(new MachineListAdapter(result));
		}
	}
	
	/**
	 * List adapter for Virtual Machines
	 */
	class MachineListAdapter extends ArrayAdapter<IMachine> {
		public MachineListAdapter(List<IMachine> machines) {
			super(getActivity(), 0, machines);
		}
		
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) 
				view = new MachineView(getActivity());
			((MachineView)view).update(getItem(position));
			return view;
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		_machineSelectedListener = (OnSelectMachineListener)activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState!=null)  { 
			_version = savedInstanceState.getString("version");
			_curCheckPosition = savedInstanceState.getInt("curChoice", -1);
			_machines = savedInstanceState.getParcelableArrayList("machines");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		_listView = new ListView(getActivity());
       	_listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				showDetails(position);
			}
		});
       	return _listView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		_vmgr = (VBoxSvc)((getArguments() != null && getArguments().containsKey(VBoxSvc.BUNDLE)) ? 
		            getArguments().getParcelable(VBoxSvc.BUNDLE)
		            : getActivity().getIntent().getParcelableExtra(VBoxSvc.BUNDLE) );
		if(_version!=null) 
			getSherlockActivity().getSupportActionBar().setSubtitle(getResources().getString(R.string.vbox_version, _version));

		if(_machines!=null) {
			_listView.setAdapter(new MachineListAdapter(_machines));
    			if(_curCheckPosition>-1)
    				showDetails(_curCheckPosition);
		} else
    			new LoadMachinesTask(_vmgr).execute();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("curChoice", _curCheckPosition);
		outState.putParcelableArrayList("machines", (ArrayList<? extends Parcelable>) _machines);
		outState.putString("version", _vmgr.getVBox().getVersion());
	}
	
	protected MachineListAdapter getAdapter() {
		return (MachineListAdapter)_listView.getAdapter();
	}

	public VBoxApplication getApp() { 
		return (VBoxApplication)getActivity().getApplication(); 
	}
	
	void showDetails(int index) {
       	_machineSelectedListener.onMachineSelected(getAdapter().getItem(index));
    }
}
