package com.kedzie.vbox.machine

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
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
import com.kedzie.vbox.VBoxApplication
import com.kedzie.vbox.VMAction
import com.kedzie.vbox.api.*
import com.kedzie.vbox.api.jaxb.BitmapFormat
import com.kedzie.vbox.api.jaxb.SessionState
import com.kedzie.vbox.api.jaxb.VBoxEventType
import com.kedzie.vbox.app.BundleBuilder
import com.kedzie.vbox.app.Utils
import com.kedzie.vbox.event.EventIntentService
import com.kedzie.vbox.machine.settings.VMSettingsActivity
import com.kedzie.vbox.metrics.MetricActivity
import com.kedzie.vbox.soap.VBoxSvc
import com.kedzie.vbox.task.BaseTask
import com.kedzie.vbox.task.LaunchVMProcessTask
import com.kedzie.vbox.task.MachineTask
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.machine_action_item.view.*
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class ActionsFragment : Fragment(), AdapterView.OnItemClickListener, CoroutineScope {

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default

    private var _headerView: MachineView? = null
    private var _listView: ListView? = null

    /** VirtualBox API  */
    private lateinit var _vmgr: VBoxSvc
    /** The Virtual Machine  */
    private lateinit var _machine: IMachine

    @set:Inject
    lateinit var lbm: LocalBroadcastManager

    /** Event-handling local broadcasts  */
    private val _receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Timber.i("Received Broadcast: %s", intent.action!!)
            if (intent.action == VBoxEventType.ON_MACHINE_STATE_CHANGED.name) {
                val m = BundleBuilder.getProxy(intent.extras, IMachine.BUNDLE, IMachine::class.java)
                loadData()
            } else if (intent.action == VBoxEventType.ON_SESSION_STATE_CHANGED.name) {
                HandleSessionChangedEvent(_vmgr).execute(intent.extras)
            }
        }
    }

    val app: VBoxApplication
        get() = activity!!.application as VBoxApplication

    private fun loadData() {
        launch {
            Utils.cacheProperties(_machine)
            _machine.memorySize
            _machine.sessionState

            withContext(Dispatchers.Main) {
                _headerView!!.update(_machine)
                if (activity != null)
                    _listView!!.adapter = MachineActionAdapter(VMAction.getVMActions(_machine.state))
            }
        }
    }

    /**
     * Handle SessionStateChanged event
     */
    internal inner class HandleSessionChangedEvent(vmgr: VBoxSvc) : BaseTask<Bundle, SessionState>(activity as AppCompatActivity?, vmgr) {

        @Throws(Exception::class)
        override fun work(vararg params: Bundle): SessionState {
            val event = BundleBuilder.getProxy(params[0], EventIntentService.BUNDLE_EVENT, IEvent::class.java) as ISessionStateChangedEvent
            return event.state
        }

        override fun onSuccess(result: SessionState) {}
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
            view.action_item_icon.setImageResource(app.getDrawable(getItem(position)))
            return view
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        _vmgr = BundleBuilder.getVBoxSvc(arguments!!)
        _machine = BundleBuilder.getProxy(savedInstanceState
                ?: arguments, IMachine.BUNDLE, IMachine::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val swipeLayout = SwipeRefreshLayout(activity!!)
        swipeLayout.setOnRefreshListener { loadData() }
        _headerView = MachineView(activity)
        _listView = ListView(activity)
        _listView!!.clipChildren = false
        _listView!!.addHeaderView(_headerView)
        _listView!!.onItemClickListener = this
        swipeLayout.addView(_listView)
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

    override fun onSaveInstanceState(outState: Bundle) {
        BundleBuilder.putProxy(outState, IMachine.BUNDLE, _machine)
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val action = _listView!!.adapter.getItem(position) as VMAction ?: return
        if (action == VMAction.START)
            LaunchVMProcessTask(activity as AppCompatActivity?, _vmgr).execute(_machine)
        else if (action == VMAction.POWER_OFF)
            object : MachineTask<IMachine, Void>(activity as AppCompatActivity?, _vmgr, app.getDrawable(VMAction.POWER_OFF), false, _machine) {
                @Throws(Exception::class)
                override fun workWithProgress(m: IMachine, console: IConsole, vararg i: IMachine): IProgress? {
                    return console.powerDown()
                }
            }.execute(_machine)
        else if (action == VMAction.RESET)
            object : MachineTask<IMachine, Void>(activity as AppCompatActivity?, _vmgr, app.getDrawable(VMAction.RESET), true, _machine) {
                @Throws(Exception::class)
                override fun work(m: IMachine, console: IConsole, vararg i: IMachine): Void? {
                    console.reset()
                    return null
                }
            }.execute(_machine)
        else if (action == VMAction.PAUSE)
            object : MachineTask<IMachine, Void>(activity as AppCompatActivity?, _vmgr, app.getDrawable(VMAction.PAUSE), true, _machine) {
                @Throws(Exception::class)
                override fun work(m: IMachine, console: IConsole, vararg i: IMachine): Void? {
                    console.pause()
                    return null
                }
            }.execute(_machine)
        else if (action == VMAction.RESUME)
            object : MachineTask<IMachine, Void>(activity as AppCompatActivity?, _vmgr, app.getDrawable(VMAction.RESUME), true, _machine) {
                @Throws(Exception::class)
                override fun work(m: IMachine, console: IConsole, vararg i: IMachine): Void? {
                    console.resume()
                    return null
                }
            }.execute(_machine)
        else if (action == VMAction.POWER_BUTTON)
            object : MachineTask<IMachine, Void>(activity as AppCompatActivity?, _vmgr, app.getDrawable(VMAction.POWER_BUTTON), true, _machine) {
                @Throws(Exception::class)
                override fun work(m: IMachine, console: IConsole, vararg i: IMachine): Void? {
                    console.powerButton()
                    return null
                }
            }.execute(_machine)
        else if (action == VMAction.SAVE_STATE)
            object : MachineTask<IMachine, Void>(activity as AppCompatActivity?, _vmgr, app.getDrawable(VMAction.SAVE_STATE), false, _machine) {
                @Throws(Exception::class)
                override fun workWithProgress(m: IMachine, console: IConsole, vararg i: IMachine): IProgress? {
                    return console.saveState()
                }
            }.execute(_machine)
        else if (action == VMAction.DISCARD_STATE)
            object : MachineTask<IMachine, Void>(activity as AppCompatActivity?, _vmgr, app.getDrawable(VMAction.DISCARD_STATE), true, _machine) {
                @Throws(Exception::class)
                override fun work(m: IMachine, console: IConsole, vararg i: IMachine): Void? {
                    console.discardSavedState(true)
                    return null
                }
            }.execute(_machine)
        else if (action == VMAction.TAKE_SNAPSHOT) {
            Utils.showDialog((activity as AppCompatActivity).supportFragmentManager, "snapshotDialog",
                    TakeSnapshotFragment.getInstance(_vmgr, _machine, null))
        } else if (action == VMAction.VIEW_METRICS) {
            startActivity(Intent(activity, MetricActivity::class.java).putExtra(VBoxSvc.BUNDLE, _vmgr as Parcelable?)
                    .putExtra(MetricActivity.INTENT_TITLE, _machine!!.name + " Metrics")
                    .putExtra(MetricActivity.INTENT_ICON, app.getOSDrawable(_machine!!.osTypeId))
                    .putExtra(MetricActivity.INTENT_OBJECT, _machine!!.idRef)
                    .putExtra(MetricActivity.INTENT_RAM_AVAILABLE, _machine!!.memorySize)
                    .putExtra(MetricActivity.INTENT_CPU_METRICS, arrayOf("CPU/Load/User", "CPU/Load/Kernel"))
                    .putExtra(MetricActivity.INTENT_RAM_METRICS, arrayOf("RAM/Usage/Used")))
        } else if (action == VMAction.TAKE_SCREENSHOT) {
            object : MachineTask<Void, ByteArray>(activity as AppCompatActivity?, _vmgr, app.getDrawable(VMAction.TAKE_SCREENSHOT), true, _machine) {
                @Throws(Exception::class)
                override fun work(m: IMachine, console: IConsole, vararg i: Void): ByteArray? {
                    val display = console.display
                    val res = display.getScreenResolution(0)
                    return display.takeScreenShotToArray(0, Integer.valueOf(res["width"]!!), Integer.valueOf(res["height"]!!), BitmapFormat.PNG)
                }

                override fun onSuccess(result: ByteArray) {
                    super.onSuccess(result)
                    Utils.showDialog(activity!!.supportFragmentManager, "screenshotDialog", ScreenshotDialogFragment.getInstance(result))
                }
            }.execute()
        } else if (action == VMAction.EDIT_SETTINGS) {
            if (_machine!!.sessionState != SessionState.UNLOCKED) {
                AlertDialog.Builder(activity)
                        .setTitle("Cannot edit machine")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("Session state is " + _machine!!.sessionState)
                        .show()
            } else
                Utils.startActivity(activity, Intent(activity, VMSettingsActivity::class.java).putExtras(arguments!!))

        }
    }
}