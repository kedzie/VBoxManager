package com.kedzie.vbox.api;

import com.kedzie.vbox.soap.KSOAP;


public interface IPerformanceMetric extends IManagedObjectRef {
	@KSOAP(cacheable=true)  public String getMetricName();
	@KSOAP(cacheable=true)  public String getDescription();
	@KSOAP(cacheable=true)  public Integer getMinimumValue();
	@KSOAP(cacheable=true)  public Integer getMaximumValue();
	@KSOAP(cacheable=true)  public String getUnit();
	@KSOAP(cacheable=true)  public String getObject();
}
