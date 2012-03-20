







package com.kedzie.vbox.api.jaxb;




 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
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
        FUTURE("Future");
    private final String value;

    SettingsVersion(String v) {
        value = v;
    }

    public String value() {
        return value;
    }
    public String toString() { return value; }
    public static SettingsVersion fromValue(String v) {
        for (SettingsVersion c: SettingsVersion.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
