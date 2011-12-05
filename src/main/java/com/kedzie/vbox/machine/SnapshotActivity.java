package com.kedzie.vbox.machine;

import java.util.ArrayList;
import java.util.List;

import pl.polidea.treeview.AbstractTreeViewAdapter;
import pl.polidea.treeview.InMemoryTreeStateManager;
import pl.polidea.treeview.TreeBuilder;
import pl.polidea.treeview.TreeNodeInfo;
import pl.polidea.treeview.TreeStateManager;
import pl.polidea.treeview.TreeViewList;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.VBoxSvc;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.ISnapshot;
import com.kedzie.vbox.task.BaseTask;

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
        _vmgr =getIntent().getParcelableExtra("vmgr");
		_machine = _vmgr.getProxy(IMachine.class, getIntent().getStringExtra("machine"));
		new LoadSnapshotsTask(this, _vmgr).execute(_machine);
    }
	
	class LoadSnapshotsTask extends BaseTask<IMachine, List<ISnapshot>>	{
		public LoadSnapshotsTask(Context ctx, VBoxSvc vmgr) { super( "vbox.LoadSnapshotsTask", ctx, vmgr, "Loading Snapshots"); 	}

		@Override
		protected List<ISnapshot> work(IMachine... params) throws Exception {
			List<ISnapshot> snapshots = new ArrayList<ISnapshot>();
			ISnapshot c = params[0].getCurrentSnapshot();
			while(c!=null) {
				c.getChildren(); //cache values
				snapshots.add(c);
				c = c.getParent();
			}
			return snapshots;
		}

		@Override
		protected void onPostExecute(List<ISnapshot> result)	{
			super.onPostExecute(result);
			if(result!=null)	{
				_treeView.setAdapter(new SnapshotTreeAdapter(SnapshotActivity.this, _stateManager, result.size()));
				ISnapshot root = result.get(result.size()-1);
				populate(null, root);
			}
		}
	}
	
	private void populate(ISnapshot parent, ISnapshot child) {
		if(parent==null)
			_treeBuilder.sequentiallyAddNextNode(child, 0);
		else
			_treeBuilder.addRelation(parent, child);
		if(child.getChildren()!=null && child.getChildren().size()>0) {
			for(ISnapshot c : child.getChildren()) {
				populate(child, c);	
			}
		}
	}

public VBoxApplication getApp() { 
		return (VBoxApplication)getApplication(); 
	}
	
	class SnapshotTreeAdapter extends AbstractTreeViewAdapter<ISnapshot> {

		public SnapshotTreeAdapter(Activity activity, TreeStateManager<ISnapshot> treeStateManager, int numberOfLevels) {
			super(activity, treeStateManager, numberOfLevels);
		}

		@Override public long getItemId(int position) {
			return 0;
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
