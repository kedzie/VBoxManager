package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.api.jaxb.NATProtocol
import com.kedzie.vbox.soap.Cacheable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy

@KsoapProxy
@Ksoap
interface INATNetwork : IManagedObjectRef, Parcelable {
    companion object {
        const val BUNDLE = "NATNetwork"
    }

    @Cacheable("NetworkName")
	suspend fun getNetworkName(): String;

    suspend fun setNetworkName(@Cacheable("NetworkName") networkName: String)

    /**
     * @return	This is CIDR IPv4 string.  Specifying it user defines IPv4 addresses of gateway
     * (low address + 1) and DHCP server (= low address + 2). Note: If there are defined IPv4
     * port-forward rules update of network will be ignored (because new assignment could break
     * existing rules).
     */
    @Cacheable("Network")
	suspend fun getNetwork(): String;
    suspend fun setNetwork(@Cacheable("Network") network: String)

    @Cacheable("Enabled")
	suspend fun getEnabled(): Boolean;
    suspend fun setEnabled(@Cacheable("Enabled") enabled: Boolean)

    @Cacheable("Gateway")
	suspend fun getGateway(): String;
    suspend fun setGateway(@Cacheable("Gateway") gateway: String)

    @Cacheable("IPv6Enabled")
	suspend fun getIPv6Enabled(): Boolean;
    suspend fun setIPv6Enabled(@Cacheable("IPv6Enabled") IPv6Enabled: Boolean)

    @Cacheable("IPv6Prefix")
	suspend fun getIPv6Prefix(): String;
    suspend fun setIPv6Prefix(@Cacheable("IPv6Prefix") IPv6Prefix: String)

    @Cacheable("AdvertiseDefaultIPv6RouteEnabled")
	suspend fun getAdvertiseDefaultIPv6RouteEnabled(): Boolean;
    suspend fun setAdvertiseDefaultIPv6RouteEnabled(@Cacheable("AdvertiseDefaultIPv6RouteEnabled") advertiseDefaultIPv6RouteEnabled: Boolean)

    @Cacheable("NeedDhcpServer")
	suspend fun getNeedDhcpServer(): Boolean;
    suspend fun setNeedDhcpServer(@Cacheable("NeedDhcpServer") needDhcpServer: Boolean)

    @Cacheable("EventSource")
	suspend fun getEventSource(): IEventSource

    /**
     * @return	Offset in ipv6 network from network id for address
     * mapped into loopback6 interface of the host.
     */
    @Cacheable("LoopbackIp6")
	suspend fun getLoopbackIp6(): Int;
    suspend fun setLoopbackIp6(@Cacheable("LoopbackIp6") @Ksoap(type = "int") loopbackIp6: Int)

    /**
     * @return	Array of NAT port-forwarding rules in string representation,
     * in the following format: "name:protocolid:[host ip]:host port:[guest ip]:guest port".
     */
    @Cacheable("PortForwardRules4")
	suspend fun getPortForwardRules4(): List<String>;

    /**
     * @return	Array of NAT port-forwarding rules in string representation,
     * in the following format: "name:protocolid:[host ip]:host port:[guest ip]:guest port".
     */
    @Cacheable("PortForwardRules6")
	suspend fun getPortForwardRules6(): List<String>;

    /**
     * @return	Array of mappings (address,offset),e.g.
     * ("127.0.1.1=4") maps 127.0.1.1 to networkid + 4.
     */
    @Cacheable("LocalMappings")
	suspend fun getLocalMappings(): List<String>;

    suspend fun addLocalMapping(hostid: String, offset: Int)

    suspend fun addPortForwardRule(isIPv6: Boolean, ruleName: String, proto: NATProtocol,
                                    hostIP: String, @Ksoap(type = "unsignedShort") hostPort: Short,
                                    guestIP: String, @Ksoap(type = "unsignedShort") guestPort: Short)

    suspend fun removePortForwardRule(iSipv6: Boolean, ruleName: String)

    suspend fun start(trunkType: String)

    suspend fun stop()
}