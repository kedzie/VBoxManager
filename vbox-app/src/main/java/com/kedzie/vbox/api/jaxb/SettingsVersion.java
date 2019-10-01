

package com.kedzie.vbox.api.jaxb;



/**
 * <p>Java class for SettingsVersion.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SettingsVersion">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Null"/>
 *     &lt;enumeration value="v1_0"/>
 *     &lt;enumeration value="v1_1"/>
 *     &lt;enumeration value="v1_2"/>
 *     &lt;enumeration value="v1_3pre"/>
 *     &lt;enumeration value="v1_3"/>
 *     &lt;enumeration value="v1_4"/>
 *     &lt;enumeration value="v1_5"/>
 *     &lt;enumeration value="v1_6"/>
 *     &lt;enumeration value="v1_7"/>
 *     &lt;enumeration value="v1_8"/>
 *     &lt;enumeration value="v1_9"/>
 *     &lt;enumeration value="v1_10"/>
 *     &lt;enumeration value="v1_11"/>
 *     &lt;enumeration value="v1_12"/>
 *     &lt;enumeration value="v1_13"/>
 *     &lt;enumeration value="v1_14"/>
 *     &lt;enumeration value="v1_15"/>
 *     &lt;enumeration value="v1_16"/>
 *     &lt;enumeration value="v1_17"/>
 *     &lt;enumeration value="Future"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum SettingsVersion {

    NULL("Null"),
    V_1_0("v1_0"),
    V_1_1("v1_1"),
    V_1_2("v1_2"),
    V_1_3_PRE("v1_3pre"),
    V_1_3("v1_3"),
    V_1_4("v1_4"),
    V_1_5("v1_5"),
    V_1_6("v1_6"),
    V_1_7("v1_7"),
    V_1_8("v1_8"),
    V_1_9("v1_9"),
    V_1_10("v1_10"),
    V_1_11("v1_11"),
    V_1_12("v1_12"),
    V_1_13("v1_13"),
    V_1_14("v1_14"),
    V_1_15("v1_15"),
    V_1_16("v1_16"),
    V_1_17("v1_17"),
    FUTURE("Future");
    private final String value;

    SettingsVersion(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SettingsVersion fromValue(String v) {
        for (SettingsVersion c: SettingsVersion.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
