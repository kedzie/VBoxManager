package com.kedzie.vbox.machine.settings

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.google.common.base.Objects
import com.kedzie.vbox.R
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.api.IMedium
import com.kedzie.vbox.api.jaxb.IMediumAttachment
import com.kedzie.vbox.api.jaxb.Slot
import com.kedzie.vbox.api.jaxb.StorageBus
import kotlinx.android.synthetic.main.settings_storage_details_dvd.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 *
 * @apiviz.stereotype fragment
 */
class StorageDVDFragment : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

    private lateinit var machine: IMachine
    private lateinit var attachment: IMediumAttachment

    /**
     * List mountable mediums
     */
    private fun listMediums() {
        launch {
            val mediums = mutableListOf<IMedium>()
            mediums.addAll(machine.api.vbox!!.getHost().getDvdDrives())
            mediums.addAll(machine.api.vbox!!.getDVDImages())

            val items = arrayOfNulls<CharSequence>(mediums.size + 1)
            for (i in mediums.indices) {
                val m = mediums[i]
                items[i] = (if (m.getHostDrive()) "Host Drive " else "") + m.getName()
            }
            items[items.size - 1] = "No Disc"

            AlertDialog.Builder(context)
                    .setTitle("Select Disk")
                    .setItems(items) { dialog, item ->
                        val selected = items[item]
                        mount(if (selected == "No Disc") null else mediums[item])
                    }.show()
        }
    }

    /**
     * Move the medium to a different slot within controller.
     */
    private fun move(slot: Slot) {
        launch {
            val controller = attachment.controller!!
            if (slot != attachment.slot) {
                machine.detachDevice(controller, attachment.port, attachment.device)
                if (attachment.medium != null)
                    machine.attachDevice(controller, slot.port, slot.device, attachment.deviceType,attachment.medium)
                else
                    machine.attachDeviceWithoutMedium(controller, slot.port, slot.device, attachment.deviceType)
            }
        }
    }

    /**
     * Mount different medium
     */
    private fun mount(medium: IMedium?) {
        launch {
            val controller = attachment.controller!!

            attachment.medium?.let {
                machine.unmountMedium(controller, attachment.port, attachment.device, false)
            }

            medium?.let {
                attachment.medium = it
                machine.mountMedium(controller, attachment.port, attachment.device, it, false)
            }
            loadInfo()
        }
    }

    /**
     * Fetch DVD/Medium details
     */
    private fun loadInfo() {
        launch {
            val controller = machine.getStorageControllerByName(attachment.controller!!)

            val attachments = machine.getMediumAttachmentsOfController(controller.getName())

            val devicesPerPort = controller.getMaxDevicesPerPortCount()

            val slots = mutableListOf<Slot>()
            for (i in 0 until controller.getMaxPortCount()) {
                for (j in 0 until devicesPerPort) {
                    val slot = Slot(i, j)
                    var isUsed = false
                    for (a in attachments) {
                        if (a.slot == slot && !Objects.equal(a.medium, attachment.medium)) {
                            isUsed = true
                            break
                        }
                    }
                    if (!isUsed)
                        slots!!.add(slot)
                    if (devicesPerPort == 1)
                        slot.name = StringBuffer(controller.getBus().toString()).append(" Port ").append(i).toString()
                    else if (controller.getBus() == StorageBus.IDE) {
                        slot.name = (if (i == 0) "Primary " else "Secondary ") + if (j == 0) "MASTER" else "SLAVE"
                    }
                }
            }
            val slotAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, slots)
            storage_port.adapter = slotAdapter
            storage_port.setSelection(slots.indexOf(attachment.slot))

            storage_port.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    move(slotAdapter.getItem(position))
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
            attachment.medium?.let {
                storage_type.text = it.getType().toString()
                storage_size.text = (it.getSize() / 1024).toString() + " MB"
                storage_location.text = it.getLocation()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        machine = arguments!!.getParcelable(IMachine.BUNDLE)
        attachment = arguments!!.getParcelable(IMedium.BUNDLE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.settings_storage_details_dvd, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storage_mount.setOnClickListener { listMediums() }
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
