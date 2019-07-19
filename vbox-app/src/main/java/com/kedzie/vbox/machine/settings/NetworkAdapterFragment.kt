package com.kedzie.vbox.machine.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.kedzie.vbox.R
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.api.INetworkAdapter
import com.kedzie.vbox.api.jaxb.HostNetworkInterfaceType
import com.kedzie.vbox.api.jaxb.NetworkAdapterPromiscModePolicy
import com.kedzie.vbox.api.jaxb.NetworkAdapterType
import com.kedzie.vbox.api.jaxb.NetworkAttachmentType
import com.kedzie.vbox.app.Utils
import kotlinx.android.synthetic.main.settings_network_adapter.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * @apiviz.stereotype fragment
 */
class NetworkAdapterFragment(arguments: Bundle) : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

    init {
        this.arguments = arguments
    }

    private lateinit var machine: IMachine
    private var adapterIndex : Int = 0

    private fun loadInfo() {
        launch {
            val adapterTypes = Utils.removeNull(NetworkAdapterType.values())
            val adapterTypeAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, adapterTypes)
            network_adapter_type.adapter = adapterTypeAdapter
            val attachmentTypes = Utils.removeNull(NetworkAttachmentType.values())
            val attachmentTypeAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, attachmentTypes)
            network_attached.adapter = attachmentTypeAdapter
            val promiscuousModeAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, NetworkAdapterPromiscModePolicy.values())
            network_promiscuous.adapter = promiscuousModeAdapter

            val adapter = machine.getNetworkAdapter(adapterIndex)

            var interfaces = mutableListOf<String>()
            if (adapter.getAttachmentType() == NetworkAttachmentType.BRIDGED) {
                val types = adapter.api.vbox!!.getHost().findHostNetworkInterfacesOfType(HostNetworkInterfaceType.BRIDGED)
                interfaces[0] = "Not Attached"
                for (type in types)
                    interfaces.add(type.getName())
            } else if (adapter.getAttachmentType() == NetworkAttachmentType.HOST_ONLY) {
                val types = adapter.api.vbox!!.getHost().findHostNetworkInterfacesOfType(HostNetworkInterfaceType.HOST_ONLY)
                interfaces.add( "Not Attached")
                for (type in types)
                    interfaces.add(type.getName())
            } else if (adapter.getAttachmentType() == NetworkAttachmentType.NAT) {
                interfaces.add( "Not Attached")
            }

            val nameAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, interfaces)
            network_name_spinner.adapter = nameAdapter

            val enabled = adapter.getEnabled()
            network_enabled.isChecked = enabled
            network_enabled.setOnCheckedChangeListener { buttonView, isChecked ->
                launch {
                    adapter.setEnabled(isChecked)
                    loadInfo()
                }
            }
            network_cable_connected.isEnabled = enabled
            network_mac.isEnabled = enabled
            network_adapter_type.isEnabled = enabled
            network_attached.isEnabled = enabled
            network_name_spinner.isEnabled = enabled
            network_name_text.isEnabled = enabled
            network_promiscuous.isEnabled = enabled

            network_attached.setSelection(Utils.indexOf(attachmentTypes, adapter.getAttachmentType()))
            network_attached.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    launch {
                        adapter.setAttachmentType(attachmentTypeAdapter.getItem(position))
                        loadInfo()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
            if (adapter.getAttachmentType() == NetworkAttachmentType.BRIDGED) {
                network_name_spinner.visibility = View.VISIBLE
                network_name_text.visibility = View.GONE
                if (Utils.isEmpty(adapter.getBridgedInterface()))
                    network_name_spinner.setSelection(0)
                else
                    network_name_spinner.setSelection(interfaces.indexOf(adapter.getBridgedInterface()))
            } else if (adapter.getAttachmentType() == NetworkAttachmentType.HOST_ONLY) {
                network_name_spinner.visibility = View.VISIBLE
                network_name_text.visibility = View.GONE
                if (Utils.isEmpty(adapter.getHostOnlyInterface()))
                    network_name_spinner.setSelection(0)
                else
                    network_name_spinner.setSelection(interfaces.indexOf(adapter.getHostOnlyInterface()))
            } else if (adapter.getAttachmentType() == NetworkAttachmentType.GENERIC) {
                network_name_spinner.visibility = View.GONE
                network_name_text.visibility = View.VISIBLE
                network_name_text.setText(adapter.getGenericDriver())
            } else if (adapter.getAttachmentType() == NetworkAttachmentType.INTERNAL) {
                network_name_spinner.visibility = View.GONE
                network_name_text.visibility = View.VISIBLE
                network_name_text.setText(adapter.getInternalNetwork())
            } else if (adapter.getAttachmentType() == NetworkAttachmentType.NAT) {
                network_name_spinner.visibility = View.VISIBLE
                network_name_text.visibility = View.GONE
                network_name_spinner.setSelection(0)
                network_name_spinner.isEnabled = false
            }
            network_name_spinner.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    launch {
                        if (adapter.getAttachmentType() == NetworkAttachmentType.BRIDGED)
                            adapter.setBridgedInterface(nameAdapter.getItem(position))
                        else if (adapter.getAttachmentType() == NetworkAttachmentType.HOST_ONLY)
                            adapter.setHostOnlyInterface(nameAdapter.getItem(position))
                        else if (adapter.getAttachmentType() == NetworkAttachmentType.GENERIC)
                            adapter.setGenericDriver(nameAdapter.getItem(position))
                        else if (adapter.getAttachmentType() == NetworkAttachmentType.INTERNAL)
                            adapter.setInternalNetwork(nameAdapter.getItem(position))
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
            network_adapter_type.setSelection(Utils.indexOf(adapterTypes, adapter.getAdapterType()))
            network_adapter_type.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    launch { adapter.setAdapterType(adapterTypeAdapter.getItem(position)) }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
            network_promiscuous.setSelection(Utils.indexOf(NetworkAdapterPromiscModePolicy.values(), adapter.getPromiscModePolicy()))
            network_promiscuous.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    launch { adapter.setPromiscModePolicy(promiscuousModeAdapter.getItem(position)) }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
            network_cable_connected.isChecked = adapter.getCableConnected()
            network_cable_connected.setOnCheckedChangeListener { buttonView, isChecked -> 
                launch { adapter.setCableConnected(isChecked) } }
            network_mac.setText(adapter.getMACAddress())
            network_mac.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    launch { adapter.setMACAddress(network_mac.text.toString()) }
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable) {}
            })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.settings_network_adapter, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        machine = arguments!!.getParcelable(IMachine.BUNDLE)
        adapterIndex = arguments!!.getInt(INetworkAdapter.BUNDLE)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadInfo()
    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }
}
