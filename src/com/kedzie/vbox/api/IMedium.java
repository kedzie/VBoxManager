package com.kedzie.vbox.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.api.jaxb.DeviceType;
import com.kedzie.vbox.api.jaxb.MediumState;
import com.kedzie.vbox.api.jaxb.MediumType;
import com.kedzie.vbox.soap.Asyncronous;
import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;

public interface IMedium extends IManagedObjectRef, Parcelable {
	
static final ClassLoader LOADER = IMedium.class.getClassLoader();
	
	public static final Parcelable.Creator<IMedium> CREATOR = new Parcelable.Creator<IMedium>() {
		public IMedium createFromParcel(Parcel in) {
			VBoxSvc vmgr =  in.readParcelable(LOADER);
			String id = in.readString();
			Map<String, Object> cache = new HashMap<String, Object>();
			in.readMap(cache, LOADER);
			return (IMedium) vmgr.getProxy(IMedium.class, id, cache); 
		}
		public IMedium[] newArray(int size) {  
			return new IMedium[size]; 
		}
	};

     @KSOAP(cacheable=true) public String getId();
     
     @KSOAP(cacheable=true) public String getDescription();
     @Asyncronous public void setDescription(@KSOAP("description")String description);
     
     @KSOAP(cacheable=true) public MediumState getState();
     
     @KSOAP(cacheable=true) public Integer getVariant();

     @KSOAP(cacheable=true) public String getLocation();
     @Asyncronous public void setLocation(@KSOAP("location")String location);
     
     @KSOAP(cacheable=true) public String getName();
     
     @KSOAP(cacheable=true) public DeviceType getDeviceType();
     
     @KSOAP(cacheable=true) public Boolean getHostDrive();

     @KSOAP(cacheable=true) public Long getSize();

     @KSOAP(cacheable=true) public String getFormat();
     
     @KSOAP(cacheable=true) public IMediumFormat getMediumFormat();
     
     @KSOAP(cacheable=true) public MediumType getType();
     @Asyncronous public void setType(@KSOAP("type") MediumType type);
     
     @KSOAP(cacheable=true) public MediumType[]  getAllowedTypes();
     
     @KSOAP(cacheable=true) public IMedium getParent();
     
     @KSOAP(cacheable=true) public List<IMedium> getChildren();
     
     @KSOAP(cacheable=true) public IMedium getBase();

     @KSOAP(cacheable=true) public Boolean getReadOnly();

     @KSOAP(cacheable=true) public Long getLogicalSize();

     @KSOAP(cacheable=true) public Boolean getAutoReset();
     @Asyncronous public void setAutoReset(@KSOAP("autoReset") boolean autoReset);
     
     @KSOAP(cacheable=true) public String getLastAccessError();

