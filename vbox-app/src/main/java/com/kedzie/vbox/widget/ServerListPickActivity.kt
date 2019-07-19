package com.kedzie.vbox.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.kedzie.vbox.R
import com.kedzie.vbox.api.IMachine
import androidx.appcompat.app.AppCompatActivity
import com.kedzie.vbox.app.BundleBuilder
import com.kedzie.vbox.app.Utils
import com.kedzie.vbox.machine.group.MachineGroupListFragment
import com.kedzie.vbox.machine.group.TreeNode
import com.kedzie.vbox.machine.group.VMGroupListView
import com.kedzie.vbox.server.Server
import com.kedzie.vbox.server.ServerListFragment
import com.kedzie.vbox.server.login
import com.kedzie.vbox.soap.VBoxSvc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * VirtualBox server list for picking a VM for widget
 * @author Marek Kędzierski
 * @apiviz.stereotype activity
 * @apiviz.owns com.kedzie.vbox.server.ServerListFragment
 */
class ServerListPickActivity : AppCompatActivity(),
        ServerListFragment.OnSelectServerListener,
        VMGroupListView.OnTreeNodeSelectListener,
        CoroutineScope by CoroutineScope(Dispatchers.Main) {

    /** Are we in a dual pane (tablet) layout  */
    private var dualPane: Boolean = false

    /** ID of AppWidget  */
    private var mAppWidgetId: Int = 0

    /** Currently selected logged on api  */
    private var vmgr: VBoxSvc? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(Activity.RESULT_CANCELED)
        mAppWidgetId = intent.extras!!.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        supportActionBar!!.setTitle(R.string.widget_server_list)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        setContentView(R.layout.widget_server_list)
        val detailsFrame = findViewById<View>(R.id.details) as FrameLayout
        dualPane = detailsFrame != null && detailsFrame.visibility == View.VISIBLE
    }

    internal fun launchMachineList(vboxApi: VBoxSvc) {
        vmgr = vboxApi
        if (dualPane) {
            Utils.setCustomAnimations(supportFragmentManager.beginTransaction())
                    .replace(R.id.details, Fragment.instantiate(this, MachineGroupListFragment::class.java.name, BundleBuilder().putParcelable(VBoxSvc.BUNDLE, vboxApi).create()))
                    .commit()
        } else {
            Utils.startActivityForResult(this, Intent(this@ServerListPickActivity, MachineListPickActivity::class.java)
                    .putExtra(VBoxSvc.BUNDLE, vboxApi as Parcelable)
                    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId), REQUEST_CODE_MACHINE_LIST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_MACHINE_LIST -> if (resultCode == Activity.RESULT_OK) {
                setResult(resultCode, data)
                finish()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onTreeNodeSelect(node: TreeNode) {
        if (node is IMachine) {
            Provider.savePrefs(this, vmgr!!, node as IMachine, mAppWidgetId)
            setResult(Activity.RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId))
            finish()
        }
    }

    override fun onSelectServer(server: Server) {
        launch {
            vmgr?.let {
                it.logoff()
            }

            login(this@ServerListPickActivity, server) {
                launchMachineList(it)
            }
        }
    }

    companion object {

        private const val REQUEST_CODE_MACHINE_LIST = 0
    }
}
