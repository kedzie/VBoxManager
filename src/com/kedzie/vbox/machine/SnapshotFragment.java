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
import android.content.IntentFilter;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuInflater;
import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.VMAction;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.ISnapshot;
import com.kedzie.vbox.api.ISnapshotDeletedEvent;
import com.kedzie.vbox.api.ISnapshotTakenEvent;
import com.kedzie.vbox.api.jaxb.VBoxEventType;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.event.EventIntentService;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.ActionBarTask;
import com.kedzie.vbox.task.MachineTask;

/**
 * 
 * @author Marek Kędzierski
 * @apiviz.stereotype fragment
 */
public class SnapshotFragment extends SherlockFragment {
    
	/**
	 *	Load complete snapshot tree.
	 */
	class LoadSnapshotsTask extends ActionBarTask<IMachine, ISnapshot>	{
		public LoadSnapshotsTask(VBoxSvc vmgr) { 
			super( "LoadSnapshotsTask", getSherlockActivity(), vmgr); 	
		}

		@Override
		protected ISnapshot work(IMachine... params) throws Exception {
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
			for(ISnapshot child : s.getChildren())
				cache(child);
		}

		@Override
		protected void onResult(ISnapshot result)	{
			_treeBuilder.clear();
			getTreeAdapter().notifyDataSetChanged();
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
			View v = LayoutInflater.from(getActivity()).inflate(R.layout.machine_action_item, null);
			updateView(v, treeNodeInfo);
			return v;
		}

		@Override 
		public View updateView(View view, TreeNodeInfo<ISnapshot> treeNodeInfo) {
			((TextView)view.findViewById(R.id.action_item_text)).setText(treeNodeInfo.getId().getName());
			((ImageView)view.findViewById(R.id.action_item_icon)).setImageResource( getApp().getDrawable(VMAction.RESTORE_SNAPSHOT) );
			return view;
		}
	}
	
	/**
     * Handle SnapshotTaken event
     */
    class HandleTakenEventTask extends ActionBarTask<Bundle, String> {
        
        public HandleTakenEventTask(VBoxSvc vmgr) {  
            super( "HandleTakenEventTask", getSherlockActivity(), vmgr);
        }

        @Override
        protected String work(Bundle... params) throws Exception {
            ISnapshotTakenEvent event = (ISnapshotTakenEvent)BundleBuilder.getProxy(params[0], EventIntentService.BUNDLE_EVENT, IEvent.class);
            return event.getSnapshotId();
        }

        @Override
        protected void onResult(String result)    {
            Utils.toastLong(getActivity(), "Snapshot taken: "+result);
        }
    }
    
    /**
     * Handle SnapshotDeleted event
     */
    class HandleDeletedEventTask extends ActionBarTask<Bundle, String> {
        
        public HandleDeletedEventTask(VBoxSvc vmgr) {  
            super( "HandleDeletedEventTask", getSherlockActivity(), vmgr);
        }

        @Override
        protected String work(Bundle... params) throws Exception {
            ISnapshotDeletedEvent event = (ISnapshotDeletedEvent)BundleBuilder.getProxy(params[0], EventIntentService.BUNDLE_EVENT, IEvent.class);
            return event.getSnapshotId();
        }

        @Override
        protected void onResult(String result)    {
            Utils.toastLong(getActivity(), "Snapshot Deleted: "+result);
        }
    }
	
