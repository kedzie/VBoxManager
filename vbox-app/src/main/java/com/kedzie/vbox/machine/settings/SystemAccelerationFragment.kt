package com.kedzie.vbox.machine.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kedzie.vbox.R
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.api.jaxb.HWVirtExPropertyType
import com.kedzie.vbox.api.jaxb.ProcessorFeature
import kotlinx.android.synthetic.main.settings_system_acceleration.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * @apiviz.stereotype fragment
 */
class SystemAccelerationFragment(arguments: Bundle) : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

    init {
        this.arguments = arguments
    }

    private lateinit var machine: IMachine

    private fun loadInfo() = launch {
        vtx_amdv.isEnabled = machine.api.vbox!!.getHost().getProcessorFeature(ProcessorFeature.HW_VIRT_EX)
        vtx_amdv.isChecked = machine.getHWVirtExProperty(HWVirtExPropertyType.ENABLED)
        vtx_amdv.setOnCheckedChangeListener { buttonView, isChecked ->
            launch { machine.setHWVirtExProperty(HWVirtExPropertyType.ENABLED, isChecked) } }
        nested_paging.isEnabled = machine.api.vbox!!.getHost().getProcessorFeature(ProcessorFeature.NESTED_PAGING)
        nested_paging.isChecked = machine.getHWVirtExProperty(HWVirtExPropertyType.NESTED_PAGING)
        nested_paging.setOnCheckedChangeListener { buttonView, isChecked ->
            launch { machine.setHWVirtExProperty(HWVirtExPropertyType.NESTED_PAGING, isChecked) } }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        machine = arguments!!.getParcelable(IMachine.BUNDLE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.settings_system_acceleration, container, false)
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
