package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.api.jaxb.BIOSBootMenuMode
import com.kedzie.vbox.soap.Cacheable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy


@KsoapProxy
@Ksoap
interface IBIOSSettings: IManagedObjectRef, Parcelable {

    @Cacheable(value = "LogoFadeIn")
    suspend fun getLogoFadeIn(): Boolean

    suspend fun setLogoFadeIn(@Cacheable(value = "LogoFadeIn") logoFadeIn: Boolean)

    @Cacheable(value = "LogoFadeOut")
    suspend fun getLogoFadeOut(): Boolean

    suspend fun setLogoFadeOut(@Cacheable(value = "LogoFadeOut") logoFadeOut: Boolean)

    @Cacheable(value = "LogoDisplayTime")
    suspend fun getLogoDisplayTime(): Int

    suspend fun setLogoDisplayTime(@Cacheable(value = "LogoDisplayTime") @Ksoap(type = "unsignedInt") logoDisplayTime: Int)

    @Cacheable(value = "LogoImagePath")
    suspend fun getLogoImagePath(): String

    suspend fun setLogoImagePath(@Cacheable(value = "LogoImagePath") logoImagePath: String)

    @Cacheable(value = "TimeOffset")
    suspend fun getTimeOffset(): Int

    suspend fun setTimeOffset(@Cacheable(value = "TimeOffset") @Ksoap(type = "unsignedInt") timeOffset: Int)

    @Cacheable(value = "ACPIEnabled")
    suspend fun getACPIEnabled(): Boolean

    suspend fun setACPIEnabled(@Cacheable(value = "ACPIEnabled") ACPIEnabled: Boolean)

    @Cacheable(value = "IOAPICEnabled")
    suspend fun getIOAPICEnabled(): Boolean

    suspend fun setIOAPICEnabled(@Cacheable(value = "IOAPICEnabled") IOAPICEnabled: Boolean)

    @Cacheable(value = "BootMenuMode")
    suspend fun getBootMenuMode(): BIOSBootMenuMode

    suspend fun setBootMenuMode(@Cacheable(value = "BootMenuMode") bootMenuMode: BIOSBootMenuMode)

    @Cacheable(value = "PXEDebugEnabled")
    suspend fun getPXEDebugEnabled(): Boolean

    suspend fun setPXEDebugEnabled(@Cacheable(value = "PXEDebugEnabled") PXEDebugEnabled: Boolean)
}