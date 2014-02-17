

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for ProcessStatus.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="ProcessStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Undefined"/>
 *     &lt;enumeration value="Starting"/>
 *     &lt;enumeration value="Started"/>
 *     &lt;enumeration value="Paused"/>
 *     &lt;enumeration value="Terminating"/>
 *     &lt;enumeration value="TerminatedNormally"/>
 *     &lt;enumeration value="TerminatedSignal"/>
 *     &lt;enumeration value="TerminatedAbnormally"/>
 *     &lt;enumeration value="TimedOutKilled"/>
 *     &lt;enumeration value="TimedOutAbnormally"/>
 *     &lt;enumeration value="Down"/>
 *     &lt;enumeration value="Error"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum ProcessStatus {

	UNDEFINED("Undefined"),
	STARTING("Starting"),
	STARTED("Started"),
	PAUSED("Paused"),
	TERMINATING("Terminating"),
	TERMINATED_NORMALLY("TerminatedNormally"),
	TERMINATED_SIGNAL("TerminatedSignal"),
	TERMINATED_ABNORMALLY("TerminatedAbnormally"),
	TIMED_OUT_KILLED("TimedOutKilled"),
	TIMED_OUT_ABNORMALLY("TimedOutAbnormally"),
	DOWN("Down"),
	ERROR("Error");
	private final String value;

	ProcessStatus(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static ProcessStatus fromValue(String v) {
		for (ProcessStatus c : ProcessStatus.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
