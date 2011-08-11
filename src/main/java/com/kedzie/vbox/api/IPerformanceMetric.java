package com.kedzie.vbox.api;

import com.kedzie.vbox.KSOAP;


public interface IPerformanceMetric extends IRemoteObject {
	@KSOAP(cache=true) public String getMetricName();
	@KSOAP(cache=true) public String getDescription();
	@KSOAP(cache=true) public Integer getMinimumValue();
	@KSOAP(cache=true) public Integer getMaximumValue();
	@KSOAP(cache=true) public String getUnit();
	@KSOAP(cache=true) public String getObject();
}
