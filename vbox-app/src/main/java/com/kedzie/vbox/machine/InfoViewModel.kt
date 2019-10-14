package com.kedzie.vbox.machine

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.kedzie.vbox.R
import com.kedzie.vbox.api.*
import com.kedzie.vbox.api.jaxb.*
import com.kedzie.vbox.app.Utils
import timber.log.Timber

class InfoViewModel(private val app: Application,
                    private val vbox: IVirtualBox,
                    private val machine: IMachine) : AndroidViewModel(app) {

    val name = machine.getName()
    val description = machine.getDescription()
    val osTypeId = machine.getOSTypeId()
    val groups = machine.getGroups()
    val memorySize = machine.getMemorySize()
    val cpuCount = machine.getCPUCount()

    val bootOrder = liveData {
        //boot order
        val bootOrderBuf = StringBuffer()
        for (i in 1..99) {
            val b = machine.getBootOrder(i)
            if (b == DeviceType.NULL) break
            Utils.appendWithComma(bootOrderBuf, b.toString())
        }
        emit(bootOrderBuf.toString())
    }

    val acceleration = liveData {
        val accelerationBuf = StringBuffer()
        if (machine.getHWVirtExProperty(HWVirtExPropertyType.ENABLED)!!)
            Utils.appendWithComma(accelerationBuf, "VT-x/AMD-V")
        if (machine.getHWVirtExProperty(HWVirtExPropertyType.NESTED_PAGING)!!)
            Utils.appendWithComma(accelerationBuf, "Nested Paging")
        if (machine.getCPUProperty(CPUPropertyType.PAE)!!)
            Utils.appendWithComma(accelerationBuf, "PAE/NX")
        emit(accelerationBuf.toString())
    }

    val storageControllers = liveData {
        //storage controllers
        val storageBuf = StringBuffer()
        val controllers = machine.getStorageControllersNow()
        for (controller in controllers) {
            storageBuf.append("Controller: ").append(controller.getName()).append("\n")
            if (controller.getBus() == StorageBus.SATA) {
                for (a in machine.getMediumAttachmentsOfController(controller.getName()))
                    storageBuf.append(String.format("SATA Port %1\$d\t\t%2\$s\n", a.port, if (a.medium == null) "" else a.medium.getBase().getName()))
            } else if (controller.getBus() == StorageBus.IDE) {
                for (a in machine.getMediumAttachmentsOfController(controller.getName())) {
                    storageBuf.append(String.format("IDE %1\$s %2\$s\t\t%3\$s\n",
                            if (a.port == 0) "Primary" else "Secondary",
                            if (a.device == 0) "Master" else "Slave",
                            if (a.medium == null) "" else a.medium.getBase().getName()))
                }
            } else {
                for (a in machine.getMediumAttachmentsOfController(controller.getName()))
                    storageBuf.append(String.format("Port: %1\$d Device: %2\$d\t\t%3\$s\n", a.port, a.device, if (a.medium == null) "" else a.medium.getBase().getName()))
            }
        }
        emit(storageBuf.toString())
    }

    val networkAdapters = liveData {
        //network devices
        val networkText = StringBuffer()
        for (i in 0..3) {
            val adapter = machine.getNetworkAdapter(i)
            if (!adapter.getEnabled())
                continue
            if (i > 0)
                networkText.append("\n")
            networkText.append("Adapter ").append(i + 1).append(": ").append(adapter.getAdapterType())
            val type = adapter.getAttachmentType()
            Timber.v("Adapter #%d attachment Type: %s", (i + 1), type)
            if (type == NetworkAttachmentType.BRIDGED)
                networkText.append("  (Bridged Adapter, ").append(adapter.getBridgedInterface()).append(")")
            else if (type == NetworkAttachmentType.HOST_ONLY)
                networkText.append("  (Host-Only Adapter, ").append(adapter.getHostOnlyInterface()).append(")")
            else if (type == NetworkAttachmentType.GENERIC)
                networkText.append("  (Generic-Driver Adapter, ").append(adapter.getGenericDriver()).append(")")
            else if (type == NetworkAttachmentType.INTERNAL)
                networkText.append("  (Internal-Network Adapter, ").append(adapter.getInternalNetwork()).append(")")
            else if (type == NetworkAttachmentType.NAT)
                networkText.append("  (NAT)")
        }
        emit(networkText.toString())
    }

    val audioController = liveData {
        emit(machine.getAudioAdapter().getAudioController().toString())
    }

    val audioDriver = liveData {
        emit(machine.getAudioAdapter().getAudioDriver().toString())
    }

    val vramSize = machine.getVRAMSize()

    val accelerationVideo = liveData {
        emit("${if(machine.getAccelerate2DVideoEnabledNow()) "2D" else ""} ${if(machine.getAccelerate3DEnabledNow()) "3D" else ""}")
    }

    val rdpPort = liveData {
        emit(machine.getVRDEServer().getVRDEProperty(IVRDEServer.PROPERTY_PORT))
    }

    val screenshot = liveData {
        //screenshots
        val size = app.resources.getDimensionPixelSize(R.dimen.screenshot_size)
        emit(when (machine.getState()) {
            MachineState.SAVED -> {
                machine.readSavedScreenshot(0).apply {
                    scaleBitmap(size, size)
                }
            }
            MachineState.RUNNING -> {
                vbox.takeScreenshot(machine, size, size)
            }
            else -> null
        })
    }
}