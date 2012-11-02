package com.kedzie.vbox.api;

import java.util.List;

import com.kedzie.vbox.api.jaxb.CopyFileFlag;
import com.kedzie.vbox.api.jaxb.DirectoryCreateFlag;
import com.kedzie.vbox.api.jaxb.DirectoryOpenFlag;
import com.kedzie.vbox.api.jaxb.DirectoryRemoveRecFlag;
import com.kedzie.vbox.api.jaxb.PathRenameFlag;
import com.kedzie.vbox.soap.KSOAP;

@KSOAP
public interface IGuestSession extends IManagedObjectRef {

    @KSOAP(cacheable=true) public String getUser();
    @KSOAP(cacheable=true) public String getDomain();
    @KSOAP(cacheable=true) public String getName();
    @KSOAP(cacheable=true) public Integer getId();
    
    @KSOAP(cacheable=true) public int getTimeout();
    public void setTimeout(@KSOAP(type="unsignedint", value="timeout") int timeout);
    
    @KSOAP(cacheable=true) public List<String> getEnvironment();
    public void setEnvironment(@KSOAP("environment") String...environment);
    
    //TODO replace return type
    @KSOAP(cacheable=true) public List<IGuestProcess> getProcesses();
    
    //TODO replace return type
    @KSOAP(cacheable=true) public List<IGuestDirectory> getDirectories();
    
    //TODO replace return type
    @KSOAP(cacheable=true) public List<IGuestFile> getFiles();
    
    public void close();
    
    //TODO replace return type
    public String copyFrom(@KSOAP("source") String source, @KSOAP("dest") String dest, @KSOAP("flags") CopyFileFlag...flags);
    
    //TODO replace return type
    public String copyTo(@KSOAP("source") String source, @KSOAP("dest") String dest, @KSOAP("flags") CopyFileFlag...flags);
    
    public void directoryCreate(@KSOAP("path") String path, @KSOAP(type="unsignedint", value="mode") int mode, @KSOAP("flags") DirectoryCreateFlag...flags);
    
    //TODO replace return type
    public IGuestDirectory directoryCreateTemp(@KSOAP("templateName") String templateName, @KSOAP(type="unsignedint", value="mode") int mode, 
            @KSOAP("path") String path, @KSOAP(type="boolean", value="secure") boolean secure);

    public boolean directoryExists(@KSOAP("path") String path);
    
    //TODO replace return type
    public IGuestDirectory directoryOpen(@KSOAP("path") String path, @KSOAP("filter") String filter, @KSOAP("flags") DirectoryOpenFlag...flags);
    
    //TODO replace return type
    public String directoryQueryInfo(@KSOAP("path") String path);
    
    public void directoryRemove(@KSOAP("path") String path);
    
    public IProgress directoryRemoveRecursive(@KSOAP("path") String path, @KSOAP("flags") DirectoryRemoveRecFlag...flags);
    
    public void directoryRename(@KSOAP("source") String source, @KSOAP("dest") String dest, @KSOAP("flags") PathRenameFlag...flags);
    
    public void directorySetACL(@KSOAP("path") String path, @KSOAP("acl") String acl);
    
    public void environmentClear();
    
    public String environmentGet(@KSOAP("name") String name);
    
    public void environmentSet(@KSOAP("name") String name, @KSOAP("value") String value);
    
    public void environmentUnSet(@KSOAP("name") String name);
    
    //TODO replace return type
    public IGuestFile fileCreateTemp(@KSOAP("templateName") String templateName, @KSOAP(type="unsignedint", value="mode") int mode, 
            @KSOAP("path") String path, @KSOAP(type="boolean", value="secure") boolean secure);
    
    public boolean fileExists(@KSOAP("path") String path);
    
    public void fileRemove(@KSOAP("path") String path);
    
  //TODO replace return type
    public IGuestFile fileOpen(@KSOAP("path") String path, @KSOAP("openMode") String openMode, @KSOAP("disposition") String disposition,
            @KSOAP(type="unsignedint", value="creationMode") int creationMode, @KSOAP(type="long", value="offset") long offset);
    
