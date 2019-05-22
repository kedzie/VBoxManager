package com.kedzie.vbox.machine

import android.app.ActionBar
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.navigation.NavigationView
import com.kedzie.vbox.R
import com.kedzie.vbox.SettingsActivity
import com.kedzie.vbox.api.IHost
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.api.jaxb.VBoxEventType
import com.kedzie.vbox.app.*
import com.kedzie.vbox.event.EventIntentService
import com.kedzie.vbox.event.EventNotificationReceiver
import com.kedzie.vbox.host.HostInfoFragment
import com.kedzie.vbox.host.HostNetworkListFragment
import com.kedzie.vbox.machine.group.GroupInfoFragment
import com.kedzie.vbox.machine.group.TreeNode
import com.kedzie.vbox.machine.group.VMGroup
import com.kedzie.vbox.machine.group.VMGroupListView
import com.kedzie.vbox.server.LoginSupport
import com.kedzie.vbox.server.Server
import com.kedzie.vbox.server.ServerSQlite
import com.kedzie.vbox.soap.VBoxSvc
import com.kedzie.vbox.task.ConfigureMetricsTask
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.machine_list.*
import timber.log.Timber
import javax.inject.Inject

class MachineListActivity : BaseActivity(), VMGroupListView.OnTreeNodeSelectListener, HasSupportFragmentInjector {

    /** Is the dual Fragment Layout active?  */
    private var _dualPane: Boolean = false
    /** VirtualBox API  */
    private var _vmgr: VBoxSvc? = null

    private var mDrawerToggle: ActionBarDrawerToggle? = null

    @Inject
    internal lateinit var dispatchingFragmentInjector: DispatchingAndroidInjector<Fragment>

    @set:Inject
    internal lateinit var localBroadcastManager: LocalBroadcastManager

    private val notificationReceiver = EventNotificationReceiver()

    override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
        return dispatchingFragmentInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        _vmgr = BundleBuilder.getVBoxSvc(intent)

