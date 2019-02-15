

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for DhcpOpt.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DhcpOpt">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SubnetMask"/>
 *     &lt;enumeration value="TimeOffset"/>
 *     &lt;enumeration value="Router"/>
 *     &lt;enumeration value="TimeServer"/>
 *     &lt;enumeration value="NameServer"/>
 *     &lt;enumeration value="DomainNameServer"/>
 *     &lt;enumeration value="LogServer"/>
 *     &lt;enumeration value="Cookie"/>
 *     &lt;enumeration value="LPRServer"/>
 *     &lt;enumeration value="ImpressServer"/>
 *     &lt;enumeration value="ResourseLocationServer"/>
 *     &lt;enumeration value="HostName"/>
 *     &lt;enumeration value="BootFileSize"/>
 *     &lt;enumeration value="MeritDumpFile"/>
 *     &lt;enumeration value="DomainName"/>
 *     &lt;enumeration value="SwapServer"/>
 *     &lt;enumeration value="RootPath"/>
 *     &lt;enumeration value="ExtensionPath"/>
 *     &lt;enumeration value="IPForwardingEnableDisable"/>
 *     &lt;enumeration value="NonLocalSourceRoutingEnableDisable"/>
 *     &lt;enumeration value="PolicyFilter"/>
 *     &lt;enumeration value="MaximumDatagramReassemblySize"/>
 *     &lt;enumeration value="DefaultIPTime2Live"/>
 *     &lt;enumeration value="PathMTUAgingTimeout"/>
 *     &lt;enumeration value="IPLayerParametersPerInterface"/>
 *     &lt;enumeration value="InterfaceMTU"/>
 *     &lt;enumeration value="AllSubnetsAreLocal"/>
 *     &lt;enumeration value="BroadcastAddress"/>
 *     &lt;enumeration value="PerformMaskDiscovery"/>
 *     &lt;enumeration value="MaskSupplier"/>
 *     &lt;enumeration value="PerformRouteDiscovery"/>
 *     &lt;enumeration value="RouterSolicitationAddress"/>
 *     &lt;enumeration value="StaticRoute"/>
 *     &lt;enumeration value="TrailerEncapsulation"/>
 *     &lt;enumeration value="ARPCacheTimeout"/>
 *     &lt;enumeration value="EthernetEncapsulation"/>
 *     &lt;enumeration value="TCPDefaultTTL"/>
 *     &lt;enumeration value="TCPKeepAliveInterval"/>
 *     &lt;enumeration value="TCPKeepAliveGarbage"/>
 *     &lt;enumeration value="NetworkInformationServiceDomain"/>
 *     &lt;enumeration value="NetworkInformationServiceServers"/>
 *     &lt;enumeration value="NetworkTimeProtocolServers"/>
 *     &lt;enumeration value="VendorSpecificInformation"/>
 *     &lt;enumeration value="Option_44"/>
 *     &lt;enumeration value="Option_45"/>
 *     &lt;enumeration value="Option_46"/>
 *     &lt;enumeration value="Option_47"/>
 *     &lt;enumeration value="Option_48"/>
 *     &lt;enumeration value="Option_49"/>
 *     &lt;enumeration value="IPAddressLeaseTime"/>
 *     &lt;enumeration value="Option_64"/>
 *     &lt;enumeration value="Option_65"/>
 *     &lt;enumeration value="TFTPServerName"/>
 *     &lt;enumeration value="BootfileName"/>
 *     &lt;enumeration value="Option_68"/>
 *     &lt;enumeration value="Option_69"/>
 *     &lt;enumeration value="Option_70"/>
 *     &lt;enumeration value="Option_71"/>
 *     &lt;enumeration value="Option_72"/>
 *     &lt;enumeration value="Option_73"/>
 *     &lt;enumeration value="Option_74"/>
 *     &lt;enumeration value="Option_75"/>
 *     &lt;enumeration value="Option_119"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum DhcpOpt {

    SUBNET_MASK("SubnetMask"),
    TIME_OFFSET("TimeOffset"),
    ROUTER("Router"),
    TIME_SERVER("TimeServer"),
    NAME_SERVER("NameServer"),
    DOMAIN_NAME_SERVER("DomainNameServer"),
    LOG_SERVER("LogServer"),
    COOKIE("Cookie"),
    LPR_SERVER("LPRServer"),
    IMPRESS_SERVER("ImpressServer"),
    RESOURSE_LOCATION_SERVER("ResourseLocationServer"),
    HOST_NAME("HostName"),
    BOOT_FILE_SIZE("BootFileSize"),
    MERIT_DUMP_FILE("MeritDumpFile"),
    DOMAIN_NAME("DomainName"),
    SWAP_SERVER("SwapServer"),
    ROOT_PATH("RootPath"),
    EXTENSION_PATH("ExtensionPath"),
    IP_FORWARDING_ENABLE_DISABLE("IPForwardingEnableDisable"),
    NON_LOCAL_SOURCE_ROUTING_ENABLE_DISABLE("NonLocalSourceRoutingEnableDisable"),
    POLICY_FILTER("PolicyFilter"),
    MAXIMUM_DATAGRAM_REASSEMBLY_SIZE("MaximumDatagramReassemblySize"),
    DEFAULT_IP_TIME_2_LIVE("DefaultIPTime2Live"),
    PATH_MTU_AGING_TIMEOUT("PathMTUAgingTimeout"),
    IP_LAYER_PARAMETERS_PER_INTERFACE("IPLayerParametersPerInterface"),
    INTERFACE_MTU("InterfaceMTU"),
    ALL_SUBNETS_ARE_LOCAL("AllSubnetsAreLocal"),
    BROADCAST_ADDRESS("BroadcastAddress"),
    PERFORM_MASK_DISCOVERY("PerformMaskDiscovery"),
    MASK_SUPPLIER("MaskSupplier"),
    PERFORM_ROUTE_DISCOVERY("PerformRouteDiscovery"),
    ROUTER_SOLICITATION_ADDRESS("RouterSolicitationAddress"),
    STATIC_ROUTE("StaticRoute"),
    TRAILER_ENCAPSULATION("TrailerEncapsulation"),
    ARP_CACHE_TIMEOUT("ARPCacheTimeout"),
    ETHERNET_ENCAPSULATION("EthernetEncapsulation"),
    TCP_DEFAULT_TTL("TCPDefaultTTL"),
    TCP_KEEP_ALIVE_INTERVAL("TCPKeepAliveInterval"),
    TCP_KEEP_ALIVE_GARBAGE("TCPKeepAliveGarbage"),
    NETWORK_INFORMATION_SERVICE_DOMAIN("NetworkInformationServiceDomain"),
    NETWORK_INFORMATION_SERVICE_SERVERS("NetworkInformationServiceServers"),
    NETWORK_TIME_PROTOCOL_SERVERS("NetworkTimeProtocolServers"),
    VENDOR_SPECIFIC_INFORMATION("VendorSpecificInformation"),
    OPTION_44("Option_44"),
    OPTION_45("Option_45"),
    OPTION_46("Option_46"),
    OPTION_47("Option_47"),
    OPTION_48("Option_48"),
    OPTION_49("Option_49"),
    IP_ADDRESS_LEASE_TIME("IPAddressLeaseTime"),
    OPTION_64("Option_64"),
    OPTION_65("Option_65"),
    TFTP_SERVER_NAME("TFTPServerName"),
    BOOTFILE_NAME("BootfileName"),
    OPTION_68("Option_68"),
    OPTION_69("Option_69"),
    OPTION_70("Option_70"),
    OPTION_71("Option_71"),
    OPTION_72("Option_72"),
    OPTION_73("Option_73"),
    OPTION_74("Option_74"),
    OPTION_75("Option_75"),
    OPTION_119("Option_119");
    private final String value;

    DhcpOpt(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DhcpOpt fromValue(String v) {
        for (DhcpOpt c: DhcpOpt.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
