package com.kedzie.vbox.api.jaxb;

public enum AudioDriverType {
    NULL("Null"),
    WIN_MM("WinMM"),
    OSS("OSS"),
    ALSA("ALSA"),
    DIRECT_SOUND("DirectSound"),
    CORE_AUDIO("CoreAudio"),
    MMPM("MMPM"),
    PULSE("Pulse"),
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
}
