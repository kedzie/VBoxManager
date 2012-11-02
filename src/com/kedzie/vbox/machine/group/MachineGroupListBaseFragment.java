package com.kedzie.vbox.machine.group;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IManagedObjectRef;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.machine.MachineView;
import com.kedzie.vbox.machine.group.VMGroupListView.OnTreeNodeSelectListener;
import com.kedzie.vbox.metrics.MetricPreferencesActivity;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.ActionBarTask;

/**
 * New machine list based on groups
 * @apiviz.stereotype fragment
 */
public class MachineGroupListBaseFragment extends SherlockFragment {
	protected static final String TAG = MachineGroupListBaseFragment.class.getSimpleName();
	
	protected VBoxSvc _vmgr;
	protected VMGroup _root;
	protected VMGroupListView _listView;
	protected OnTreeNodeSelectListener _selectListener;
	
	 class LoadGroupsTask extends  ActionBarTask<Void, VMGroup> {
	        private Map<String, VMGroup> groupCache = new HashMap<String, VMGroup>();

	        public LoadGroupsTask(VBoxSvc vboxApi) { 
	            super("Load Groups Task", getSherlockActivity(), vboxApi); 
	        }

	        private VMGroup get(String name) {
	            if(!groupCache.containsKey(name))
	                groupCache.put(name, new VMGroup(name));
	            return groupCache.get(name);
	        }

	        @Override
	        protected VMGroup work(Void... params) throws Exception {
	            _vmgr.getVBox().getVersion();
	            _vmgr.getVBox().getHost();
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
	                get("/").addChild(get(tmp));
	            }
	            for(IMachine machine : _vmgr.getVBox().getMachines()) {
	                MachineView.cacheProperties(machine);
	                List<String> groups = machine.getGroups();
	                if(groups.isEmpty() || groups.get(0).equals("") || groups.get(0).equals("/"))
	                    get("/").addChild(machine);
	                else
	                    get(groups.get(0)).addChild(machine);
	            }
	            VMGroup root = get("/");
	            _vmgr.getVBox().getPerformanceCollector().setupMetrics(new String[] { "*:" }, 
	                    Utils.getIntPreference(getActivity(), MetricPreferencesActivity.PERIOD), 
	                    Utils.getIntPreference(getActivity(), MetricPreferencesActivity.COUNT), 
	                    (IManagedObjectRef)null);
	            Log.i(TAG, VMGroup.getTreeString(0, root));
	            return root;
	        }
	        @Override
	        protected void onResult(VMGroup root) {
	            getSherlockActivity().getSupportActionBar().setSubtitle(getResources().getString(R.string.vbox_version, _vmgr.getVBox().getVersion()));
	            _listView.setRoot(root);
	            _root = root;
	        }
	    }
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		_selectListener = (OnTreeNodeSelectListener)activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		_listView = new VMGroupListView(getActivity());
		_listView.setOnTreeNodeSelectListener(_selectListener);
		return _listView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		_vmgr = (VBoxSvc)((getArguments() != null && getArguments().containsKey(VBoxSvc.BUNDLE)) ? 
		            getArguments().getParcelable(VBoxSvc.BUNDLE)
		            : getActivity().getIntent().getParcelableExtra(VBoxSvc.BUNDLE) );
		
        _listView.setSelectionEnabled(getActivity().findViewById(R.id.details)!=null);
		
		if(savedInstanceState!=null)  { 
			getSherlockActivity().getSupportActionBar().setSubtitle(getResources().getString(R.string.vbox_version, savedInstanceState.getString("version")));
			_root = (VMGroup)savedInstanceState.getParcelable(VMGroup.BUNDLE);
    	} else
    		new LoadGroupsTask(_vmgr).execute();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("version", _vmgr.getVBox().getVersion());
		outState.putParcelable(VMGroup.BUNDLE, _root);
	}
}
