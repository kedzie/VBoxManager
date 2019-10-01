

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for ProcessCreateFlag.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ProcessCreateFlag">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="WaitForProcessStartOnly"/>
 *     &lt;enumeration value="IgnoreOrphanedProcesses"/>
 *     &lt;enumeration value="Hidden"/>
 *     &lt;enumeration value="Profile"/>
 *     &lt;enumeration value="WaitForStdOut"/>
 *     &lt;enumeration value="WaitForStdErr"/>
 *     &lt;enumeration value="ExpandArguments"/>
 *     &lt;enumeration value="UnquotedArguments"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum ProcessCreateFlag {

    NONE("None"),
    WAIT_FOR_PROCESS_START_ONLY("WaitForProcessStartOnly"),
    IGNORE_ORPHANED_PROCESSES("IgnoreOrphanedProcesses"),
    HIDDEN("Hidden"),
    PROFILE("Profile"),
    WAIT_FOR_STD_OUT("WaitForStdOut"),
    WAIT_FOR_STD_ERR("WaitForStdErr"),
    EXPAND_ARGUMENTS("ExpandArguments"),
    UNQUOTED_ARGUMENTS("UnquotedArguments");
    private final String value;

    ProcessCreateFlag(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ProcessCreateFlag fromValue(String v) {
        for (ProcessCreateFlag c: ProcessCreateFlag.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
