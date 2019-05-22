package com.kedzie.vbox.machine.group

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kedzie.vbox.R
import com.kedzie.vbox.SettingsActivity
import com.kedzie.vbox.api.IHost
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.api.IManagedObjectRef
import com.kedzie.vbox.api.jaxb.VBoxEventType
import com.kedzie.vbox.app.BundleBuilder
import com.kedzie.vbox.app.Utils
import com.kedzie.vbox.soap.VBoxSvc
import com.kedzie.vbox.task.BaseTask
import com.kedzie.vbox.task.DialogTask
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.HashMap
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class MachineGroupListFragment : Fragment(), CoroutineScope {

    private lateinit var _vmgr: VBoxSvc
    private var _root: VMGroup? = null
    private lateinit var _host: IHost
    private var _version: String? = null
    private lateinit var _listView: VMGroupListView
    private lateinit var _selectListener: VMGroupListView.OnTreeNodeSelectListener

    @set:Inject
    lateinit var lbm: LocalBroadcastManager

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default

    private val _receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == VBoxEventType.ON_MACHINE_STATE_CHANGED.name)
                launch {
                    val m = BundleBuilder.getProxy(intent.extras, IMachine.BUNDLE, IMachine::class.java)
                    Utils.cacheProperties(m)

                    withContext(Dispatchers.Main) {
                        _listView.update(m)
                    }
                }
        }
    }

    private val groupCache = HashMap<String, VMGroup>()

    private fun get(name: String): VMGroup? {
        if (!groupCache.containsKey(name))
            groupCache[name] = VMGroup(name)
        return groupCache[name]
    }

    private fun loadGroups() {
        launch {
            _vmgr.vBox.version
            _host = _vmgr.vBox.host
            _host.memorySize
            _version = _host.api.vBox.version
            val vGroups = _vmgr.vBox.machineGroups
            for (group in vGroups) {
                if (group == "/") continue
                var previous = get(group)
                var lastIndex = group.lastIndexOf('/')
                var tmp = group
                while (lastIndex > 0) {
                    tmp = tmp.substring(0, lastIndex)
                    val current = get(tmp)
                    current!!.addChild(previous)
                    previous = current
                    lastIndex = tmp.lastIndexOf('/')
                }
                get("/")!!.addChild(get(tmp))
            }
            for (machine in _vmgr.vBox.machines) {
                Utils.cacheProperties(machine)
                val groups = machine.groups
                get(groups[0])!!.addChild(machine)
            }
            _vmgr.vBox.performanceCollector.setupMetrics(arrayOf("*:"),
                    Utils.getIntPreference(activity!!.applicationContext, SettingsActivity.PREF_PERIOD),
                    Utils.getIntPreference(activity!!.applicationContext, SettingsActivity.PREF_COUNT),
                    null as IManagedObjectRef?)

            withContext(Dispatchers.Main) {
                _listView.setRoot(get("/"), _host, _version)
                _root = get("/")
            }
        }
    }

    override fun onAttach(activity: Context) {
        super.onAttach(activity)
        try {
            _selectListener = activity as VMGroupListView.OnTreeNodeSelectListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity does not implement OnTreeNodeSelectListener")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this);
        super.onCreate(savedInstanceState)

        if (arguments != null && arguments!!.containsKey(VBoxSvc.BUNDLE)) {
            _vmgr = BundleBuilder.getVBoxSvc(arguments!!)
        } else {
            _vmgr = BundleBuilder.getVBoxSvc(activity!!.intent)
        }

        if (savedInstanceState != null) {
            _root = savedInstanceState.getParcelable(VMGroup.BUNDLE)
            _vmgr = BundleBuilder.getVBoxSvc(savedInstanceState)
            _host = _vmgr.vBox.host
            _version = savedInstanceState.getString("version")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = SwipeRefreshLayout(activity!!)
        view.setOnRefreshListener { loadGroups() }
        _listView = VMGroupListView(activity, _vmgr)
        _listView.setOnTreeNodeSelectListener(_selectListener)
        view.addView(_listView)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        _listView.isSelectionEnabled = activity!!.findViewById<View>(R.id.details) != null
    }

    override fun onStart() {
        super.onStart()
        lbm.registerReceiver(_receiver, IntentFilter(VBoxEventType.ON_MACHINE_STATE_CHANGED.name))
        Timber.d("onStart")
        job = Job()
        if (_root == null)
            loadGroups()
        else
            _listView.setRoot(_root, _host, _version)
    }

    override fun onStop() {
        Timber.d("onStop")
        job.cancel()
        super.onStop()
        lbm.unregisterReceiver(_receiver)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        BundleBuilder.putVBoxSvc(outState, _vmgr)
        outState.putParcelable(VMGroup.BUNDLE, _root)
        outState.putString("version", _version)
        //save current machine
        outState.putParcelable("checkedItem", _listView.selectedObject)
    }
}
