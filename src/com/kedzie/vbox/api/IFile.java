package com.kedzie.vbox.api;

import com.kedzie.vbox.soap.KSOAP;

@KSOAP(prefix="IFile")
public interface IFile extends IManagedObjectRef {

//    <!-- readonly attribute IFile::creationMode-->
//    <xsd:element name="IFile_getCreationMode">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFile_getCreationModeResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:unsignedInt"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- readonly attribute IFile::disposition-->
//    <xsd:element name="IFile_getDisposition">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFile_getDispositionResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:unsignedInt"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- readonly attribute IFile::fileName-->
//    <xsd:element name="IFile_getFileName">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFile_getFileNameResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- readonly attribute IFile::initialSize-->
//    <xsd:element name="IFile_getInitialSize">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFile_getInitialSizeResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:long"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- readonly attribute IFile::openMode-->
//    <xsd:element name="IFile_getOpenMode">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFile_getOpenModeResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:unsignedInt"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- readonly attribute IFile::offset-->
//    <xsd:element name="IFile_getOffset">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFile_getOffsetResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:long"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- method IFile::close-->
//    <xsd:element name="IFile_close">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFile_closeResponse">
//      <xsd:complexType>
//        <xsd:sequence/>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- method IFile::queryInfo-->
//    <xsd:element name="IFile_queryInfo">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFile_queryInfoResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- method IFile::read-->
//    <xsd:element name="IFile_read">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//          <xsd:element name="toRead" type="xsd:unsignedInt"/>
//          <xsd:element name="timeoutMS" type="xsd:unsignedInt"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFile_readResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- method IFile::readAt-->
//    <xsd:element name="IFile_readAt">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//          <xsd:element name="offset" type="xsd:long"/>
//          <xsd:element name="toRead" type="xsd:unsignedInt"/>
//          <xsd:element name="timeoutMS" type="xsd:unsignedInt"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFile_readAtResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- method IFile::seek-->
//    <xsd:element name="IFile_seek">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//          <xsd:element name="offset" type="xsd:long"/>
//          <xsd:element name="whence" type="vbox:FileSeekType"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFile_seekResponse">
//      <xsd:complexType>
//        <xsd:sequence/>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- method IFile::setACL-->
//    <xsd:element name="IFile_setACL">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//          <xsd:element name="acl" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFile_setACLResponse">
//      <xsd:complexType>
//        <xsd:sequence/>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- method IFile::write-->
//    <xsd:element name="IFile_write">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//          <xsd:element name="data" type="xsd:string"/>
//          <xsd:element name="timeoutMS" type="xsd:unsignedInt"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFile_writeResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:unsignedInt"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- method IFile::writeAt-->
//    <xsd:element name="IFile_writeAt">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//          <xsd:element name="offset" type="xsd:long"/>
//          <xsd:element name="data" type="xsd:string"/>
//          <xsd:element name="timeoutMS" type="xsd:unsignedInt"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFile_writeAtResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:unsignedInt"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
}
