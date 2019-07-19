package com.kedzie.vbox.machine.settings

import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import com.kedzie.vbox.R
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.api.jaxb.LockType
import com.kedzie.vbox.app.*
import com.kedzie.vbox.machine.settings.CategoryFragment.OnSelectCategoryListener
import com.kedzie.vbox.soap.VBoxSvc
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Obtain a write-lock and then edit virtual machine settings
 *
 * @apiviz.stereotype activity
 */
class VMSettingsActivity : AppCompatActivity(), CoroutineScope by CoroutineScope(Dispatchers.Main),
        OnSelectCategoryListener, HasSupportFragmentInjector {

    @Inject
    internal var dispatchingFragmentInjector: DispatchingAndroidInjector<Fragment>? = null

    /** Is the dual Fragment Layout active?  */
    private var _dualPane: Boolean = false

    /** VirtualBox API  */
    private lateinit var vmgr: VBoxSvc

    /** Immutable vm reference  */
    private lateinit var machine: IMachine

    /** Mutable vm reference.  Initialized when machine is successfully WRITE-Locked  */
    private var mutable: IMachine? = null

    /** Currently selected settings category  */
    private var currentCategory: String? = null
    /** Currently selected category index  */
    private var currentPosition = -1

    override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
        return dispatchingFragmentInjector
    }

    private fun lockMachine() = launch {
        val session = vmgr.vbox!!.getSessionObject()
        machine.lockMachine(session, LockType.WRITE)
        mutable = session.getMachine()
        supportFragmentManager.beginTransaction()
        .add(R.id.list, Fragment.instantiate(this@VMSettingsActivity, CategoryFragment::class.java.name, BundleBuilder().putProxy(IMachine.BUNDLE, mutable).create()))
        .commit()
    }

    private fun saveSettings() = launch {
        mutable!!.saveSettings()
        vmgr.vbox!!.getSessionObject().unlockMachine()
        Utils.toastLong(this@VMSettingsActivity, this@VMSettingsActivity.getString(R.string.toast_saved_settings))
        finish()
    }

    private fun discardSettings() = launch {
        mutable?.let {
            it.discardSettings()
            vmgr.vbox!!.getSessionObject().unlockMachine()
            Utils.toastLong(this@VMSettingsActivity, getString(R.string.toast_discarding_settings))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vmgr = intent.getParcelableExtra(VBoxSvc.BUNDLE)
        machine = intent.getParcelableExtra(IMachine.BUNDLE)

        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_HOME or ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE

        launch {
            supportActionBar!!.setTitle(machine!!.getName() + " Settings")
            supportActionBar!!.setIcon(app.getOSDrawable(machine!!.getOSTypeId()))
        }

        setContentView(R.layout.fragment_list_layout)
        _dualPane = findViewById<View>(R.id.details) != null

        if (savedInstanceState == null)
            lockMachine()
        else {
            mutable = savedInstanceState.getParcelable(MUTABLE_KEY)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (Utils.getScreenSize(newConfig) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            Timber.i("Handling orientation change")

            val list = supportFragmentManager.findFragmentById(R.id.list)
            supportFragmentManager.beginTransaction().detach(list!!).commit()

            val details = supportFragmentManager.findFragmentById(R.id.details)
            if (details != null) {
                //                _mgr.beginTransaction().remove(details).commit();
            }

            setContentView(R.layout.fragment_list_layout)
            _dualPane = findViewById<View>(R.id.details) != null
            supportFragmentManager.beginTransaction().attach(list).commit()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (mutable != null)
            BundleBuilder.putProxy(outState, MUTABLE_KEY, mutable)
    }

    override fun onSelectCategory(position: Int, category: FragmentElement) {
        if (_dualPane) {
            val tx = supportFragmentManager.beginTransaction()
            if (currentPosition == -1) {
                tx.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out)
            } else if (position > currentPosition) {
                tx.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top, R.anim.slide_in_top, R.anim.slide_out_bottom)
            } else {
                tx.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_bottom, R.anim.slide_in_bottom, R.anim.slide_out_top)
            }
            Utils.detachFragment(supportFragmentManager, tx, R.id.details)
            Utils.addOrAttachFragment(this, supportFragmentManager, tx, R.id.details, category)
            tx.commit()
        } else {
            Utils.startActivity(this, Intent(this, FragmentActivity::class.java).putExtra(FragmentElement.BUNDLE, category))
        }
        currentCategory = category.name
        currentPosition = position
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.machine_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.option_menu_save -> {
                saveSettings()
                return true
            }
            R.id.option_menu_discard -> {
                discardSettings()
                return true
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return false
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.details)
        if (frag != null && frag.childFragmentManager.popBackStackImmediate())
            return

        AlertDialog.Builder(this)
                .setTitle("Save Changes?")
                .setMessage("Do you wish to save changes?")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("OK") { dialog, which ->
                    dialog.dismiss()
                    saveSettings()
                    finish()
                }
                .setNegativeButton("Discard") { dialog, which ->
                    dialog.dismiss()
                    discardSettings()
                    finish()
                }
                .show()
    }


    override fun finish() {
        super.finish()
        Utils.overrideBackTransition(this)
    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }

    companion object {
        private const val MUTABLE_KEY = "mutable"
    }
}
