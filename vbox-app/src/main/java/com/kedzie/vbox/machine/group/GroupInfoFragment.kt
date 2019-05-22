package com.kedzie.vbox.machine.group

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kedzie.vbox.R
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.api.Screenshot
import com.kedzie.vbox.api.jaxb.*
import com.kedzie.vbox.app.CollapsiblePanelView
import com.kedzie.vbox.app.Utils
import com.kedzie.vbox.soap.VBoxSvc
import com.kedzie.vbox.task.BaseTask
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.IOException
import java.util.ArrayList
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class GroupInfoFragment : Fragment(), CoroutineScope {

    private lateinit var _group: VMGroup

    private var _maxBootPosition: Int = 0
    private var _info: ArrayList<MachineInfo>? = null
    private var _view: LinearLayout? = null

    @set:Inject
    lateinit var lbm: LocalBroadcastManager

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default

    /** Event-handling local broadcasts  */
    private val _receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Timber.i( "Recieved Broadcast: %s", intent.action!!)
            if (intent.action == VBoxEventType.ON_MACHINE_STATE_CHANGED.name) {
                loadInfo()
            }
        }
    }

    class MachineInfo(var machine: IMachine, var screenshot: Screenshot?) : Parcelable {

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeParcelable(machine, flags)
            dest.writeParcelable(screenshot, flags)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object {

            @JvmField
            val CREATOR: Parcelable.Creator<MachineInfo> = object : Parcelable.Creator<MachineInfo> {

                override fun createFromParcel(source: Parcel): MachineInfo {
                    return MachineInfo(source.readParcelable<Parcelable>(LOADER) as IMachine, source.readParcelable<Parcelable>(LOADER) as Screenshot)
                }

                override fun newArray(size: Int): Array<MachineInfo?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    private fun loadInfo() {
        launch {
            val info = ArrayList<MachineInfo>(_group.children.size)
            for (child in _group.children) {
                if (child is IMachine) {
                    Utils.cacheProperties(child)
                    child.groups
                    child.memorySize
                    child.cpuCount
                    child.getHWVirtExProperty(HWVirtExPropertyType.NESTED_PAGING)
                    child.getHWVirtExProperty(HWVirtExPropertyType.ENABLED)
                    child.getCPUProperty(CPUPropertyType.PAE)
                    for (i in 1..99) {
                        if (child.getBootOrder(i) == DeviceType.NULL) break
                    }
                    val size = resources.getDimensionPixelSize(R.dimen.screenshot_size)
                    val mi = MachineInfo(child, null)
                    if (child.state == MachineState.SAVED) {
                        mi.screenshot = VBoxSvc.readSavedScreenshot(child, 0)
                        mi.screenshot!!.scaleBitmap(size, size)
                    } else if (child.state == MachineState.RUNNING) {
                        try {
                            mi.screenshot = child.api.takeScreenshot(child, size, size)
                        } catch (e: IOException) {
                            Timber.e(e, "Exception taking screenshot")
                        }

                    }
                    info.add(mi)
                }
            }
            withContext(Dispatchers.Main) {
                populateViews(info)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        if (savedInstanceState != null) {
            _group = savedInstanceState.getParcelable(VMGroup.BUNDLE)!!
            _maxBootPosition = savedInstanceState.getInt("maxBootPosition")
            _info = savedInstanceState.getParcelableArrayList("info")
        } else {
            _group = arguments!!.getParcelable(VMGroup.BUNDLE)!!
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(VMGroup.BUNDLE, _group)
        outState.putParcelableArrayList("info", _info)
        outState.putInt("maxBootPosition", _maxBootPosition)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (Utils.getScreenSize(newConfig) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            Timber.i("Handling orientation change")
            populateViews(_info!!)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _view = LinearLayout(activity)
        _view!!.orientation = LinearLayout.VERTICAL
        val swipeLayout = SwipeRefreshLayout(activity!!)
        swipeLayout.setOnRefreshListener { loadInfo() }
        val scrollView = ScrollView(activity)
        scrollView.addView(_view)
        swipeLayout.addView(scrollView)
        return swipeLayout
    }

    override fun onStart() {
        super.onStart()
        job = Job()
        lbm.registerReceiver(_receiver, Utils.createIntentFilter(VBoxEventType.ON_MACHINE_STATE_CHANGED.name))
    }

    override fun onStop() {
        job.cancel()
        lbm.unregisterReceiver(_receiver)
        super.onStop()
    }

    private fun populateViews(m: List<MachineInfo>) {
        _view!!.removeAllViews()
        val inflater = LayoutInflater.from(activity)
        for (node in m) {
            val view = inflater.inflate(R.layout.group_info, _view, false)
            Utils.setTextView(view, R.id.name, node.machine.name)
            Utils.setTextView(view, R.id.ostype, node.machine.osTypeId)
            if (!Utils.isEmpty(node.machine.groups))
                Utils.setTextView(view, R.id.groups, node.machine.groups[0])
            Utils.setTextView(view, R.id.baseMemory, node.machine.memorySize!!.toString() + "")
            Utils.setTextView(view, R.id.processors, node.machine.cpuCount!!.toString() + "")
            var acceleration = ""
            if (node.machine.getHWVirtExProperty(HWVirtExPropertyType.ENABLED)!!)
                acceleration = "VT-x/AMD-V"
            if (node.machine.getHWVirtExProperty(HWVirtExPropertyType.NESTED_PAGING)!!)
                acceleration += (if (acceleration == "") "" else ", ") + "Nested Paging"
            if (node.machine.getCPUProperty(CPUPropertyType.PAE)!!)
                acceleration += (if (acceleration == "") "" else ", ") + "PAE/NX"
            Utils.setTextView(view, R.id.acceleration, acceleration)
            val bootOrder = StringBuffer()
            for (i in 1..99) {
                val b = node.machine.getBootOrder(i)
                if (b == DeviceType.NULL) break
                Utils.appendWithComma(bootOrder, b.toString())
            }
            Utils.setTextView(view, R.id.bootOrder, bootOrder.toString())
            val previewPanel = view.findViewById<View>(R.id.previewPanel) as CollapsiblePanelView
            if (node.screenshot != null) {
                val preview = view.findViewById<View>(R.id.preview) as ImageView
                preview.adjustViewBounds = true
                preview.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                preview.setImageBitmap(node.screenshot!!.bitmap)
                previewPanel.expand(false)
            } else {
                previewPanel.collapse(false)
            }
            _view!!.addView(view)
        }
    }

    companion object {
        internal val LOADER = GroupInfoFragment::class.java.classLoader
    }
}