    public IFsObjInfo fileQueryInfo(@KSOAP("path") String path);
    
    public void fileRename(@KSOAP("source") String source, @KSOAP("dest") String dest, @KSOAP("flags") PathRenameFlag...flags);
    
    public long fileQuerySize(@KSOAP("path") String path);
    
    public void fileSetACL(@KSOAP("path") String path, @KSOAP("acl") String acl);
    
//    <!-- method IGuestSession::processCreate-->
//    <xsd:element name="IGuestSession_processCreate">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//          <xsd:element name="command" type="xsd:string"/>
//          <xsd:element name="arguments" minOccurs="0" maxOccurs="unbounded" type="xsd:string"/>
//          <xsd:element name="environment" minOccurs="0" maxOccurs="unbounded" type="xsd:string"/>
//          <xsd:element name="flags" minOccurs="0" maxOccurs="unbounded" type="vbox:ProcessCreateFlag"/>
//          <xsd:element name="timeoutMS" type="xsd:unsignedInt"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IGuestSession_processCreateResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- method IGuestSession::processCreateEx-->
//    <xsd:element name="IGuestSession_processCreateEx">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//          <xsd:element name="command" type="xsd:string"/>
//          <xsd:element name="arguments" minOccurs="0" maxOccurs="unbounded" type="xsd:string"/>
//          <xsd:element name="environment" minOccurs="0" maxOccurs="unbounded" type="xsd:string"/>
//          <xsd:element name="flags" minOccurs="0" maxOccurs="unbounded" type="vbox:ProcessCreateFlag"/>
//          <xsd:element name="timeoutMS" type="xsd:unsignedInt"/>
//          <xsd:element name="priority" type="vbox:ProcessPriority"/>
//          <xsd:element name="affinity" minOccurs="0" maxOccurs="unbounded" type="xsd:int"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IGuestSession_processCreateExResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- method IGuestSession::processGet-->
//    <xsd:element name="IGuestSession_processGet">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//          <xsd:element name="pid" type="xsd:unsignedInt"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IGuestSession_processGetResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- method IGuestSession::symlinkCreate-->
//    <xsd:element name="IGuestSession_symlinkCreate">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//          <xsd:element name="source" type="xsd:string"/>
//          <xsd:element name="target" type="xsd:string"/>
//          <xsd:element name="type" type="vbox:SymlinkType"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IGuestSession_symlinkCreateResponse">
//      <xsd:complexType>
//        <xsd:sequence/>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- method IGuestSession::symlinkExists-->
//    <xsd:element name="IGuestSession_symlinkExists">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//          <xsd:element name="symlink" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IGuestSession_symlinkExistsResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:boolean"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- method IGuestSession::symlinkRead-->
//    <xsd:element name="IGuestSession_symlinkRead">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//          <xsd:element name="symlink" type="xsd:string"/>
//          <xsd:element name="flags" minOccurs="0" maxOccurs="unbounded" type="vbox:SymlinkReadFlag"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IGuestSession_symlinkReadResponse">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="returnval" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- method IGuestSession::symlinkRemoveDirectory-->
//    <xsd:element name="IGuestSession_symlinkRemoveDirectory">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//          <xsd:element name="path" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IGuestSession_symlinkRemoveDirectoryResponse">
//      <xsd:complexType>
//        <xsd:sequence/>
//      </xsd:complexType>
//    </xsd:element>
    
//    <!-- method IGuestSession::symlinkRemoveFile-->
//    <xsd:element name="IGuestSession_symlinkRemoveFile">
//      <xsd:complexType>
//        <xsd:sequence>
//          <xsd:element name="_this" type="xsd:string"/>
//          <xsd:element name="file" type="xsd:string"/>
//        </xsd:sequence>
//      </xsd:complexType>
//    </xsd:element>
//    <xsd:element name="IGuestSession_symlinkRemoveFileResponse">
//      <xsd:complexType>
//        <xsd:sequence/>
//      </xsd:complexType>
//    </xsd:element>
}
