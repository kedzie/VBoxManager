package com.kedzie.vbox.api;

import java.util.List;

import com.kedzie.vbox.api.jaxb.AdditionsRunLevelType;
import com.kedzie.vbox.api.jaxb.AdditionsUpdateFlag;
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
	
    @KSOAP(cacheable=true) public Integer getMemoryBalloonSize();
	public void setMemoryBalloonSize(@KSOAP(type="unsignedint", value="memoryBalloonSize") int memoryBalloonSize);
	
	@KSOAP(cacheable=true) public Integer getStatisticsUpdateInterval();
	public void setStatisticsUpdateInterval(@KSOAP(type="unsignedint", value="statisticsUpdateInterval") int statisticsUpdateInterval);
    
    public boolean getAdditionsStatus(@KSOAP("level") AdditionsRunLevelType level);
    
    public void setCredentials(@KSOAP("userName") String userName, @KSOAP("password") String password, @KSOAP("domain") String domain, 
            @KSOAP(type="boolean", value="allowInteractiveLogon") boolean allowInteractiveLogon);

	@KSOAP(cacheable=true) public String getOSTypeId();
	@KSOAP(cacheable=true) public AdditionsRunLevelType getAdditionsRunLevel();
	@KSOAP(cacheable=true) public String getAdditionsVersion();
	@KSOAP(cacheable=true) public int getAdditionsRevision();
	@KSOAP(cacheable=true) public List<IGuestSession> getSessions();
	
	public IGuestSession createSession(@KSOAP("userName") String userName, @KSOAP("password") String password, @KSOAP("domain") String domain,
	        @KSOAP("sessionName") String sessionName);

	public List<IGuestSession> findSession(@KSOAP("sessionName") String sessionName);
	
	public IProgress updateGuestAdditions(@KSOAP("source") String source, @KSOAP("flags") List<AdditionsUpdateFlag> flags);
}
