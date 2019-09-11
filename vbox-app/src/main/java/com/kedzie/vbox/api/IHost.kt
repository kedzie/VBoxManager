package com.kedzie.vbox.api

import android.os.Parcelable
import androidx.lifecycle.LiveData
import com.kedzie.vbox.api.jaxb.HostNetworkInterfaceType
import com.kedzie.vbox.api.jaxb.IMediumAttachment
import com.kedzie.vbox.api.jaxb.ProcessorFeature
import com.kedzie.vbox.machine.group.TreeNode
import com.kedzie.vbox.soap.Cacheable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy

/**
 * The IHost interface represents the physical machine that this VirtualBox installation runs on.
 * An object implementing this interface is returned by the [IVirtualBox.getHost] attribute. This interface
 * contains read-only information about the host's physical hardware (such as what processors and disks
 * are available, what the host operating system is, and so on) and also allows for manipulating some of the host's
 * hardware, such as global USB device filters and host interface networking.
 */
@KsoapProxy
@Ksoap
interface IHost : IManagedObjectRef, Parcelable {

    /**
     * @return    Amount of system memory in megabytes installed in the host system.
     */
    @Cacheable(value = "memorySize", get = true, put = true)
    suspend fun getMemorySize(): Int

    /**
     * @return    Available system memory in the host system.
     */
    @Cacheable(value = "memoryAvailable", get = true, put = true)
    suspend fun getMemoryAvailable(): Int

    /**
     * @return    Number of (logical) CPUs installed in the host system.
     */
    @Cacheable(value = "processorCount", get = true, put = true)
    suspend fun getProcessorCount(): Int

    /**
     * @return    Number of physical processor cores installed in the host system.
     */
    @Cacheable(value = "processorCoreCount", get = true, put = true)
    suspend fun getProcessorCoreCount(): Int

    /**
     * @return    Number of (logical) CPUs online in the host system.
     */
    @Cacheable(value = "processorOnlineCount", get = true, put = true)
    suspend fun getProcessorOnlineCount(): Int

    /**
     * @return    Name of the host system's operating system.
     */
    @Cacheable(value = "operatingSystem", get = true, put = true)
    suspend fun getOperatingSystem(): String

    /**
     * @return    Host operating system's version string.
     */
    @Cacheable(value = "osVersion", get = true, put = true)
    suspend fun getOsVersion(): String

    /**
     * @return    Returns true when the host supports 3D hardware acceleration.
     */
    @Cacheable(value = "acceleration3DAvailable", get = true, put = true)
    suspend fun getAcceleration3DAvailable(): Boolean

    /**
     * @return    Returns the current host time in milliseconds since 1970-01-01 UTC.
     */
    @Cacheable(value = "utcTime", get = true, put = true)
    suspend fun getUtcTime(): Long

    suspend fun getNetworkInterfaces(): List<IHostNetworkInterface>

    //	@Ksoap(cacheable=true) public ArrayList<IHostUSBDeviceFilter> getUSBDeviceFilters();

    //	@Ksoap(cacheable=true) public ArrayList<IHostUSBDevice> getUSBDevices();

    suspend fun getDvdDrives(): List<IMedium>

    suspend fun getFloppyDrives(): List<IMedium>

    /**
     * Query the (approximate) maximum speed of a specified host CPU in Megahertz.
     * @param cpuId        Identifier of the CPU
     * @return    Speed value. 0 is returned if value is not known or cpuId is invalid.
     */
    suspend fun getProcessorSpeed(@Ksoap(type = "unsignedInt") cpuId: Int): Int

    suspend fun getProcessorDescription(@Ksoap(type = "unsignedInt") cpuId: Int): String

    suspend fun findHostDVDDrive(name: String): IMedium

    suspend fun findHostFloppyDrive(name: String): IMedium

    suspend fun getProcessorFeature(feature: ProcessorFeature): Boolean

    suspend fun generateMACAddress(): String

    suspend fun createHostOnlyNetworkInterface(): Map<String, String>

    suspend fun removeHostOnlyNetworkInterface(id: String): IProgress

    suspend fun findHostNetworkInterfaceById(id: String): IHostNetworkInterface

    suspend fun findHostNetworkInterfaceByName(name: String): IHostNetworkInterface

    suspend fun findHostNetworkInterfacesOfType(type: HostNetworkInterfaceType): List<IHostNetworkInterface>

    companion object {

        suspend fun createHostOnlyNetworkInterface(host: IHost): Pair<IHostNetworkInterface, IProgress> {
            val ret = host.createHostOnlyNetworkInterface()
            return Pair(
                    IHostNetworkInterfaceProxy(host.api, ret["hostInterface"]!!),
                    IProgressProxy(host.api, ret["returnval"]!!))
        }
    }
}
