package com.isa.cottages.Model;

public enum CancellationCondition {
    FREE("Free"),
    PERCENTAGE("Percentage");

    private final String displayName;

    CancellationCondition(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName(){
        return displayName;
    }
}
