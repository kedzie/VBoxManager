package com.kedzie.vbox.host

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kedzie.vbox.R
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.api.IVirtualBox
import com.kedzie.vbox.api.jaxb.HostNetworkInterfaceType
import com.kedzie.vbox.api.jaxb.ProcessorFeature
import com.kedzie.vbox.app.Utils
import com.kedzie.vbox.machine.MachineListViewModel
import com.kedzie.vbox.soap.VBoxSvc
import kotlinx.android.synthetic.main.host_info.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf

class HostInfoFragment : Fragment() {

    private val model: MachineListViewModel by sharedViewModel { activity!!.intent.let {
        parametersOf(it.getParcelableExtra(VBoxSvc.BUNDLE), it.getParcelableExtra(IMachine.BUNDLE)) } }

    private fun loadInfo(virtualBox: IVirtualBox, clearCache: Boolean) =
        model.viewModelScope.launch {
            val host = virtualBox.getHost()
            if (clearCache)
                host.clearCache()

            vbox.text = virtualBox.getVersion()
            ostype.text = host.getOperatingSystem() + "(" + host.getOsVersion() + ")"
            memory.text = host.getMemorySize().toString() + " MB"

            var sb = StringBuffer()
            for (i in 0 until (host.getProcessorCount())) {
                if (i > 0)
                    sb.append("\n")
                sb.append(host.getProcessorDescription(i))
            }
            val sb2 = StringBuffer()
            if (host.getProcessorFeature(ProcessorFeature.HW_VIRT_EX))
                Utils.appendWithComma(sb2, "HW VirtEx")
            if (host.getProcessorFeature(ProcessorFeature.PAE)!!)
                Utils.appendWithComma(sb2, "PAE")
            if (host.getProcessorFeature(ProcessorFeature.LONG_MODE))
                Utils.appendWithComma(sb2, "Long Mode")
            processors.setLines(host.getProcessorCount() + 1)
            processors.text = sb.toString() + "\n" + sb2.toString()

            sb = StringBuffer()
            val nets = host.findHostNetworkInterfacesOfType(HostNetworkInterfaceType.BRIDGED)
            for (i in nets.indices) {
                val net = nets[i]
                if (i > 0)
                    sb.append("\n\n")
                sb.append(net.getNetworkName())
                sb.append("\n\t").append(net.getIPAddress()).append(" / ").append(net.getNetworkMask())
                sb.append("\n\t").append(net.getIPV6Address()).append(" / ").append(net.getIPV6NetworkMaskPrefixLength())
            }
            networks!!.setLines(nets.size * 4 - 1)
            networks!!.text = sb.toString()

            sb = StringBuffer()
            val dvdArray = host.getDvdDrives()
            for (i in dvdArray.indices) {
                val dvd = dvdArray[i]
                if (i > 0)
                    sb.append("\n")
                sb.append(dvd.getName()).append(" ").append(dvd.getDescription())
            }
            dvds!!.setLines(dvdArray.size)
            dvds!!.text = sb.toString()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val swipeLayout = SwipeRefreshLayout(activity!!)
        swipeLayout.setOnRefreshListener {
            model.vmgr.value?.vbox?.let {
                loadInfo(it,true)
            }
        }
        inflater.inflate(R.layout.host_info, swipeLayout)
        return swipeLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.vmgr.observe(this, Observer { vmgr ->
            vmgr?.vbox?.let { loadInfo(it, false) }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.host_actions, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.host_option_menu_metrics -> {
                model.vmgr.value?.vbox?.let { virtualBox ->
                    model.viewModelScope.launch {
                        virtualBox.getHost().let {
                            findNavController().navigate(HostInfoFragmentDirections.hostMetrics(it.idRef, it.getMemorySize()))
                        }
                    }
                }
                return true
            }
        }
        return false
    }
}