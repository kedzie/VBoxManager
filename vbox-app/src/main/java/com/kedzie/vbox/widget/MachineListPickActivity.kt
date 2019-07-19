package com.kedzie.vbox.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import com.kedzie.vbox.R
import com.kedzie.vbox.api.IMachine
import androidx.appcompat.app.AppCompatActivity
import com.kedzie.vbox.app.FragmentElement
import com.kedzie.vbox.app.Utils
import com.kedzie.vbox.machine.group.MachineGroupListFragment
import com.kedzie.vbox.machine.group.TreeNode
import com.kedzie.vbox.machine.group.VMGroupListView
import com.kedzie.vbox.soap.VBoxSvc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MachineListPickActivity : AppCompatActivity(),
    VMGroupListView.OnTreeNodeSelectListener,
    CoroutineScope by CoroutineScope(Dispatchers.Main){

    /** VirtualBox API  */
    private var vmgr: VBoxSvc? = null

    /** ID of AppWidget  */
    private var mAppWidgetId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(Activity.RESULT_CANCELED)
        supportActionBar!!.setTitle(R.string.select_virtual_machine_widget_config)
        vmgr = intent.getParcelableExtra(VBoxSvc.BUNDLE)
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)

        if (savedInstanceState == null) {
            Utils.replaceFragment(this, supportFragmentManager, android.R.id.content,
                    FragmentElement("list", MachineGroupListFragment::class.java, intent.extras))
        }
    }

    override fun onTreeNodeSelect(node: TreeNode) {
        if (node is IMachine) {
            Provider.savePrefs(this, vmgr!!, node as IMachine, mAppWidgetId)
            setResult(Activity.RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId))
            finish()
        }
    }

    override fun onBackPressed() {
        if (vmgr?.vbox == null)
            return super.onBackPressed()
        else {
            launch {
                vmgr!!.logoff()
                finish()
            }
        }
    }
}
