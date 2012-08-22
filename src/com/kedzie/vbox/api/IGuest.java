package com.kedzie.vbox.api;

import com.kedzie.vbox.soap.KSOAP;

/**
 * The {@link IGuest} interface represents information about the operating system running inside the virtual machine. 
 * <p>
 * Used in {@link IConsole#getGuest}<b></b>.<p>
 * {@link IGuest} provides information about the guest operating system, whether Guest Additions are installed and other OS-specific virtual machine properties.<p>
 * <dl class="user" compact><dt><b>Interface ID:</b></dt><dd><code>{ED109B6E-0578-4B17-8ACE-52646789F1A0}</code> </dd></dl>
 */
@KSOAP
public interface IGuest extends IManagedObjectRef {
	@KSOAP(cacheable=true)  public Integer getMemoryBalloonSize();
}
