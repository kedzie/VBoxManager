package com.kedzie.vbox.metrics;


/**
 * Metric data queried from server
 */
class MetricQuery {
	public String name;
	public String unit;
	public String scale;
	public String object;
	public int[] values;
}