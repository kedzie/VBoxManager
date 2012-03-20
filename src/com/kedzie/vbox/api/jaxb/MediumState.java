







package com.kedzie.vbox.api.jaxb;




 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
public enum MediumState {

        NOT_CREATED("NotCreated"),
        CREATED("Created"),
        LOCKED_READ("LockedRead"),
        LOCKED_WRITE("LockedWrite"),
        INACCESSIBLE("Inaccessible"),
        CREATING("Creating"),
        DELETING("Deleting");
    private final String value;

    MediumState(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MediumState fromValue(String v) {
        for (MediumState c: MediumState.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
