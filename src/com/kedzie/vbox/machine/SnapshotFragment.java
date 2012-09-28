package com.kedzie.vbox.machine;

import pl.polidea.treeview.AbstractTreeViewAdapter;
import pl.polidea.treeview.InMemoryTreeStateManager;
import pl.polidea.treeview.TreeBuilder;
import pl.polidea.treeview.TreeNodeInfo;
import pl.polidea.treeview.TreeStateManager;
import pl.polidea.treeview.TreeViewList;
import android.app.Activity;
import android.os.Bundle;
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
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.ISnapshot;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.ActionBarTask;
import com.kedzie.vbox.task.MachineTask;

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
			_rootSnapshot=result;
			_treeBuilder.clear();
			populate(null, _rootSnapshot);
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
	
	protected VBoxSvc _vmgr;
	protected IMachine _machine;
	protected View _view;
	protected TreeViewList _treeView;
	protected TreeStateManager<ISnapshot> _stateManager;
	protected TreeBuilder<ISnapshot> _treeBuilder;
	/** Root of snapshot tree; retained on configuration change */
	protected ISnapshot _rootSnapshot;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        _vmgr = getArguments().getParcelable(VBoxSvc.BUNDLE);
        _machine = BundleBuilder.getProxy(getArguments(), IMachine.BUNDLE, IMachine.class);
		if(savedInstanceState!=null)
			_rootSnapshot = BundleBuilder.getProxy(savedInstanceState, "rootSnapshot", ISnapshot.class);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		_view = inflater.inflate(R.layout.snapshot_tree, null);
        _treeView = (TreeViewList)_view.findViewById(R.id.mainTreeView);
        registerForContextMenu(_treeView);
        _stateManager = new InMemoryTreeStateManager<ISnapshot>();
        _treeBuilder = new TreeBuilder<ISnapshot>(_stateManager);
        _treeView.setAdapter(new SnapshotTreeAdapter(getActivity(), _stateManager, 10));
        return _view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(_rootSnapshot==null) 
			new LoadSnapshotsTask(_vmgr).execute(_machine);
		else
			populate(null, _rootSnapshot);
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
		BundleBuilder.putProxy(outState, "rootSnapshot", _rootSnapshot);
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
			TakeSnapshotFragment.getInstance(getArguments())
				.show(getSherlockActivity().getSupportFragmentManager(), "dialog");
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

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		TreeNodeInfo<ISnapshot> nodeinfo = null;
		ISnapshot snapshot = null;
		switch (item. getItemId()) {
		case R.id.context_menu_delete_snapshot:  
			nodeinfo = ((SnapshotTreeAdapter)_treeView.getAdapter()).getTreeNodeInfo(((AdapterContextMenuInfo)item.getMenuInfo()).position);
			snapshot = nodeinfo.getId();
			new MachineTask<ISnapshot, Void>("DeleteSnapshotTask", getActivity(), _vmgr, "Deleting Snapshot", false, _machine) { 
				protected IProgress workWithProgress(IMachine m, IConsole console, ISnapshot...s) throws Exception { 	
					return console.deleteSnapshot(s[0].getId()); 
				}

                @Override
                protected void onResult(Void result) {
                    super.onResult(result);
                    _treeBuilder.
                }
				
			}.execute(snapshot);
			return true;
		case R.id.context_menu_restore_snapshot:
			nodeinfo = ((SnapshotTreeAdapter)_treeView.getAdapter()).getTreeNodeInfo(((AdapterContextMenuInfo)item.getMenuInfo()).position);
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