	protected VBoxSvc _vmgr;
	protected IMachine _machine;
	protected View _view;
	protected TreeViewList _treeView;
	protected TreeStateManager<ISnapshot> _stateManager;
	protected TreeBuilder<ISnapshot> _treeBuilder;
	private LocalBroadcastManager _lbm;
	private BroadcastReceiver _receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(VBoxEventType.ON_SNAPSHOT_TAKEN.name())) {
                new HandleTakenEventTask(_vmgr).execute(intent.getExtras());
            }  else if(intent.getAction().equals(VBoxEventType.ON_SNAPSHOT_DELETED.name())) {
                new HandleDeletedEventTask(_vmgr).execute(intent.getExtras());
            }
        }
    };
	
    @Override
    @SuppressWarnings("unchecked")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    setHasOptionsMenu(true);
        _vmgr = getArguments().getParcelable(VBoxSvc.BUNDLE);
        _machine = BundleBuilder.getProxy(getArguments(), IMachine.BUNDLE, IMachine.class);
        
		_view = inflater.inflate(R.layout.snapshot_tree, null);
        _treeView = (TreeViewList)_view.findViewById(R.id.mainTreeView);
        registerForContextMenu(_treeView);
        
        if(savedInstanceState==null)
            _stateManager = new InMemoryTreeStateManager<ISnapshot>();
        else
            _stateManager = (TreeStateManager<ISnapshot>)savedInstanceState.getSerializable("manager");
        
        _treeBuilder = new TreeBuilder<ISnapshot>(_stateManager);
        _treeView.setAdapter(new SnapshotTreeAdapter(getActivity(), _stateManager, 10));
        return _view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		_lbm = LocalBroadcastManager.getInstance(getActivity().getApplicationContext());
		if(savedInstanceState==null) 
			new LoadSnapshotsTask(_vmgr).execute(_machine);
	}
	
	@Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(VBoxEventType.ON_SNAPSHOT_DELETED.name());
        filter.addAction(VBoxEventType.ON_SNAPSHOT_TAKEN.name());
        _lbm.registerReceiver(_receiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        _lbm.unregisterReceiver(_receiver);
    }

    /**
	 * Recursively populate the tree structure
	 * @param parent
	 * @param child
	 */
	protected void populate(ISnapshot parent, ISnapshot child) {
		if(parent==null)
			_treeBuilder.sequentiallyAddNextNode(child, 0);
		else
			_treeBuilder.addRelation(parent, child);
		for(ISnapshot c : child.getChildren())
			populate(child, c);	
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("manager", _stateManager);
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
		    Utils.showDialog(getSherlockActivity().getSupportFragmentManager(), 
		                        "snapshotDialog", 
		                        TakeSnapshotFragment.getInstance(getArguments()) );
			return true;
		case R.id.option_menu_refresh:
			new LoadSnapshotsTask( _vmgr).execute(_machine);
			return false;
		}
		return false;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.add(Menu.NONE, R.id.context_menu_restore_snapshot, Menu.NONE, VMAction.RESTORE_SNAPSHOT.toString());
		menu.add(Menu.NONE, R.id.context_menu_delete_snapshot, Menu.NONE, VMAction.DELETE_SNAPSHOT.toString());
	}
	
	private SnapshotTreeAdapter getTreeAdapter() {
	    return (SnapshotTreeAdapter)_treeView.getAdapter();
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		TreeNodeInfo<ISnapshot> nodeinfo = null;
		final ISnapshot snapshot;
		
		switch (item. getItemId()) {
		case R.id.context_menu_delete_snapshot:  
			nodeinfo = getTreeAdapter().getTreeNodeInfo(info.position);
			snapshot = nodeinfo.getId();
			new MachineTask<ISnapshot, Void>("DeleteSnapshotTask", getActivity(), _vmgr, "Deleting Snapshot", false, _machine) { 
				protected IProgress workWithProgress(IMachine m, IConsole console, ISnapshot...s) throws Exception { 	
					return console.deleteSnapshot(s[0].getId()); 
				}
                @Override
                protected void onResult(Void result) {
                    super.onResult(result);
                    _stateManager.removeNodeRecursively(snapshot);
                }
			}.execute(snapshot);
			return true;
		case R.id.context_menu_restore_snapshot:
			nodeinfo = getTreeAdapter().getTreeNodeInfo(info.position);
			snapshot = nodeinfo.getId();
			new MachineTask<ISnapshot, Void>("RestoreSnapshotTask", getActivity(), _vmgr, "Restoring Snapshot", false, _machine) { 
				protected IProgress workWithProgress(IMachine m, IConsole console, ISnapshot...s) throws Exception { 	
					return console.restoreSnapshot(s[0]);
				}
			}.execute(snapshot);
			return true;
		}
		return false;
	}
}
