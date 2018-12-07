package com.kedzie.vbox.machine;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
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

import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.VMAction;
import com.kedzie.vbox.api.IConsole;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.polidea.treeview.AbstractTreeViewAdapter;
import pl.polidea.treeview.InMemoryTreeStateManager;
import pl.polidea.treeview.TreeBuilder;
import pl.polidea.treeview.TreeNodeInfo;
import pl.polidea.treeview.TreeStateManager;
import pl.polidea.treeview.TreeViewList;

/**
 * 
 * @apiviz.stereotype fragment
 */
public class SnapshotFragment extends Fragment {

    /**
     *	Load complete snapshot tree.
     */
    class LoadSnapshotsTask extends ActionBarTask<IMachine, ISnapshot>	{

        public LoadSnapshotsTask(VBoxSvc vmgr) { 
            super((AppCompatActivity)getActivity(), vmgr);
        }

        @Override
        protected ISnapshot work(IMachine... params) throws Exception {
            ISnapshot root = null;
            synchronized(params[0]) {
                params[0].clearCacheNamed("getCurrentSnapshot");
                root = params[0].getCurrentSnapshot();
            }
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
            _root=result;
            _stateManager = new InMemoryTreeStateManager<ISnapshot>();
            _treeBuilder = new TreeBuilder<ISnapshot>(_stateManager);
            _treeView.setAdapter(new SnapshotTreeAdapter(getActivity(), _stateManager, 10));
            if(result!=null) 
                populate(null, result);
        }
    }

    class HandleDeletedEventTask extends ActionBarTask<ISnapshotDeletedEvent, ISnapshot> {

        public HandleDeletedEventTask(VBoxSvc vmgr) { 
            super((AppCompatActivity)getActivity(), vmgr);
        }

        @Override
        protected ISnapshot work(ISnapshotDeletedEvent... params) throws Exception {
            ISnapshot snapshot = findSnapshot(_root, params[0].getSnapshotId());
            snapshot.getName();
            snapshot.getDescription();
            if(snapshot.getParent()!=null) 
                snapshot.getParent().getName();
            return snapshot;
        }

        @Override
        protected void onSuccess(ISnapshot result)  {
            _stateManager.removeNodeRecursively(result);
            if(result.equals(_root)) {
                _root=null;
            }
        }
    }

    class HandleAddedEventTask extends ActionBarTask<ISnapshotTakenEvent, ISnapshot> {

        public HandleAddedEventTask(VBoxSvc vmgr) { 
            super((AppCompatActivity)getActivity(), vmgr);
        }

        @Override
        protected ISnapshot work(ISnapshotTakenEvent... params) throws Exception {
            _machine.clearCacheNamed("getCurrentSnapshot");
            ISnapshot snapshot = _machine.getCurrentSnapshot();
            snapshot.getName();
            snapshot.getDescription();
            if(snapshot.getParent()!=null) 
                snapshot.getParent().getName();
            return snapshot;
        }

        @Override
        protected void onSuccess(ISnapshot result)  {
            if(result.getParent()==null) {
                _root=result;
                _treeBuilder.sequentiallyAddNextNode(result, 0);
            } else {
                ISnapshot parent = _stateManager.getNodeInfo(result.getParent()).getId();
                _treeBuilder.addRelation(parent, result);
            }
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
    @BindView(R.id.mainTreeView)
     TreeViewList _treeView;
    @BindView(R.id.addButton)
     FloatingActionButton _addButton;
    private ISnapshot _root;
    protected TreeStateManager<ISnapshot> _stateManager;
    protected TreeBuilder<ISnapshot> _treeBuilder;
    private LocalBroadcastManager _lbm;
    private BroadcastReceiver _receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(VBoxEventType.ON_SNAPSHOT_TAKEN.name())){
                ISnapshotTakenEvent event = intent.getParcelableExtra(EventIntentService.BUNDLE_EVENT);
                Utils.toastShort(getActivity(), "Snapshot event: %1$s", intent.getAction());
                new HandleAddedEventTask(_vmgr).execute(event);
//                refresh();
            } else if(intent.getAction().equals(VBoxEventType.ON_SNAPSHOT_DELETED.name())){
                ISnapshotDeletedEvent event = intent.getParcelableExtra(EventIntentService.BUNDLE_EVENT);
                Utils.toastShort(getActivity(), "Snapshot event: %1$s", intent.getAction());
                new HandleDeletedEventTask(_vmgr).execute(event);
//                refresh();
            }
        }
    };
    
    private ISnapshot findSnapshot(ISnapshot current, String id) {
        if(current.getId().equals(id))
            return current;
        for(ISnapshot child : current.getChildren()) {
            ISnapshot found = findSnapshot(child, id);
            if(found!=null)
                return found;
        }
        return null;
    }

    /**
     * Recursively populate the tree structure
     * @param parent
     * @param snapshot
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
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("manager", _stateManager);
        outState.putParcelable("root", _root);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        _vmgr = BundleBuilder.getVBoxSvc(getArguments());

        _machine = getArguments().getParcelable(IMachine.BUNDLE);
        _machine = _vmgr.getProxy(IMachine.class, _machine.getIdRef());
        if(savedInstanceState!=null) {
            _stateManager = (TreeStateManager<ISnapshot>)savedInstanceState.getSerializable("manager");
            _root = savedInstanceState.getParcelable("root");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.snapshot_tree, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        _treeView.setChoiceMode(ListView.CHOICE_MODE_NONE);
        registerForContextMenu(_treeView);
        _addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showDialog(getFragmentManager(), "snapshotDialog", TakeSnapshotFragment.getInstance(_vmgr, _machine, null) );
            }
        });
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

    private VBoxApplication getApp() {
        return (VBoxApplication)getActivity().getApplication();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
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
                Utils.showDialog(getFragmentManager(), "snapshotDialog", TakeSnapshotFragment.getInstance(_vmgr, _machine, nodeinfo.getId()) );
                return true;
            case R.id.context_menu_delete_snapshot:  
                nodeinfo = getTreeAdapter().getTreeNodeInfo(info.position);
                new MachineTask<ISnapshot, Void>((AppCompatActivity)getActivity(), _vmgr, R.string.progress_deleting_snapshot, false, _machine) {
                    protected IProgress workWithProgress(IMachine m, IConsole console, ISnapshot...s) throws Exception { 	
                        return console.deleteSnapshot(s[0].getId());
                    }
                }.execute(nodeinfo.getId());
                return true;
            case R.id.context_menu_restore_snapshot:
                nodeinfo = getTreeAdapter().getTreeNodeInfo(info.position);
                new MachineTask<ISnapshot, Void>((AppCompatActivity)getActivity(), _vmgr, R.string.progress_restore_snapshot, false, _machine) {
                    protected IProgress workWithProgress(IMachine m, IConsole console, ISnapshot...s) throws Exception { 	
                        return console.restoreSnapshot(s[0]);
                    }
                }.execute(nodeinfo.getId());
                return true;
        }
        return false;
    }
}