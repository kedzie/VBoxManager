package com.kedzie.vbox.machine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kedzie.vbox.R
import com.kedzie.vbox.api.*
import com.kedzie.vbox.api.jaxb.*
import com.kedzie.vbox.app.BundleBuilder
import com.kedzie.vbox.app.Utils
import kotlinx.android.synthetic.main.group_info.*
import kotlinx.android.synthetic.main.machine_info_column_1.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class InfoFragment(arguments: Bundle) : Fragment(), CoroutineScope {

    init {
        this.arguments = arguments
    }

    private lateinit var machineInfo: MachineInfo

    private val lbm: LocalBroadcastManager by inject { parametersOf(activity!!)}

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    /** Event-handling local broadcasts  */
    private val _receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == VBoxEventType.ON_MACHINE_STATE_CHANGED.name) {
                val m = BundleBuilder.getProxy(intent.extras, IMachine.BUNDLE, IMachine::class.java)
                loadInfo(false)
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
            loadInfo(false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        machineInfo = if (savedInstanceState != null) {
            savedInstanceState.getParcelable("info")
        } else {
            MachineInfo(arguments!!.getParcelable(IMachine.BUNDLE)!!, null)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("info", machineInfo)
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

        loadInfo(false)
    }

    override fun onStop() {
        Timber.d("onStop")
        job.cancel()
        lbm.unregisterReceiver(_receiver)
        super.onStop()
    }

    private fun loadInfo(clearCache: Boolean) {
        launch {
            with(machineInfo.machine) {
                if (clearCache)
                    clearCache()

                val m = machineInfo.machine
                name.text = m.getName()
                ostype.text = m.getOSTypeId()
                if (!Utils.isEmpty(m.getGroups()))
                    groups.text = m.getGroups()[0]
                else
                    groups.text = "None"
                baseMemory.text = m.getMemorySize().toString() + ""
                processors.text = m.getCPUCount().toString() + ""
                //boot order
                val bootOrderBuf = StringBuffer()
                for (i in 1..99) {
                    val b = m.getBootOrder(i)
                    if (b == DeviceType.NULL) break
                    Utils.appendWithComma(bootOrderBuf, b.toString())
                }
                bootOrder.text = bootOrderBuf.toString()

                val accelerationBuf = StringBuffer()
                if (m.getHWVirtExProperty(HWVirtExPropertyType.ENABLED)!!)
                    Utils.appendWithComma(accelerationBuf, "VT-x/AMD-V")
                if (m.getHWVirtExProperty(HWVirtExPropertyType.NESTED_PAGING)!!)
                    Utils.appendWithComma(accelerationBuf, "Nested Paging")
                if (m.getCPUProperty(CPUPropertyType.PAE)!!)
                    Utils.appendWithComma(accelerationBuf, "PAE/NX")
                acceleration.text = accelerationBuf.toString()

                //storage controllers
                val storageBuf = StringBuffer()
                val controllers = m.getStorageControllers()
                for (controller in controllers) {
                    storageBuf.append("Controller: ").append(controller.getName()).append("\n")
                    if (controller.getBus() == StorageBus.SATA) {
                        for (a in m.getMediumAttachmentsOfController(controller.getName()))
                            storageBuf.append(String.format("SATA Port %1\$d\t\t%2\$s\n", a.port, if (a.medium == null) "" else a.medium.getBase().getName()))
                    } else if (controller.getBus() == StorageBus.IDE) {
                        for (a in m.getMediumAttachmentsOfController(controller.getName())) {
                            storageBuf.append(String.format("IDE %1\$s %2\$s\t\t%3\$s\n",
                                    if (a.port == 0) "Primary" else "Secondary",
                                    if (a.device == 0) "Master" else "Slave",
                                    if (a.medium == null) "" else a.medium.getBase().getName()))
                        }
                    } else {
                        for (a in m.getMediumAttachmentsOfController(controller.getName()))
                            storageBuf.append(String.format("Port: %1\$d Device: %2\$d\t\t%3\$s\n", a.port, a.device, if (a.medium == null) "" else a.medium.getBase().getName()))
                    }
                }
                storage.text = storageBuf.toString()
                //audio devices
                audio_controller.text = m.getAudioAdapter().getAudioController().toString()
                audio_driver.text = m.getAudioAdapter().getAudioDriver().toString()

                //network devices
                val networkText = StringBuffer()
                for (i in 0..3) {
                    val adapter = m.getNetworkAdapter(i)
                    if (!adapter.getEnabled())
                        continue
                    if (i > 0)
                        networkText.append("\n")
                    networkText.append("Adapter ").append(i + 1).append(": ").append(adapter.getAdapterType())
                    val type = adapter.getAttachmentType()
                    Timber.v("Adapter #%d attachment Type: %s", (i + 1), type)
                    if (type == NetworkAttachmentType.BRIDGED)
                        networkText.append("  (Bridged Adapter, ").append(adapter.getBridgedInterface()).append(")")
                    else if (type == NetworkAttachmentType.HOST_ONLY)
                        networkText.append("  (Host-Only Adapter, ").append(adapter.getHostOnlyInterface()).append(")")
                    else if (type == NetworkAttachmentType.GENERIC)
                        networkText.append("  (Generic-Driver Adapter, ").append(adapter.getGenericDriver()).append(")")
                    else if (type == NetworkAttachmentType.INTERNAL)
                        networkText.append("  (Internal-Network Adapter, ").append(adapter.getInternalNetwork()).append(")")
                    else if (type == NetworkAttachmentType.NAT)
                        networkText.append("  (NAT)")
                }
                network.text = networkText.toString()

                videoMemory.text = m.getVRAMSize().toString() + " MB"
                accelerationVideo.text = (if (m.getAccelerate2DVideoEnabled()) "2D" else "") + " " + if (m.getAccelerate3DEnabled()) "3D" else ""

                rdpPort.text = m.getVRDEServer().getVRDEProperty(IVRDEServer.PROPERTY_PORT)
                description.text = m.getDescription() + ""

                //screenshots
                val size = resources.getDimensionPixelSize(R.dimen.screenshot_size)
                if (getState() == MachineState.SAVED) {
                    machineInfo.screenshot = m.readSavedScreenshot(0)
                    machineInfo.screenshot!!.scaleBitmap(size, size)
                } else if (getState() == MachineState.RUNNING) {
                    machineInfo.screenshot = api.vbox!!.takeScreenshot(this, size, size)
                }

                if (machineInfo.screenshot != null) {
                    preview.setImageBitmap(machineInfo.screenshot!!.bitmap)
                    preview.adjustViewBounds = true
                    preview.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    previewPanel.expand(false)
                } else
                    previewPanel.collapse(false)
            }
        }
    }
}