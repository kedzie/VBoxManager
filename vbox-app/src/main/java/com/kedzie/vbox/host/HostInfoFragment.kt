package com.kedzie.vbox.host

import android.content.Intent
import android.os.Bundle
import android.os.NetworkOnMainThreadException
import android.os.Parcelable
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kedzie.vbox.R
import com.kedzie.vbox.api.IHost
import com.kedzie.vbox.api.jaxb.HostNetworkInterfaceType
import com.kedzie.vbox.api.jaxb.ProcessorFeature
import com.kedzie.vbox.app.BundleBuilder
import com.kedzie.vbox.app.Utils
import com.kedzie.vbox.metrics.MetricActivity
import com.kedzie.vbox.soap.VBoxSvc
import com.kedzie.vbox.task.BaseTask
import kotlinx.android.synthetic.main.host_info.*
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class HostInfoFragment : Fragment(), CoroutineScope {

    private lateinit var _host: IHost
    private lateinit var _vmgr: VBoxSvc

    private var mDualPane: Boolean = false

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default

    private fun loadInfo(clearCache: Boolean) {
        launch {
            if (clearCache)
                _host.clearCache()
            //cache values
            _vmgr.vBox.version
            _host.memorySize
            _host.memoryAvailable
            _host.operatingSystem
            _host.osVersion
            for (drive in _host.dvdDrives)
                Utils.cacheProperties(drive)
            for (net in _host.findHostNetworkInterfacesOfType(HostNetworkInterfaceType.BRIDGED)) {
                net.ipAddress
                net.ipV6Address
                net.name
                net.networkName
                net.networkMask
                net.ipV6NetworkMaskPrefixLength
            }

            for (i in 0 until (_host.processorCount ?: 0)) {
                _host.getProcessorDescription(i)
                _host.getProcessorSpeed(i)
            }
            _host.getProcessorFeature(ProcessorFeature.HW_VIRT_EX)
            _host.getProcessorFeature(ProcessorFeature.LONG_MODE)
            _host.getProcessorFeature(ProcessorFeature.PAE)

            withContext(Dispatchers.Main) {
                populateViews(_host)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        _vmgr = BundleBuilder.getVBoxSvc(arguments!!)
        _host = BundleBuilder.getProxy(arguments, IHost.BUNDLE, IHost::class.java)
        mDualPane = arguments!!.getBoolean("dualPane")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val swipeLayout = SwipeRefreshLayout(activity!!)
        swipeLayout.setOnRefreshListener { loadInfo(true) }
        inflater.inflate(R.layout.host_info, swipeLayout)
        return swipeLayout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (savedInstanceState != null) {
            _vmgr = BundleBuilder.getVBoxSvc(savedInstanceState)
            _host = savedInstanceState.getParcelable(IHost.BUNDLE)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        BundleBuilder.putProxy(outState, IHost.BUNDLE, _host)
        BundleBuilder.putVBoxSvc(outState, _vmgr!!)
    }

    private fun populateViews(host: IHost) {
        try {
            ostype!!.text = host.operatingSystem + "(" + host.osVersion + ")"
            vbox!!.text = _vmgr!!.vBox.version
            memory!!.text = host.memorySize!!.toString() + " MB"

            var sb = StringBuffer()
            for (i in 0 until (host.processorCount ?: 0)) {
                if (i > 0)
                    sb.append("\n")
                sb.append(host.getProcessorDescription(i))
            }
            val sb2 = StringBuffer()
            if (host.getProcessorFeature(ProcessorFeature.HW_VIRT_EX)!!)
                Utils.appendWithComma(sb2, "HW VirtEx")
            if (host.getProcessorFeature(ProcessorFeature.PAE)!!)
                Utils.appendWithComma(sb2, "PAE")
            if (host.getProcessorFeature(ProcessorFeature.LONG_MODE)!!)
                Utils.appendWithComma(sb2, "Long Mode")
            processors!!.setLines(host.processorCount!! + 1)
            processors!!.text = sb.toString() + "\n" + sb2.toString()

            sb = StringBuffer()
            val nets = host.findHostNetworkInterfacesOfType(HostNetworkInterfaceType.BRIDGED)
            for (i in nets.indices) {
                val net = nets[i]
                if (i > 0)
                    sb.append("\n\n")
                sb.append(net.networkName)
                sb.append("\n\t").append(net.ipAddress).append(" / ").append(net.networkMask)
                sb.append("\n\t").append(net.ipV6Address).append(" / ").append(net.ipV6NetworkMaskPrefixLength)
            }
            networks!!.setLines(nets.size * 4 - 1)
            networks!!.text = sb.toString()

            sb = StringBuffer()
            val dvdArray = host.dvdDrives
            for (i in dvdArray.indices) {
                val dvd = dvdArray[i]
                if (i > 0)
                    sb.append("\n")
                sb.append(dvd.name).append(" ").append(dvd.description)
            }
            dvds!!.setLines(dvdArray.size)
            dvds!!.text = sb.toString()
        } catch (e: NetworkOnMainThreadException) {
            loadInfo(false)
        }
    }

    override fun onStart() {
        super.onStart()
        Timber.d("onStart")
        job = Job()
        loadInfo(false)
    }

    override fun onStop() {
        Timber.d("onStop")
        job.cancel()
        super.onStop()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.host_actions, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.host_option_menu_metrics -> {
                startActivity(Intent(activity, MetricActivity::class.java).putExtra(VBoxSvc.BUNDLE, _vmgr as Parcelable?)
                        .putExtra(MetricActivity.INTENT_TITLE, resources.getString(R.string.host_metrics))
                        .putExtra(MetricActivity.INTENT_ICON, R.drawable.ic_launcher)
                        .putExtra(MetricActivity.INTENT_OBJECT, _host!!.idRef)
                        .putExtra(MetricActivity.INTENT_RAM_AVAILABLE, _host!!.memorySize)
                        .putExtra(MetricActivity.INTENT_CPU_METRICS, arrayOf("CPU/Load/User", "CPU/Load/Kernel"))
                        .putExtra(MetricActivity.INTENT_RAM_METRICS, arrayOf("RAM/Usage/Used")))
                return true
            }
        }
        return false
    }
}