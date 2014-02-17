

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for AudioDriverType.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="AudioDriverType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Null"/>
 *     &lt;enumeration value="WinMM"/>
 *     &lt;enumeration value="OSS"/>
 *     &lt;enumeration value="ALSA"/>
 *     &lt;enumeration value="DirectSound"/>
 *     &lt;enumeration value="CoreAudio"/>
 *     &lt;enumeration value="MMPM"/>
 *     &lt;enumeration value="Pulse"/>
 *     &lt;enumeration value="SolAudio"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
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

	AudioDriverType(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static AudioDriverType fromValue(String v) {
		for (AudioDriverType c : AudioDriverType.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

	/**
	 * Get valid Audio Drivers for a particular operating system
	 *
	 * @param os Operating System family
	 * @return valid {@link AudioDriverType}s for particular operating system
	 */
	public static AudioDriverType[] getAudioDrivers(String os) {
		if (os.toLowerCase().equals("linux"))
			return new AudioDriverType[]{NULL, OSS, ALSA, PULSE};
		else if (os.toLowerCase().equals("solaris"))
			return new AudioDriverType[]{NULL, SOL_AUDIO};
		else if (os.toLowerCase().equals("windows"))
			return new AudioDriverType[]{NULL, WIN_MM, DIRECT_SOUND};
		else if (os.toLowerCase().equals("macos"))
			return new AudioDriverType[]{NULL, CORE_AUDIO};
		return new AudioDriverType[0];
	}
}
