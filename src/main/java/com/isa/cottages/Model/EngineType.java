package com.isa.cottages.Model;

public enum EngineType {
    INBOARD("Inboard"),
    OUTBOARD("Outboard"),
    JET_PROPULSION("Jet propulsion"),
    SURFACE_DRIVES("Surface drives"),
    POD_DRIVES("Pod drives");

    private final String displayName;

    EngineType(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName(){
        return displayName;
    }
}
