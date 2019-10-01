

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for ProcessWaitResult.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ProcessWaitResult">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="Start"/>
 *     &lt;enumeration value="Terminate"/>
 *     &lt;enumeration value="Status"/>
 *     &lt;enumeration value="Error"/>
 *     &lt;enumeration value="Timeout"/>
 *     &lt;enumeration value="StdIn"/>
 *     &lt;enumeration value="StdOut"/>
 *     &lt;enumeration value="StdErr"/>
 *     &lt;enumeration value="WaitFlagNotSupported"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum ProcessWaitResult {

    NONE("None"),
    START("Start"),
    TERMINATE("Terminate"),
    STATUS("Status"),
    ERROR("Error"),
    TIMEOUT("Timeout"),
    STD_IN("StdIn"),
    STD_OUT("StdOut"),
    STD_ERR("StdErr"),
    WAIT_FLAG_NOT_SUPPORTED("WaitFlagNotSupported");
    private final String value;

    ProcessWaitResult(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ProcessWaitResult fromValue(String v) {
        for (ProcessWaitResult c: ProcessWaitResult.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
