package com.kedzie.vbox.host

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.common.base.Throwables
import com.kedzie.vbox.R
import com.kedzie.vbox.api.IDHCPServer
import com.kedzie.vbox.api.IHost
import com.kedzie.vbox.api.IHostNetworkInterface
import com.kedzie.vbox.api.jaxb.HostNetworkInterfaceType
import com.kedzie.vbox.app.BundleBuilder
import com.kedzie.vbox.app.Utils
import com.kedzie.vbox.soap.VBoxSvc
import com.kedzie.vbox.task.BaseTask
import com.kedzie.vbox.task.DialogTask
import kotlinx.coroutines.*
import org.ksoap2.SoapFault
import timber.log.Timber
import java.util.ArrayList
import kotlin.coroutines.CoroutineContext

class HostNetworkListFragment : Fragment(), CoroutineScope {

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default

    private var _view: View? = null
    private var _listView: ListView? = null
    private var _hostInterfaceAdapterText: TextView? = null
    private var _hostInterfaceDHCPEnabledText: TextView? = null
    private var _listAdapter: ArrayAdapter<IHostNetworkInterface>? = null
    //	private boolean _dualPane;

    private lateinit var _vmgr: VBoxSvc
    private var _host: IHost? = null
    private var _interfaces: ArrayList<IHostNetworkInterface>? = null
    private var _dhcpServers: ArrayList<IDHCPServer?>? = null


    private fun addInterface() {
        launch {
            val (int, progress) = IHost.createHostOnlyNetworkInterface(_host!!)
            while (!progress.completed) {
                delay(500)
            }
            loadData()
            withContext(Dispatchers.Main) {
                showInterfaceDialog(int)
            }
        }
    }

    private fun deleteInterface(int: IHostNetworkInterface) {
        launch {
            val progress = _host!!.removeHostOnlyNetworkInterface(int.id)
            while (!progress.completed) {
                delay(500)
            }

            loadData()
        }
    }

    private fun loadData() {
        launch {
            _host = _vmgr!!.vBox.host
            val data = _host!!.findHostNetworkInterfacesOfType(HostNetworkInterfaceType.HOST_ONLY)
            _dhcpServers = ArrayList(data.size)
            for (net in data) {
                net.id
                net.name
                net.networkName
                net.dhcpEnabled
                try {
                    val dhcp = _vmgr.vBox.findDHCPServerByNetworkName(net.networkName)
                    if (dhcp != null) {
                        dhcp.enabled
                        _dhcpServers!!.add(dhcp)
                    }
                } catch (e: Throwable) {
                    val cause = Throwables.getRootCause(e)
                    if (cause is SoapFault) {
                        Timber.e(e, "SoapFault finding DHCP Server %s", cause.detail.getText(0))
                    }
                    _dhcpServers!!.add(null)
                }

            }

            withContext(Dispatchers.Main) {
                _interfaces = data
                Timber.d("# of interfaces: %d", _interfaces!!.size)
                _listAdapter = ItemAdapter(activity!!, _interfaces!!)
                _listView!!.adapter = _listAdapter
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
                convertView!!.tag = convertView.findViewById<View>(android.R.id.text1) as TextView
            }
            val text1 = convertView.tag as TextView
            text1.text = info!!.name
            return convertView
        }
    }

    internal fun showInterfaceDialog(hostInterface: IHostNetworkInterface?) {
        Utils.showDialog(fragmentManager!!, "dialog", HostNetworkDialog.getInstance(BundleBuilder().putParcelable(IHostNetworkInterface.BUNDLE, hostInterface).create()))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("interfaces", _interfaces)
        outState.putParcelable(IHost.BUNDLE, _host)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        _vmgr = BundleBuilder.getVBoxSvc(arguments!!)
        if (savedInstanceState != null) {
            _host = savedInstanceState.getParcelable(IHost.BUNDLE)
            _interfaces = savedInstanceState.getParcelableArrayList("interfaces")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //		_dualPane = getActivity().findViewById(R.id.details)!=null;
        _view = inflater.inflate(R.layout.host_settings_network_list, null)
        _listView = _view!!.findViewById<View>(R.id.host_interface_list) as ListView
        _listView!!.choiceMode = ListView.CHOICE_MODE_SINGLE
        _listView!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            _listView!!.setItemChecked(position, true)
            _hostInterfaceAdapterText!!.text = "Manually Configured"
            val dhcp = _dhcpServers!![position]
            if (dhcp != null && dhcp.enabled)
                _hostInterfaceDHCPEnabledText!!.text = "Enabled"
            else
                _hostInterfaceDHCPEnabledText!!.text = "Disabled"
        }
        registerForContextMenu(_listView!!)
        _hostInterfaceAdapterText = _view!!.findViewById<View>(R.id.host_interface_adapter) as TextView
        _hostInterfaceDHCPEnabledText = _view!!.findViewById<View>(R.id.host_interface_dhcp_enabled) as TextView
        return _view
    }

    override fun onStart() {
        super.onStart()
        job = Job()
        loadData()
    }

    override fun onStop() {
        job.cancel()
        super.onStop()
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
        val hostInterface = _listAdapter!!.getItem(position)
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