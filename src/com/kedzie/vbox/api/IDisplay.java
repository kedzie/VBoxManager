package com.kedzie.vbox.api;

import java.util.List;
import java.util.Map;

import com.kedzie.vbox.soap.KSOAP;

/**
 * The {@link IDisplay} interface represents the virtual machine's display. 
 * <p>The object implementing this interface is contained in each {@link IConsole#getDisplay}<b></b> attribute and represents the visual output of the virtual machine.
 * <p>The virtual display supports pluggable output targets represented by the {@link IFramebuffer} interface. Examples of the output target are a window on the host computer or an RDP session's display on a remote computer.
 * <p><dl class="user" compact><dt><b>Interface ID:</b></dt><dd><code>{09EED313-CD56-4D06-BD56-FAC0F716B5DD}</code> </dd></dl>
 */
public interface IDisplay extends IManagedObjectRef {
	public Map<String, List<String>> getScreenResolution(@KSOAP(type="unsignedInt", value="screenId") int screenId);
}
