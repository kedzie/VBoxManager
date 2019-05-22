package com.kedzie.vbox.machine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.NetworkOnMainThreadException
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.kedzie.vbox.R
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.api.IVRDEServer
import com.kedzie.vbox.api.jaxb.*
import com.kedzie.vbox.app.BundleBuilder
import com.kedzie.vbox.app.CollapsiblePanelView
import com.kedzie.vbox.app.Utils
import com.kedzie.vbox.machine.group.GroupInfoFragment
import com.kedzie.vbox.soap.VBoxSvc
import com.kedzie.vbox.task.BaseTask
import com.kedzie.vbox.task.MachineRunnable
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.group_info.*
import kotlinx.android.synthetic.main.machine_info_column_1.*
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class InfoFragment : Fragment(), CoroutineScope {

    //	private VBoxSvc _vmgr;
    private lateinit var _machine: IMachine
    private var _machineInfo: GroupInfoFragment.MachineInfo? = null

    @set:Inject
    lateinit var lbm: LocalBroadcastManager

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default

    /** Event-handling local broadcasts  */
    private val _receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == VBoxEventType.ON_MACHINE_STATE_CHANGED.name) {
                val m = BundleBuilder.getProxy(intent.extras, IMachine.BUNDLE, IMachine::class.java)
                loadInfo(false)
            }
        }
    }

    private fun loadInfo(clearCache: Boolean) {
        launch {
            with(_machine) {
                if (clearCache)
                    clearCache()
                //cache values
                com.kedzie.vbox.app.Utils.cacheProperties(this)

                getMemorySize()
                getCPUCount()
                getVRAMSize()
                getAccelerate2DVideoEnabled()
                getAccelerate3DEnabled()
                getDescription()
                getGroups()
                getHWVirtExProperty(com.kedzie.vbox.api.jaxb.HWVirtExPropertyType.NESTED_PAGING)
                getHWVirtExProperty(com.kedzie.vbox.api.jaxb.HWVirtExPropertyType.ENABLED)
                getCPUProperty(com.kedzie.vbox.api.jaxb.CPUPropertyType.PAE)

                //audio
                getAudioAdapter().getAudioController()
                getAudioAdapter().getAudioDriver()

                //boot order
                for (i in 1..99)
                    if (getBootOrder(i) == com.kedzie.vbox.api.jaxb.DeviceType.NULL) break

                //Remote Desktop
                getVRDEServer().getVRDEProperty(com.kedzie.vbox.api.IVRDEServer.PROPERTY_PORT)

                //storage controllers
                val controllers = getStorageControllers()
                for (controller in controllers) {
                    controller.getBus()
                    for (a in getMediumAttachmentsOfController(controller.getName())) {
                        if (a.getMedium() != null) {
                            a.getMedium().getName()
                            a.getMedium().getBase().getName()
                        }
                    }
                }

                //network adapters
                for (i in 0..3) {
                    val adapter = getNetworkAdapter(i)
                    adapter.getEnabled()
                    adapter.getAdapterType()
                    adapter.getAttachmentType()
                    adapter.getBridgedInterface()
                    adapter.getHostOnlyInterface()
                    adapter.getGenericDriver()
                    adapter.getInternalNetwork()
                }
                //screenshots
                val size = resources.getDimensionPixelSize(com.kedzie.vbox.R.dimen.screenshot_size)
                val info = com.kedzie.vbox.machine.group.GroupInfoFragment.MachineInfo(this, null)
                if (state == com.kedzie.vbox.api.jaxb.MachineState.SAVED) {
                    info.screenshot = com.kedzie.vbox.soap.VBoxSvc.readSavedScreenshot(this, 0)
                    info.screenshot!!.scaleBitmap(size, size)
                } else if (state == com.kedzie.vbox.api.jaxb.MachineState.RUNNING) {
                    try {
                        info.screenshot = api.takeScreenshot(this, size, size)
                    } catch (e: IOException) {
                        Timber.e(e, "Exception taking screenshot")
                    }

                }
                withContext(Dispatchers.Main) {
                    _machineInfo = info
                    populateViews()
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (Utils.getScreenSize(newConfig) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            Timber.i( "Handling orientation change")
            val view = view as FrameLayout?
            view!!.removeAllViews()
            LayoutInflater.from(activity).inflate(R.layout.machine_info, view, true)
            populateViews()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this);
        if (savedInstanceState != null) {
            _machineInfo = savedInstanceState.getParcelable("info")
        }
        _machine = BundleBuilder.getProxy(arguments, IMachine.BUNDLE, IMachine::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = SwipeRefreshLayout(activity!!)
        view.setOnRefreshListener { loadInfo(true) }
        inflater.inflate(R.layout.machine_info, view, true)
        return view
    }

    override fun onStart() {
        super.onStart()
        Timber.d("onStart")
        job = Job()
        lbm.registerReceiver(_receiver, Utils.createIntentFilter(VBoxEventType.ON_MACHINE_STATE_CHANGED.name))
        if (_machineInfo != null)
            populateViews()
        else
            loadInfo(false)
    }

    override fun onStop() {
        Timber.d("onStop")
        job.cancel()
        lbm.unregisterReceiver(_receiver)
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("info", _machineInfo)
    }

    private fun populateViews() {
        try {
            val m = _machineInfo!!.machine
            name!!.text = m.name
            ostype!!.text = m.osTypeId
            if (!Utils.isEmpty(m.groups))
                groups!!.text = m.groups[0]
            else
                groups!!.text = "None"
            baseMemory!!.text = m.memorySize!!.toString() + ""
            processors!!.text = m.cpuCount!!.toString() + ""
            //boot order
            val bootOrderBuf = StringBuffer()
            for (i in 1..99) {
                val b = m.getBootOrder(i)
                if (b == DeviceType.NULL) break
                Utils.appendWithComma(bootOrderBuf, b.toString())
            }
            bootOrder!!.text = bootOrderBuf.toString()

            val accelerationBuf = StringBuffer()
            if (m.getHWVirtExProperty(HWVirtExPropertyType.ENABLED)!!)
                Utils.appendWithComma(accelerationBuf, "VT-x/AMD-V")
            if (m.getHWVirtExProperty(HWVirtExPropertyType.NESTED_PAGING)!!)
                Utils.appendWithComma(accelerationBuf, "Nested Paging")
            if (m.getCPUProperty(CPUPropertyType.PAE)!!)
                Utils.appendWithComma(accelerationBuf, "PAE/NX")
            acceleration!!.text = accelerationBuf.toString()

            //storage controllers
            val storageBuf = StringBuffer()
            val controllers = m.storageControllers
            for (controller in controllers) {
                storageBuf.append("Controller: ").append(controller.name).append("\n")
                if (controller.bus == StorageBus.SATA) {
                    for (a in m.getMediumAttachmentsOfController(controller.name))
                        storageBuf.append(String.format("SATA Port %1\$d\t\t%2\$s\n", a.port, if (a.medium == null) "" else a.medium.base.name))
                } else if (controller.bus == StorageBus.IDE) {
                    for (a in m.getMediumAttachmentsOfController(controller.name)) {
                        storageBuf.append(String.format("IDE %1\$s %2\$s\t\t%3\$s\n", if (a.port == 0) "Primary" else "Secondary", if (a.device == 0) "Master" else "Slave", if (a.medium == null) "" else a.medium.base.name))
                    }
                } else {
                    for (a in m.getMediumAttachmentsOfController(controller.name))
                        storageBuf.append(String.format("Port: %1\$d Device: %2\$d\t\t%3\$s\n", a.port, a.device, if (a.medium == null) "" else a.medium.base.name))
                }
            }
            storage!!.text = storageBuf.toString()
            //audio devices
            audio_controller!!.text = m.audioAdapter.audioController.toString()
            audio_driver!!.text = m.audioAdapter.audioDriver.toString()

            //network devices
            val networkText = StringBuffer()
            for (i in 0..3) {
                val adapter = m.getNetworkAdapter(i)
                if (!adapter.enabled)
                    continue
                if (i > 0)
                    networkText.append("\n")
                networkText.append("Adapter ").append(i + 1).append(": ").append(adapter.adapterType)
                val type = adapter.attachmentType
                Timber.v( "Adapter #%d attachment Type: %s",(i + 1), type)
                if (type == NetworkAttachmentType.BRIDGED)
                    networkText.append("  (Bridged Adapter, ").append(adapter.bridgedInterface).append(")")
                else if (type == NetworkAttachmentType.HOST_ONLY)
                    networkText.append("  (Host-Only Adapter, ").append(adapter.hostOnlyInterface).append(")")
                else if (type == NetworkAttachmentType.GENERIC)
                    networkText.append("  (Generic-Driver Adapter, ").append(adapter.genericDriver).append(")")
                else if (type == NetworkAttachmentType.INTERNAL)
                    networkText.append("  (Internal-Network Adapter, ").append(adapter.internalNetwork).append(")")
                else if (type == NetworkAttachmentType.NAT)
                    networkText.append("  (NAT)")
            }
            network!!.text = networkText.toString()

            videoMemory!!.text = m.vramSize!!.toString() + " MB"
            accelerationVideo!!.text = (if (m.accelerate2DVideoEnabled) "2D" else "") + " " + if (m.accelerate3DEnabled) "3D" else ""

            rdpPort!!.text = m.vrdeServer.getVRDEProperty(IVRDEServer.PROPERTY_PORT)
            description!!.text = m.description + ""

            if (_machineInfo!!.screenshot != null) {
                preview!!.setImageBitmap(_machineInfo!!.screenshot!!.bitmap)
                preview!!.adjustViewBounds = true
                preview!!.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                previewPanel!!.expand(false)
            } else
                previewPanel!!.collapse(false)
        } catch (e: NetworkOnMainThreadException) {
            loadInfo(false)
        }

    }
}