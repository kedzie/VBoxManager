package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;


public enum AudioDriverType implements Serializable {
    /** Null value, also means "dummy audio driver". */
    NULL("Null"),
    /** Windows multimedia (Windows hosts only). */
    WIN_MM("WinMM"),
    /** Open Sound System (Linux hosts only). */
    OSS("OSS"),
    /**Advanced Linux Sound Architecture (Linux hosts only). */
    ALSA("ALSA"),
    /** DirectSound (Windows hosts only). */
    DIRECT_SOUND("DirectSound"),
    /** CoreAudio (Mac hosts only). */
    CORE_AUDIO("CoreAudio"),
    /** Reserved for historical reasons.  */
    MMPM("MMPM"),
    /** PulseAudio (Linux hosts only). */
    PULSE("Pulse"),
    /** Solaris audio (Solaris hosts only). */
    SOL_AUDIO("SolAudio");
    
    private final String value;
    
    public String toString() {
        return value;
    }
    AudioDriverType(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static AudioDriverType fromValue(String v) {
        for (AudioDriverType c: AudioDriverType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
    
    /**
     * Get valid Audio Drivers for a particular operating system
     * @param os		Operating System family
     * @return		valid {@link AudioDriverType}s for particular operating system
     */
    public static AudioDriverType[] getAudioDrivers(String os) {
    	if(os.toLowerCase().equals("linux"))
    		return new AudioDriverType[] { NULL, OSS, ALSA, PULSE };
    	else if(os.toLowerCase().equals("solaris"))
    		return new AudioDriverType[] { NULL, SOL_AUDIO };
    	else if(os.toLowerCase().equals("windows"))
    		return new AudioDriverType[] { NULL, WIN_MM, DIRECT_SOUND };
    	else if(os.toLowerCase().equals("macos"))
    		return new AudioDriverType[] { NULL, CORE_AUDIO };
    	return new AudioDriverType[0];
    }
}
