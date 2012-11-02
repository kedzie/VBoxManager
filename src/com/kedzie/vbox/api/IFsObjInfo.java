package com.kedzie.vbox.api;

import com.kedzie.vbox.soap.KSOAP;

@KSOAP(prefix="IFsObjInfo")
public interface IFsObjInfo extends IManagedObjectRef {

//    <!-- readonly attribute IFsObjInfo::accessTime-->
//    <xsd:element name="IFsObjInfo_getAccessTime">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFsObjInfo_getAccessTimeResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:long"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- readonly attribute IFsObjInfo::allocatedSize-->
//    <xsd:element name="IFsObjInfo_getAllocatedSize">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFsObjInfo_getAllocatedSizeResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:long"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- readonly attribute IFsObjInfo::birthTime-->
//    <xsd:element name="IFsObjInfo_getBirthTime">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFsObjInfo_getBirthTimeResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:long"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- readonly attribute IFsObjInfo::changeTime-->
//    <xsd:element name="IFsObjInfo_getChangeTime">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFsObjInfo_getChangeTimeResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:long"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- readonly attribute IFsObjInfo::deviceNumber-->
//    <xsd:element name="IFsObjInfo_getDeviceNumber">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFsObjInfo_getDeviceNumberResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:unsignedInt"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- readonly attribute IFsObjInfo::fileAttributes-->
//    <xsd:element name="IFsObjInfo_getFileAttributes">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFsObjInfo_getFileAttributesResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- readonly attribute IFsObjInfo::generationId-->
//    <xsd:element name="IFsObjInfo_getGenerationId">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFsObjInfo_getGenerationIdResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:unsignedInt"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- readonly attribute IFsObjInfo::GID-->
//    <xsd:element name="IFsObjInfo_getGID">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFsObjInfo_getGIDResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:unsignedInt"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- readonly attribute IFsObjInfo::groupName-->
//    <xsd:element name="IFsObjInfo_getGroupName">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFsObjInfo_getGroupNameResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- readonly attribute IFsObjInfo::hardLinks-->
//    <xsd:element name="IFsObjInfo_getHardLinks">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFsObjInfo_getHardLinksResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:unsignedInt"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- readonly attribute IFsObjInfo::modificationTime-->
//    <xsd:element name="IFsObjInfo_getModificationTime">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFsObjInfo_getModificationTimeResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:long"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- readonly attribute IFsObjInfo::name-->
//    <xsd:element name="IFsObjInfo_getName">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFsObjInfo_getNameResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- readonly attribute IFsObjInfo::nodeId-->
//    <xsd:element name="IFsObjInfo_getNodeId">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFsObjInfo_getNodeIdResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:long"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- readonly attribute IFsObjInfo::nodeIdDevice-->
//    <xsd:element name="IFsObjInfo_getNodeIdDevice">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFsObjInfo_getNodeIdDeviceResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:unsignedInt"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- readonly attribute IFsObjInfo::objectSize-->
//    <xsd:element name="IFsObjInfo_getObjectSize">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFsObjInfo_getObjectSizeResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:long"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- readonly attribute IFsObjInfo::type-->
//    <xsd:element name="IFsObjInfo_getType">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFsObjInfo_getTypeResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="vbox:FsObjType"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- readonly attribute IFsObjInfo::UID-->
//    <xsd:element name="IFsObjInfo_getUID">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFsObjInfo_getUIDResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:unsignedInt"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- readonly attribute IFsObjInfo::userFlags-->
//    <xsd:element name="IFsObjInfo_getUserFlags">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFsObjInfo_getUserFlagsResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:unsignedInt"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- readonly attribute IFsObjInfo::userName-->
//    <xsd:element name="IFsObjInfo_getUserName">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IFsObjInfo_getUserNameResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
}