package com.kedzie.vbox.machine.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.kedzie.vbox.R
import com.kedzie.vbox.api.IStorageController
import com.kedzie.vbox.api.jaxb.StorageControllerType
import com.kedzie.vbox.app.Utils
import kotlinx.android.synthetic.main.settings_storage_controller.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 *
 * @author Marek Kędzierski
 * @apiviz.stereotype fragment
 */
class StorageControllerFragment : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

    private lateinit var controller: IStorageController

    private fun loadInfo() {
        launch {
            controller_name.setText(controller.getName())
            controller_host_io_cache.isChecked = controller.getUseHostIOCache()
            val types = StorageControllerType.getValidTypes(controller.getBus())
            val typeAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, types)
            controller_type.adapter = typeAdapter
            controller_type.setSelection(Utils.indexOf(types, controller.getControllerType()))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controller = arguments!!.getParcelable(IStorageController.BUNDLE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.settings_storage_controller, null)
    }

    override fun onStart() {
        super.onStart()
        loadInfo()
    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }
}
