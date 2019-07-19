package com.kedzie.vbox.machine.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kedzie.vbox.R
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.api.jaxb.CPUPropertyType
import com.kedzie.vbox.api.jaxb.ProcessorFeature
import com.kedzie.vbox.app.SliderView.OnSliderViewChangeListener
import kotlinx.android.synthetic.main.settings_system_processors.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * @apiviz.stereotype fragment
 */
class SystemProcessorsFragment(arguments: Bundle) : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

    init {
        this.arguments = arguments
    }

    private lateinit var machine: IMachine

    private fun loadInfo() = launch {
        pae_nx.isEnabled = machine.api.vbox!!.getHost().getProcessorFeature(ProcessorFeature.PAE)
        pae_nx.isChecked = machine.getCPUProperty(CPUPropertyType.PAE)
        pae_nx.setOnCheckedChangeListener { buttonView, isChecked ->
            launch { machine.setCPUProperty(CPUPropertyType.PAE, isChecked) } }
        processors.minValue = 1
        processors.minValidValue = 1
        processors.maxValidValue = machine.api.vbox!!.getHost().getProcessorOnlineCount()
        processors.maxValue = machine.api.vbox!!.getHost().getProcessorOnlineCount()
        processors.value = machine.getCPUCount()
        processors.setOnSliderViewChangeListener(object : OnSliderViewChangeListener {
            override fun onSliderValidValueChanged(newValue: Int) {
                launch { machine.setCPUCount(newValue) }
            }

            override fun onSliderInvalidValueChanged(newValue: Int) {}
        })
        execution_cap.setOnSliderViewChangeListener(object : OnSliderViewChangeListener {
            override fun onSliderValidValueChanged(newValue: Int) {
                launch { machine.setCPUExecutionCap(newValue) }
            }

            override fun onSliderInvalidValueChanged(newValue: Int) {
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        machine = arguments!!.getParcelable(IMachine.BUNDLE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.settings_system_processors, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadInfo()
    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }
}
