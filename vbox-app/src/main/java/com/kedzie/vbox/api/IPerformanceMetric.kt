package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.soap.Cacheable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy

@KsoapProxy
@Ksoap
interface IPerformanceMetric : IManagedObjectRef, Parcelable {
    @Cacheable("MetricName")
	suspend fun getMetricName(): String
    @Cacheable("Description")
	suspend fun getDescription(): String
    @Cacheable("MinimumValue")
	suspend fun getMinimumValue(): Int
    @Cacheable("MaximumValue")
	suspend fun getMaximumValue(): Int
    @Cacheable("Period")
	suspend fun getPeriod(): Long
    @Cacheable("Count")
	suspend fun getCount(): Long
    @Cacheable("Unit")
	suspend fun getUnit(): String
    @Cacheable("Object")
	suspend fun getObject(): String
}