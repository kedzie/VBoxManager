package com.kedzie.vbox.api;

public interface IProcess extends IManagedObjectRef {

//  <xsd:element name="IProcess_getPID">
//  <xsd:complexType>
//    <xsd:sequence>
//      <xsd:element name="_this" type="xsd:string"/>
//    </xsd:sequence>
//  </xsd:complexType>
//</xsd:element>
//<xsd:element name="IProcess_getPIDResponse">
//  <xsd:complexType>
//    <xsd:sequence>
//      <xsd:element name="returnval" type="xsd:unsignedInt"/>
//    </xsd:sequence>
//  </xsd:complexType>
//</xsd:element>
    
//<!-- readonly attribute IProcess::status-->
//<xsd:element name="IProcess_getStatus">
//  <xsd:complexType>
//    <xsd:sequence>
//      <xsd:element name="_this" type="xsd:string"/>
//    </xsd:sequence>
//  </xsd:complexType>
//</xsd:element>
//<xsd:element name="IProcess_getStatusResponse">
//  <xsd:complexType>
//    <xsd:sequence>
//      <xsd:element name="returnval" type="vbox:ProcessStatus"/>
//    </xsd:sequence>
//  </xsd:complexType>
//</xsd:element>
    
//<!-- readonly attribute IProcess::exitCode-->
//<xsd:element name="IProcess_getExitCode">
//  <xsd:complexType>
//    <xsd:sequence>
//      <xsd:element name="_this" type="xsd:string"/>
//    </xsd:sequence>
//  </xsd:complexType>
//</xsd:element>
//<xsd:element name="IProcess_getExitCodeResponse">
//  <xsd:complexType>
//    <xsd:sequence>
//      <xsd:element name="returnval" type="xsd:int"/>
//    </xsd:sequence>
//  </xsd:complexType>
//</xsd:element>
    
//<!-- readonly attribute IProcess::environment-->
//<xsd:element name="IProcess_getEnvironment">
//  <xsd:complexType>
//    <xsd:sequence>
//      <xsd:element name="_this" type="xsd:string"/>
//    </xsd:sequence>
//  </xsd:complexType>
//</xsd:element>
//<xsd:element name="IProcess_getEnvironmentResponse">
//  <xsd:complexType>
//    <xsd:sequence>
//      <xsd:element name="returnval" minOccurs="0" maxOccurs="unbounded" type="xsd:string"/>
//    </xsd:sequence>
//  </xsd:complexType>
//</xsd:element>
    
//<!-- readonly attribute IProcess::arguments-->
//<xsd:element name="IProcess_getArguments">
//  <xsd:complexType>
//    <xsd:sequence>
//      <xsd:element name="_this" type="xsd:string"/>
//    </xsd:sequence>
//  </xsd:complexType>
//</xsd:element>
//<xsd:element name="IProcess_getArgumentsResponse">
//  <xsd:complexType>
//    <xsd:sequence>
//      <xsd:element name="returnval" minOccurs="0" maxOccurs="unbounded" type="xsd:string"/>
//    </xsd:sequence>
//  </xsd:complexType>
//</xsd:element>
    
    
//<!-- readonly attribute IProcess::executablePath-->
//<xsd:element name="IProcess_getExecutablePath">
//  <xsd:complexType>
//    <xsd:sequence>
//      <xsd:element name="_this" type="xsd:string"/>
//    </xsd:sequence>
//  </xsd:complexType>
//</xsd:element>
//<xsd:element name="IProcess_getExecutablePathResponse">
//  <xsd:complexType>
//    <xsd:sequence>
//      <xsd:element name="returnval" type="xsd:string"/>
//    </xsd:sequence>
//  </xsd:complexType>
//</xsd:element>
    
    
//<!-- readonly attribute IProcess::name-->
//<xsd:element name="IProcess_getName">
//  <xsd:complexType>
//    <xsd:sequence>
//      <xsd:element name="_this" type="xsd:string"/>
//    </xsd:sequence>
//  </xsd:complexType>
//</xsd:element>
//<xsd:element name="IProcess_getNameResponse">
//  <xsd:complexType>
//    <xsd:sequence>
//      <xsd:element name="returnval" type="xsd:string"/>
//    </xsd:sequence>
//  </xsd:complexType>
//</xsd:element>
    
    
//<!-- method IProcess::waitFor-->
//<xsd:element name="IProcess_waitFor">
//  <xsd:complexType>
//    <xsd:sequence>
//      <xsd:element name="_this" type="xsd:string"/>
//      <xsd:element name="waitFor" type="xsd:unsignedInt"/>
//      <xsd:element name="timeoutMS" type="xsd:unsignedInt"/>
//    </xsd:sequence>
//  </xsd:complexType>
//</xsd:element>
//<xsd:element name="IProcess_waitForResponse">
//  <xsd:complexType>
//    <xsd:sequence>
//      <xsd:element name="returnval" type="vbox:ProcessWaitResult"/>
//    </xsd:sequence>
//  </xsd:complexType>
//</xsd:element>
    
    
//<!-- method IProcess::waitForArray-->
//<xsd:element name="IProcess_waitForArray">
//  <xsd:complexType>
//    <xsd:sequence>
//      <xsd:element name="_this" type="xsd:string"/>
//      <xsd:element name="waitFor" minOccurs="0" maxOccurs="unbounded" type="vbox:ProcessWaitForFlag"/>
//      <xsd:element name="timeoutMS" type="xsd:unsignedInt"/>
//    </xsd:sequence>
//  </xsd:complexType>
//</xsd:element>
//<xsd:element name="IProcess_waitForArrayResponse">
//  <xsd:complexType>
//    <xsd:sequence>
//      <xsd:element name="returnval" type="vbox:ProcessWaitResult"/>
//    </xsd:sequence>
//  </xsd:complexType>
//</xsd:element>
    
    
//<!-- method IProcess::read-->
//<xsd:element name="IProcess_read">
//  <xsd:complexType>
//    <xsd:sequence>
//      <xsd:element name="_this" type="xsd:string"/>
//      <xsd:element name="handle" type="xsd:unsignedInt"/>
//      <xsd:element name="toRead" type="xsd:unsignedInt"/>
//      <xsd:element name="timeoutMS" type="xsd:unsignedInt"/>
//    </xsd:sequence>
//  </xsd:complexType>
//</xsd:element>
//<xsd:element name="IProcess_readResponse">
//  <xsd:complexType>
//    <xsd:sequence>
//      <xsd:element name="returnval" type="xsd:string"/>
//    </xsd:sequence>
//  </xsd:complexType>
//</xsd:element>
    
    
//<!-- method IProcess::write-->
//<xsd:element name="IProcess_write">
//  <xsd:complexType>
//    <xsd:sequence>
//      <xsd:element name="_this" type="xsd:string"/>
//      <xsd:element name="handle" type="xsd:unsignedInt"/>
//      <xsd:element name="flags" type="xsd:unsignedInt"/>
//      <xsd:element name="data" type="xsd:string"/>
//      <xsd:element name="timeoutMS" type="xsd:unsignedInt"/>
//    </xsd:sequence>
//  </xsd:complexType>
//</xsd:element>
//<xsd:element name="IProcess_writeResponse">
//  <xsd:complexType>
//    <xsd:sequence>
//      <xsd:element name="returnval" type="xsd:unsignedInt"/>
//    </xsd:sequence>
//  </xsd:complexType>
//</xsd:element>
    
    
//<!-- method IProcess::writeArray-->
//<xsd:element name="IProcess_writeArray">
//  <xsd:complexType>
//    <xsd:sequence>
//      <xsd:element name="_this" type="xsd:string"/>
//      <xsd:element name="handle" type="xsd:unsignedInt"/>
//      <xsd:element name="flags" minOccurs="0" maxOccurs="unbounded" type="vbox:ProcessInputFlag"/>
//      <xsd:element name="data" type="xsd:string"/>
//      <xsd:element name="timeoutMS" type="xsd:unsignedInt"/>
//    </xsd:sequence>
//  </xsd:complexType>
//</xsd:element>
//<xsd:element name="IProcess_writeArrayResponse">
//  <xsd:complexType>
//    <xsd:sequence>
//      <xsd:element name="returnval" type="xsd:unsignedInt"/>
//    </xsd:sequence>
//  </xsd:complexType>
//</xsd:element>
    
//<!-- method IProcess::terminate-->
//<xsd:element name="IProcess_terminate">
//  <xsd:complexType>
//    <xsd:sequence>
//      <xsd:element name="_this" type="xsd:string"/>
//    </xsd:sequence>
//  </xsd:complexType>
//</xsd:element>
//<xsd:element name="IProcess_terminateResponse">
//  <xsd:complexType>
//    <xsd:sequence/>
//  </xsd:complexType>
//</xsd:element>
    
}
