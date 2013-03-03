package com.kedzie.vbox.machine;

import pl.polidea.treeview.AbstractTreeViewAdapter;
import pl.polidea.treeview.InMemoryTreeStateManager;
import pl.polidea.treeview.TreeBuilder;
import pl.polidea.treeview.TreeNodeInfo;
import pl.polidea.treeview.TreeStateManager;
import pl.polidea.treeview.TreeViewList;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuInflater;
import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.VMAction;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.ISnapshot;
import com.kedzie.vbox.api.jaxb.VBoxEventType;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.ActionBarTask;
import com.kedzie.vbox.task.MachineTask;

/**
 * 
 * @apiviz.stereotype fragment
 */
public class SnapshotFragment extends SherlockFragment {
    
	/**
	 *	Load complete snapshot tree.
	 */
	class LoadSnapshotsTask extends ActionBarTask<IMachine, ISnapshot>	{
		
		public LoadSnapshotsTask(VBoxSvc vmgr) { 
			super(getSherlockActivity(), vmgr); 	
		}

		@Override
		protected ISnapshot work(IMachine... params) throws Exception {
		    params[0].clearCacheNamed("getCurrentSnapshot");
			ISnapshot root = params[0].getCurrentSnapshot();
			if(root==null) return null;
			while(root.getParent()!=null)
				root = root.getParent();
			cache(root);
			return root;
		}
		
		/**
		 * Recursively cache all the children of a {@link ISnapshot}
		 * @param s the {@link ISnapshot}
		 */
		protected void cache(ISnapshot s) {
			s.getName();
			s.getDescription();
			for(ISnapshot child : s.getChildren())
				cache(child);
		}

		@Override
		protected void onSuccess(ISnapshot result)	{
		    _stateManager = new InMemoryTreeStateManager<ISnapshot>();
	        _treeBuilder = new TreeBuilder<ISnapshot>(_stateManager);
	        _treeView.setAdapter(new SnapshotTreeAdapter(getActivity(), _stateManager, 10));
			populate(null, result);
		}
	}
	
	/**
	 * Snapshot tree node
	 */
	class SnapshotTreeAdapter extends AbstractTreeViewAdapter<ISnapshot> {
		
		public SnapshotTreeAdapter(Activity activity, TreeStateManager<ISnapshot> treeStateManager, int numberOfLevels) {
			super(activity, treeStateManager, numberOfLevels);
		}

		@Override 
		public long getItemId(int position) {
			return position;
		}

		@Override 
		public View getNewChildView(TreeNodeInfo<ISnapshot> treeNodeInfo) {
			View v = LayoutInflater.from(getActivity()).inflate(android.R.layout.simple_list_item_1, null);
			TextView text1 = (TextView)v.findViewById(android.R.id.text1);
			v.setTag(text1);
			updateView(v, treeNodeInfo);
			return v;
		}

		@Override 
		public View updateView(View view, TreeNodeInfo<ISnapshot> treeNodeInfo) {
			TextView text1 = (TextView)view.getTag();
	        text1.setText(treeNodeInfo.getId().getName());
	        text1.setCompoundDrawablesWithIntrinsicBounds(getApp().getDrawable(VMAction.RESTORE_SNAPSHOT) , 0, 0, 0);
			return view;
		}
	}
	
