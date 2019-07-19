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
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kedzie.vbox.R
import com.kedzie.vbox.VBoxApplication
import com.kedzie.vbox.VMAction
import com.kedzie.vbox.api.IProgress
import com.kedzie.vbox.api.ISnapshot
import com.kedzie.vbox.api.ISnapshotDeletedEvent
import com.kedzie.vbox.api.ISnapshotTakenEvent
import com.kedzie.vbox.api.jaxb.LockType
import com.kedzie.vbox.api.jaxb.SessionState
import com.kedzie.vbox.api.jaxb.VBoxEventType
import com.kedzie.vbox.app.Utils
import com.kedzie.vbox.event.EventIntentService
import com.kedzie.vbox.task.ProgressService
import kotlinx.android.synthetic.main.snapshot_tree.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf
import pl.polidea.treeview.*
import kotlin.coroutines.CoroutineContext

/**
 *
 * @apiviz.stereotype fragment
 */
class SnapshotFragment(arguments: Bundle) : Fragment(), CoroutineScope {

    init {
        this.arguments = arguments
    }

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main


    private val model: MachineListViewModel by sharedViewModel { parametersOf(activity!!) }

    private val args: SnapshotFragmentArgs by navArgs()

    private var root: ISnapshot? = null
    private lateinit var stateManager: TreeStateManager<ISnapshot>
    private lateinit var treeBuilder: TreeBuilder<ISnapshot>

    private val lbm: LocalBroadcastManager by inject { parametersOf(activity!!) }

    private val _receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == VBoxEventType.ON_SNAPSHOT_TAKEN.name) {
                val event = intent.getParcelableExtra<ISnapshotTakenEvent>(EventIntentService.BUNDLE_EVENT)
                Utils.toastShort(activity, "Snapshot event: %1\$s", intent.action)
                launch {
                    args.machine.getCurrentSnapshotNoCache()?.let { snapshot ->
                        if (snapshot.getParent() == null) {
                            root = snapshot
                            treeBuilder.sequentiallyAddNextNode(snapshot, 0)
                        } else {
                            val parent = stateManager.getNodeInfo(snapshot.getParent()).id
                            treeBuilder.addRelation(parent, snapshot)
                        }
                    }
                }
            } else if (intent.action == VBoxEventType.ON_SNAPSHOT_DELETED.name) {
                val event = intent.getParcelableExtra<ISnapshotDeletedEvent>(EventIntentService.BUNDLE_EVENT)
                Utils.toastShort(activity, "Snapshot event: %1\$s", intent.action)
                launch {
                    findSnapshot(root!!, event.getSnapshotId())?.let { snapshot ->
                        stateManager.removeNodeRecursively(snapshot)
                        if (snapshot == root) {
                            root = null
                        }
                    }

                }
            }
        }
    }

    private val app: VBoxApplication
        get() = activity!!.application as VBoxApplication

    private val treeAdapter: SnapshotTreeAdapter
        get() = mainTreeView!!.adapter as SnapshotTreeAdapter


    private fun loadSnapshots() {
        launch {
            root = model.machine.value?.getCurrentSnapshotNoCache()

            while (root?.getParent() != null)
                root = root?.getParent()

            stateManager = InMemoryTreeStateManager()
            treeBuilder = TreeBuilder(stateManager)
            mainTreeView!!.adapter = SnapshotTreeAdapter(activity!!, stateManager, 10)

            root?.let {
                populate(null, it)
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
            launch {
                text1.text = treeNodeInfo.id.getName()
            }
            text1.setCompoundDrawablesWithIntrinsicBounds(VMAction.RESTORE_SNAPSHOT.drawable(), 0, 0, 0)
            return view
        }
    }

    private suspend fun findSnapshot(current: ISnapshot, id: String): ISnapshot? {
        if (current.getId() == id)
            return current
        for (child in current.getChildren()) {
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
    private suspend fun populate(parent: ISnapshot?, snapshot: ISnapshot) {
        if (parent == null)
            treeBuilder.sequentiallyAddNextNode(snapshot, 0)
        else
            treeBuilder.addRelation(parent, snapshot)
        for (child in snapshot.getChildren())
            populate(snapshot, child)
    }

    /**
     * Refresh the snapshot tree
     */
    private fun refresh() {
        stateManager.clear()
        stateManager = InMemoryTreeStateManager()
        treeBuilder = TreeBuilder(stateManager)
        mainTreeView!!.adapter = SnapshotTreeAdapter(activity!!, stateManager, 10)
        loadSnapshots()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("manager", stateManager)
        outState.putParcelable("root", root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            stateManager = savedInstanceState.getSerializable("manager") as TreeStateManager<ISnapshot>
            root = savedInstanceState.getParcelable("root")
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
        mainTreeView.choiceMode = ListView.CHOICE_MODE_NONE
        registerForContextMenu(mainTreeView!!)
        addButton.setOnClickListener {
            findNavController().navigate(SnapshotFragmentDirections.showSnapshot(null))
        }
    }

    override fun onStart() {
        super.onStart()
        job = Job()
        lbm.registerReceiver(_receiver, Utils.createIntentFilter(
                VBoxEventType.ON_SNAPSHOT_DELETED.name,
                VBoxEventType.ON_SNAPSHOT_TAKEN.name))

        if (stateManager == null)
            loadSnapshots()
        else {
            treeBuilder = TreeBuilder(stateManager)
            mainTreeView.adapter = SnapshotTreeAdapter(activity!!, stateManager, 10)
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

    private fun handleProgress(p: IProgress, action: VMAction) =
            activity?.startService(Intent(activity, ProgressService::class.java)
                    .putExtra(IProgress.BUNDLE, p)
                    .putExtra(ProgressService.INTENT_ICON, action.drawable()))


    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterContextMenuInfo
        val nodeinfo: TreeNodeInfo<ISnapshot>
        when (item.itemId) {
            R.id.context_menu_details_snapshot -> {
                nodeinfo = treeAdapter.getTreeNodeInfo(info.position)
                findNavController().navigate(SnapshotFragmentDirections.showSnapshot(nodeinfo.id))
                return true
            }
            R.id.context_menu_delete_snapshot -> {
                nodeinfo = treeAdapter.getTreeNodeInfo(info.position)
                launch {
                    val session = args.vmgr.vbox!!.getSessionObject()
                    if (session.getState() == SessionState.UNLOCKED)
                        args.machine.lockMachine(session, LockType.SHARED)
                    try {
                        handleProgress(session.getConsole().deleteSnapshot(nodeinfo.id.idRef), VMAction.DELETE_SNAPSHOT)
                    } finally {
                        if (session.getState() == SessionState.LOCKED)
                            session.unlockMachine()
                    }
                }
                return true
            }
            R.id.context_menu_restore_snapshot -> {
                nodeinfo = treeAdapter.getTreeNodeInfo(info.position)
                launch {
                    val session = args.vmgr.vbox!!.getSessionObject()
                    if (session.getState() == SessionState.UNLOCKED)
                        args.machine.lockMachine(session, LockType.SHARED)
                    try {
                        handleProgress(session.getConsole().restoreSnapshot(nodeinfo.id), VMAction.RESTORE_SNAPSHOT)
                    } finally {
                        if (session.getState() == SessionState.LOCKED)
                            session.unlockMachine()
                    }
                }
                return true
            }
        }
        return false
    }
}