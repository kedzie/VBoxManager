package com.kedzie.vbox.host

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.common.base.Throwables
import com.kedzie.vbox.R
import com.kedzie.vbox.api.IDHCPServer
import com.kedzie.vbox.api.IHost
import com.kedzie.vbox.api.IHostNetworkInterface
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.api.jaxb.HostNetworkInterfaceType
import com.kedzie.vbox.machine.MachineListViewModel
import com.kedzie.vbox.soap.VBoxSvc
import kotlinx.android.synthetic.main.host_settings_network_list.*
import kotlinx.android.synthetic.main.simple_selectable_list_item.view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf
import org.ksoap2.SoapFault
import timber.log.Timber

class HostNetworkListFragment : Fragment() {

    private val model: MachineListViewModel by sharedViewModel { activity!!.intent.let {
        parametersOf(it.getParcelableExtra(VBoxSvc.BUNDLE), it.getParcelableExtra(IMachine.BUNDLE)) } }

    private lateinit var listView: ListView
    private lateinit var listAdapter: ArrayAdapter<IHostNetworkInterface>

    private lateinit var dhcpServers: ArrayList<IDHCPServer?>

    private fun addInterface() {
        model.vmgr.value?.vbox?.let { vbox ->
            model.viewModelScope.launch {
                val (int, progress) = IHost.createHostOnlyNetworkInterface(vbox.getHost())
                while (!progress.getCompleted()) {
                    delay(500)
                }
                loadData()

                showInterfaceDialog(int)
            }
        }
    }

    private fun deleteInterface(int: IHostNetworkInterface) {
        model.vmgr.value?.vbox?.let { vbox ->
            model.viewModelScope.launch {
                val progress = vbox.getHost().removeHostOnlyNetworkInterface(int.getId())
                while (!progress.getCompleted()) {
                    delay(500)
                }
                loadData()
            }
        }
    }

    private fun loadData() {
        model.vmgr.value?.vbox?.let { vbox ->
            model.viewModelScope.launch {
                val interfaces = mutableListOf<IHostNetworkInterface>()
                interfaces.addAll(vbox.getHost().findHostNetworkInterfacesOfType(HostNetworkInterfaceType.HOST_ONLY))
                dhcpServers = arrayListOf()
                for (net in interfaces) {
                    try {
                        val dhcp = vbox.findDHCPServerByNetworkName(net.getNetworkName())
                        if (dhcp != null) {
                            dhcp.getEnabled()
                            dhcpServers.add(dhcp)
                        }
                    } catch (e: Throwable) {
                        val cause = Throwables.getRootCause(e)
                        if (cause is SoapFault) {
                            Timber.e(e, "SoapFault finding DHCP Server %s", cause.detail.getText(0))
                        }
                        dhcpServers.add(null)
                    }

                }

                Timber.d("# of interfaces: %d", interfaces.size)
                listAdapter = ItemAdapter(activity!!, interfaces)
                listView.adapter = listAdapter
            }
        }
    }

    /**
     * List adapter for Fragments
     */
    private inner class ItemAdapter(context: Context, objects: List<IHostNetworkInterface>) : ArrayAdapter<IHostNetworkInterface>(context, 0, objects) {
        private val inflater = LayoutInflater.from(context)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            val info = getItem(position)
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.simple_selectable_list_item, parent, false)
            }
            model.viewModelScope.launch {
                convertView!!.text1.text = info.getName()
            }
            return convertView!!
        }
    }

    private fun showInterfaceDialog(hostInterface: IHostNetworkInterface) {
        findNavController().navigate(HostNetworkListFragmentDirections.showNetworkInterface(hostInterface))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.host_settings_network_list, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        host_interface_list.choiceMode = ListView.CHOICE_MODE_SINGLE
        host_interface_list.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            host_interface_list.setItemChecked(position, true)
            model.viewModelScope.launch {
                host_interface_adapter.text = "Manually Configured"
                dhcpServers[position]?.let {
                    if (it.getEnabled())
                        host_interface_dhcp_enabled.text = "Enabled"
                    else
                        host_interface_dhcp_enabled.text = "Disabled"
                }
            }

        }
        registerForContextMenu(host_interface_list)

        model.vmgr.observe(this, Observer {
            it?.vbox?.let { loadData() }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.host_interface_actions, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.option_menu_add -> {
                addInterface()
                return true
            }
        }
        return false
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        menu.add(Menu.NONE, R.id.host_interface_context_menu_edit, Menu.NONE, R.string.edit)
        menu.add(Menu.NONE, R.id.host_interface_context_menu_delete, Menu.NONE, R.string.delete)
    }

    override fun onContextItemSelected(item: android.view.MenuItem): Boolean {
        val position = (item.menuInfo as AdapterView.AdapterContextMenuInfo).position
        val hostInterface = listAdapter.getItem(position)
        when (item.itemId) {
            R.id.host_interface_context_menu_edit -> {
                showInterfaceDialog(hostInterface)
                return true
            }
            R.id.host_interface_context_menu_delete -> {
                deleteInterface(hostInterface)
                return true
            }
        }
        return false
    }
}