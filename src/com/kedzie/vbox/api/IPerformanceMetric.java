package com.kedzie.vbox.api;

import com.kedzie.vbox.soap.KSOAP;

@KSOAP(cacheable=true)  
public interface IPerformanceMetric extends IManagedObjectRef {
	public String getMetricName();
	public String getDescription();
	public Integer getMinimumValue();
	public Integer getMaximumValue();
	public String getUnit();
	public String getObject();
}
