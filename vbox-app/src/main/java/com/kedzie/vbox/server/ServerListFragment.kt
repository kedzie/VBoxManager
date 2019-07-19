package com.kedzie.vbox.server

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
import androidx.navigation.ui.onNavDestinationSelected
import com.kedzie.vbox.R
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.machine.MachineListViewModel
import com.kedzie.vbox.soap.VBoxSvc
import kotlinx.android.synthetic.main.server_list.*
import kotlinx.android.synthetic.main.simple_selectable_list_item.view.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf

class ServerListFragment : Fragment() {

    private val model: MachineListViewModel by sharedViewModel { activity!!.intent.let {
        parametersOf(it.getParcelableExtra(VBoxSvc.BUNDLE), it.getParcelableExtra(IMachine.BUNDLE)) } }

    private var dualPane: Boolean = false

    private val adapter: ServerListAdapter
        get() = list!!.adapter as ServerListAdapter

    private var listener: OnServerSelectedListener? = null

    interface OnServerSelectedListener {
        fun onServerSelected(server: Server)
    }

    /**
     * Server list adapter
     */
    inner class ServerListAdapter(context: Context, servers: List<Server>) : ArrayAdapter<Server>(context, 0, servers) {

        private val _inflater = LayoutInflater.from(context)

        override fun hasStableIds(): Boolean {
            return true
        }

        override fun getItemId(position: Int): Long {
            return getItem(position)!!.id!!
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            if (convertView == null) {
                convertView = _inflater.inflate(R.layout.simple_selectable_list_item, parent, false)
            }
            convertView!!.text1.text = getItem(position)!!.toString()
            convertView!!.text1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_list_vbox, 0, 0, 0)
            return convertView
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is OnServerSelectedListener) {
            listener = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.server_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dualPane = activity!!.findViewById<View>(R.id.details) != null
        list!!.choiceMode = if (dualPane) ListView.CHOICE_MODE_SINGLE else ListView.CHOICE_MODE_NONE
        list!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            listener?.onServerSelected(adapter.getItem(position))
        }
        model.vmgr.observe(this, Observer {
            if (dualPane) {
                list!!.setSelection(adapter.getPosition(it.server))
            }
        })
        registerForContextMenu(list!!)
        addButton!!.setOnClickListener { addServer() }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.server_list_actions, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.helpFragment -> {
                item.onNavDestinationSelected(findNavController())
                return true
            }
        }
        return false
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        menu.add(Menu.NONE, R.id.server_list_context_menu_select, Menu.NONE, R.string.server_connect)
        menu.add(Menu.NONE, R.id.server_list_context_menu_edit, Menu.NONE, R.string.edit)
        menu.add(Menu.NONE, R.id.server_list_context_menu_delete, Menu.NONE, R.string.delete)
    }

    override fun onContextItemSelected(item: android.view.MenuItem): Boolean {
        val position = (item.menuInfo as AdapterView.AdapterContextMenuInfo).position
        val s = adapter.getItem(position)
        when (item.itemId) {
            R.id.server_list_context_menu_select -> {
                model.viewModelScope.launch {
                    login(activity!!, adapter.getItem(position)) {
                        model.vmgr.postValue(it)
                    }
                }
                return true
            }
            R.id.server_list_context_menu_edit -> {
                findNavController().navigate(ServerListFragmentDirections.editServer(s))
                return true
            }
            R.id.server_list_context_menu_delete -> {
                model.deleteServer(s)
                adapter.remove(s)
                adapter.notifyDataSetChanged()
                return true
            }
        }
        return false
    }

    /**
     * Launch activity to create a new Server
     */
    private fun addServer() {
        findNavController().navigate(ServerListFragmentDirections.editServer(Server()))
    }
}
