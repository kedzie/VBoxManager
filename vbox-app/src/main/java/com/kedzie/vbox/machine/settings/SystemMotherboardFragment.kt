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
import com.kedzie.vbox.api.jaxb.ChipsetType
import com.kedzie.vbox.api.jaxb.FirmwareType
import com.kedzie.vbox.app.SliderView.OnSliderViewChangeListener
import kotlinx.android.synthetic.main.settings_system_motherboard.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * @apiviz.stereotype fragment
 */
class SystemMotherboardFragment(arguments: Bundle) : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

    init {
        this.arguments = arguments
    }

    private lateinit var machine: IMachine

    private fun loadInfo() = launch {
        utc.isChecked = machine.getRTCUseUTC()
        utc.setOnCheckedChangeListener { buttonView, isChecked -> launch { machine.setRTCUseUTC(isChecked) } }
        val type = machine.getFirmwareType()
        efi.isChecked = type == FirmwareType.EFI ||
                type == FirmwareType.EFI_32 ||
                type == FirmwareType.EFI_64 ||
                type == FirmwareType.EFIDUAL
        efi.setOnCheckedChangeListener { buttonView, isChecked ->
            launch { machine.setFirmwareType(if (isChecked) FirmwareType.EFI else FirmwareType.BIOS) } }
        io_apic.isChecked = machine.getBIOSSettings().getIOAPICEnabled()
        io_apic.setOnCheckedChangeListener { buttonView, isChecked ->
            launch { machine.getBIOSSettings().setIOAPICEnabled(isChecked) } }

        val chipsetAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, arrayOf(ChipsetType.PIIX_3, ChipsetType.ICH_9))
        chipset.setAdapter(chipsetAdapter)
        chipset.setSelection(if (machine.getChipsetType() == ChipsetType.PIIX_3) 0 else 1)
        chipset.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                launch { machine.setChipsetType(chipsetAdapter.getItem(position)) }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        baseMemory.minValue = 1
        baseMemory.minValidValue = 1
        baseMemory.maxValidValue = (machine.api.vbox!!.getHost().getMemorySize() * .8f).toInt()
        baseMemory.maxValue = machine.api.vbox!!.getHost().getMemorySize()
        baseMemory.value = machine.getMemorySize()
        baseMemory.setOnSliderViewChangeListener(object : OnSliderViewChangeListener {
            override fun onSliderValidValueChanged(newValue: Int) {
                launch { machine.setMemorySize(newValue * 1024) }
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
        return inflater.inflate(R.layout.settings_system_motherboard, container, false)
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
