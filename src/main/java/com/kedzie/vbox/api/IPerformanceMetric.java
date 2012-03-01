package com.kedzie.vbox.api;

import com.kedzie.vbox.soap.Cacheable;


public interface IPerformanceMetric extends IManagedObjectRef {
	@Cacheable  public String getMetricName();
	@Cacheable  public String getDescription();
	@Cacheable  public Integer getMinimumValue();
	@Cacheable  public Integer getMaximumValue();
	@Cacheable  public String getUnit();
	@Cacheable  public String getObject();
}
