package com.kedzie.vbox.machine

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kedzie.vbox.R
import com.kedzie.vbox.VBoxApplication
import com.kedzie.vbox.VMAction
import com.kedzie.vbox.api.*
import com.kedzie.vbox.api.jaxb.VBoxEventType
import com.kedzie.vbox.app.BundleBuilder
import com.kedzie.vbox.app.Utils
import com.kedzie.vbox.event.EventIntentService
import com.kedzie.vbox.soap.VBoxSvc
import com.kedzie.vbox.task.BaseTask
import com.kedzie.vbox.task.MachineTask
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.snapshot_tree.*
import kotlinx.coroutines.*
import pl.polidea.treeview.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 *
 * @apiviz.stereotype fragment
 */
class SnapshotFragment : Fragment(), CoroutineScope {

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default

    private lateinit var _vmgr: VBoxSvc
    private lateinit var _machine: IMachine

    private var _root: ISnapshot? = null
    private var _stateManager: TreeStateManager<ISnapshot>? = null
    private var _treeBuilder: TreeBuilder<ISnapshot>? = null

    @Inject
    lateinit var lbm: LocalBroadcastManager

    private val _receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == VBoxEventType.ON_SNAPSHOT_TAKEN.name) {
                val event = intent.getParcelableExtra<ISnapshotTakenEvent>(EventIntentService.BUNDLE_EVENT)
                Utils.toastShort(activity, "Snapshot event: %1\$s", intent.action)
                HandleAddedEventTask(_vmgr).execute(event)
                //                refresh();
            } else if (intent.action == VBoxEventType.ON_SNAPSHOT_DELETED.name) {
                val event = intent.getParcelableExtra<ISnapshotDeletedEvent>(EventIntentService.BUNDLE_EVENT)
                Utils.toastShort(activity, "Snapshot event: %1\$s", intent.action)
                HandleDeletedEventTask(_vmgr).execute(event)
                //                refresh();
            }
        }
    }

    private val app: VBoxApplication
        get() = activity!!.application as VBoxApplication

    private val treeAdapter: SnapshotTreeAdapter
        get() = mainTreeView!!.adapter as SnapshotTreeAdapter


    private fun loadSnapshots() {
        launch {
            var root: ISnapshot?

            synchronized(_machine) {
                _machine.clearCacheNamed("getCurrentSnapshot")
                root = _machine.currentSnapshot
            }

            root?.let {
                while (root!!.parent != null)
                    root = root!!.parent
                cache(root!!)
            }
            withContext(Dispatchers.Main) {
                _root = root
                _stateManager = InMemoryTreeStateManager()
                _treeBuilder = TreeBuilder(_stateManager)
                mainTreeView!!.adapter = SnapshotTreeAdapter(activity!!, _stateManager!!, 10)
                if (root != null)
                    populate(null, _root!!)
            }
        }
    }

    private fun cache(s: ISnapshot) {
        s.name
        s.description
        for (child in s.children)
            cache(child)
    }

    internal inner class HandleDeletedEventTask(vmgr: VBoxSvc) : BaseTask<ISnapshotDeletedEvent, ISnapshot>(activity as AppCompatActivity?, vmgr) {

        @Throws(Exception::class)
        override fun work(vararg params: ISnapshotDeletedEvent): ISnapshot {
            val snapshot = findSnapshot(_root!!, params[0].snapshotId)
            snapshot!!.name
            snapshot.description
            if (snapshot.parent != null)
                snapshot.parent.name
            return snapshot
        }

        override fun onSuccess(result: ISnapshot) {
            _stateManager!!.removeNodeRecursively(result)
            if (result == _root) {
                _root = null
            }
        }
    }

    internal inner class HandleAddedEventTask(vmgr: VBoxSvc) : BaseTask<ISnapshotTakenEvent, ISnapshot>(activity as AppCompatActivity?, vmgr) {

        @Throws(Exception::class)
        override fun work(vararg params: ISnapshotTakenEvent): ISnapshot {
            _machine!!.clearCacheNamed("getCurrentSnapshot")
            val snapshot = _machine!!.currentSnapshot
            snapshot.name
            snapshot.description
            if (snapshot.parent != null)
                snapshot.parent.name
            return snapshot
        }

        override fun onSuccess(result: ISnapshot) {
            if (result.parent == null) {
                _root = result
                _treeBuilder!!.sequentiallyAddNextNode(result, 0)
            } else {
                val parent = _stateManager!!.getNodeInfo(result.parent).id
                _treeBuilder!!.addRelation(parent, result)
            }
        }
    }

    /**
     * Snapshot tree node
     */
    internal inner class SnapshotTreeAdapter(activity: Activity, treeStateManager: TreeStateManager<ISnapshot>, numberOfLevels: Int) : AbstractTreeViewAdapter<ISnapshot>(activity, treeStateManager, numberOfLevels) {

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getNewChildView(treeNodeInfo: TreeNodeInfo<ISnapshot>): View {
            val v = LayoutInflater.from(activity).inflate(android.R.layout.simple_list_item_1, null)
            val text1 = v.findViewById<View>(android.R.id.text1) as TextView
            v.tag = text1
            updateView(v, treeNodeInfo)
            return v
        }

        override fun updateView(view: View, treeNodeInfo: TreeNodeInfo<ISnapshot>): View {
            val text1 = view.tag as TextView
            text1.text = treeNodeInfo.id.name
            text1.setCompoundDrawablesWithIntrinsicBounds(app.getDrawable(VMAction.RESTORE_SNAPSHOT), 0, 0, 0)
            return view
        }
    }

    private fun findSnapshot(current: ISnapshot, id: String): ISnapshot? {
        if (current.id == id)
            return current
        for (child in current.children) {
            val found = findSnapshot(child, id)
            if (found != null)
                return found
        }
        return null
    }

    /**
     * Recursively populate the tree structure
     * @param parent
     * @param snapshot
     */
    protected fun populate(parent: ISnapshot?, snapshot: ISnapshot) {
        if (parent == null)
            _treeBuilder!!.sequentiallyAddNextNode(snapshot, 0)
        else
            _treeBuilder!!.addRelation(parent, snapshot)
        for (child in snapshot.children)
            populate(snapshot, child)
    }

    /**
     * Refresh the snapshot tree
     */
    private fun refresh() {
        _stateManager!!.clear()
        _stateManager = InMemoryTreeStateManager()
        _treeBuilder = TreeBuilder(_stateManager)
        mainTreeView!!.adapter = SnapshotTreeAdapter(activity!!, _stateManager!!, 10)
        loadSnapshots()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("manager", _stateManager)
        outState.putParcelable("root", _root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        _vmgr = BundleBuilder.getVBoxSvc(arguments!!)

        _machine = arguments!!.getParcelable(IMachine.BUNDLE)
        _machine = _vmgr.getProxy(IMachine::class.java, _machine!!.idRef)
        if (savedInstanceState != null) {
            _stateManager = savedInstanceState.getSerializable("manager") as TreeStateManager<ISnapshot>
            _root = savedInstanceState.getParcelable("root")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val swipeLayout = SwipeRefreshLayout(activity!!)
        swipeLayout.setOnRefreshListener { loadSnapshots() }
        inflater.inflate(R.layout.snapshot_tree, swipeLayout)
        return swipeLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainTreeView!!.choiceMode = ListView.CHOICE_MODE_NONE
        registerForContextMenu(mainTreeView!!)
        addButton!!.setOnClickListener { Utils.showDialog(fragmentManager!!, "snapshotDialog", TakeSnapshotFragment.getInstance(_vmgr, _machine, null)) }
    }

    override fun onStart() {
        super.onStart()
        job = Job()
        lbm.registerReceiver(_receiver, Utils.createIntentFilter(
                VBoxEventType.ON_SNAPSHOT_DELETED.name,
                VBoxEventType.ON_SNAPSHOT_TAKEN.name))

        if (_stateManager == null)
            loadSnapshots()
        else {
            _treeBuilder = TreeBuilder(_stateManager)
            mainTreeView!!.adapter = SnapshotTreeAdapter(activity!!, _stateManager!!, 10)
        }
    }

    override fun onStop() {
        job.cancel()
        lbm.unregisterReceiver(_receiver)
        super.onStop()
    }


    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
        menu.add(Menu.NONE, R.id.context_menu_restore_snapshot, Menu.NONE, R.string.action_restore_snapshot)
        menu.add(Menu.NONE, R.id.context_menu_delete_snapshot, Menu.NONE, R.string.action_delete_snapshot)
        menu.add(Menu.NONE, R.id.context_menu_details_snapshot, Menu.NONE, R.string.action_edit_snapshot)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterContextMenuInfo
        val nodeinfo: TreeNodeInfo<ISnapshot>
        when (item.itemId) {
            R.id.context_menu_details_snapshot -> {
                nodeinfo = treeAdapter.getTreeNodeInfo(info.position)
                Utils.showDialog(fragmentManager!!, "snapshotDialog", TakeSnapshotFragment.getInstance(_vmgr, _machine, nodeinfo.id))
                return true
            }
            R.id.context_menu_delete_snapshot -> {
                nodeinfo = treeAdapter.getTreeNodeInfo(info.position)
                object : MachineTask<ISnapshot, Void>(activity as AppCompatActivity?, _vmgr, R.drawable.ic_list_snapshot_del, false, _machine) {
                    @Throws(Exception::class)
                    override fun workWithProgress(m: IMachine, console: IConsole, vararg s: ISnapshot): IProgress? {
                        return console.deleteSnapshot(s[0].id)
                    }
                }.execute(nodeinfo.id)
                return true
            }
            R.id.context_menu_restore_snapshot -> {
                nodeinfo = treeAdapter.getTreeNodeInfo(info.position)
                object : MachineTask<ISnapshot, Void>(activity as AppCompatActivity?, _vmgr, R.drawable.ic_list_snapshot, false, _machine) {
                    @Throws(Exception::class)
                    override fun workWithProgress(m: IMachine, console: IConsole, vararg s: ISnapshot): IProgress? {
                        return console.restoreSnapshot(s[0])
                    }
                }.execute(nodeinfo.id)
                return true
            }
        }
        return false
    }
}