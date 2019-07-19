package com.kedzie.vbox.machine.settings

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.kedzie.vbox.R
import com.kedzie.vbox.api.IMedium
import com.kedzie.vbox.api.IStorageController
import com.kedzie.vbox.api.jaxb.DeviceType
import com.kedzie.vbox.api.jaxb.IMediumAttachment
import com.kedzie.vbox.app.BundleBuilder
import com.kedzie.vbox.app.FragmentElement
import com.kedzie.vbox.app.Utils
import com.kedzie.vbox.machine.settings.StorageListFragment.OnMediumAttachmentClickedListener
import com.kedzie.vbox.machine.settings.StorageListFragment.OnStorageControllerClickedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber


class StorageFragment : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Main),
        OnStorageControllerClickedListener, OnMediumAttachmentClickedListener {
    private var _dualPane: Boolean = false
    private var _listFragment: StorageListFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = FrameLayout(activity!!)
        LayoutInflater.from(activity).inflate(R.layout.settings_storage, view, true)
        val detailsFrame = view.findViewById<View>(R.id.details) as FrameLayout
        _dualPane = detailsFrame != null && detailsFrame.visibility == View.VISIBLE

        val f = childFragmentManager.findFragmentByTag("list")
        if (f == null) {
            _listFragment = StorageListFragment()
            _listFragment!!.arguments = arguments
            childFragmentManager.beginTransaction().add(R.id.list, _listFragment!!, "list").commit()
        } else {
            _listFragment = f as StorageListFragment?
        }
        return view
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (Utils.getScreenSize(newConfig) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            Timber.d( "Handling orientation change")
            val mgr = childFragmentManager
            var tx = mgr.beginTransaction()
            for (fragment in mgr.fragments) {
                tx.detach(fragment)
            }
            tx.commit()
            val view = view as FrameLayout?
            view!!.removeAllViews()
            LayoutInflater.from(activity).inflate(R.layout.settings_storage, view, true)
            val detailsFrame = view.findViewById<View>(R.id.details) as FrameLayout
            _dualPane = detailsFrame != null && detailsFrame.visibility == View.VISIBLE

            _listFragment = childFragmentManager.findFragmentByTag("list") as StorageListFragment?
            val detailsFragment = childFragmentManager.findFragmentByTag("details")
            tx = childFragmentManager.beginTransaction()
            tx.attach(_listFragment!!)
            if (detailsFragment != null && _dualPane) {
                tx.remove(detailsFragment).add(R.id.details, Fragment.instantiate(activity!!, detailsFragment.javaClass.name, detailsFragment.arguments), "details")
            }
            tx.commit()
        }
    }

    override fun onStorageControllerClicked(element: IStorageController) {
        launch {
            show(FragmentElement(element.getName(), R.drawable.ic_settings_storage, StorageControllerFragment::class.java, BundleBuilder().putAll(arguments).putParcelable(IStorageController.BUNDLE, element).create()))
        }
    }

    override fun onMediumAttachmentClicked(element: IMediumAttachment) {
        val details = FragmentElement("Attachment",
                R.drawable.ic_settings_storage,
                if (element.deviceType == DeviceType.HARD_DISK)
                    StorageHardDiskFragment::class.java
                else StorageDVDFragment::class.java,
                Bundle().apply {
                    putAll(arguments)
                    putParcelable(IMedium.BUNDLE, element)
                })

        //    	else if(element.getType().equals(DeviceType.FLOPPY))
        //    		details.clazz = StorageFloppyFragment.class;
        show(details)
    }

    private fun show(details: FragmentElement) {
        if (_dualPane) {
            Utils.setCustomAnimations(childFragmentManager.beginTransaction()).replace(R.id.details, details.instantiate(activity)).commit()
        } else {
            Utils.setCustomAnimations(childFragmentManager.beginTransaction())
                    .addToBackStack(null)
                    .detach(_listFragment!!)
                    .add(R.id.list, details.instantiate(activity), "details")
                    .commit()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (_listFragment != null)
            _listFragment!!.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (_listFragment != null) _listFragment!!.onOptionsItemSelected(item) else false
    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }
}
