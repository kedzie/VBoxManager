package com.kedzie.vbox.machine.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.kedzie.vbox.R
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.app.SliderView
import com.kedzie.vbox.app.SliderView.OnSliderViewChangeListener
import kotlinx.android.synthetic.main.settings_display_video.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * @apiviz.stereotype fragment
 */
class DisplayVideoFragment(arguments: Bundle) : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

    init {
        this.arguments = arguments
    }

    private lateinit var machine: IMachine

    private fun loadInfo() {
        launch {
            val props = machine.api.vbox!!.getSystemProperties()
            val host = machine.api.vbox!!.getHost()
            videoMemory.minValue = 1
            videoMemory.minValidValue = 1
            videoMemory.maxValue = props.getMaxGuestVRAM()
            videoMemory.maxValidValue = props.getMaxGuestVRAM()
            videoMemory.value = machine.getVRAMSize()
            videoMemory.setOnSliderViewChangeListener(object : OnSliderViewChangeListener {
                override fun onSliderValidValueChanged(newValue: Int) {
                    launch { machine.setVRAMSize(newValue) }
                }

                override fun onSliderInvalidValueChanged(newValue: Int) {}
            })
            numMonitors.minValue = 1
            numMonitors.minValidValue = 1
            numMonitors.maxValue = props.getMaxGuestMonitors()
            numMonitors.maxValidValue = props.getMaxGuestMonitors()
            numMonitors.value = machine.getMonitorCount()
            numMonitors.setOnSliderViewChangeListener(object : OnSliderViewChangeListener {
                override fun onSliderValidValueChanged(newValue: Int) {
                    launch {
                        machine.setMonitorCount(newValue)
                    }
                }

                override fun onSliderInvalidValueChanged(newValue: Int) {}
            })
            acceleration2D.isChecked = machine.getAccelerate2DVideoEnabled()
            acceleration2D.setOnCheckedChangeListener { buttonView, isChecked -> launch { machine.setAccelerate2DVideoEnabled(isChecked) } }
            acceleration3D.isEnabled = host.getAcceleration3DAvailable()
            acceleration3D.isChecked = machine.getAccelerate3DEnabled()
            acceleration3D.setOnCheckedChangeListener { buttonView, isChecked -> launch { machine.setAccelerate3DEnabled(isChecked) } }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        machine = arguments!!.getParcelable(IMachine.BUNDLE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.settings_display_video, null)
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