        if (Utils.isVersion(Build.VERSION_CODES.O)) {
            startForegroundService(Intent(this, EventIntentService::class.java).putExtras(intent))
        } else {
            startService(Intent(this, EventIntentService::class.java).putExtras(intent))
        }
        localBroadcastManager!!.registerReceiver(notificationReceiver, IntentFilter(VBoxEventType.ON_MACHINE_STATE_CHANGED.name))
        initUI()
    }

    override fun onDestroy() {
        stopService(Intent(this, EventIntentService::class.java))
        localBroadcastManager!!.unregisterReceiver(notificationReceiver)
        super.onDestroy()
    }

    private fun initUI() {
        setContentView(R.layout.machine_list)
        _dualPane = findViewById<View>(R.id.details) != null

        setSupportActionBar(toolbar)
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_TITLE or ActionBar.DISPLAY_SHOW_HOME or ActionBar.DISPLAY_HOME_AS_UP

        nav!!.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                }
                R.id.navigation_host_network -> show(FragmentElement("Host", HostNetworkListFragment::class.java,
                        BundleBuilder().putVBoxSvc(_vmgr).create()))
                R.id.navigation_prefs -> Utils.startActivityForResult(this@MachineListActivity, Intent(this@MachineListActivity, SettingsActivity::class.java), REQUEST_CODE_PREFERENCES)
                R.id.navigation_logoff -> logoff()
            }
            false
        }
        initNavHeader()

        mDrawerToggle = object : ActionBarDrawerToggle(
                this, /* host Activity */
                drawer_layout, /* DrawerLayout object */
                toolbar, /* action baraqa */
                R.string.drawer_open, /* "open drawer" description for accessibility */
                R.string.drawer_closed        /* "close drawer" description for accessibility */
        ) {
            override fun onDrawerClosed(drawerView: View) {
                supportInvalidateOptionsMenu() // creates call to onPrepareOptionsMenu()
            }

            override fun onDrawerOpened(drawerView: View) {
                supportInvalidateOptionsMenu() // creates call to onPrepareOptionsMenu()
            }
        }
        mDrawerToggle!!.isDrawerIndicatorEnabled = true
        drawer_layout!!.setDrawerListener(mDrawerToggle)

        // set a custom shadow that overlays the main content when the drawer opens
        drawer_layout!!.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START)
    }

    private fun initNavHeader() {
        val header = LayoutInflater.from(this).inflate(R.layout.drawer_header, nav, false)
        val serverSpinner = header.findViewById<View>(R.id.server_spinner) as Spinner
        val db = ServerSQlite(this)
        val servers = db.query()
        val serverAdapter = object : ArrayAdapter<Server>(this, R.layout.simple_selectable_list_item, android.R.id.text1, servers) {
            override fun hasStableIds(): Boolean {
                return true
            }

            override fun getItemId(position: Int): Long {
                return getItem(position)!!.id!!
            }
        }
        db.close()
        serverSpinner.adapter = serverAdapter
        serverSpinner.setSelection(servers.indexOf(_vmgr!!.server))
        serverSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                val serverAdapter = adapterView.adapter as ArrayAdapter<Server>
                val server = serverAdapter.getItem(position)
                Timber.i("Server selected from navigation drawer %s", server)
                if (server != _vmgr!!.server) {
                    setResult(5, Intent().putExtra("server", server as Parcelable))
                    logoff()
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }
        nav!!.addHeaderView(header)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle!!.syncState()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PREFERENCES) {
            ConfigureMetricsTask(this, _vmgr).execute(
                    Utils.getIntPreference(this, SettingsActivity.PREF_PERIOD),
                    Utils.getIntPreference(this, SettingsActivity.PREF_COUNT))
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mDrawerToggle!!.onConfigurationChanged(newConfig)
        if (Utils.getScreenSize(newConfig) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            Timber.i("Handling orientation change")
            //            FragmentManager mgr = getSupportFragmentManager();
            //            FragmentTransaction tx = mgr.beginTransaction();
            //            for(Fragment fragment : mgr.getFragments()) {
            //                tx.detach(fragment);
            //            }
            //            tx.commit();
            initUI()
        }
    }

    override fun onTreeNodeSelect(node: TreeNode) {
        if (node is IMachine)
            onMachineSelected(node)
        else if (node is VMGroup)
            onGroupSelected(node)
        else if (node is IHost)
            onHostSelected(node)
    }

    private fun onMachineSelected(machine: IMachine) {
        show(FragmentElement(machine.name, app.getOSDrawable(machine.osTypeId), MachineFragment::class.java,
                BundleBuilder().putVBoxSvc(_vmgr)
                        .putProxy(IMachine.BUNDLE, machine)
                        .putBoolean("dualPane", _dualPane).create()))
    }

    private fun onGroupSelected(group: VMGroup) {
        show(FragmentElement(group.name, GroupInfoFragment::class.java,
                BundleBuilder().putVBoxSvc(_vmgr)
                        .putParcelable(VMGroup.BUNDLE, group)
                        .putBoolean("dualPane", _dualPane).create()))
    }

    private fun onHostSelected(host: IHost) {
        show(FragmentElement("Host", HostInfoFragment::class.java,
                BundleBuilder().putVBoxSvc(_vmgr)
                        .putParcelable(IHost.BUNDLE, host)
                        .putBoolean("dualPane", _dualPane).create()))
    }

    private fun show(details: FragmentElement) {
        if (_dualPane) {
            Utils.setCustomAnimations(supportFragmentManager.beginTransaction()).replace(R.id.details, details.instantiate(this)).commit()
        } else {
            Utils.startActivity(this, Intent(this, FragmentActivity::class.java).putExtra(FragmentElement.BUNDLE, details))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (mDrawerToggle!!.onOptionsItemSelected(item)) true else false
    }

    override fun onBackPressed() {
        logoff()
    }

    override fun finish() {
        super.finish()
        Utils.overrideBackTransition(this)
    }

    fun logoff() {
        stopService(Intent(this, EventIntentService::class.java))
        if (_vmgr!!.vBox != null)
            object : LoginSupport.LogoffTask(this, _vmgr) {
                override fun onPostExecute(result: Void) {
                    super.onPostExecute(result)
                    finish()
                }
            }.execute()
    }

    companion object {
        private val REQUEST_CODE_PREFERENCES = 6
    }
}
