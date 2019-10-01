package com.kedzie.vbox.api;

import java.util.List;
import java.util.Map;

import com.kedzie.vbox.soap.KSOAP;

@KSOAP
public interface IMediumFormat extends IManagedObjectRef {

    @KSOAP(cacheable=true) public String getId();
    @KSOAP(cacheable=true) public String getName();
    @KSOAP(cacheable=true) public Integer getCapabilities();
    @KSOAP(cacheable=true) public Map<String, List<String>> describeFileExtensions();
    @KSOAP(cacheable=true) public Map<String, List<String>> describeProperties();
}
