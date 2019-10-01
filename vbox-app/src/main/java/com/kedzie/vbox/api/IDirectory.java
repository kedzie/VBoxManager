package com.kedzie.vbox.api;

import com.kedzie.vbox.soap.KSOAP;

@KSOAP(prefix="IDirectory")
public interface IDirectory extends IManagedObjectRef {

    @KSOAP(prefix="IDirectory") public String getDirectoryName();

    @KSOAP(prefix="IDirectory") public String getFilter();

    @KSOAP(prefix="IDirectory") public void close();

    @KSOAP(prefix="IDirectory") public String read();
}