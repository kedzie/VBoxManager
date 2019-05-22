package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.api.jaxb.AudioControllerType
import com.kedzie.vbox.api.jaxb.AudioDriverType
import com.kedzie.vbox.soap.Cacheable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy

@KsoapProxy
@Ksoap
interface IAudioAdapter : IManagedObjectRef, Parcelable {

    @Cacheable(value = "Enabled")
	suspend fun getEnabled(): Boolean

    suspend fun setEnabled(@Cacheable(value = "Enabled") enabled: Boolean)

    @Cacheable(value = "AudioController")
	suspend fun getAudioController(): AudioControllerType

    suspend fun setAudioController(@Cacheable(value = "AudioController") audioController: AudioControllerType)

    @Cacheable(value = "AudioDriver")
	suspend fun getAudioDriver(): AudioDriverType

    suspend fun setAudioDriver(@Cacheable(value = "AudioDriver") audioDriver: AudioDriverType)
}
