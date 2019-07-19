package com.kedzie.vbox.machine

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.INVALID_POSITION
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.kedzie.vbox.MachineListNavDirections
import com.kedzie.vbox.R
import com.kedzie.vbox.SettingsGeneralFragment
import com.kedzie.vbox.SettingsMetricFragment
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.api.IMachineStateChangedEvent
import com.kedzie.vbox.app.Utils
import com.kedzie.vbox.machine.group.MachineGroupListFragment
import com.kedzie.vbox.machine.group.TreeNode
import com.kedzie.vbox.machine.group.VMGroup
import com.kedzie.vbox.server.Server
import com.kedzie.vbox.server.ServerListFragment
import com.kedzie.vbox.server.login
import com.kedzie.vbox.soap.VBoxSvc
import kotlinx.android.synthetic.main.drawer_header.view.*
import kotlinx.android.synthetic.main.machine_list.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class MachineListActivity : AppCompatActivity(),
        MachineGroupListFragment.OnTreeNodeSelectListener,
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback,
        ServerListFragment.OnServerSelectedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    /** Is the dual Fragment Layout active?  */
    private var dualPane: Boolean = false

    private val sharedPreferences: SharedPreferences by inject { parametersOf(this) }

    private val notificationManager: NotificationManager by inject { parametersOf(this) }

    private val notificationBuilder: NotificationCompat.Builder
        get() = if (Utils.isVersion(Build.VERSION_CODES.O))
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
        else
            NotificationCompat.Builder(this)

    private val model: MachineListViewModel by viewModel { intent.let {
        parametersOf(it.getParcelableExtra(VBoxSvc.BUNDLE), it.getParcelableExtra(IMachine.BUNDLE)) } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Utils.isVersion(Build.VERSION_CODES.O)) {
            if (notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL) == null) {
                notificationManager.createNotificationChannel(NotificationChannel(NOTIFICATION_CHANNEL, "VboxManager", NotificationManager.IMPORTANCE_DEFAULT))
            }
        }
        setContentView(R.layout.machine_list)
        dualPane = findViewById<View>(R.id.details) != null
        setSupportActionBar(toolbar)
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_TITLE or ActionBar.DISPLAY_SHOW_HOME or ActionBar.DISPLAY_HOME_AS_UP

        initNavHeader()

        nav_host_fragment.findNavController()?.let { navController ->
            val appBarConfiguration = AppBarConfiguration(setOf(R.id.machine_actions,
                    R.id.machine_info,
                    R.id.machine_log,
                    R.id.machine_snapshot), drawer_layout)
            toolbar.setupWithNavController(navController, appBarConfiguration)
            nav.setupWithNavController(navController)
            bottom_nav.setupWithNavController(navController)

            navController.addOnDestinationChangedListener { _, destination, _ ->
                when(destination.id) {
                    R.id.machine_actions,
                    R.id.machine_info,
                    R.id.machine_log,
                    R.id.machine_snapshot -> {
                        toolbar.visibility = View.VISIBLE
                        bottom_nav.visibility = View.VISIBLE
                    }
                    else -> {
                        toolbar.visibility = View.VISIBLE
                        bottom_nav.visibility = View.GONE
                    }
                }
            }
        }

        model.events.observe(this, Observer {
            when(it) {
                is IMachineStateChangedEvent -> {
                    if(sharedPreferences.getBoolean(SettingsGeneralFragment.PREF_NOTIFICATIONS, false)) {
                        model.vmgr.value?.let { vmgr ->
                            model.viewModelScope.launch {
                                val machine = vmgr.vbox.findMachine(it.getMachineId())
                                Timber.d("Got machine state changed event")

                                val title = getString(R.string.notification_title, machine.getName(), machine.getState())
                                notificationManager.notify(0, notificationBuilder
                                        .setContentTitle(title)
                                        .setContentText(getString(R.string.notification_text, machine.getName(), machine.getState()))
                                        .setWhen(System.currentTimeMillis())
                                        .setSmallIcon(R.drawable.ic_list_vbox)
                                        .setLargeIcon(BitmapFactory.decodeResource(resources, machine.getState().drawable()))
                                        .setContentIntent(PendingIntent.getActivity(this@MachineListActivity, 0,
                                                Intent(this@MachineListActivity, MachineListActivity::class.java)
                                                        .putExtra(VBoxSvc.BUNDLE, vmgr), 0))
                                        .setTicker(title)
                                        .setAutoCancel(true)
                                        .build())
                            }
                        }
                    }
                }
            }
        })

        model.machine.observe(this, Observer {
            it?.let {
                nav_host_fragment.findNavController().navigate(MachineListNavDirections.showMachine())
            }
        })

        model.group.observe(this, Observer {
            it?.let {
                nav_host_fragment.findNavController().navigate(MachineListNavDirections.showGroup())
            }
        })

        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
    }

    private fun initNavHeader() {
        val header = LayoutInflater.from(this).inflate(R.layout.drawer_header, nav, false)

//        TODO migrate servers

        model.servers.observe(this, Observer {
            val serverAdapter = object : ArrayAdapter<Server>(this, R.layout.simple_selectable_list_item, android.R.id.text1, it) {
                override fun hasStableIds(): Boolean {
                    return true
                }

                override fun getItemId(position: Int): Long {
                    return getItem(position)!!.id!!
                }
            }

            header.server_spinner.adapter = serverAdapter

            if(it.isEmpty()) {
                nav_host_fragment.findNavController().navigate(MachineListNavDirections.showWelcome())
            }
        })

        model.vmgr.observe(this, Observer {
            if(it != null) {
                Timber.d("Server changed")
                model.viewModelScope.launch {
                    header.hostView.update(it.vbox.getHost())
                }
                header.server_spinner.setSelection(model.servers.value?.indexOf(it.server) ?: INVALID_POSITION)
                nav_host_fragment.findNavController().navigate(MachineListNavDirections.showHostInfo())
            } else {
                header.server_spinner.setSelection(INVALID_POSITION)
            }
        })

        header.server_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                val serverAdapter = adapterView.adapter as ArrayAdapter<Server>
                val server = serverAdapter.getItem(position)
                Timber.i("Server selected from navigation drawer %s", server)

                model.viewModelScope.launch {
                    model.vmgr.value?.logoff()
                    login(this@MachineListActivity, server) {
                        model.vmgr.postValue(it)
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }
        nav.addHeaderView(header)
    }

    private fun checkIfFirstRun() =
        if (!sharedPreferences.contains(FIRST_RUN_PREFERENCE)) {
            val editor = sharedPreferences.edit()
            editor.putBoolean(FIRST_RUN_PREFERENCE, false)
            editor.commit()
            true
        } else false


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences , key: String?) {
        val count = sharedPreferences.getString(SettingsMetricFragment.PREF_METRIC_COUNT, "30").toInt()
        val period = sharedPreferences.getString(SettingsMetricFragment.PREF_METRIC_PERIOD, "2").toInt()
        model.enableMetrics(period, count)
    }

    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat, pref: Preference): Boolean {
        nav_host_fragment.findNavController().navigate(when(pref.fragment) {
            SettingsGeneralFragment::class.simpleName -> { R.id.settings_general}
            else -> { R.id.settings_metrics } })
        return true
    }

    override fun onServerSelected(server: Server) {
        model.viewModelScope.launch {
            login(this@MachineListActivity, server) {
                model.vmgr.postValue(it)
            }
        }
    }

    override fun onTreeNodeSelect(node: TreeNode?) {
        model.selectedNode.postValue(node)
    }

    companion object {
        private const val FIRST_RUN_PREFERENCE = "first_run"
        private const val NOTIFICATION_CHANNEL = "vbox"
    }
}
