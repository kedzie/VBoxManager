package com.kedzie.vbox.api;

import java.util.List;
import java.util.Map;

import com.kedzie.vbox.soap.KSOAP;

public interface IDisplay extends IManagedObjectRef {
	public Map<String, List<String>> getScreenResolution(@KSOAP(type="unsignedInt", value="screenId") int screenId);
}
