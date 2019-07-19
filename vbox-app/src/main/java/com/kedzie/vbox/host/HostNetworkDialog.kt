package com.kedzie.vbox.host

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import com.kedzie.vbox.R
import com.kedzie.vbox.api.IDHCPServer
import com.kedzie.vbox.machine.MachineListViewModel
import kotlinx.android.synthetic.main.host_settings_network.view.*
import kotlinx.android.synthetic.main.host_settings_network_dhcp.*
import kotlinx.android.synthetic.main.host_settings_network_dhcp.view.*
import kotlinx.android.synthetic.main.host_settings_network_static.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf

class HostNetworkDialog : DialogFragment(),
    CoroutineScope by CoroutineScope(Dispatchers.Main){

    private val model: MachineListViewModel by sharedViewModel { parametersOf(activity!!) }

    private val args by navArgs<HostNetworkDialogArgs>()

    private var dhcp: IDHCPServer? = null

    internal fun loadData() {
        model.viewModelScope.launch {
            hostnet_ipv4_ip.setText(args.hostNetwork.getIPAddress())
            hostnet_ipv4_mask.setText(args.hostNetwork.getNetworkMask())
            hostnet_ipv6_ip.setText(args.hostNetwork.getIPV6Address())
            hostnet_ipv6_mask.setText(args.hostNetwork.getIPV6NetworkMaskPrefixLength().toString())
            args.hostNetwork.api.vbox!!.findDHCPServerByNetworkName(args.hostNetwork.getNetworkName())?.let {
                dhcp = it
                dhcp_enabled.isChecked = it.getEnabled()
                dhcp_mask.setText(it.getNetworkMask())
                dhcp_lowerbound.setText(it.getLowerIP())
                dhcp_upperbound.setText(it.getUpperIP())
            }
        }
    }

    private fun save() {
        model.viewModelScope.launch {
            args.hostNetwork.enableStaticIPConfig(hostnet_ipv4_ip.text.toString(), hostnet_ipv4_mask.text.toString())
            args.hostNetwork.enableStaticIPConfigV6(hostnet_ipv6_ip.text.toString(), hostnet_ipv6_mask.text.toString().toInt())
            if(dhcp_enabled.isChecked) {
                if(dhcp == null) {
                    dhcp = args.hostNetwork.api.vbox!!.createDHCPServer(args.hostNetwork.getNetworkName())
                }
                dhcp!!.setEnabled(true)
                dhcp!!.setConfiguration(dhcp_address.text.toString(), dhcp_mask.text.toString(),
                        dhcp_lowerbound.text.toString(), dhcp_lowerbound.text.toString())
            } else {
                dhcp?.setEnabled(false)
            }
        }
        loadData()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setTitle("Host Network Interface")
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.host_settings_network, container, false)
        view.dhcp_enabled.setOnCheckedChangeListener { buttonView, isChecked ->
            view.dhcp_address.isEnabled = isChecked
            view.dhcp_mask.isEnabled = isChecked
            view.dhcp_lowerbound.isEnabled = isChecked
            view.dhcp_upperbound.isEnabled = isChecked
        }
        view.dhcp_address.isEnabled = false
        view.dhcp_mask.isEnabled = false
        view.dhcp_lowerbound.isEnabled = false
        view.dhcp_upperbound.isEnabled = false
        view.ok_button.setOnClickListener {
            save()
            dialog!!.dismiss()
        }
        view.cancel_button.setOnClickListener { dialog!!.dismiss() }
        view.tabhost.setup()
        view.tabhost.addTab(view.tabhost.newTabSpec("static").setIndicator("Static").setContent(R.id.staticTab))
        view.tabhost.addTab(view.tabhost.newTabSpec("dhcp").setIndicator("DHCP").setContent(R.id.dhcpTab))
        return view
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }
}
