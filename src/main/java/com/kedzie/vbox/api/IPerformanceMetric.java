package com.kedzie.vbox.api;

import com.kedzie.vbox.Cacheable;
import com.kedzie.vbox.KSOAP;


public interface IPerformanceMetric extends IRemoteObject {
	@Cacheable  public String getMetricName();
	@Cacheable  public String getDescription();
	@Cacheable  public Integer getMinimumValue();
	@Cacheable  public Integer getMaximumValue();
	@Cacheable  public String getUnit();
	@Cacheable  public String getObject();
}
