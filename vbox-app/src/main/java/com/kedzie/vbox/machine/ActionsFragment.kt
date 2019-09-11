package com.kedzie.vbox.machine

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kedzie.vbox.R
import com.kedzie.vbox.SettingsActivity
import com.kedzie.vbox.VBoxApplication
import com.kedzie.vbox.VMAction
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.api.IPerformanceCollector
import com.kedzie.vbox.api.IProgress
import com.kedzie.vbox.api.ISessionStateChangedEvent
import com.kedzie.vbox.api.jaxb.BitmapFormat
import com.kedzie.vbox.api.jaxb.LockType
import com.kedzie.vbox.api.jaxb.SessionState
import com.kedzie.vbox.api.jaxb.VBoxEventType
import com.kedzie.vbox.app.Utils
import com.kedzie.vbox.event.EventIntentService
import com.kedzie.vbox.machine.settings.VMSettingsActivity
import com.kedzie.vbox.metrics.MetricFragment
import com.kedzie.vbox.soap.VBoxSvc
import com.kedzie.vbox.task.ProgressService
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.machine_action_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class ActionsFragment(arguments: Bundle) : Fragment(), AdapterView.OnItemClickListener, CoroutineScope {

    init {
        this.arguments = arguments
    }

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private var listView: ListView? = null

    /** VirtualBox API  */
    private lateinit var vmgr: VBoxSvc
    /** The Virtual Machine  */
    private lateinit var machine: IMachine

    @set:Inject
    lateinit var lbm: LocalBroadcastManager

    /** Event-handling local broadcasts  */
    private val _receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Timber.i("Received Broadcast: %s", intent.action!!)
            if (intent.action == VBoxEventType.ON_MACHINE_STATE_CHANGED.name) {
                val m = intent.getParcelableExtra<IMachine>(IMachine.BUNDLE)
                loadData()
            } else if (intent.action == VBoxEventType.ON_SESSION_STATE_CHANGED.name) {
                val event = intent.getParcelableExtra<ISessionStateChangedEvent>(EventIntentService.BUNDLE_EVENT)
            }
        }
    }

    private fun loadData() {
        launch {
            listView?.adapter = MachineActionAdapter(VMAction.getVMActions(machine.getState()))
        }
    }

    /**
     * List Adapter for Virtual Machine Actions
     */
    internal inner class MachineActionAdapter(actions: Array<VMAction>) : ArrayAdapter<VMAction>(activity, 0, actions) {

        private val _layoutInflater = LayoutInflater.from(activity)

        override fun getView(position: Int, existingView: View?, parent: ViewGroup): View {
            var view = if (existingView != null) existingView
                            else _layoutInflater.inflate(R.layout.machine_action_item, parent, false)
            view.action_item_text.text = getItem(position)!!.toString()
            view.action_item_icon.setImageResource(getItem(position).drawable())
            return view
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        vmgr = arguments!!.getParcelable(VBoxSvc.BUNDLE)
        machine = arguments!!.getParcelable(IMachine.BUNDLE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val swipeLayout = SwipeRefreshLayout(activity!!)
        swipeLayout.setOnRefreshListener { loadData() }
        listView = ListView(activity)
        listView!!.clipChildren = false
        listView!!.onItemClickListener = this
        swipeLayout.addView(listView)
        return swipeLayout
    }

    override fun onStart() {
        super.onStart()
        job = Job()
        loadData()
        lbm.registerReceiver(_receiver, Utils.createIntentFilter(
                VBoxEventType.ON_MACHINE_STATE_CHANGED.name,
                VBoxEventType.ON_SESSION_STATE_CHANGED.name))
    }

    override fun onStop() {
        job.cancel()
        lbm.unregisterReceiver(_receiver)
        super.onStop()
    }

    private fun handleProgress(p: IProgress, action: VMAction) =
            activity?.startService(Intent(activity, ProgressService::class.java)
                    .putExtra(IProgress.BUNDLE, p)
                    .putExtra(ProgressService.INTENT_ICON, action.drawable()))

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val action = listView!!.adapter.getItem(position) as VMAction

        launch {
            if (action == VMAction.TAKE_SNAPSHOT) {
                Utils.showDialog((activity as AppCompatActivity).supportFragmentManager, "snapshotDialog",
                        TakeSnapshotFragment.getInstance(vmgr, machine, null))
            } else if (action == VMAction.VIEW_METRICS) {
                startActivity(Intent(activity, MetricFragment::class.java).putExtra(VBoxSvc.BUNDLE, vmgr)
                        .putExtra(MetricFragment.INTENT_TITLE, machine.getName() + " Metrics")
                        .putExtra(MetricFragment.INTENT_ICON, VBoxApplication.getOSDrawable(activity, machine.getOSTypeId()))
                        .putExtra(MetricFragment.INTENT_OBJECT, machine.idRef)
                        .putExtra(MetricFragment.INTENT_RAM_AVAILABLE, machine.getMemorySize())
                        .putExtra(MetricFragment.INTENT_CPU_METRICS, arrayOf(IPerformanceCollector.CPU_LOAD_USER, IPerformanceCollector.CPU_LOAD_KERNEL))
                        .putExtra(MetricFragment.INTENT_RAM_METRICS, arrayOf(IPerformanceCollector.RAM_USAGE_USED)))
            } else if (action == VMAction.EDIT_SETTINGS) {
                if (machine.getSessionStateNoCache() != SessionState.UNLOCKED) {
                    AlertDialog.Builder(activity)
                            .setTitle("Cannot edit machine")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setMessage("Session state is " + machine.getSessionStateNoCache())
                            .show()
                } else {
                    Utils.startActivity(activity, Intent(activity, VMSettingsActivity::class.java).putExtras(arguments!!))
                }
            } else {
                val session = vmgr.vbox!!.getSessionObject()
                if (session.getState() == SessionState.UNLOCKED)
                    machine.lockMachine(session, LockType.SHARED)
                try {
                    when (action) {
                        VMAction.START -> {
                            handleProgress(machine.launchVMProcess(session, IMachine.LaunchMode.headless), VMAction.START)
                            vmgr.vbox!!.getPerformanceCollector().setupMetrics(arrayOf<String>("*:"),
                                    Utils.getIntPreference(context, SettingsActivity.PREF_PERIOD),
                                    Utils.getIntPreference(context, SettingsActivity.PREF_COUNT), machine)
                        }
                        VMAction.POWER_OFF -> {
                            handleProgress(session.getConsole().powerDown(), VMAction.POWER_OFF)
                        }
                        VMAction.RESET -> {
                            session.getConsole().reset()
                        }
                        VMAction.PAUSE -> {
                            session.getConsole().pause()
                        }
                        VMAction.RESUME -> {
                            session.getConsole().resume()
                        }
                        VMAction.POWER_BUTTON -> {
                            session.getConsole().powerButton()
                        }
                        VMAction.SAVE_STATE -> {
                            handleProgress(session.getConsole().saveState(), VMAction.SAVE_STATE)
                        }
                        VMAction.DISCARD_STATE -> {
                            session.getConsole().discardSavedState(true)
                        }
                        VMAction.TAKE_SCREENSHOT -> {
                            val display = session.getConsole().getDisplay()
                            val res = display.getScreenResolution(0)
                            val result = display.takeScreenShotToArray(0, res["width"]!!.toInt(), res["height"]!!.toInt(), BitmapFormat.PNG)
                            Utils.showDialog(activity!!.supportFragmentManager, "screenshotDialog", ScreenshotDialogFragment.getInstance(result))
                        }
                    }
                } finally {
                    if (session.getState() == SessionState.LOCKED)
                        session.unlockMachine()
                }
            }
        }
    }
}