package com.kedzie.vbox.api;

import android.os.Parcel;
import android.os.Parcelable;
import com.kedzie.vbox.api.jaxb.NATProtocol;
import com.kedzie.vbox.soap.Asyncronous;
import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@KSOAP
public interface INATNetwork extends IManagedObjectRef {

	public static final String BUNDLE = "NATNetwork";
	static ClassLoader loader = IHost.class.getClassLoader();

	public static final Parcelable.Creator<INATNetwork> CREATOR = new Parcelable.Creator<INATNetwork>() {
		@Override
		public INATNetwork createFromParcel(Parcel in) {
			VBoxSvc vmgr =  in.readParcelable(loader);
			String id = in.readString();
			Map<String, Object> cache = new HashMap<String, Object>();
			in.readMap(cache, loader);
			return vmgr.getProxy(INATNetwork.class, id, cache);
		}
		@Override
		public INATNetwork[] newArray(int size) {
			return new INATNetwork[size];
		}
	};

	@KSOAP(cacheable=true) public String getNetworkName();
	@Asyncronous public void setNetworkName(@KSOAP("networkName") String networkName);

	/**
	 * @return	This is CIDR IPv4 string.  Specifying it user defines IPv4 addresses of gateway
	 * (low address + 1) and DHCP server (= low address + 2). Note: If there are defined IPv4
	 * port-forward rules update of network will be ignored (because new assignment could break
	 * existing rules).
	 */
	@KSOAP(cacheable=true) public String getNetwork();
	@Asyncronous public void setNetwork(@KSOAP("network") String network);

	@KSOAP(cacheable=true) public Boolean getEnabled();
	@Asyncronous public void setEnabled(@KSOAP("enabled") boolean enabled);

	@KSOAP(cacheable=true) public String getGateway();
	@Asyncronous public void setGateway(@KSOAP("gateway") String gateway);

	@KSOAP(cacheable=true) public Boolean getIPv6Enabled();
	@Asyncronous public void setIPv6Enabled(@KSOAP("IPv6Enabled") boolean IPv6Enabled);

	@KSOAP(cacheable=true) public String getIPv6Prefix();
	@Asyncronous public void setIPv6Prefix(@KSOAP("IPv6Prefix") String IPv6Prefix);

	@KSOAP(cacheable=true) public Boolean getAdvertiseDefaultIPv6RouteEnabled();
	@Asyncronous public void setAdvertiseDefaultIPv6RouteEnabled(@KSOAP("advertiseDefaultIPv6RouteEnabled") boolean advertiseDefaultIPv6RouteEnabled );

	@KSOAP(cacheable=true) public Boolean getNeedDhcpServer();
	@Asyncronous public void setNeedDhcpServer(@KSOAP("needDhcpServer") boolean needDhcpServer );

	@KSOAP(cacheable=true) public IEventSource getEventSource() throws IOException;

	/**
	 * @return	Offset in ipv6 network from network id for address
	 * mapped into loopback6 interface of the host.
	 */
	@KSOAP(cacheable=true) public Integer getLoopbackIp6();
	@Asyncronous public void setLoopbackIp6(@KSOAP(type="int", value="loopbackIp6") int loopbackIp6);

	/**
	 * @return	Array of NAT port-forwarding rules in string representation,
	 * in the following format: "name:protocolid:[host ip]:host port:[guest ip]:guest port".
	 */
	@KSOAP(cacheable=true) public List<String> getPortForwardRules4();

	/**
	 * @return	Array of NAT port-forwarding rules in string representation,
	 * in the following format: "name:protocolid:[host ip]:host port:[guest ip]:guest port".
	 */
	@KSOAP(cacheable=true) public List<String> getPortForwardRules6();

	/**
	 * @return	Array of mappings (address,offset),e.g.
	 * ("127.0.1.1=4") maps 127.0.1.1 to networkid + 4.
	 */
	@KSOAP(cacheable=true) public List<String> getLocalMappings();

	public void addLocalMapping(@KSOAP("hostid") String hostid, @KSOAP("offset") int offset) throws IOException;

	public void addPortForwardRule(@KSOAP("isIPv6") boolean isIPv6, @KSOAP("ruleName") String ruleName, @KSOAP("proto") NATProtocol proto,
						@KSOAP("hostIP") String hostIP, @KSOAP(type = "unsignedShort", value="hostPort") short hostPort,
						@KSOAP("guestIP") String guestIP, @KSOAP(type = "unsignedShort", value="guestPort") short guestPort) throws IOException;

	public void removePortForwardRule(@KSOAP("iSipv6") boolean iSipv6, @KSOAP("ruleName") String ruleName) throws IOException;

	public void start(@KSOAP("trunkType") String trunkType) throws IOException;

	public void stop() throws IOException;
}