	protected VBoxSvc _vmgr;
	protected IMachine _machine;
	protected TreeViewList _treeView;
	protected TreeStateManager<ISnapshot> _stateManager;
	protected TreeBuilder<ISnapshot> _treeBuilder;
	private LocalBroadcastManager _lbm;
	private BroadcastReceiver _receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(VBoxEventType.ON_SNAPSHOT_TAKEN.name()) || intent.getAction().equals(VBoxEventType.ON_SNAPSHOT_DELETED.name())){
            	Utils.toastShort(getActivity(), "Snapshot event: %1$s", intent.getAction());
                refresh();
            }
        }
    };
    
    @Override
	public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
		outState.putSerializable("manager", _stateManager);
	}
    
    @SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setHasOptionsMenu(true);
    	_vmgr = getArguments().getParcelable(VBoxSvc.BUNDLE);
        _machine = getArguments().getParcelable(IMachine.BUNDLE);
        _machine = _vmgr.getProxy(IMachine.class, _machine.getIdRef());
        if(savedInstanceState!=null) {
        	_stateManager = (TreeStateManager<ISnapshot>)savedInstanceState.getSerializable("manager");
    	}
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.snapshot_tree, container, false);
        _treeView = (TreeViewList)view.findViewById(R.id.mainTreeView);
        _treeView.setChoiceMode(ListView.CHOICE_MODE_NONE);
        registerForContextMenu(_treeView);
        return view;
    }
	
	@Override
    public void onStart() {
        super.onStart();
        _lbm = LocalBroadcastManager.getInstance(getActivity());
        _lbm.registerReceiver(_receiver, Utils.createIntentFilter(
                VBoxEventType.ON_SNAPSHOT_DELETED.name(),
                VBoxEventType.ON_SNAPSHOT_TAKEN.name()));
        
        if(_stateManager==null) 
			new LoadSnapshotsTask(_vmgr).execute(_machine);
        else {
           	_treeBuilder = new TreeBuilder<ISnapshot>(_stateManager);
             _treeView.setAdapter(new SnapshotTreeAdapter(getActivity(), _stateManager, 10));
        }
    }

    @Override
    public void onStop() {
    	_lbm.unregisterReceiver(_receiver);
        super.onStop();
    }

    /**
	 * Recursively populate the tree structure
	 * @param parent
	 * @param child
	 */
	protected void populate(ISnapshot parent, ISnapshot snapshot) {
		if(parent==null)
			_treeBuilder.sequentiallyAddNextNode(snapshot, 0);
		else
			_treeBuilder.addRelation(parent, snapshot);
		for(ISnapshot child : snapshot.getChildren())
			populate(snapshot, child);	
	}
	
	/**
	 * Refresh the snapshot tree
	 */
	private void refresh() {
	    _stateManager.clear();
        _stateManager = new InMemoryTreeStateManager<ISnapshot>();
        _treeBuilder = new TreeBuilder<ISnapshot>(_stateManager);
        _treeView.setAdapter(new SnapshotTreeAdapter(getActivity(), _stateManager, 10));
        new LoadSnapshotsTask( _vmgr).execute(_machine);
	}
	
	private VBoxApplication getApp() {
		return (VBoxApplication)getActivity().getApplication();
	}
	
	@Override
	public void onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.snapshot_actions, menu);
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		switch(item.getItemId()) {
		case R.id.option_menu_add:
		    Utils.showDialog(getFragmentManager(), "snapshotDialog", TakeSnapshotFragment.getInstance(getArguments()) );
			return true;
		case R.id.option_menu_refresh:
		    refresh();
			return false;
		}
		return false;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    menu.add(Menu.NONE, R.id.context_menu_restore_snapshot, Menu.NONE, R.string.action_restore_snapshot);
		menu.add(Menu.NONE, R.id.context_menu_delete_snapshot, Menu.NONE, R.string.action_delete_snapshot);
		menu.add(Menu.NONE, R.id.context_menu_details_snapshot, Menu.NONE, R.string.action_edit_snapshot);
	}
	
	private SnapshotTreeAdapter getTreeAdapter() {
	    return (SnapshotTreeAdapter)_treeView.getAdapter();
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		TreeNodeInfo<ISnapshot> nodeinfo;
		switch (item. getItemId()) {
		    case R.id.context_menu_details_snapshot:
		        nodeinfo = getTreeAdapter().getTreeNodeInfo(info.position);
		        Bundle arguments = new BundleBuilder()
		                    .putParcelable(VBoxSvc.BUNDLE, _vmgr)
		                    .putParcelable(IMachine.BUNDLE, _machine)
		                    .putParcelable("snapshot", nodeinfo.getId())
		                    .create();
		        Utils.showDialog(getFragmentManager(), "snapshotDialog", TakeSnapshotFragment.getInstance(arguments) );
		        return true;
		    case R.id.context_menu_delete_snapshot:  
		        nodeinfo = getTreeAdapter().getTreeNodeInfo(info.position);
		        new MachineTask<ISnapshot, Void>(getSherlockActivity(), _vmgr, R.string.progress_deleting_snapshot, false, _machine) { 
		            protected IProgress workWithProgress(IMachine m, IConsole console, ISnapshot...s) throws Exception { 	
		                return console.deleteSnapshot(s[0].getId());
		            }
		        }.execute(nodeinfo.getId());
		        return true;
		    case R.id.context_menu_restore_snapshot:
		        nodeinfo = getTreeAdapter().getTreeNodeInfo(info.position);
		        new MachineTask<ISnapshot, Void>(getSherlockActivity(), _vmgr, R.string.progress_restore_snapshot, false, _machine) { 
		            protected IProgress workWithProgress(IMachine m, IConsole console, ISnapshot...s) throws Exception { 	
		                return console.restoreSnapshot(s[0]);
		            }
		        }.execute(nodeinfo.getId());
		        return true;
		}
		return false;
	}
}