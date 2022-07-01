package com.isa.cottages.Model;

public enum ComplaintType {
    BOAT("Boat"),
    COTTAGE("Cottage"),
    INSTRUCTOR("Instructor");

    private final String displayName;

    ComplaintType(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName(){
        return displayName;
    }
}