     @KSOAP(cacheable=true) public List<String> getMachineIds();
//     
//     <!-- method IMedium::setIds-->
//     <xsd:element name="IMedium_setIds">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="_this" type="xsd:string"/>
//           <xsd:element name="setImageId" type="xsd:boolean"/>
//           <xsd:element name="imageId" type="xsd:string"/>
//           <xsd:element name="setParentId" type="xsd:boolean"/>
//           <xsd:element name="parentId" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     <xsd:element name="IMedium_setIdsResponse">
//       <xsd:complexType>
//         <xsd:sequence/>
//       </xsd:complexType>
//     </xsd:element>
//     
//     <!-- method IMedium::refreshState-->
//     <xsd:element name="IMedium_refreshState">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="_this" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     <xsd:element name="IMedium_refreshStateResponse">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="returnval" type="vbox:MediumState"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     
//     <!-- method IMedium::getSnapshotIds-->
//     <xsd:element name="IMedium_getSnapshotIds">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="_this" type="xsd:string"/>
//           <xsd:element name="machineId" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     <xsd:element name="IMedium_getSnapshotIdsResponse">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="returnval" minOccurs="0" maxOccurs="unbounded" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     
//     <!-- method IMedium::lockRead-->
//     <xsd:element name="IMedium_lockRead">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="_this" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     <xsd:element name="IMedium_lockReadResponse">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="returnval" type="vbox:MediumState"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     
//     <!-- method IMedium::unlockRead-->
//     <xsd:element name="IMedium_unlockRead">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="_this" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     <xsd:element name="IMedium_unlockReadResponse">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="returnval" type="vbox:MediumState"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     
//     <!-- method IMedium::lockWrite-->
//     <xsd:element name="IMedium_lockWrite">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="_this" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     <xsd:element name="IMedium_lockWriteResponse">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="returnval" type="vbox:MediumState"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     
//     <!-- method IMedium::unlockWrite-->
//     <xsd:element name="IMedium_unlockWrite">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="_this" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     <xsd:element name="IMedium_unlockWriteResponse">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="returnval" type="vbox:MediumState"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     
//     <!-- method IMedium::close-->
//     <xsd:element name="IMedium_close">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="_this" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     <xsd:element name="IMedium_closeResponse">
//       <xsd:complexType>
//         <xsd:sequence/>
//       </xsd:complexType>
//     </xsd:element>
//     
//     <!-- method IMedium::getProperty-->
//     <xsd:element name="IMedium_getProperty">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="_this" type="xsd:string"/>
//           <xsd:element name="name" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     <xsd:element name="IMedium_getPropertyResponse">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="returnval" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     
//     <!-- method IMedium::setProperty-->
//     <xsd:element name="IMedium_setProperty">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="_this" type="xsd:string"/>
//           <xsd:element name="name" type="xsd:string"/>
//           <xsd:element name="value" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     <xsd:element name="IMedium_setPropertyResponse">
//       <xsd:complexType>
//         <xsd:sequence/>
//       </xsd:complexType>
//     </xsd:element>
//     
//     <!-- method IMedium::getProperties-->
//     <xsd:element name="IMedium_getProperties">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="_this" type="xsd:string"/>
//           <xsd:element name="names" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     <xsd:element name="IMedium_getPropertiesResponse">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="returnNames" minOccurs="0" maxOccurs="unbounded" type="xsd:string"/>
//           <xsd:element name="returnval" minOccurs="0" maxOccurs="unbounded" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     
//     <!-- method IMedium::setProperties-->
//     <xsd:element name="IMedium_setProperties">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="_this" type="xsd:string"/>
//           <xsd:element name="names" minOccurs="0" maxOccurs="unbounded" type="xsd:string"/>
//           <xsd:element name="values" minOccurs="0" maxOccurs="unbounded" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     <xsd:element name="IMedium_setPropertiesResponse">
//       <xsd:complexType>
//         <xsd:sequence/>
//       </xsd:complexType>
//     </xsd:element>
//     
//     <!-- method IMedium::createBaseStorage-->
//     <xsd:element name="IMedium_createBaseStorage">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="_this" type="xsd:string"/>
//           <xsd:element name="logicalSize" type="xsd:long"/>
//           <xsd:element name="variant" type="xsd:unsignedInt"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     <xsd:element name="IMedium_createBaseStorageResponse">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="returnval" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     
//     <!-- method IMedium::deleteStorage-->
//     <xsd:element name="IMedium_deleteStorage">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="_this" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     <xsd:element name="IMedium_deleteStorageResponse">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="returnval" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     
//     <!-- method IMedium::createDiffStorage-->
//     <xsd:element name="IMedium_createDiffStorage">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="_this" type="xsd:string"/>
//           <xsd:element name="target" type="xsd:string"/>
//           <xsd:element name="variant" type="xsd:unsignedInt"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     <xsd:element name="IMedium_createDiffStorageResponse">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="returnval" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     
//     <!-- method IMedium::mergeTo-->
//     <xsd:element name="IMedium_mergeTo">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="_this" type="xsd:string"/>
//           <xsd:element name="target" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     <xsd:element name="IMedium_mergeToResponse">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="returnval" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     
//     <!-- method IMedium::cloneTo-->
//     <xsd:element name="IMedium_cloneTo">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="_this" type="xsd:string"/>
//           <xsd:element name="target" type="xsd:string"/>
//           <xsd:element name="variant" type="xsd:unsignedInt"/>
//           <xsd:element name="parent" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     <xsd:element name="IMedium_cloneToResponse">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="returnval" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     
//     <!-- method IMedium::cloneToBase-->
//     <xsd:element name="IMedium_cloneToBase">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="_this" type="xsd:string"/>
//           <xsd:element name="target" type="xsd:string"/>
//           <xsd:element name="variant" type="xsd:unsignedInt"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     <xsd:element name="IMedium_cloneToBaseResponse">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="returnval" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     
//     <!-- method IMedium::compact-->
//     <xsd:element name="IMedium_compact">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="_this" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     <xsd:element name="IMedium_compactResponse">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="returnval" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     
//     <!-- method IMedium::resize-->
//     <xsd:element name="IMedium_resize">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="_this" type="xsd:string"/>
//           <xsd:element name="logicalSize" type="xsd:long"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     <xsd:element name="IMedium_resizeResponse">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="returnval" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     
//     <!-- method IMedium::reset-->
//     <xsd:element name="IMedium_reset">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="_this" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
//     <xsd:element name="IMedium_resetResponse">
//       <xsd:complexType>
//         <xsd:sequence>
//           <xsd:element name="returnval" type="xsd:string"/>
//         </xsd:sequence>
//       </xsd:complexType>
//     </xsd:element>
}
