package com.kedzie.vbox.machine.group;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.kedzie.vbox.R;
import com.kedzie.vbox.SettingsActivity;
import com.kedzie.vbox.api.IHost;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IManagedObjectRef;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.machine.group.VMGroupListView.OnTreeNodeSelectListener;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.DialogTask;
import com.kedzie.vbox.task.MachineRunnable;

/**
 * New machine list based on groups
 * @apiviz.stereotype fragment
 */
public class MachineGroupListBaseFragment extends SherlockFragment {
	protected static final String TAG = MachineGroupListBaseFragment.class.getSimpleName();
	
	protected VBoxSvc _vmgr;
	protected VMGroup _root;
	protected IHost _host;
	protected VMGroupListView _listView;
	protected OnTreeNodeSelectListener _selectListener;
	
	 /**
	 * Load Groups and VMs
	 */
	class LoadGroupsTask extends  DialogTask<Void, VMGroup> {
		
	        private Map<String, VMGroup> groupCache = new HashMap<String, VMGroup>();

	        public LoadGroupsTask(VBoxSvc vboxApi) { 
	            super(getSherlockActivity(), vboxApi, R.string.progress_loading_machines); 
	        }

	        private VMGroup get(String name) {
	            if(!groupCache.containsKey(name))
	                groupCache.put(name, new VMGroup(name));
	            return groupCache.get(name);
	        }

	        @Override
	        protected VMGroup work(Void... params) throws Exception {
	            _vmgr.getVBox().getVersion();
	            _host = _vmgr.getVBox().getHost();
	            List<String> vGroups = _vmgr.getVBox().getMachineGroups();
	            for(String tmp : vGroups) {
	                if(tmp.equals("/")) continue;
	                VMGroup previous = get(tmp);
	                int lastIndex=0;
	                while((lastIndex=tmp.lastIndexOf('/'))>0) {
	                    tmp=tmp.substring(0, lastIndex);
	                    VMGroup current = get(tmp);
	                    current.addChild(previous);
	                    previous=current;
	                }
	                get("").addChild(get(tmp));
	            }
	            for(IMachine machine : _vmgr.getVBox().getMachines()) {
	                fork(new MachineRunnable(machine) {
	                    public void run() {
	                        Utils.cacheProperties(m);
	                        List<String> groups = m.getGroups();
	                        get(groups.get(0)).addChild(m);
	                    }
	                });
	            }
	            join();
	            _vmgr.getVBox().getPerformanceCollector().setupMetrics(new String[] { "*:" }, 
	                    Utils.getIntPreference(getActivity().getApplicationContext(), SettingsActivity.PREF_PERIOD), 
	                    Utils.getIntPreference(getActivity().getApplicationContext(), SettingsActivity.PREF_COUNT), 
	                    (IManagedObjectRef)null);
	            return get("");
	        }
	        
	        @Override
	        protected void onSuccess(VMGroup root) {
	            getSherlockActivity().getSupportActionBar().setSubtitle(getResources().getString(R.string.vbox_version, _vmgr.getVBox().getVersion()));
	            _listView.setRoot(root, _host);
	            _root = root;
	        }
	    }
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			_selectListener = (OnTreeNodeSelectListener)activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " does not implement OnTreeNodeSelectListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    if(_vmgr==null) {
    	    _vmgr = (VBoxSvc)((getArguments() != null && getArguments().containsKey(VBoxSvc.BUNDLE)) ? 
                    getArguments().getParcelable(VBoxSvc.BUNDLE)
                    : getActivity().getIntent().getParcelableExtra(VBoxSvc.BUNDLE) );
	    }
		_listView = new VMGroupListView(getActivity(), _vmgr);
		_listView.setOnTreeNodeSelectListener(_selectListener);
		return _listView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        _listView.setSelectionEnabled(getActivity().findViewById(R.id.details)!=null);
		
		if(savedInstanceState!=null)  { 
			getSherlockActivity().getSupportActionBar().setSubtitle(getResources().getString(R.string.vbox_version, savedInstanceState.getString("version")));
			_root = savedInstanceState.getParcelable(VMGroup.BUNDLE);
			_vmgr = savedInstanceState.getParcelable(VBoxSvc.BUNDLE);
			_host = savedInstanceState.getParcelable(IHost.BUNDLE);
    	} 
		
		if(_root==null)
    		new LoadGroupsTask(_vmgr).execute();
		else
			_listView.setRoot(_root, _host);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(VBoxSvc.BUNDLE, _vmgr);
		outState.putString("version", _vmgr.getVBox().getVersion());
		outState.putParcelable(VMGroup.BUNDLE, _root);
		outState.putParcelable(IHost.BUNDLE, _host);
		//save current machine
	}
}
