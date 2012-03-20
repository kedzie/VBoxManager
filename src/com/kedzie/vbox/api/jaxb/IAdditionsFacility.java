package com.kedzie.vbox.api.jaxb;

 
 

public class IAdditionsFacility {

    protected AdditionsFacilityClass classType;
    protected long lastUpdated;
    protected String name;
    protected AdditionsFacilityStatus status;
    protected AdditionsFacilityType type;
     
    public AdditionsFacilityClass getClassType() {
        return classType;
    }
     
    public void setClassType(AdditionsFacilityClass value) {
        this.classType = value;
    }

    
     
     
     
    public long getLastUpdated() {
        return lastUpdated;
    }
     
    public void setLastUpdated(long value) {
        this.lastUpdated = value;
    }

    
    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }
    public AdditionsFacilityStatus getStatus() {
        return status;
    }
     
    public void setStatus(AdditionsFacilityStatus value) {
        this.status = value;
    }

    public AdditionsFacilityType getType() {
        return type;
    }
     
     
    public void setType(AdditionsFacilityType value) {
        this.type = value;
    }

}
