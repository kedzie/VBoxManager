package com.kedzie.vbox.api;

import java.util.Map;

import com.kedzie.vbox.soap.KSOAP;

/**
 * The {@link IDisplay} interface represents the virtual machine's display. 
 * <p>The object implementing this interface is contained in each {@link IConsole#getDisplay}<b></b> attribute and represents the visual output of the virtual machine.</p>
 * <p>The virtual display supports pluggable output targets represented by the {@link IFramebuffer} interface. Examples of the output target are a window on the host computer or an RDP session's display on a remote computer.</p>
 */
@KSOAP
public interface IDisplay extends IManagedObjectRef {

    @KSOAP public Map<String, String> getScreenResolution(@KSOAP(type="unsignedInt", value="screenId") int screenId);

    @KSOAP public byte[] takeScreenShotToArray(@KSOAP(type="unsignedInt", value="screenId") int screenId, @KSOAP(type="unsignedInt", value="width") int width, @KSOAP(type="unsignedInt", value="height") int height);

    @KSOAP public byte[] takeScreenShotPNGToArray(@KSOAP(type="unsignedInt", value="screenId") int screenId, @KSOAP(type="unsignedInt", value="width") int width, @KSOAP(type="unsignedInt", value="height") int height);
	
}
