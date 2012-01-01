package com.kedzie.vbox.machine;

import pl.polidea.treeview.AbstractTreeViewAdapter;
import pl.polidea.treeview.InMemoryTreeStateManager;
import pl.polidea.treeview.TreeBuilder;
import pl.polidea.treeview.TreeNodeInfo;
import pl.polidea.treeview.TreeStateManager;
import pl.polidea.treeview.TreeViewList;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxSvc;
import com.kedzie.vbox.VBoxApplication.BundleBuilder;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.ISnapshot;
import com.kedzie.vbox.task.BaseTask;
import com.kedzie.vbox.task.MachineTask;

public class SnapshotActivity extends Activity {

	private VBoxSvc _vmgr;
	private IMachine _machine;
	private TreeViewList _treeView;
	private TreeStateManager<ISnapshot> _stateManager;
	private TreeBuilder<ISnapshot> _treeBuilder;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tree);
        _treeView = (TreeViewList)findViewById(R.id.mainTreeView);
        _stateManager = new InMemoryTreeStateManager<ISnapshot>();
        _treeBuilder = new TreeBuilder<ISnapshot>(_stateManager);
        _vmgr = getIntent().getParcelableExtra(VBoxSvc.BUNDLE);
        _machine = BundleBuilder.getProxy(getIntent(), EventThread.BUNDLE_MACHINE, IMachine.class);
		registerForContextMenu(_treeView);
		new LoadSnapshotsTask(this, _vmgr).execute(_machine);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.snapshots_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.option_menu_add:
			new TakeSnapshotDialog(this, _vmgr, _machine).show();
			return true;
		case R.id.option_menu_refresh:
			new LoadSnapshotsTask(this, _vmgr).execute(_machine);
			return true;
		default:
			return true;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.snapshots_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		TreeNodeInfo<ISnapshot> nodeinfo = ((SnapshotTreeAdapter)_treeView.getAdapter()).getTreeNodeInfo(((AdapterContextMenuInfo) item.getMenuInfo()).position);
		 ISnapshot snapshot = nodeinfo.getId();
		  switch (item. getItemId()) {
		  case R.id.context_menu_delete_snapshot:  
			  new MachineTask<ISnapshot>("DeleteSnapshotTask", this, _vmgr, "Deleting Snapshot", false, snapshot.getMachine()) { 
					protected IProgress workWithProgress(IMachine m, IConsole console, ISnapshot...s) throws Exception { 	
						return console.deleteSnapshot(s[0].getIdRef()); 
					}
				}.execute(snapshot);
			  break;
		  case R.id.context_menu_restore_snapshot:
			  new MachineTask<ISnapshot>("RestoreSnapshotTask", this, _vmgr, "Restoring Snapshot", false, snapshot.getMachine()) { 
					protected IProgress workWithProgress(IMachine m, IConsole console, ISnapshot...s) throws Exception { 	
						return console.restoreSnapshot(s[0].getName());
					}
				}.execute(snapshot);
			  break;
		  }
		  return true;
	}

	class LoadSnapshotsTask extends BaseTask<IMachine, ISnapshot>	{
		public LoadSnapshotsTask(Context ctx, VBoxSvc vmgr) { 
			super( "LoadSnapshotsTask", ctx, vmgr, "Loading Snapshots"); 	
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
			for(ISnapshot child : s.getChildren())
				cache(child);
		}

		@Override
		protected void onPostExecute(ISnapshot result)	{
			super.onPostExecute(result);
			if(result!=null)	{
				_treeView.setAdapter(new SnapshotTreeAdapter(SnapshotActivity.this, _stateManager, 10));
				if(result!=null)
					populate(null, result);
			}
		}
	}
	
	private void populate(ISnapshot parent, ISnapshot child) {
		if(parent==null)
			_treeBuilder.sequentiallyAddNextNode(child, 0);
		else
			_treeBuilder.addRelation(parent, child);
		if(child.getChildren()!=null && child.getChildren().size()>0) {
			for(ISnapshot c : child.getChildren())
				populate(child, c);	
		}
	}

	class SnapshotTreeAdapter extends AbstractTreeViewAdapter<ISnapshot> {
		
		public SnapshotTreeAdapter(Activity activity, TreeStateManager<ISnapshot> treeStateManager, int numberOfLevels) {
			super(activity, treeStateManager, numberOfLevels);
		}

		@Override public long getItemId(int position) {
			return position;
		}

		@Override public View getNewChildView(TreeNodeInfo<ISnapshot> treeNodeInfo) {
			View v = getLayoutInflater().inflate(R.layout.machine_action_item, null);
			updateView(v, treeNodeInfo);
			return v;
		}

		@Override public View updateView(View view, TreeNodeInfo<ISnapshot> treeNodeInfo) {
			((TextView)view.findViewById(R.id.action_item_text)).setText(treeNodeInfo.getId().getName());
			((ImageView)view.findViewById(R.id.action_item_icon)).setImageResource( R.drawable.ic_list_snapshot_c);
			return view;
		}
	}
}
