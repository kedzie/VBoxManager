package com.kedzie.vbox.api

import com.kedzie.vbox.api.jaxb.BitmapFormat
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy

/**
 * The [IDisplay] interface represents the virtual machine's display.
 *
 * The object implementing this interface is contained in each [IConsole.getDisplay]**** attribute and represents the visual output of the virtual machine.
 *
 * The virtual display supports pluggable output targets represented by the [IFramebuffer] interface. Examples of the output target are a window on the host computer or an RDP session's display on a remote computer.
 */
@KsoapProxy
@Ksoap
interface IDisplay : IManagedObjectRef {

    suspend fun getScreenResolution(@Ksoap(type = "unsignedInt", value = "screenId") screenId: Int):
            Map<String, String>

    suspend fun takeScreenShotToArray(@Ksoap(type = "unsignedInt", value = "screenId") screenId: Int,
                              @Ksoap(type = "unsignedInt", value = "width") width: Int,
                              @Ksoap(type = "unsignedInt", value = "height") height: Int,
                              bitmapFormat: BitmapFormat): ByteArray
}
