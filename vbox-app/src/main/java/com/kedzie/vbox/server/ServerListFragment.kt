package com.kedzie.vbox.server

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.preference.PreferenceManager
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kedzie.vbox.R
import com.kedzie.vbox.app.Utils
import com.kedzie.vbox.task.BaseTask
import kotlinx.android.synthetic.main.server_list.*
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class ServerListFragment : Fragment(), CoroutineScope {

    private var _listener: OnSelectServerListener? = null
    private var _db: ServerSQlite? = null

    private var _dualPane: Boolean = false

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default

    private val adapter: ServerListAdapter
        get() = list!!.adapter as ServerListAdapter

    /**
     * Handle Server selection
     */
    interface OnSelectServerListener {

        /**
         * @param server    the selected [Server]
         */
        fun onSelectServer(server: Server?)
    }

    private fun loadServers() {
        launch {
            val result = _db!!.query()

            withContext(Dispatchers.Main) {
                list!!.adapter = ServerListAdapter(activity!!, result)
                if (result.isEmpty())
                    showAddNewServerPrompt()
                else
                    checkIfFirstRun(result[0])
            }
        }
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
                convertView!!.tag = convertView.findViewById<View>(android.R.id.text1) as TextView
            }
            val text1 = convertView.tag as TextView
            text1.text = getItem(position)!!.toString()
            text1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_list_vbox, 0, 0, 0)
            return convertView
        }
    }

    override fun onAttach(activity: Context) {
        super.onAttach(activity)
        if (activity is OnSelectServerListener)
            _listener = activity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.server_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _dualPane = activity!!.findViewById<View>(R.id.details) != null
        list!!.choiceMode = if (_dualPane) ListView.CHOICE_MODE_SINGLE else ListView.CHOICE_MODE_NONE
        list!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            if (_dualPane)
                list!!.setSelection(position)
            _listener!!.onSelectServer(adapter.getItem(position))
        }
        registerForContextMenu(list!!)
        addButton!!.setOnClickListener { addServer() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        _db = ServerSQlite(activity)
    }

    override fun onStart() {
        super.onStart()
        job = Job()
        loadServers()
    }

    override fun onStop() {
        job.cancel()
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        _db!!.close()
    }

    private fun checkIfFirstRun(s: Server) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        if (!prefs.contains(FIRST_RUN_PREFERENCE)) {
            val editor = prefs.edit()
            editor.putBoolean(FIRST_RUN_PREFERENCE, false)
            editor.commit()
            AlertDialog.Builder(activity)
                    .setTitle(R.string.firstrun_welcome)
                    .setMessage(getString(R.string.firstrun_message, s.host, s.port))
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("OK") { dialog, which -> dialog.dismiss() }
                    .show()
        }
    }

    private fun showAddNewServerPrompt() {
        AlertDialog.Builder(activity)
                .setTitle(R.string.add_server_title)
                .setMessage(R.string.add_server_question)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("OK") { dialog, which ->
                    dialog.dismiss()
                    addServer()
                }
                .show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (!_dualPane)
            inflater.inflate(R.menu.server_list_actions, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_help -> {
                Utils.startActivity(activity, Intent(activity, HelpActivity::class.java))
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
                _listener!!.onSelectServer(adapter.getItem(position))
                return true
            }
            R.id.server_list_context_menu_edit -> {
                Utils.startActivity(activity, Intent(activity, EditServerActivity::class.java).putExtra(EditServerActivity.INTENT_SERVER, s as Parcelable))
                return true
            }
            R.id.server_list_context_menu_delete -> {
                _db!!.delete(s!!.id)
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
        Utils.startActivity(activity, Intent(activity, EditServerActivity::class.java).putExtra(EditServerActivity.INTENT_SERVER, Server() as Parcelable))
    }

    companion object {
        private const val FIRST_RUN_PREFERENCE = "first_run"
    }
}
