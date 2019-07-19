package com.kedzie.vbox.machine.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.kedzie.vbox.R
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.api.jaxb.AudioControllerType
import com.kedzie.vbox.api.jaxb.AudioDriverType
import com.kedzie.vbox.app.Utils
import kotlinx.android.synthetic.main.settings_audio_adapter.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Edit audio settings of virtual machine
 */
class AudioFragment : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

    private lateinit var machine: IMachine

    private fun loadInfo() {
        launch {
            val adapter = machine.getAudioAdapter()

            enabled.isChecked = adapter.getEnabled()
            enabled.setOnCheckedChangeListener { buttonView, isChecked ->
                launch {
                    adapter.setEnabled(isChecked)
                }
            }

            val audioControllerAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, AudioControllerType.values());
            audio_controller.setAdapter(audioControllerAdapter)
            audio_controller.setSelection(Utils.indexOf(AudioControllerType.values(), adapter.getAudioController()))
            audio_controller.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    launch {
                        adapter.setAudioController(audioControllerAdapter.getItem(position))
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

            val types = AudioDriverType.getAudioDrivers(machine.api.vbox!!.getHost().getOperatingSystem())
            val audioDriverAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, types)
            audio_driver.adapter = audioDriverAdapter
            audio_driver.setSelection(Utils.indexOf(types, adapter.getAudioDriver()))
            audio_driver.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    launch {
                        adapter.setAudioDriver(audioDriverAdapter.getItem(position))
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        machine = arguments!!.getParcelable(IMachine.BUNDLE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.settings_audio_adapter, null)
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